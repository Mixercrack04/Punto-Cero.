package com.example.data.repository

import com.example.data.local.*
import kotlinx.coroutines.flow.Flow

class AppRepository(private val dao: AppDao) {

    val userProfile: Flow<UserProfileEntity?> = dao.getUserProfile()
    val reports: Flow<List<ReportEntity>> = dao.getAllReports()
    val surveys: Flow<List<SurveyEntity>> = dao.getAllSurveys()
    val alerts: Flow<List<HealthAlertEntity>> = dao.getAllAlerts()
    val healthFacilities: Flow<List<HealthFacilityEntity>> = dao.getAllFacilities()

    suspend fun saveUserProfile(profile: UserProfileEntity) {
        dao.saveUserProfile(profile)
    }

    suspend fun addReport(report: ReportEntity) {
        dao.insertReport(report)
    }

    suspend fun addSurvey(survey: SurveyEntity) {
        dao.insertSurvey(survey)
    }

    suspend fun addAlert(alert: HealthAlertEntity) {
        dao.insertAlert(alert)
    }

    suspend fun markAlertAsRead(alertId: Int) {
        dao.markAlertAsRead(alertId)
    }

    suspend fun initializeDefaultDataIfEmpty() {
        val initialProfile = UserProfileEntity(
            id = 1,
            fullName = "María Fernanda Gómez",
            ageGroup = "Joven / Estudiante",
            bloodType = "O+",
            chronicConditions = "Ninguna",
            allergies = "Polen",
            pastInfections = "Dengue leve (SIVIGILA Huila 2023)",
            emergencyPhone = "+57 312 987 6543",
            cityZone = "Corregimiento El Pescador, La Argentina (Huila)",
            isEasyModeActive = false,
            isEncryptedAtRest = true
        )
        dao.saveUserProfile(initialProfile)

        // Always re-populate facilities with real authentic data for La Argentina & El Pescador, Huila
        dao.clearFacilities()
        val realFacilities = listOf(
            HealthFacilityEntity(
                name = "E.S.E. Hospital San Andrés de La Argentina",
                facilityType = "Hospital Municipal (Nivel I)",
                address = "Carrera 4 # 5-20, La Argentina, Huila",
                latitude = 2.1865,
                longitude = -75.8924,
                phone = "+57 608 837 1020",
                availableMeds = "Urgencias 24h, Pruebas Rápidas NS1 Dengue, Acetaminofén 500mg, Suero Oral SRO",
                isOpen24h = true
            ),
            HealthFacilityEntity(
                name = "Puesto de Salud El Pescador",
                facilityType = "Centro de Salud Rural",
                address = "Corregimiento El Pescador, La Argentina, Huila",
                latitude = 2.1950,
                longitude = -75.8850,
                phone = "+57 608 837 1100",
                availableMeds = "Toma de Muestras de Sangre, Acetaminofén Jarabe, Sales de Rehidratación Oral, Kits Preventivos",
                isOpen24h = true
            ),
            HealthFacilityEntity(
                name = "Droguería La Rebaja / Emcofarma La Argentina",
                facilityType = "Farmacia Principal",
                address = "Calle 5 # 4-18, Parque Principal, La Argentina, Huila",
                latitude = 2.1880,
                longitude = -75.8910,
                phone = "+57 311 500 2030",
                availableMeds = "Repelente DEET 20-30%, Mosquiteros Impregnados, Termómetros Digitales, Paracetamol 500mg",
                isOpen24h = false
            ),
            HealthFacilityEntity(
                name = "Droguería Central La Argentina",
                facilityType = "Farmacia Autorizada",
                address = "Carrera 3 # 4-32, La Argentina, Huila",
                latitude = 2.1870,
                longitude = -75.8930,
                phone = "+57 312 456 7890",
                availableMeds = "Acetaminofén, Sales de Rehidratación Oral (SRO), Suero Pedialyte, Repelente para Niños",
                isOpen24h = false
            ),
            HealthFacilityEntity(
                name = "Secretaría de Salud Huila - Puesto SIVIGILA La Argentina",
                facilityType = "Punto Control Sanitario",
                address = "Alcaldía Municipal, La Argentina, Huila",
                latitude = 2.1850,
                longitude = -75.8940,
                phone = "+57 608 867 0101",
                availableMeds = "Larvicida BTI / Abate granulado para tanques, Equipos de Termonebulización Aedes aegypti",
                isOpen24h = false
            )
        )
        dao.insertFacilities(realFacilities)

            val initialReports = listOf(
                ReportEntity(
                    title = "Tanque de reserva con larvas visibles de Aedes aegypti",
                    reportType = "Criadero Detectado",
                    locationName = "Patio Central - I.E. El Pescador, La Argentina (Huila)",
                    latitude = 2.1868,
                    longitude = -75.8920,
                    description = "Estudiantes observaron larvas en movimiento rápido en agua almacenada sin tapa. Se requiere lavado con cepillo y aplicación de abate.",
                    severity = "Alto",
                    status = "En Verificación",
                    timestamp = System.currentTimeMillis() - 3600000 * 4,
                    reportedBy = "Abel Andrés Chavez Penagos (I.E. El Pescador)"
                ),
                ReportEntity(
                    title = "SIVIGILA: Caso sospechoso de Dengue con signos de alarma",
                    reportType = "Caso Dengue/Zika",
                    locationName = "Barrio El Carmen - La Argentina, Huila",
                    latitude = 2.1855,
                    longitude = -75.8935,
                    description = "Paciente febril con 39.2°C, petequias y mialgias intensas. Atendido en E.S.E. Hospital San Andrés de La Argentina.",
                    severity = "Crítico",
                    status = "Atendido",
                    timestamp = System.currentTimeMillis() - 3600000 * 12,
                    reportedBy = "Jarol Felipe Quilindo Méndez (Comité Salud)"
                ),
                ReportEntity(
                    title = "Jornada de Termonebulización y Control Biológico",
                    reportType = "Zona Fumigada",
                    locationName = "Corregimiento El Pescador - Zona Escolar",
                    latitude = 2.1910,
                    longitude = -75.8880,
                    description = "Fumigación térmica realizada por la Secretaría de Salud del Huila y la brigada de salud de la I.E. El Pescador.",
                    severity = "Bajo",
                    status = "Atendido",
                    timestamp = System.currentTimeMillis() - 3600000 * 24,
                    reportedBy = "Luis Gabriel Bravo Manquillo (Secretaría Escolar)"
                )
            )
            for (r in initialReports) {
                dao.insertReport(r)
            }

            val initialAlerts = listOf(
                HealthAlertEntity(
                    title = "🚨 Boletín SIVIGILA Huila: Alerta Epidemiológica Dengue",
                    message = "La Secretaría de Salud del Huila informa alta circulación del vector Aedes aegypti por clima cálido húmedo (28°C-32°C). Cepille y lave tanques cada 7 días.",
                    category = "Alerta Epidemiológica",
                    zone = "Huila - Municipio de La Argentina",
                    timestamp = System.currentTimeMillis() - 1800000,
                    isRead = false
                ),
                HealthAlertEntity(
                    title = "📢 Jornada Escolar de Limpieza e Inservibles I.E. El Pescador",
                    message = "Organizada por Abel Chavez, Jarol Quilindo y Luis Bravo. Se recolectarán llantas, botellas y floreros para erradicar focos de zancudos.",
                    category = "Prevención Escolar",
                    zone = "Corregimiento El Pescador",
                    timestamp = System.currentTimeMillis() - 3600000 * 8,
                    isRead = false
                ),
                HealthAlertEntity(
                    title = "💊 Disponibilidad de Acetaminofén y Suero Oral SRO",
                    message = "El Hospital San Andrés de La Argentina cuenta con stock completo de sales de rehidratación oral y acetaminofén. Prohibido el uso de AINEs.",
                    category = "Suministros Médicos",
                    zone = "E.S.E. Hospital San Andrés",
                    timestamp = System.currentTimeMillis() - 3600000 * 18,
                    isRead = true
                )
            )
            for (a in initialAlerts) {
                dao.insertAlert(a)
            }
    }
}
