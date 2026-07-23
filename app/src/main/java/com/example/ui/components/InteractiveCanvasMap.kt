package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.HealthFacilityEntity
import com.example.data.local.ReportEntity

data class MapPinItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val type: String, // "Caso", "Criadero", "Fumigado", "CentroSalud", "Farmacia"
    val xRatio: Float, // 0.0 to 1.0
    val yRatio: Float, // 0.0 to 1.0
    val severity: String = "Medio",
    val phone: String = "",
    val details: String = ""
)

@Composable
fun InteractiveCanvasMap(
    reports: List<ReportEntity>,
    facilities: List<HealthFacilityEntity>,
    selectedFilter: String, // "Todos", "Brotes/Casos", "Criaderos", "Centros Salud", "Farmacias"
    onAddReportClick: () -> Unit,
    isEasyMode: Boolean
) {
    var selectedPin by remember { mutableStateOf<MapPinItem?>(null) }

    // Map pins derived from DB reports and facilities
    val mapPins = remember(reports, facilities) {
        val list = mutableListOf<MapPinItem>()

        reports.forEachIndexed { idx, r ->
            val type = when (r.reportType) {
                "Caso Dengue/Zika" -> "Caso"
                "Criadero Detectado" -> "Criadero"
                "Zona Fumigada" -> "Fumigado"
                else -> "Criadero"
            }
            val x = when (idx % 4) {
                0 -> 0.25f
                1 -> 0.70f
                2 -> 0.45f
                else -> 0.82f
            }
            val y = when (idx % 4) {
                0 -> 0.30f
                1 -> 0.40f
                2 -> 0.75f
                else -> 0.20f
            }
            list.add(
                MapPinItem(
                    id = "rep_${r.id}",
                    title = r.title,
                    subtitle = "${r.reportType} • ${r.locationName}",
                    type = type,
                    xRatio = x,
                    yRatio = y,
                    severity = r.severity,
                    details = r.description
                )
            )
        }

        facilities.forEachIndexed { idx, f ->
            val type = if (f.facilityType == "Farmacia") "Farmacia" else "CentroSalud"
            val x = when (idx % 3) {
                0 -> 0.55f
                1 -> 0.18f
                else -> 0.85f
            }
            val y = when (idx % 3) {
                0 -> 0.50f
                1 -> 0.65f
                else -> 0.80f
            }
            list.add(
                MapPinItem(
                    id = "fac_${f.id}",
                    title = f.name,
                    subtitle = "${f.facilityType} • ${f.address}",
                    type = type,
                    xRatio = x,
                    yRatio = y,
                    phone = f.phone,
                    details = "Medicamentos: ${f.availableMeds}"
                )
            )
        }

        list
    }

    val filteredPins = remember(mapPins, selectedFilter) {
        mapPins.filter { pin ->
            when (selectedFilter) {
                "Brotes/Casos" -> pin.type == "Caso"
                "Criaderos" -> pin.type == "Criadero"
                "Centros Salud" -> pin.type == "CentroSalud"
                "Farmacias" -> pin.type == "Farmacia"
                else -> true
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("interactive_map_container")
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFE2ECE9))
                .border(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
        ) {
            // Draw Stylized Neighborhood Map Canvas
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            val width = size.width
                            val height = size.height

                            val tapped = filteredPins.find { pin ->
                                val pinX = pin.xRatio * width
                                val pinY = pin.yRatio * height
                                val dist = kotlin.math.hypot(offset.x - pinX, offset.y - pinY)
                                dist <= 40 // 40px tap radius
                            }
                            selectedPin = tapped
                        }
                    }
            ) {
                val w = size.width
                val h = size.height

                // Draw background river/water channel (safe zone)
                val riverPath = Path().apply {
                    moveTo(0f, h * 0.15f)
                    cubicTo(w * 0.3f, h * 0.1f, w * 0.6f, h * 0.35f, w, h * 0.25f)
                    lineTo(w, h * 0.32f)
                    cubicTo(w * 0.6f, h * 0.42f, w * 0.3f, h * 0.18f, 0f, h * 0.22f)
                    close()
                }
                drawPath(riverPath, Color(0xFFBBE1FA).copy(alpha = 0.7f))

                // Draw roads/blocks
                drawRoundRect(
                    color = Color.White,
                    topLeft = Offset(w * 0.08f, h * 0.35f),
                    size = Size(w * 0.35f, h * 0.25f),
                    cornerRadius = CornerRadius(12f, 12f)
                )

                drawRoundRect(
                    color = Color.White,
                    topLeft = Offset(w * 0.50f, h * 0.35f),
                    size = Size(w * 0.42f, h * 0.38f),
                    cornerRadius = CornerRadius(12f, 12f)
                )

                drawRoundRect(
                    color = Color.White,
                    topLeft = Offset(w * 0.10f, h * 0.68f),
                    size = Size(w * 0.33f, h * 0.25f),
                    cornerRadius = CornerRadius(12f, 12f)
                )

                // Main avenue lines
                drawLine(
                    color = Color(0xFF8899A6),
                    start = Offset(w * 0.46f, 0f),
                    end = Offset(w * 0.46f, h),
                    strokeWidth = 14f
                )

                drawLine(
                    color = Color(0xFF8899A6),
                    start = Offset(0f, h * 0.63f),
                    end = Offset(w, h * 0.63f),
                    strokeWidth = 14f
                )

                // Render Map Pins
                filteredPins.forEach { pin ->
                    val px = pin.xRatio * w
                    val py = pin.yRatio * h
                    val isSelected = selectedPin?.id == pin.id

                    val pinColor = when (pin.type) {
                        "Caso" -> Color(0xFFD32F2F)      // Red
                        "Criadero" -> Color(0xFFF57C00)  // Orange
                        "Fumigado" -> Color(0xFF388E3C)  // Green
                        "CentroSalud" -> Color(0xFF0288D1)// Blue
                        "Farmacia" -> Color(0xFF7B1FA2)   // Purple
                        else -> Color.Gray
                    }

                    // Outer pulse ring if selected
                    if (isSelected) {
                        drawCircle(
                            color = pinColor.copy(alpha = 0.35f),
                            radius = 34f,
                            center = Offset(px, py)
                        )
                    }

                    // Pin base circle
                    drawCircle(
                        color = pinColor,
                        radius = if (isSelected) 22f else 16f,
                        center = Offset(px, py)
                    )

                    drawCircle(
                        color = Color.White,
                        radius = if (isSelected) 8f else 6f,
                        center = Offset(px, py)
                    )
                }
            }

            // Map Overlay Legend / Controls
            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
                    .background(Color.White.copy(alpha = 0.92f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(Icons.Default.Map, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                Text(
                    text = "Mapa Barrio Central (${filteredPins.size} puntos)",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            // Floating Add Report FAB on map
            FloatingActionButton(
                onClick = onAddReportClick,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp)
                    .testTag("fab_add_report_map"),
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.AddLocation, contentDescription = "Reportar Foco")
                    Text("Reportar", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }

        // Selected Pin Card Details
        AnimatedVisibility(visible = selectedPin != null) {
            selectedPin?.let { pin ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        .testTag("card_map_pin_detail"),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(
                                    imageVector = when(pin.type) {
                                        "Caso" -> Icons.Default.Warning
                                        "Criadero" -> Icons.Default.WaterDrop
                                        "CentroSalud" -> Icons.Default.LocalHospital
                                        "Farmacia" -> Icons.Default.LocalPharmacy
                                        else -> Icons.Default.CheckCircle
                                    },
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = pin.title,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }

                            IconButton(
                                onClick = { selectedPin = null },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Cerrar")
                            }
                        }

                        Text(
                            text = pin.subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        if (pin.details.isNotBlank()) {
                            Text(
                                text = pin.details,
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp)
                            )
                        }

                        if (pin.phone.isNotBlank()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.Phone, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                Text("Teléfono de contacto: ${pin.phone}", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
        }
    }
}
