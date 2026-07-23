package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.SurveyEntity

data class QuizQuestion(
    val id: Int,
    val question: String,
    val options: List<String>,
    val correctAnswerIndex: Int,
    val explanation: String
)

@Composable
fun EducationScreen(
    isEasyMode: Boolean,
    onSaveSurveyResult: (SurveyEntity) -> Unit,
    surveysCompleted: List<SurveyEntity>
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("education_screen_content")
    ) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Biología Aedes", fontWeight = FontWeight.Bold, fontSize = if (isEasyMode) 15.sp else 13.sp) },
                icon = { Icon(Icons.Default.BugReport, contentDescription = null) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("10 Consejos", fontWeight = FontWeight.Bold, fontSize = if (isEasyMode) 15.sp else 13.sp) },
                icon = { Icon(Icons.Default.CleanHands, contentDescription = null) }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("Encuesta de Hogar", fontWeight = FontWeight.Bold, fontSize = if (isEasyMode) 15.sp else 13.sp) },
                icon = { Icon(Icons.Default.HomeWork, contentDescription = null) }
            )
        }

        when (selectedTab) {
            0 -> MosquitoBiologyTab(isEasyMode)
            1 -> PreventionTipsTab(isEasyMode)
            2 -> InteractiveSurveyTab(isEasyMode, onSaveSurveyResult, surveysCompleted)
        }
    }
}

