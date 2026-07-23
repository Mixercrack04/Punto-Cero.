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
        if (dao.getFacilityCount() == 0) {
            val initialProfile = UserProfileEntity(
                id = 1,
                fullName = "María Fernanda Gómez",
                ageGroup = "Joven / Adulto",
                bloodType = "O+",
                chronicConditions = "Ninguna",
                allergies = "Polen",
                pastInfections = "Dengue leve (2023)",
                emergencyPhone = "+57 312 987 6543",
                cityZone = "Comuna 4 - Sector Norte",
                isEasyModeActive = false,
                isEncryptedAtRest = true
            )
            dao.saveUserProfile(initialProfile)

            val initialFacilities = listOf(
                HealthFacilityEntity(
                    name = "Centro de Salud Comunitario San José",
                    facilityType = "Centro de Salud",
                    address = "Av. Principal # 12-45",
                    latitude = 4.6097,
                    longitude = -74.0817,
                    phone = "+57 601 555 0101",
                    availableMeds = "Paracetamol 500mg, Suero Oral, Sales de Rehidratación, Repelente",
                    isOpen24h = true
                ),
                HealthFacilityEntity(
                    name = "Hospital Infantil Santa Sofía",
                    facilityType = "Hospital",
                    address = "Calle 45 # 23-10",
                    latitude = 4.6150,
                    longitude = -74.0750,
                    phone = "+57 601 555 0202",
                    availableMeds = "Atención de Emergencia Dengue, Pruebas Rápidas NS1, Acetaminofén jarabe",
                    isOpen24h = true
                ),
                HealthFacilityEntity(
                    name = "Farmacia Sanitaria San Gabriel",
                    facilityType = "Farmacia",
                    address = "Carrera 15 # 8-30",
                    latitude = 4.6020,
                    longitude = -74.0880,
                    phone = "+57 601 555 0303",
                    availableMeds = "Repelente DEET 20%, Mosquiteros, Paracetamol, Termómetros digitales",
                    isOpen24h = false
                ),
                HealthFacilityEntity(
                    name = "Puesto de Vacunación y Control Sanitario",
                    facilityType = "Punto Vacunación",
                    address = "Plaza Principal - Módulo 3",
                    latitude = 4.6180,
                    longitude = -74.0850,
                    phone = "+57 601 555 0404",
                    availableMeds = "Insumos de Fumigación, Kits de Abate para agua, Guías Preventivas",
                    isOpen24h = false
                )
            )
            dao.insertFacilities(initialFacilities)

            val initialReports = listOf(
                ReportEntity(
                    title = "Charco acumulado en llantas abandonadas",
                    reportType = "Criadero Detectado",
                    locationName = "Parque del Barrio La Esperanza",
                    latitude = 4.6090,
                    longitude = -74.0820,
                    description = "Se observan larvas activas de mosquito en agua estancada dentro de neumáticos.",
                    severity = "Alto",
                    status = "En Verificación",
                    timestamp = System.currentTimeMillis() - 3600000 * 4,
                    reportedBy = "Carlos Ruiz (Vecino)"
                ),
                ReportEntity(
                    title = "Posible caso síntoma febril",
                    reportType = "Caso Dengue/Zika",
                    locationName = "Sector Los Pinos - Bloque 3",
                    latitude = 4.6120,
                    longitude = -74.0790,
                    description = "Persona presenta fiebre alta de 39°C, sarpullido y dolor muscular.",
                    severity = "Crítico",
                    status = "Atendido",
                    timestamp = System.currentTimeMillis() - 3600000 * 12,
                    reportedBy = "Comité de Salud"
                ),
                ReportEntity(
                    title = "Jornada de fumigación realizada",
                    reportType = "Zona Fumigada",
                    locationName = "Escuela Distrital Simón Bolívar",
                    latitude = 4.6050,
                    longitude = -74.0860,
                    description = "Personal de salud pública aplicó nebulización en áreas verdes y aulas.",
                    severity = "Bajo",
                    status = "Atendido",
                    timestamp = System.currentTimeMillis() - 3600000 * 24,
                    reportedBy = "Secretaría de Salud"
                )
            )
            for (r in initialReports) {
                dao.insertReport(r)
            }

            val initialAlerts = listOf(
                HealthAlertEntity(
                    title = "🌧️ Alerta Preventiva: Temporada de Lluvias",
                    message = "Se pronostican precipitaciones intensas esta semana. Revise y vacíe floreros, macetas y recipientes descubiertos.",
                    category = "Clima / Lluvia",
                    zone = "Toda la zona urbana",
                    timestamp = System.currentTimeMillis() - 1800000,
                    isRead = false
                ),
                HealthAlertEntity(
                    title = "🚜 Jornada de Nebulización Programada",
                    message = "El equipo de control vectorial estará fumigando mañana a las 6:00 AM en el Sector Norte. Mantenga ventanas cerradas.",
                    category = "Jornada Fumigación",
                    zone = "Sector Norte y Los Pinos",
                    timestamp = System.currentTimeMillis() - 3600000 * 8,
                    isRead = false
                ),
                HealthAlertEntity(
                    title = "💊 Disponibilidad de Paracetamol y Repelentes",
                    message = "Se han reabastecido los centros de salud locales con suero de rehidratación y paracetamol sin costo.",
                    category = "Medicamentos",
                    zone = "Centro de Salud San José",
                    timestamp = System.currentTimeMillis() - 3600000 * 18,
                    isRead = true
                )
            )
            for (a in initialAlerts) {
                dao.insertAlert(a)
            }
        }
    }
}
