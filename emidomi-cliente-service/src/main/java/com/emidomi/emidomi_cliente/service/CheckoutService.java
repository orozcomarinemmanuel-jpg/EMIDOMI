package com.emidomi.emidomi_cliente.service;

import com.emidomi.emidomi_cliente.dto.ConfirmarPedidoRequest;
import com.emidomi.emidomi_cliente.dto.ConfirmarPedidoResponse;
import com.emidomi.emidomi_cliente.dto.GrupoPedidoRequest;
import com.emidomi.emidomi_cliente.dto.ItemPedidoRequest;
import com.emidomi.emidomi_cliente.dto.PagoResumenResponse;
import com.emidomi.emidomi_cliente.dto.PedidoClienteResponse;
import com.emidomi.emidomi_cliente.dto.PedidoConfirmadoResponse;
import com.emidomi.emidomi_cliente.dto.ProductoPedidoResponse;
import com.emidomi.emidomi_cliente.model.Cliente;
import com.emidomi.emidomi_cliente.model.DetallePedido;
import com.emidomi.emidomi_cliente.model.Pago;
import com.emidomi.emidomi_cliente.model.Pedido;
import com.emidomi.emidomi_cliente.model.Producto;
import com.emidomi.emidomi_cliente.model.Restaurante;
import com.emidomi.emidomi_cliente.repository.ClienteRepository;
import com.emidomi.emidomi_cliente.repository.DetallePedidoRepository;
import com.emidomi.emidomi_cliente.repository.PagoRepository;
import com.emidomi.emidomi_cliente.repository.PedidoRepository;
import com.emidomi.emidomi_cliente.repository.ProductoRepository;
import com.emidomi.emidomi_cliente.repository.RestauranteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Confirmación de carrito: un pedido por restaurante, con su pago simulado.
 *
 * Reglas:
 * - Los precios salen de la tabla productos; el cliente nunca envía precios ni totales.
 * - El cliente sale del token (idCliente), no del cuerpo.
 * - Fase 1 valida TODO el carrito y calcula totales sin escribir nada.
 * - Fase 2 escribe pedidos, detalles y pagos dentro de UNA transacción: todo o nada.
 */
@Service
public class CheckoutService {

    /** Costo de domicilio fijo POR RESTAURANTE. Cambiar aquí es el único lugar a tocar. */
    public static final BigDecimal COSTO_DOMICILIO = new BigDecimal("5000.00");

    private static final int MAX_CANTIDAD_POR_LINEA = 99;
    private static final int MAX_DIRECCION = 255;
    private static final int MAX_CIUDAD = 100;
    private static final int MAX_NOTAS = 500;
    private static final String ALFABETO_REFERENCIA = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final SecureRandom ALEATORIO = new SecureRandom();

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private RestauranteRepository restauranteRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private DetallePedidoRepository detallePedidoRepository;

    @Autowired
    private PagoRepository pagoRepository;

    private record LineaValidada(Producto producto, int cantidad, BigDecimal subtotal) {}

    private record GrupoValidado(Restaurante restaurante, List<LineaValidada> lineas, BigDecimal subtotal) {}