@Composable
private fun MosquitoBiologyTab(isEasyMode: Boolean) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentPadding = PaddingValues(bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column {
                    Image(
                        painter = painterResource(id = R.drawable.img_mosquito_guide_1784763123003),
                        contentDescription = "Guía de mosquito Aedes aegypti",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentScale = ContentScale.Crop
                    )

                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Aedes aegypti: El Mosquito del Dengue",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Es un mosquito doméstico que vive cerca de las personas. Coloca sus huevos en agua limpia y estancada dentro de recipientes artificiales.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = "Ciclo de Vida (8 a 10 días)",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }

        val stages = listOf(
            Triple("1. Huevos (0.5 mm)", "Depositados en las paredes internas de tanques, floreros o llantas con agua.", Icons.Default.Egg),
            Triple("2. Larva (Gusanito)", "Nada libremente en agua estancada. Se alimenta de materia orgánica.", Icons.Default.Water),
            Triple("3. Pupa (Crisálida)", "Fase de transformación activa antes de emerger como adulto volador.", Icons.Default.Science),
            Triple("4. Adulto (Con rayas)", "Mosquito con patas con franjas blancas. Solo la hembra pica para alimentar sus huevos.", Icons.Default.FlightTakeoff)
        )

        items(stages) { (title, desc, icon) ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                        Text(text = desc, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

@Composable
private fun PreventionTipsTab(isEasyMode: Boolean) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentPadding = PaddingValues(bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                Column {
                    Image(
                        painter = painterResource(id = R.drawable.img_prevention_tips_1784763131845),
                        contentDescription = "Familia protegida de zancudos",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        contentScale = ContentScale.Crop
                    )
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "10 Reglas de Oro Anti-Criaderos",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "¡Sin agua estancada no hay mosquitos, y sin mosquitos no hay Dengue!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        val tips = listOf(
            "Lava y cepilla las paredes de tanques y albercas cada 4 días.",
            "Tapa herméticamente los recipientes con agua almacenada.",
            "Voltea baldes, tinas y recipientes vacíos que acumulen lluvia.",
            "Cambia el agua de los floreros y bebederos de mascotas diariamente.",
            "Perfora o elimina llantas viejas expuestas a la intemperie.",
            "Limpia canaletas de techo y desagües eliminando hojas secas.",
            "Aplica repelente autorizado en piel expuesta y ropa ligera.",
            "Instala mosquiteros en puertas y ventanas de la vivienda.",
            "Usa ropa de manga larga y pantalones al amanecer y atardecer.",
            "Reporta criaderos en la comunidad a través del mapa de esta app."
        )

        items(tips) { tip ->
            var isChecked by remember { mutableStateOf(false) }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (isChecked) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = { isChecked = it }
                    )
                    Text(
                        text = tip,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = if (isChecked) FontWeight.Bold else FontWeight.Normal,
                            fontSize = if (isEasyMode) 16.sp else 14.sp
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun InteractiveSurveyTab(
    isEasyMode: Boolean,
    onSaveSurveyResult: (SurveyEntity) -> Unit,
    surveysCompleted: List<SurveyEntity>
) {
    val questions = remember {
        listOf(
            QuizQuestion(
                id = 1,
                question = "¿Cuántas personas viven en tu hogar y cuántas son vulnerables?",
                options = listOf(
                    "1 a 2 adultos (Bajo riesgo poblacional)",
                    "3 a 5 personas con niños o adultos mayores (Riesgo Moderado)",
                    "Más de 5 personas con bebés, niños o mujeres embarazadas (Riesgo Alto)"
                ),
                correctAnswerIndex = 0,
                explanation = "A mayor concentración de personas vulnerables, mayor es el impacto de una picadura infectada."
            ),
            QuizQuestion(
                id = 2,
                question = "¿Tienen tanques de agua, albercas o canecas abiertas en casa?",
                options = listOf(
                    "No, todos los depósitos tienen tapa hermética o están bajo techo",
                    "Sí, 1 o 2 albercas o tanques abiertos sin tapa",
                    "Sí, varios tanques, canecas o barriles descubiertos con agua estancada"
                ),
                correctAnswerIndex = 0,
                explanation = "Los tanques sin tapa son el sitio #1 de reproducción del Aedes aegypti en el Huila."
            ),
            QuizQuestion(
                id = 3,
                question = "¿Con qué frecuencia cepillan y lavan las albercas y lavaderos?",
                options = listOf(
                    "Cada semana sin falta con cepillo, jabón y cloro",
                    "Cada 15 a 30 días o cuando se observa sucio",
                    "Rara vez o nunca cepillamos las paredes internas del tanque"
                ),
                correctAnswerIndex = 0,
                explanation = "El cepillado fuerte despega los huevos pegados a las paredes del estanque."
            ),
            QuizQuestion(
                id = 4,
                question = "¿Tienen floreros con agua, llantas o botellas en patios/jardines?",
                options = listOf(
                    "No, patio totalmente limpio, sin inservibles ni floreros con agua",
                    "Sí, floreros con agua o algunos envases pequeños en el patio",
                    "Sí, varias llantas, botellas o recipientes que acumulan agua de lluvia"
                ),
                correctAnswerIndex = 0,
                explanation = "Llantas y floreros acumulan agua limpia donde eclosionan las larvas rápidamente."
            ),
            QuizQuestion(
                id = 5,
                question = "¿Cuentan con mallas (anjeos) en ventanas o mosquiteros (toldillos)?",
                options = listOf(
                    "Sí, mallas de anjeo en ventanas y toldillos en dormitorios",
                    "Solo mallas en algunas ventanas pero sin toldillos",
                    "No tenemos protección física contra mosquitos y entran libremente"
                ),
                correctAnswerIndex = 0,
                explanation = "El uso de mallas físicas en ventanas y toldillos reduce en un 90% la picadura en casa."
            ),
            QuizQuestion(
                id = 6,
                question = "¿Alguien en la casa ha tenido fiebre o síntomas en los últimos 14 días?",
                options = listOf(
                    "Ninguno, todos los integrantes están sanos",
                    "Sí, 1 persona con fiebre ligera o malestar",
                    "Sí, 1 o más personas con fiebre alta (>38.5°C), sarpullido o dolor muscular"
                ),
                correctAnswerIndex = 0,
                explanation = "Fiebre con dolor retroocular y manchas rojas requiere atención médica inmediata."
            )
        )
    }

    var selectedAnswers by remember { mutableStateOf(mapOf<Int, Int>()) }
    var isSubmitted by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentPadding = PaddingValues(bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.HomeWork, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text(
                            text = "Encuesta Epidemiológica de Hogar y Prevención",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                    Text(
                        text = "Responde acerca de los integrantes de tu vivienda y las condiciones de tu casa. Al finalizar, la app analizará los datos y te entregará consejos médicos y preventivos personalizados.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        items(questions) { q ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "${q.id}. ${q.question}",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )

                    q.options.forEachIndexed { idx, opt ->
                        val isSelected = selectedAnswers[q.id] == idx

                        val cardBg = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(cardBg),
                            onClick = {
                                if (!isSubmitted) {
                                    selectedAnswers = selectedAnswers + (q.id to idx)
                                }
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = {
                                        if (!isSubmitted) {
                                            selectedAnswers = selectedAnswers + (q.id to idx)
                                        }
                                    }
                                )
                                Text(
                                    text = opt,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(start = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            if (!isSubmitted) {
                Button(
                    onClick = {
                        isSubmitted = true

                        // Calculate risk score
                        val totalRiskPoints = selectedAnswers.values.sum()
                        val scoreToSave = 12 - totalRiskPoints

                        // Save result to Room DB
                        onSaveSurveyResult(
                            SurveyEntity(
                                title = "Encuesta Epidemiológica de Hogar",
                                score = scoreToSave,
                                totalQuestions = questions.size
                            )
                        )
                    },
                    enabled = selectedAnswers.size == questions.size,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("btn_submit_survey"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.Analytics, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Analizar Mi Hogar y Recibir Consejos", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            } else {
                val q1 = selectedAnswers[1] ?: 0
                val q2 = selectedAnswers[2] ?: 0
                val q3 = selectedAnswers[3] ?: 0
                val q4 = selectedAnswers[4] ?: 0
                val q5 = selectedAnswers[5] ?: 0
                val q6 = selectedAnswers[6] ?: 0

                val totalRiskPoints = q1 + q2 + q3 + q4 + q5 + q6
                val riskLevel = when {
                    totalRiskPoints <= 2 -> "RIESGO BAJO (Hogar Seguro)"
                    totalRiskPoints <= 6 -> "RIESGO MODERADO"
                    totalRiskPoints <= 9 -> "RIESGO ALTO"
                    else -> "RIESGO CRÍTICO VECTORIAL"
                }

                val headerBg = when {
                    totalRiskPoints <= 2 -> Color(0xFFC8E6C9)
                    totalRiskPoints <= 6 -> Color(0xFFFFF9C4)
                    totalRiskPoints <= 9 -> Color(0xFFFFE0B2)
                    else -> Color(0xFFFFCDD2)
                }

                val headerText = when {
                    totalRiskPoints <= 2 -> Color(0xFF1B5E20)
                    totalRiskPoints <= 6 -> Color(0xFFF57F17)
                    totalRiskPoints <= 9 -> Color(0xFFE65100)
                    else -> Color(0xFFB71C1C)
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Surface(
                            color = headerBg,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "📊 Diagnóstico Epidemiológico de Tu Hogar",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = headerText
                                )
                                Text(
                                    text = riskLevel,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                    color = headerText
                                )
                            }
                        }

                        Text(
                            text = "💡 Consejos y Recomendaciones Personalizadas según tus respuestas:",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )

                        // Custom Advice 1: Water Tanks
                        if (q2 > 0 || q3 > 0) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f))
                            ) {
                                Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Icon(Icons.Default.WaterDrop, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text("🚰 Acción en Tanques y Albercas", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                                        Text(
                                            "Tus albercas o tanques representan un potencial criadero. Cepilla las paredes internas con cepillo de cerda dura y jabón cada 5 días para desprender los huevos. Tapa herméticamente la alberca o solicita el producto larvicida BTI en la E.S.E. Hospital San Andrés de La Argentina.",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                            }
                        } else {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
                            ) {
                                Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32))
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text("✅ Excelente Manejo de Depósitos de Agua", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge, color = Color(0xFF2E7D32))
                                        Text("Mantienes tus tanques sellados y limpios. Continúa revisando semanalmente que las tapas no tengan grietas por donde entren mosquitos.", style = MaterialTheme.typography.bodySmall, color = Color(0xFF1B5E20))
                                    }
                                }
                            }
                        }

                        // Custom Advice 2: Outdoor Items
                        if (q4 > 0) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                            ) {
                                Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Icon(Icons.Default.DeleteSweep, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text("🌺 Saneamiento del Patio y Floreros", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                                        Text(
                                            "Reemplaza el agua de floreros por tierra o arena húmeda. Revisa llantas, botellas y latas en el patio: voltéalas boca abajo o entrégalas en la Jornada de Limpieza de la I.E. El Pescador.",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                            }
                        }

                        // Custom Advice 3: Physical Protection
                        if (q5 > 0) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
                            ) {
                                Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Icon(Icons.Default.Shield, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text("🛏️ Protección Física en Ventanas y Cuartos", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                                        Text(
                                            "Dado que los mosquitos pican principalmente al amanecer y atardecer, instala mallas o anjeos finos en ventanas. Para niños y adultos mayores, prioriza el uso de toldillos mosquiteros al dormir.",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                            }
                        }

                        // Custom Advice 4: Symptoms & Medical
                        if (q6 > 0) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                            ) {
                                Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Icon(Icons.Default.LocalHospital, contentDescription = null, tint = Color(0xFFC62828))
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text("🩺 ALERTA MÉDICA SINTOMÁTICA", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge, color = Color(0xFFC62828))
                                        Text(
                                            "Has reportado integrantes con síntoma febril. Acude inmediatamente a la E.S.E. Hospital San Andrés de La Argentina o al Puesto de Salud El Pescador. Ofrece abundante suero oral SRO. PROHIBIDO tomar Ibuprofeno, Aspirina o Naproxeno por alto riesgo de hemorragia.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFF8E0000)
                                        )
                                    }
                                }
                            }
                        }

                        Button(
                            onClick = {
                                isSubmitted = false
                                selectedAnswers = emptyMap()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Realizar Nueva Encuesta de Hogar")
                        }
                    }
                }
            }
        }
    }
}
