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
                text = { Text("Encuesta Web", fontWeight = FontWeight.Bold, fontSize = if (isEasyMode) 15.sp else 13.sp) },
                icon = { Icon(Icons.Default.Quiz, contentDescription = null) }
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
                question = "¿En qué tipo de agua prefiere poner sus huevos el mosquito del Dengue?",
                options = listOf(
                    "En agua estancada y limpia en recipientes artificiales",
                    "En ríos de corriente rápida",
                    "En lodo o tierra seca",
                    "En agua salada del mar"
                ),
                correctAnswerIndex = 0,
                explanation = "Aedes aegypti busca depósitos de agua limpia estancada cerca de las viviendas."
            ),
            QuizQuestion(
                id = 2,
                question = "¿Qué medicamento NO se debe tomar en caso de sospecha de Dengue?",
                options = listOf(
                    "Ibuprofeno o Aspirina",
                    "Acetaminofén / Paracetamol",
                    "Suero de rehidratación oral",
                    "Abundante agua"
                ),
                correctAnswerIndex = 0,
                explanation = "El ibuprofeno y la aspirina aumentan el riesgo de hemorragias en el dengue."
            ),
            QuizQuestion(
                id = 3,
                question = "¿Cada cuántos días se debe lavar y cepillar tanques y albercas?",
                options = listOf(
                    "Cada 4 a 5 días",
                    "Una vez al año",
                    "Cada 3 meses",
                    "Nunca es necesario"
                ),
                correctAnswerIndex = 0,
                explanation = "El ciclo de larva a adulto toma 8-10 días; lavar cada 4 días destruye las larvas."
            )
        )
    }

    var selectedAnswers by remember { mutableStateOf(mapOf<Int, Int>()) }
    var isSubmitted by remember { mutableStateOf(false) }
    var score by remember { mutableIntStateOf(0) }

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
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Encuesta de Concientización y Proyecto Escolar",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Responde las siguientes preguntas para validar tus conocimientos. Tienes ${surveysCompleted.size} encuestas registradas en el servidor.",
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
                        val isCorrect = q.correctAnswerIndex == idx

                        val cardBg = when {
                            isSubmitted && isCorrect -> Color(0xFFC8E6C9)
                            isSubmitted && isSelected && !isCorrect -> Color(0xFFFFCDD2)
                            isSelected -> MaterialTheme.colorScheme.primaryContainer
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }

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

                    if (isSubmitted) {
                        Text(
                            text = "Explicación: ${q.explanation}",
                            style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.primary)
                        )
                    }
                }
            }
        }

        item {
            if (!isSubmitted) {
                Button(
                    onClick = {
                        var totalScore = 0
                        questions.forEach { q ->
                            if (selectedAnswers[q.id] == q.correctAnswerIndex) {
                                totalScore++
                            }
                        }
                        score = totalScore
                        isSubmitted = true

                        // Save result
                        onSaveSurveyResult(
                            SurveyEntity(
                                title = "Encuesta Escolar sobre Dengue y Aedes",
                                score = totalScore,
                                totalQuestions = questions.size
                            )
                        )
                    },
                    enabled = selectedAnswers.size == questions.size,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("btn_submit_survey"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Enviar Respuestas a la Web del Colegio", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Stars, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(40.dp))
                        Text(
                            text = "¡Encuesta Completada! Puntaje: $score / ${questions.size}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Tu certificado de concientización se ha guardado y sincronizado con el proyecto escolar.",
                            style = MaterialTheme.typography.bodySmall
                        )

                        Button(
                            onClick = {
                                isSubmitted = false
                                selectedAnswers = emptyMap()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Reintentar Encuesta")
                        }
                    }
                }
            }
        }
    }
}
