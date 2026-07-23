package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profiles")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val fullName: String = "Estudiante / Usuario",
    val ageGroup: String = "Joven / Adulto", // "Infantil", "Joven / Adulto", "Adulto Mayor"
    val bloodType: String = "O+",
    val chronicConditions: String = "Ninguna",
    val allergies: String = "Sin alergias conocidas",
    val pastInfections: String = "Ninguna (Sano)",
    val emergencyPhone: String = "+57 300 123 4567",
    val cityZone: String = "Barrio Central - Sector 2",
    val isEasyModeActive: Boolean = false,
    val isEncryptedAtRest: Boolean = true
)

@Entity(tableName = "reports")
data class ReportEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val reportType: String, // "Foco de Zancudos", "Criadero Detectado", "Caso Dengue/Zika", "Zona Fumigada"
    val locationName: String,
    val latitude: Double,
    val longitude: Double,
    val description: String,
    val severity: String, // "Bajo", "Medio", "Alto", "Crítico"
    val status: String = "En Verificación",
    val timestamp: Long = System.currentTimeMillis(),
    val reportedBy: String = "Comunidad"
)

@Entity(tableName = "surveys")
data class SurveyEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val score: Int,
    val totalQuestions: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "alerts")
data class HealthAlertEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val message: String,
    val category: String, // "Clima / Lluvia", "Jornada Fumigación", "Alerta Dengue", "Medicamentos"
    val zone: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

@Entity(tableName = "health_facilities")
data class HealthFacilityEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val facilityType: String, // "Centro de Salud", "Hospital", "Farmacia", "Punto Vacunación"
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val phone: String,
    val availableMeds: String,
    val isOpen24h: Boolean = true
)
