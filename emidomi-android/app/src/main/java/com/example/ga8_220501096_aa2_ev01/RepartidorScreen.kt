package com.example.ga8_220501096_aa2_ev01

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ga8_220501096_aa2_ev01.data.model.Repartidor

@Composable
fun RepartidorScreen(
    repartidores: List<Repartidor>,
    cargando: Boolean,
    error: String?,
    onVolver: () -> Unit,
    onRecargar: () -> Unit
) {
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
                text = "Repartidores",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        when {
            cargando -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF1565C0))
                }
            }
            error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "😔", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = error, color = Color.Gray, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onRecargar,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0))
                        ) {
                            Text("Reintentar")
                        }
                    }
                }
            }
            repartidores.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "🛵", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No hay repartidores disponibles", color = Color.Gray)
                    }
                }
            }
            else -> {
                LazyColumn(modifier = Modifier.padding(12.dp)) {
                    items(repartidores.filter { it.estado == true && it.disponible == true }) { rep ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${rep.nombre ?: ""} ${rep.apellido ?: ""}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = Color(0xFF1565C0)
                                    )
                                    Card(
                                        shape = RoundedCornerShape(20.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = Color(0xFFE8F5E9)
                                        )
                                    ) {
                                        Text(
                                            text = "🟢 Disponible",
                                            modifier = Modifier.padding(
                                                horizontal = 8.dp,
                                                vertical = 2.dp
                                            ),
                                            fontSize = 11.sp,
                                            color = Color(0xFF2E7D32)
                                        )
                                    }
                                }

                                rep.vehiculo?.let {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "🚗 $it ${rep.placa ?: ""}",
                                        fontSize = 13.sp,
                                        color = Color.Gray
                                    )
                                }

                                rep.telefono?.let {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "📱 $it",
                                        fontSize = 13.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
