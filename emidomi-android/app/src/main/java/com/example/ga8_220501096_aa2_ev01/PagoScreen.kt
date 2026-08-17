package com.example.ga8_220501096_aa2_ev01

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ga8_220501096_aa2_ev01.data.model.Pago

@Composable
fun PagoScreen(
    idPedido: Int,
    total: Double,
    cargando: Boolean,
    mensaje: String?,
    pagoConfirmado: Pago?,
    onPagoRegistrado: (metodoPago: String) -> Unit,
    onVolver: () -> Unit,
    onContinuar: () -> Unit
) {
    var metodoSeleccionado by remember { mutableStateOf("") }
    val metodos = listOf(
        "Efectivo" to "efectivo",
        "Tarjeta de crédito" to "tarjeta_credito",
        "Tarjeta de débito" to "tarjeta_debito",
        "Nequi" to "nequi",
        "Daviplata" to "daviplata",
        "Transferencia" to "transferencia"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F4F7))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1565C0))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onVolver) {
                Text(text = "←", fontSize = 24.sp, color = Color.White)
            }
            Text(
                text = "Método de pago",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(24.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Pedido #$idPedido",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1565C0)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Total a pagar: $${total.toInt()}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Selecciona método de pago",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1565C0)
            )

            Spacer(modifier = Modifier.height(12.dp))

            metodos.forEach { (label, value) ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .clickable { metodoSeleccionado = value },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (metodoSeleccionado == value)
                            Color(0xFFE3F2FD) else Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = metodoSeleccionado == value,
                            onClick = { metodoSeleccionado = value },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Color(0xFF1565C0)
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = label, fontSize = 15.sp)
                    }
                }
            }

            mensaje?.let { msg ->
                Spacer(modifier = Modifier.height(12.dp))
                val isError = msg.startsWith("Error")
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isError) Color(0xFFFFEBEE) else Color(0xFFE8F5E9)
                    )
                ) {
                    Text(
                        text = msg,
                        modifier = Modifier.padding(12.dp),
                        color = if (isError) Color(0xFFC62828) else Color(0xFF2E7D32),
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (pagoConfirmado != null) {
                Button(
                    onClick = onContinuar,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                ) {
                    Text(
                        text = "Continuar",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            } else {
                Button(
                    onClick = { onPagoRegistrado(metodoSeleccionado) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0)),
                    enabled = metodoSeleccionado.isNotBlank() && !cargando
                ) {
                    if (cargando) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Confirmar pago",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
