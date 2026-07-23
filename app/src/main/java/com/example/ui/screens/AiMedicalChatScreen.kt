package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.gemini.GeminiChatService
import kotlinx.coroutines.launch

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: String, // "User" or "DrZancudo"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Composable
fun AiMedicalChatScreen(
    isEasyMode: Boolean,
    onOpenWhatsApp: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val chatService = remember { GeminiChatService() }

    var messages by remember {
        mutableStateOf(
            listOf(
                ChatMessage(
                    sender = "DrZancudo",
                    text = "🤖 **¡Hola! Soy Dr. Zancudo**, tu asistente médico de soporte e inteligencia artificial para el proyecto escolar de prevención.\n\n" +
                            "Puedo orientarte sobre los síntomas del dengue, cómo eliminar criaderos o dónde conseguir atención médica. ¿En qué puedo ayudarte?"
                )
            )
        )
    }

    var inputText by remember { mutableStateOf("") }
    var isLoadingResponse by remember { mutableStateOf(false) }

    val quickPills = listOf(
        "¿Cuáles son los síntomas del dengue?",
        "¿Qué medicamentos NO debo tomar?",
        "¿Cómo elimino larvas en un tanque?",
        "¿Cuándo ir a emergencias?"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("ai_chat_screen_content")
    ) {
        // WhatsApp Direct Support Banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .clickable { onOpenWhatsApp() }
                .testTag("banner_whatsapp_direct"),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF25D366)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(Icons.Default.Phone, contentDescription = null, tint = Color.White)
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "💬 Soporte Humano & WhatsApp Médico",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Color.White)
                    )
                    Text(
                        text = "Haz clic para chatear con un profesional de salud en WhatsApp",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.9f))
                    )
                }
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.White)
            }
        }

        // Quick Prompt Pills
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(quickPills) { pill ->
                SuggestionChip(
                    onClick = {
                        inputText = pill
                    },
                    label = { Text(pill, fontSize = if (isEasyMode) 14.sp else 12.sp) },
                    icon = { Icon(Icons.AutoMirrored.Filled.HelpOutline, contentDescription = null, modifier = Modifier.size(14.dp)) }
                )
            }
        }

        // Messages Stream
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            reverseLayout = false,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(messages) { msg ->
                val isUser = msg.sender == "User"

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                ) {
                    if (!isUser) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.SmartToy, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    Surface(
                        color = if (isUser) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.widthIn(max = 280.dp)
                    ) {
                        Text(
                            text = msg.text,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = if (isEasyMode) 16.sp else 14.sp
                            ),
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }

            if (isLoadingResponse) {
                item {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        Text("Dr. Zancudo consultando guía médica...", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }

        // Input Area
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 80.dp)
                .padding(12.dp),
            tonalElevation = 6.dp,
            shape = RoundedCornerShape(24.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text("Escribe tu pregunta sobre zancudos o salud...") },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("input_chat_prompt"),
                    shape = RoundedCornerShape(20.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        val prompt = inputText.trim()
                        if (prompt.isNotBlank() && !isLoadingResponse) {
                            val userMsg = ChatMessage(sender = "User", text = prompt)
                            messages = messages + userMsg
                            inputText = ""
                            isLoadingResponse = true

                            coroutineScope.launch {
                                val historyPairs = messages.map { Pair(it.sender, it.text) }
                                val botReply = chatService.getResponse(prompt, historyPairs)
                                messages = messages + ChatMessage(sender = "DrZancudo", text = botReply)
                                isLoadingResponse = false
                            }
                        }
                    },
                    modifier = Modifier
                        .testTag("btn_send_chat")
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Enviar", tint = Color.White)
                }
            }
        }
    }
}
