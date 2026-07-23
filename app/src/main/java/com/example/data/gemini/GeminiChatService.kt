package com.example.data.gemini

import com.example.BuildConfig
import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

data class Part(
    @field:Json(name = "text") val text: String? = null
)

data class Content(
    @field:Json(name = "parts") val parts: List<Part>,
    @field:Json(name = "role") val role: String? = null
)

data class GenerateContentRequest(
    @field:Json(name = "contents") val contents: List<Content>,
    @field:Json(name = "systemInstruction") val systemInstruction: Content? = null
)

data class Candidate(
    @field:Json(name = "content") val content: Content? = null
)

data class GenerateContentResponse(
    @field:Json(name = "candidates") val candidates: List<Candidate>? = null
)

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GenerateContentRequest
    ): GenerateContentResponse
}

object GeminiClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    val service: GeminiApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GeminiApiService::class.java)
    }
}

class GeminiChatService {

    private val systemPrompt = """
        Eres "Dr. Zancudo", un asistente médico y educativo especializado en la prevención y control del zancudo Aedes aegypti (Dengue, Chikungunya, Zika, Fiebre Amarilla) para un proyecto escolar de salud pública comunitaria.
        
        Tus objetivos:
        1. Proporcionar respuestas amables, claras, accesibles y científicamente precisas sobre zancudos, picaduras, síntomas de alarma (fiebre alta, dolor retroocular, dolor articular, sarpullido) y medidas de prevención.
        2. Enseñar a eliminar criaderos de agua estancada (lavar floreros, tapar tanques, desechar llantas).
        3. Siempre aclarar que en caso de fiebre superior a 38.5°C o signos de sangrado se debe acudir INMEDIATAMENTE al Centro de Salud más cercano y NO automedicarse con ibuprofeno ni aspirina (solo acetaminofén/paracetamol si es indicado).
        4. Ofrecer la opción de contactar al soporte de salud o redirigir a WhatsApp oficial de emergencias sanitarias si la persona requiere atención humana directa.
        5. Mantener un tono muy claro y empático, adecuado tanto para personas mayores como estudiantes y niños.
    """.trimIndent()

    suspend fun getResponse(userMessage: String, history: List<Pair<String, String>> = emptyList()): String = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext getOfflineSmartResponse(userMessage)
        }

        try {
            val contentList = mutableListOf<Content>()

            // Add previous history turns if any
            history.takeLast(6).forEach { (sender, text) ->
                val role = if (sender == "User") "user" else "model"
                contentList.add(Content(parts = listOf(Part(text = text)), role = role))
            }

            // Current question
            contentList.add(Content(parts = listOf(Part(text = userMessage)), role = "user"))

            val request = GenerateContentRequest(
                contents = contentList,
                systemInstruction = Content(parts = listOf(Part(text = systemPrompt)))
            )

            val response = GeminiClient.service.generateContent(apiKey, request)
            val replyText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text

            if (replyText != null && replyText.isNotBlank()) {
                replyText
            } else {
                getOfflineSmartResponse(userMessage)
            }
        } catch (e: Exception) {
            getOfflineSmartResponse(userMessage)
        }
    }

    private fun getOfflineSmartResponse(query: String): String {
        val q = query.lowercase()
        return when {
            q.contains("síntoma") || q.contains("sintoma") || q.contains("fiebre") || q.contains("dengue") -> {
                "🩺 **Síntomas de Alarma del Dengue**:\n" +
                        "• Fiebre alta repentina (39°C - 40°C).\n" +
                        "• Dolor detrás de los ojos (retroocular).\n" +
                        "• Dolor intenso en articulaciones y músculos.\n" +
                        "• Sarpullido o manchas rojas en la piel.\n\n" +
                        "⚠️ **Importante**: Evita tomar ibuprofeno o aspirina, ya que pueden aumentar el riesgo de sangrado. Utiliza solo acetaminofén/paracetamol y mantén abundante hidratación. Si presentas sangrado de encías o vómito, acude de inmediato al centro de salud."
            }
            q.contains("criadero") || q.contains("preven") || q.contains("agua") || q.contains("evitar") -> {
                "🛡️ **Guía Anti-Criaderos de Zancudos**:\n" +
                        "1. **Elimina agua estancada**: Lava y cepilla tanques, albercas y floreros cada 4 días.\n" +
                        "2. **Tapa bien**: Recipientes donde almacenes agua para consumo.\n" +
                        "3. **Llantas y botellas**: Voltea o desecha objetos que acumulen agua de lluvia.\n" +
                        "4. **Usa repelente**: Aplica repelente con DEET y coloca mosquiteros en ventanas."
            }
            q.contains("whatsapp") || q.contains("contacto") || q.contains("médico") || q.contains("medico") -> {
                "📱 Puedes comunicarte directamente con el personal médico o la línea de orientación comunitaria presionando el botón 'Contactar por WhatsApp' en la parte superior o acudiendo al Centro de Salud San José."
            }
            else -> {
                "🤖 Hola, soy **Dr. Zancudo**. Puedo responder preguntas sobre el mosquito Aedes aegypti, prevención de brotes, fiebre del dengue, eliminación de criaderos e historial de salud.\n\n¿En qué te puedo orientar hoy para tu proyecto escolar o tu comunidad?"
            }
        }
    }
}
