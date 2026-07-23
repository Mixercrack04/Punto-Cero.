package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.UserProfileEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClinicalProfileScreen(
    isEasyMode: Boolean,
    userProfile: UserProfileEntity?,
    onSaveProfile: (UserProfileEntity) -> Unit
) {
    var fullName by remember(userProfile) { mutableStateOf(userProfile?.fullName ?: "Estudiante / Usuario") }
    var ageGroup by remember(userProfile) { mutableStateOf(userProfile?.ageGroup ?: "Joven / Adulto") }
    var bloodType by remember(userProfile) { mutableStateOf(userProfile?.bloodType ?: "O+") }
    var allergies by remember(userProfile) { mutableStateOf(userProfile?.allergies ?: "Sin alergias") }
    var pastInfections by remember(userProfile) { mutableStateOf(userProfile?.pastInfections ?: "Ninguna (Sano)") }
    var emergencyPhone by remember(userProfile) { mutableStateOf(userProfile?.emergencyPhone ?: "+57 300 123 4567") }
    var cityZone by remember(userProfile) { mutableStateOf(userProfile?.cityZone ?: "Barrio Central - Comuna 4") }

    var isSavedNotification by remember { mutableStateOf(false) }

    val ageGroupOptions = listOf("Infantil (Niño)", "Joven / Adulto", "Adulto Mayor (Senior)")
    val bloodTypeOptions = listOf("O+", "O-", "A+", "A-", "B+", "B-", "AB+")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("clinical_profile_screen_content"),
        contentPadding = PaddingValues(bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Security & Privacy Compliance Header Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Historial Clínico Cifrado (E2E)",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Tus datos médicos locales están protegidos bajo estándares internacionales de privacidad. Solo tú y el personal autorizado pueden acceder.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }

        // Saved Confirmation Toast Banner
        item {
            AnimatedVisibility(visible = isSavedNotification) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFC8E6C9))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32))
                        Text(
                            text = "¡Historial clínico actualizado correctamente!",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B5E20)
                        )
                    }
                }
            }
        }

        // Profile Form
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Datos Básicos del Paciente / Usuario",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Nombre Completo") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_profile_name")
                    )

                    Text("Grupo de Edad:", style = MaterialTheme.typography.labelMedium)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        ageGroupOptions.forEach { opt ->
                            FilterChip(
                                selected = ageGroup == opt,
                                onClick = { ageGroup = opt },
                                label = { Text(opt, fontSize = if (isEasyMode) 13.sp else 11.sp) }
                            )
                        }
                    }

                    Text("Tipo de Sangre:", style = MaterialTheme.typography.labelMedium)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        bloodTypeOptions.take(4).forEach { opt ->
                            FilterChip(
                                selected = bloodType == opt,
                                onClick = { bloodType = opt },
                                label = { Text(opt, fontSize = 11.sp) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = allergies,
                        onValueChange = { allergies = it },
                        label = { Text("Alergias o Medicamentos Sensibles") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = pastInfections,
                        onValueChange = { pastInfections = it },
                        label = { Text("Historial de Dengue / Zika / Chikungunya") },
                        placeholder = { Text("Ej: Dengue en 2023 o Ninguno") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = cityZone,
                        onValueChange = { cityZone = it },
                        label = { Text("Barrio / Zona de Residencia") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            val updated = UserProfileEntity(
                                id = 1,
                                fullName = fullName,
                                ageGroup = ageGroup,
                                bloodType = bloodType,
                                allergies = allergies,
                                pastInfections = pastInfections,
                                emergencyPhone = emergencyPhone,
                                cityZone = cityZone,
                                isEasyModeActive = isEasyMode,
                                isEncryptedAtRest = true
                            )
                            onSaveProfile(updated)
                            isSavedNotification = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("btn_save_profile"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Guardar Historial Clínico", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}
