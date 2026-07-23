package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.HealthFacilityEntity
import com.example.data.local.ReportEntity
import com.example.ui.components.InteractiveCanvasMap

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InteractiveMapScreen(
    isEasyMode: Boolean,
    reports: List<ReportEntity>,
    facilities: List<HealthFacilityEntity>,
    onAddReport: (ReportEntity) -> Unit
) {
    var selectedFilter by remember { mutableStateOf("Todos") }
    var showAddReportDialog by remember { mutableStateOf(false) }

    val filterOptions = listOf("Todos", "Brotes/Casos", "Criaderos", "Centros Salud", "Farmacias")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("map_screen_content")
    ) {
        // Filter Chips Row
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filterOptions) { filter ->
                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = { selectedFilter = filter },
                    label = { Text(filter, fontSize = if (isEasyMode) 15.sp else 13.sp) },
                    leadingIcon = {
                        val icon = when (filter) {
                            "Brotes/Casos" -> Icons.Default.Warning
                            "Criaderos" -> Icons.Default.WaterDrop
                            "Centros Salud" -> Icons.Default.LocalHospital
                            "Farmacias" -> Icons.Default.LocalPharmacy
                            else -> Icons.Default.Map
                        }
                        Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                )
            }
        }

        // Scrollable content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Map Canvas Component
            InteractiveCanvasMap(
                reports = reports,
                facilities = facilities,
                selectedFilter = selectedFilter,
                onAddReportClick = { showAddReportDialog = true },
                isEasyMode = isEasyMode
            )

            // Health Facilities List Section
            Text(
                text = "Puntos de Atención Médica y Medicamentos Cercanos",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = if (isEasyMode) 20.sp else 16.sp
                )
            )

            facilities.forEach { facility ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("facility_card_${facility.id}"),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = facility.name,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Surface(
                                color = if (facility.isOpen24h) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = if (facility.isOpen24h) "Abierto 24h" else "Horario Regular",
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Text(
                            text = "${facility.facilityType} • ${facility.address}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = "💊 Disponibilidad: ${facility.availableMeds}",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                            color = MaterialTheme.colorScheme.primary
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.secondary)
                            Text(
                                text = facility.phone,
                                style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.secondary)
                            )
                        }
                    }
                }
            }
        }
    }

    // Add Report Modal Dialog
    if (showAddReportDialog) {
        AddReportModalDialog(
            onDismiss = { showAddReportDialog = false },
            onConfirm = { report ->
                onAddReport(report)
                showAddReportDialog = false
            }
        )
    }
}

@Composable
private fun AddReportModalDialog(
    onDismiss: () -> Unit,
    onConfirm: (ReportEntity) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var locationName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var reportType by remember { mutableStateOf("Criadero Detectado") }
    var severity by remember { mutableStateOf("Alto") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Reportar Foco o Criadero de Zancudos", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título del reporte") },
                    placeholder = { Text("Ej: Charco en neumáticos abandonados") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_report_title")
                )

                OutlinedTextField(
                    value = locationName,
                    onValueChange = { locationName = it },
                    label = { Text("Ubicación / Dirección") },
                    placeholder = { Text("Ej: Parque Comuna 4 - Sector Norte") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_report_location")
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción del foco o síntomas") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Tipo de Reporte:", style = MaterialTheme.typography.labelMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = reportType == "Criadero Detectado",
                        onClick = { reportType = "Criadero Detectado" },
                        label = { Text("Criadero", fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = reportType == "Caso Dengue/Zika",
                        onClick = { reportType = "Caso Dengue/Zika" },
                        label = { Text("Caso Síntoma", fontSize = 11.sp) }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && locationName.isNotBlank()) {
                        onConfirm(
                            ReportEntity(
                                title = title,
                                reportType = reportType,
                                locationName = locationName,
                                latitude = 4.6100,
                                longitude = -74.0800,
                                description = description.ifBlank { "Reporte registrado por ciudadano en la comunidad." },
                                severity = severity,
                                status = "En Verificación"
                            )
                        )
                    }
                },
                modifier = Modifier.testTag("btn_confirm_report")
            ) {
                Text("Guardar y Publicar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