    @Transactional(rollbackFor = Exception.class)
    public ConfirmarPedidoResponse confirmar(Integer idCliente, ConfirmarPedidoRequest peticion) {
        if (peticion == null) {
            throw new IllegalArgumentException("La petición está vacía.");
        }

        Cliente cliente = clienteRepository.findById(idCliente)
                .filter(c -> Boolean.TRUE.equals(c.getEstado()))
                .orElseThrow(() -> new IllegalArgumentException("Cliente no válido o inactivo."));

        String direccion = validarDireccion(peticion.direccionEntrega());
        String ciudad = resolverCiudad(peticion.ciudadEntrega(), cliente.getCiudad());
        String notas = validarNotas(peticion.notas());
        Pago.MetodoPago metodo = parsearMetodo(peticion.metodoPago());
        validarEstructura(peticion.grupos());

        // FASE 1: validar todo y calcular totales. No se escribe nada aquí.
        List<GrupoValidado> validados = validarGrupos(peticion.grupos());

        // FASE 2: escritura. Cualquier excepción aquí revierte TODA la transacción.
        Pago.EstadoPago estadoPago = (metodo == Pago.MetodoPago.efectivo || metodo == Pago.MetodoPago.transferencia)
                ? Pago.EstadoPago.pendiente
                : Pago.EstadoPago.aprobado;

        List<PedidoConfirmadoResponse> resultados = new ArrayList<>();
        BigDecimal totalGeneral = BigDecimal.ZERO.setScale(2);

        for (GrupoValidado grupo : validados) {
            BigDecimal total = grupo.subtotal().add(COSTO_DOMICILIO);

            Pedido pedido = new Pedido();
            pedido.setIdCliente(idCliente);
            pedido.setIdRestaurante(grupo.restaurante().getIdRestaurante());
            pedido.setDireccionEntrega(direccion);
            pedido.setCiudadEntrega(ciudad);
            pedido.setNotas(notas);
            pedido.setEstadoPedido(Pedido.EstadoPedido.PENDIENTE);
            pedido.setSubtotal(grupo.subtotal());
            pedido.setCostoDomicilio(COSTO_DOMICILIO);
            pedido.setTotal(total);
            Pedido guardado = pedidoRepository.save(pedido);

            List<DetallePedido> detalles = new ArrayList<>();
            for (LineaValidada linea : grupo.lineas()) {
                DetallePedido detalle = new DetallePedido();
                detalle.setIdPedido(guardado.getIdPedido());
                detalle.setIdProducto(linea.producto().getIdProducto());
                detalle.setCantidad(linea.cantidad());
                detalle.setPrecioUnitario(linea.producto().getPrecio());
                detalle.setSubtotal(linea.subtotal());
                detalles.add(detalle);
            }
            detallePedidoRepository.saveAll(detalles);

            Pago pago = new Pago();
            pago.setIdPedido(guardado.getIdPedido());
            pago.setMetodoPago(metodo);
            pago.setEstadoPago(estadoPago);
            pago.setMonto(total);
            pago.setReferencia(generarReferencia(guardado.getIdPedido()));
            Pago pagoGuardado = pagoRepository.save(pago);

            resultados.add(new PedidoConfirmadoResponse(
                    guardado.getIdPedido(),
                    grupo.restaurante().getIdRestaurante(),
                    grupo.restaurante().getNombre(),
                    grupo.subtotal(),
                    COSTO_DOMICILIO,
                    total,
                    guardado.getEstadoPedido().name(),
                    null,
                    new PagoResumenResponse(
                            pagoGuardado.getIdPago(),
                            metodo.name(),
                            estadoPago.name(),
                            total,
                            pagoGuardado.getReferencia())));

            totalGeneral = totalGeneral.add(total);
        }

        return new ConfirmarPedidoResponse(totalGeneral, resultados);
    }

