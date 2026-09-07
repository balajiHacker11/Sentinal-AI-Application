package com.example.service

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.example.ai.ThreatAnalysisResult
import com.example.ai.ThreatLevel
import com.example.data.model.AppLanguage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class TextToSpeechManager(context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = TextToSpeech(context, this)
    private var isInitialized = false

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isInitialized = true
            tts?.setPitch(1.0f)
            tts?.setSpeechRate(0.95f)

            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isSpeaking.value = true
                }

                override fun onDone(utteranceId: String?) {
                    _isSpeaking.value = false
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    _isSpeaking.value = false
                }

                override fun onError(utteranceId: String?, errorCode: Int) {
                    _isSpeaking.value = false
                }
            })
        }
    }

    /**
     * Builds a comprehensive and fluid spoken voice script including all solution tips and escape steps.
     */
    fun buildSolutionSpeechScript(result: ThreatAnalysisResult, isTamil: Boolean): String {
        val cleanSummary = sanitizeForSpeech(result.summary)

        return if (isTamil) {
            val levelText = when (result.threatLevel) {
                ThreatLevel.CRITICAL -> "மிகவும் அவசர ஆபத்து நிலை"
                ThreatLevel.HIGH -> "அதிக ஆபத்து நிலை"
                ThreatLevel.MEDIUM -> "மிதமான ஆபத்து நிலை"
                ThreatLevel.LOW -> "குறைந்த ஆபத்து நிலை"
            }

            val stepsBuilder = StringBuilder()
            result.immediateEscapeSteps.forEachIndexed { idx, step ->
                val cleanStep = sanitizeForSpeech(step)
                val stepWord = when (idx) {
                    0 -> "முதல் வழி"
                    1 -> "இரண்டாம் வழி"
                    2 -> "மூன்றாம் வழி"
                    3 -> "நான்காம் வழி"
                    else -> "வழி ${idx + 1}"
                }
                stepsBuilder.append("$stepWord: $cleanStep. ")
            }

            val adviceBuilder = StringBuilder()
            if (result.tacticalDeescalationAdvice.isNotEmpty()) {
                adviceBuilder.append("கூடுதல் பாதுகாப்பு ஆலோசனைகள்: ")
                result.tacticalDeescalationAdvice.forEach { advice ->
                    adviceBuilder.append("${sanitizeForSpeech(advice)}. ")
                }
            }

            "பாதுகாப்பு மதிப்பீடு: $levelText. $cleanSummary. உங்களுக்கான உடனடி தப்பிக்கும் வழிகள் இதோ. $stepsBuilder $adviceBuilder அவசர உதவிக்கு 1091 மகளிர் காவல் எண்ணை அழைக்கவும்."
        } else {
            val levelText = when (result.threatLevel) {
                ThreatLevel.CRITICAL -> "Critical emergency risk"
                ThreatLevel.HIGH -> "High risk"
                ThreatLevel.MEDIUM -> "Moderate risk"
                ThreatLevel.LOW -> "Low risk"
            }

            val stepsBuilder = StringBuilder()
            result.immediateEscapeSteps.forEachIndexed { idx, step ->
                val cleanStep = sanitizeForSpeech(step)
                stepsBuilder.append("Step ${idx + 1}: $cleanStep. ")
            }

            val adviceBuilder = StringBuilder()
            if (result.tacticalDeescalationAdvice.isNotEmpty()) {
                adviceBuilder.append("Important safety and de-escalation advice: ")
                result.tacticalDeescalationAdvice.forEach { advice ->
                    adviceBuilder.append("${sanitizeForSpeech(advice)}. ")
                }
            }

            "Threat assessment: $levelText. $cleanSummary. Here are your tactical escape solution steps. $stepsBuilder $adviceBuilder For immediate police assistance, dial 1091, or tap the emergency SOS button."
        }
    }

    private fun sanitizeForSpeech(text: String): String {
        return text
            .replace(Regex("[*#_`~]"), "") // Remove markdown asterisks and symbols
            .replace(Regex("^[-•\\d.]+\\s*"), "") // Remove leading bullet or numbering
            .replace("1091", "1 0 9 1") // Speak helpline numbers distinctly
            .replace("112", "1 1 2")
            .trim()
    }

    fun speak(text: String, language: AppLanguage) {
        if (!isInitialized || tts == null) return

        val containsTamilScript = text.any { it.code in 0x0B80..0x0BFF }
        val isTamilMode = language == AppLanguage.TAMIL || containsTamilScript

        val primaryLocale = if (isTamilMode) Locale("ta", "IN") else Locale.US
        var langResult = tts?.setLanguage(primaryLocale)

        if (isTamilMode && (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED)) {
            langResult = tts?.setLanguage(Locale("ta"))
        }

        if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
            tts?.setLanguage(Locale.US)
        }

        val cleanText = text.replace(Regex("[*#_`~]"), "").trim()
        _isSpeaking.value = true

        val params = Bundle()
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "SentinelAiVoiceResponse_${System.currentTimeMillis()}")
        tts?.speak(cleanText, TextToSpeech.QUEUE_FLUSH, params, "SentinelAiVoiceResponse")
    }

    fun stop() {
        _isSpeaking.value = false
        tts?.stop()
    }

    fun shutdown() {
        stop()
        tts?.shutdown()
        tts = null
    }
}