    /**
     * Pedidos del cliente indicado (el id viene del token), más recientes primero.
     * Consultas en lote: pedidos, detalles, productos, restaurantes y pagos (no una por pedido).
     */
    @Transactional(readOnly = true)
    public List<PedidoClienteResponse> listarMios(Integer idCliente) {
        List<Pedido> pedidos = pedidoRepository.findByIdClienteOrderByFechaPedidoDescIdPedidoDesc(idCliente);
        if (pedidos.isEmpty()) {
            return List.of();
        }

        List<Integer> idsPedidos = pedidos.stream().map(Pedido::getIdPedido).toList();

        Map<Integer, List<DetallePedido>> detallesPorPedido = detallePedidoRepository.findByIdPedidoIn(idsPedidos).stream()
                .collect(Collectors.groupingBy(DetallePedido::getIdPedido));

        List<Integer> idsProductos = detallesPorPedido.values().stream()
                .flatMap(List::stream)
                .map(DetallePedido::getIdProducto)
                .distinct()
                .toList();
        Map<Integer, String> nombresProductos = productoRepository.findAllById(idsProductos).stream()
                .collect(Collectors.toMap(Producto::getIdProducto, Producto::getNombre, (a, b) -> a, HashMap::new));

        List<Integer> idsRestaurantes = pedidos.stream()
                .map(Pedido::getIdRestaurante)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        Map<Integer, String> nombresRestaurantes = restauranteRepository.findAllById(idsRestaurantes).stream()
                .collect(Collectors.toMap(Restaurante::getIdRestaurante, Restaurante::getNombre, (a, b) -> a, HashMap::new));

        // Un pedido tiene a lo sumo un pago (pagos.id_pedido es UNIQUE); si hubiera más, se toma el primero.
        Map<Integer, Pago> pagoPorPedido = pagoRepository.findByIdPedidoIn(idsPedidos).stream()
                .collect(Collectors.toMap(Pago::getIdPedido, p -> p, (a, b) -> a, HashMap::new));

        List<PedidoClienteResponse> respuesta = new ArrayList<>();
        for (Pedido pedido : pedidos) {
            List<ProductoPedidoResponse> productos = detallesPorPedido
                    .getOrDefault(pedido.getIdPedido(), List.of())
                    .stream()
                    .map(d -> new ProductoPedidoResponse(
                            nombresProductos.getOrDefault(d.getIdProducto(), "Producto eliminado"),
                            d.getCantidad(),
                            d.getPrecioUnitario(),
                            d.getSubtotal()))
                    .toList();

            Pago pago = pagoPorPedido.get(pedido.getIdPedido());
            PagoResumenResponse pagoResumen = pago == null ? null : new PagoResumenResponse(
                    pago.getIdPago(),
                    pago.getMetodoPago() == null ? null : pago.getMetodoPago().name(),
                    pago.getEstadoPago() == null ? null : pago.getEstadoPago().name(),
                    pago.getMonto(),
                    pago.getReferencia());

            String nombreRestaurante = pedido.getIdRestaurante() == null
                    ? "Sin restaurante"
                    : nombresRestaurantes.getOrDefault(pedido.getIdRestaurante(), "Restaurante eliminado");

            respuesta.add(new PedidoClienteResponse(
                    pedido.getIdPedido(),
                    nombreRestaurante,
                    pedido.getEstadoPedido() == null ? null : pedido.getEstadoPedido().name(),
                    pedido.getSubtotal(),
                    pedido.getCostoDomicilio(),
                    pedido.getTotal(),
                    pedido.getDireccionEntrega(),
                    pedido.getCiudadEntrega(),
                    pedido.getNotas(),
                    pedido.getFechaPedido(),
                    productos,
                    pagoResumen));
        }
        return respuesta;
    }

    // ---------- Validaciones de cabecera ----------

    private String validarDireccion(String direccion) {
        if (direccion == null || direccion.isBlank()) {
            throw new IllegalArgumentException("La dirección de entrega es obligatoria.");
        }
        String limpia = direccion.trim();
        if (limpia.length() > MAX_DIRECCION) {
            throw new IllegalArgumentException("La dirección de entrega no puede tener más de " + MAX_DIRECCION + " caracteres.");
        }
        return limpia;
    }

    private String resolverCiudad(String ciudadPedido, String ciudadCliente) {
        String ciudad = (ciudadPedido == null || ciudadPedido.isBlank()) ? ciudadCliente : ciudadPedido.trim();
        if (ciudad != null && ciudad.length() > MAX_CIUDAD) {
            throw new IllegalArgumentException("La ciudad no puede tener más de " + MAX_CIUDAD + " caracteres.");
        }
        return ciudad;
    }

    private String validarNotas(String notas) {
        if (notas == null || notas.isBlank()) {
            return null;
        }
        String limpia = notas.trim();
        if (limpia.length() > MAX_NOTAS) {
            throw new IllegalArgumentException("Las notas no pueden tener más de " + MAX_NOTAS + " caracteres.");
        }
        return limpia;
    }

    private Pago.MetodoPago parsearMetodo(String metodo) {
        if (metodo == null || metodo.isBlank()) {
            throw new IllegalArgumentException("Debes elegir un método de pago.");
        }
        try {
            return Pago.MetodoPago.valueOf(metodo.trim().toLowerCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Método de pago no válido: " + metodo
                    + ". Usa: efectivo, tarjeta_credito, tarjeta_debito, transferencia, nequi o daviplata.");
        }
    }

    // ---------- Validaciones de la estructura del carrito ----------

    private void validarEstructura(List<GrupoPedidoRequest> grupos) {
        if (grupos == null || grupos.isEmpty()) {
            throw new IllegalArgumentException("El carrito está vacío.");
        }
        Set<Integer> restaurantesVistos = new HashSet<>();
        for (GrupoPedidoRequest grupo : grupos) {
            if (grupo == null || grupo.idRestaurante() == null) {
                throw new IllegalArgumentException("Cada grupo debe indicar el idRestaurante.");
            }
            if (!restaurantesVistos.add(grupo.idRestaurante())) {
                throw new IllegalArgumentException("El restaurante " + grupo.idRestaurante()
                        + " aparece en más de un grupo. Envía un solo grupo por restaurante.");
            }
            if (grupo.items() == null || grupo.items().isEmpty()) {
                throw new IllegalArgumentException("El grupo del restaurante " + grupo.idRestaurante() + " no tiene productos.");
            }
            Set<Integer> productosVistos = new HashSet<>();
            for (ItemPedidoRequest item : grupo.items()) {
                if (item == null || item.idProducto() == null) {
                    throw new IllegalArgumentException("Cada producto debe indicar idProducto.");
                }
                if (item.cantidad() == null || item.cantidad() < 1 || item.cantidad() > MAX_CANTIDAD_POR_LINEA) {
                    throw new IllegalArgumentException("La cantidad del producto " + item.idProducto()
                            + " debe estar entre 1 y " + MAX_CANTIDAD_POR_LINEA + ".");
                }
                if (!productosVistos.add(item.idProducto())) {
                    throw new IllegalArgumentException("El producto " + item.idProducto()
                            + " aparece repetido en el mismo restaurante.");
                }
            }
        }
    }

    // ---------- Fase 1: validación contra la BD y cálculo de totales (sin escritura) ----------

    private List<GrupoValidado> validarGrupos(List<GrupoPedidoRequest> grupos) {
        List<Integer> idsRestaurantes = grupos.stream()
                .map(GrupoPedidoRequest::idRestaurante)
                .distinct()
                .toList();
        List<Integer> idsProductos = grupos.stream()
                .flatMap(g -> g.items().stream())
                .map(ItemPedidoRequest::idProducto)
                .distinct()
                .toList();

        Map<Integer, Restaurante> restaurantes = restauranteRepository.findAllById(idsRestaurantes).stream()
                .collect(Collectors.toMap(Restaurante::getIdRestaurante, r -> r, (a, b) -> a, HashMap::new));
        Map<Integer, Producto> productos = productoRepository.findAllById(idsProductos).stream()
                .collect(Collectors.toMap(Producto::getIdProducto, p -> p, (a, b) -> a, HashMap::new));

        List<GrupoValidado> validados = new ArrayList<>();
        for (GrupoPedidoRequest grupo : grupos) {
            Restaurante restaurante = restaurantes.get(grupo.idRestaurante());
            if (restaurante == null) {
                throw new IllegalArgumentException("El restaurante con id " + grupo.idRestaurante() + " no existe.");
            }
            if (!Boolean.TRUE.equals(restaurante.getEstado())) {
                throw new IllegalArgumentException(restaurante.getNombre() + " está cerrado en este momento.");
            }

            List<LineaValidada> lineas = new ArrayList<>();
            BigDecimal subtotalGrupo = BigDecimal.ZERO.setScale(2);
            for (ItemPedidoRequest item : grupo.items()) {
                Producto producto = productos.get(item.idProducto());
                if (producto == null) {
                    throw new IllegalArgumentException("El producto con id " + item.idProducto() + " no existe.");
                }
                if (!Objects.equals(producto.getIdRestaurante(), restaurante.getIdRestaurante())) {
                    throw new IllegalArgumentException(producto.getNombre() + " no pertenece a " + restaurante.getNombre() + ".");
                }
                if (!Boolean.TRUE.equals(producto.getEstado())) {
                    throw new IllegalArgumentException(producto.getNombre() + " de " + restaurante.getNombre()
                            + " no está disponible. Quítalo del carrito para continuar.");
                }
                if (producto.getPrecio() == null) {
                    throw new IllegalArgumentException(producto.getNombre() + " no tiene precio configurado.");
                }

                BigDecimal subtotalLinea = producto.getPrecio()
                        .multiply(BigDecimal.valueOf(item.cantidad()))
                        .setScale(2, RoundingMode.HALF_UP);
                lineas.add(new LineaValidada(producto, item.cantidad(), subtotalLinea));
                subtotalGrupo = subtotalGrupo.add(subtotalLinea);
            }
            validados.add(new GrupoValidado(restaurante, lineas, subtotalGrupo));
        }
        return validados;
    }

    private String generarReferencia(Integer idPedido) {
        StringBuilder sb = new StringBuilder("EMD-").append(idPedido).append('-');
        for (int i = 0; i < 6; i++) {
            sb.append(ALFABETO_REFERENCIA.charAt(ALEATORIO.nextInt(ALFABETO_REFERENCIA.length())));
        }
        return sb.toString();
    }
}
