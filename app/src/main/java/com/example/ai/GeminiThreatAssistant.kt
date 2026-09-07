package com.example.ai

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

enum class ThreatLevel {
    LOW, MEDIUM, HIGH, CRITICAL
}

data class ThreatAnalysisResult(
    val threatLevel: ThreatLevel,
    val scorePercentage: Int, // 0 - 100
    val summary: String,
    val immediateEscapeSteps: List<String>,
    val tacticalDeescalationAdvice: List<String>,
    val recommendedHelpline: String = "1091 (TN Women Helpline) / 112",
    val matchedCriticalCategory: String? = null
)

class GeminiThreatAssistant {

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun evaluateAttackThreat(
        userSituation: String,
        isTamil: Boolean = false,
        isBelow18: Boolean = false
    ): ThreatAnalysisResult = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        val containsTamilScript = userSituation.any { it.code in 0x0B80..0x0BFF }
        val useTamilResponse = isTamil || containsTamilScript

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext getOfflineTacticalFallback(userSituation, useTamilResponse, isBelow18)
        }

        val primaryHelpline = if (isBelow18) "1098 (Childline & Minor Safety)" else "1091 (TN Women Helpline)"
        val userCategory = if (isBelow18) "Minor / Child Safety (<18 years)" else "Adult / Women Safety (18+ years)"

        val languageInstruction = if (useTamilResponse) {
            "CRITICAL: The user language is TAMIL. You MUST generate SUMMARY, ESCAPE_STEPS, and TACTICAL_ADVICE entirely in clear, authoritative Tamil language (தமிழ் script) so that Tamil TTS voice engine speaks it natively!"
        } else {
            "Language: English"
        }

        val systemPrompt = """
            You are Sentinel AI Emergency Threat Evaluator and Tactical Escape Assistant for Tamil Nadu Women and Child Safety.
            User Profile: $userCategory
            Primary Emergency Routing: $primaryHelpline and Tamil Nadu Emergency 112.

            TASK: Analyze the user's situation prompt and determine threat severity, calculate danger score (0-100), and provide immediate survival escape steps.

            CRITICAL THREAT SCORING RULES:
            1. If the situation mentions or implies RAPE, ATTEMPTED RAPE, SEXUAL ASSAULT, MOLESTATION, GROPING, FORCED STRIPPING, POCSO VIOLATIONS, KIDNAPPING, HOSTAGE, ACID ATTACK, KNIFE/GUNPOINT, STRANGULATION, or DRUGGED/SPIKED DRINKS:
               - THREAT_LEVEL MUST BE: CRITICAL
               - SCORE MUST BE: 100
            2. If the situation involves ACTIVE STALKING, CAR/BIKE PURSUIT, PHYSICAL BLOCKS, or TRANSIT/CAB ABDUCTION/DOOR LOCKING:
               - THREAT_LEVEL: HIGH or CRITICAL (Score 78-95)
            3. If the situation involves WORKPLACE/DOMESTIC ABUSE or CYBER BLACKMAIL:
               - THREAT_LEVEL: MEDIUM or HIGH (Score 65-80)
            4. If the situation involves GENERAL CAUTION in dark or unfamiliar streets:
               - THREAT_LEVEL: LOW or MEDIUM (Score 30-55)

            $languageInstruction

            OUTPUT FORMAT RULES:
            Line 1: THREAT_LEVEL: [LOW | MEDIUM | HIGH | CRITICAL] | SCORE: [number 0-100]
            Line 2: SUMMARY: 1-2 sentences of crisp situational threat analysis.
            ESCAPE_STEPS:
            - Step 1: Immediate physical/spatial countermeasure (e.g., Palm-heel strike to nasal cartilage, eye gouge, groin kick, screaming 'FIRE!/தீ!', breaking glass).
            - Step 2: Immediate technology dispatch (Dial $primaryHelpline, trigger 110dB loud buzzer alarm, instant 10s voice recording).
            - Step 3: Fast navigation to immediate public safe haven (crowded tea shop, petrol bunk, railway station, AWPS police station).
            - Step 4: Guardian tracking alert.
            TACTICAL_ADVICE:
            - Tactical de-escalation / legal safeguard (POSH / POCSO / BNS Sec 376 / All Women Police Station AWPS).
            - Key defense trick.

            Keep instructions concise, urgent, life-saving, and actionable in real-world Indian / Tamil Nadu environments.
        """.trimIndent()

        try {
            val jsonBody = JSONObject().apply {
                val contentsArray = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val partsArray = JSONArray().apply {
                            put(JSONObject().put("text", "User Situation: $userSituation"))
                        }
                        put("parts", partsArray)
                    }
                    put(contentObj)
                }
                put("contents", contentsArray)

                val sysInstructionObj = JSONObject().apply {
                    val sysPartsArray = JSONArray().apply {
                        put(JSONObject().put("text", systemPrompt))
                    }
                    put("parts", sysPartsArray)
                }
                put("systemInstruction", sysInstructionObj)
            }

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = jsonBody.toString().toRequestBody(mediaType)
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = okHttpClient.newCall(request).execute()
            val responseBody = response.body?.string()

            if (response.isSuccessful && !responseBody.isNullOrBlank()) {
                val rootJson = JSONObject(responseBody)
                val candidates = rootJson.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val firstCandidate = candidates.getJSONObject(0)
                    val content = firstCandidate.optJSONObject("content")
                    val parts = content?.optJSONArray("parts")
                    if (parts != null && parts.length() > 0) {
                        val responseText = parts.getJSONObject(0).optString("text")
                        if (responseText.isNotBlank()) {
                            return@withContext parseGeminiResponse(responseText, userSituation, isBelow18)
                        }
                    }
                }
            }
            getOfflineTacticalFallback(userSituation, useTamilResponse, isBelow18)
        } catch (e: Exception) {
            getOfflineTacticalFallback(userSituation, useTamilResponse, isBelow18)
        }
    }

    private fun parseGeminiResponse(
        text: String,
        originalPrompt: String,
        isBelow18: Boolean
    ): ThreatAnalysisResult {
        var level = ThreatLevel.MEDIUM
        var score = 60
        var summary = "Threat evaluation completed."
        val escapeSteps = mutableListOf<String>()
        val tacticalAdvice = mutableListOf<String>()

        val lowerPrompt = originalPrompt.lowercase()
        val isExtremePrompt = lowerPrompt.contains("rape") || lowerPrompt.contains("molest") ||
                lowerPrompt.contains("sexual") || lowerPrompt.contains("grope") ||
                lowerPrompt.contains("strip") || lowerPrompt.contains("kidnap") ||
                lowerPrompt.contains("pocso") || lowerPrompt.contains("கற்பழிப்பு") ||
                lowerPrompt.contains("வன்கொடுமை") || lowerPrompt.contains("வன்புணர்வு") ||
                lowerPrompt.contains("கடத்தல்") || lowerPrompt.contains("தீ")

        val lines = text.lines()
        var currentSection = ""

        for (line in lines) {
            val trimmed = line.trim()
            if (trimmed.startsWith("THREAT_LEVEL:", ignoreCase = true)) {
                if (trimmed.contains("CRITICAL", ignoreCase = true) || isExtremePrompt) {
                    level = ThreatLevel.CRITICAL
                    score = if (isExtremePrompt) 100 else 95
                } else if (trimmed.contains("HIGH", ignoreCase = true)) {
                    level = ThreatLevel.HIGH
                    score = 80
                } else if (trimmed.contains("LOW", ignoreCase = true)) {
                    level = ThreatLevel.LOW
                    score = 25
                } else {
                    level = ThreatLevel.MEDIUM
                    score = 55
                }

                // Extract numeric score if present in header
                val scoreMatch = Regex("SCORE:\\s*(\\d+)", RegexOption.IGNORE_CASE).find(trimmed)
                if (scoreMatch != null) {
                    val parsedScore = scoreMatch.groupValues[1].toIntOrNull()
                    if (parsedScore != null) {
                        score = if (isExtremePrompt) 100 else parsedScore.coerceIn(0, 100)
                    }
                }
            } else if (trimmed.startsWith("SUMMARY:", ignoreCase = true)) {
                summary = trimmed.substringAfter("SUMMARY:").trim()
            } else if (trimmed.startsWith("ESCAPE_STEPS:", ignoreCase = true)) {
                currentSection = "ESCAPE"
            } else if (trimmed.startsWith("TACTICAL_ADVICE:", ignoreCase = true)) {
                currentSection = "TACTICAL"
            } else if (trimmed.startsWith("-") || trimmed.startsWith("*") || trimmed.matches(Regex("^\\d+\\..*"))) {
                val cleanStep = trimmed.replace(Regex("^[-*\\d.]+\\s*"), "")
                if (cleanStep.isNotBlank()) {
                    if (currentSection == "ESCAPE") {
                        escapeSteps.add(cleanStep)
                    } else if (currentSection == "TACTICAL") {
                        tacticalAdvice.add(cleanStep)
                    }
                }
            }
        }

        val primaryHelpline = if (isBelow18) "1098 (Child Safety Helpline)" else "1091 (TN Women Helpline)"

        if (escapeSteps.isEmpty()) {
            escapeSteps.addAll(listOf(
                "Instantly sprint towards the nearest open tea stall, petrol bunk, or crowd cluster.",
                "Trigger the Sentinel AI SOS button to auto-call $primaryHelpline.",
                "Activate the 110dB loud siren alarm to command public intervention.",
                "If grabbed, strike palm-heel to the nose or kick groin and escape towards people."
            ))
        }

        if (tacticalAdvice.isEmpty()) {
            tacticalAdvice.addAll(listOf(
                "Yell 'FIRE!' or 'POLICE!' at top volume instead of 'help' to command crowd action.",
                "Direct emergency call to $primaryHelpline and dispatch voice evidence to registered Guardians."
            ))
        }

        if (isExtremePrompt) {
            level = ThreatLevel.CRITICAL
            score = 100
        }

        return ThreatAnalysisResult(
            threatLevel = level,
            scorePercentage = score,
            summary = if (summary.length > 250) summary.take(250) + "..." else summary,
            immediateEscapeSteps = escapeSteps,
            tacticalDeescalationAdvice = tacticalAdvice,
            recommendedHelpline = primaryHelpline
        )
    }

    private fun getOfflineTacticalFallback(
        prompt: String,
        isTamil: Boolean = false,
        isBelow18: Boolean = false
    ): ThreatAnalysisResult {
        val lower = prompt.lowercase()
        val containsTamil = isTamil || prompt.any { it.code in 0x0B80..0x0BFF }
        val targetHelpline = if (isBelow18) "1098 (Childline Helpline)" else "1091 (TN Women Helpline)"

        // 1. Extreme Critical Check: Rape, Sexual Assault, POCSO, Kidnap, Lethal Weapons
        val isExtreme = lower.contains("rape") || lower.contains("molest") || lower.contains("sexual") ||
                lower.contains("grope") || lower.contains("strip") || lower.contains("kidnap") ||
                lower.contains("pocso") || lower.contains("knife at throat") || lower.contains("gunpoint") ||
                lower.contains("acid") || lower.contains("kill") || lower.contains("கற்பழிப்பு") ||
                lower.contains("வன்கொடுமை") || lower.contains("வன்புணர்வு") || lower.contains("தொல்லை") ||
                lower.contains("சீண்டல்") || lower.contains("கடத்தல்") || lower.contains("ஆசிட்") ||
                lower.contains("கொலை") || lower.contains("போக்சோ")

        if (isExtreme) {
            return if (containsTamil) {
                ThreatAnalysisResult(
                    threatLevel = ThreatLevel.CRITICAL,
                    scorePercentage = 100,
                    summary = "🚨 தீவிர ஆபத்து: பாலியல் அச்சுறுத்தல் அல்லது உடனடி உடல்ரீதியான வன்முறை கண்டறியப்பட்டது. 100% உடனடி தற்காப்பு மற்றும் காவல்துறை ($targetHelpline) தலையீடு தேவை.",
                    immediateEscapeSteps = listOf(
                        "பயப்பட வேண்டாம்! உரத்த குரலில் 'தீ!' அல்லது 'காப்பாத்துங்கள்!' என்று உச்சபட்ச சத்தத்தில் கத்தவும்.",
                        "தாக்க வந்தால் மூக்கு எலும்பில் உள்ளங்கை அடி (Palm Heel Strike) அல்லது கண் / பிறப்புறுப்பில் ஓங்கி உதைத்து விட்டு ஓடவும்.",
                        "உடனடியாக $targetHelpline எண்ணை அழைக்கவும் அல்லது அவசர SOS பொத்தானை அழுத்தவும்.",
                        "அருகிலுள்ள தேநீர் கடை, பெட்ரோல் பங்க், பேருந்து நிலையம் அல்லது அனைத்து மகளிர் காவல் நிலையத்திற்குள் (AWPS) தஞ்சம் அடையவும்."
                    ),
                    tacticalDeescalationAdvice = listOf(
                        if (isBelow18) "POCSO சட்டம் மற்றும் 1098 Childline குழந்தைகள் பாதுகாப்பு உங்களுக்கு முழு சட்ட பாதுகாப்பு வழங்குகிறது." else "தமிழ்நாடு அனைத்து மகளிர் காவல் நிலையங்கள் (AWPS) மற்றும் 1091 எண் 24/7 பெண்களுக்கு பாதுகாப்பு தரும்.",
                        "பாதுகாவலர்களுக்கு உடனடி குரல் பதிவு மற்றும் GPS நேரலை இருப்பிடத்தை அனுப்பவும்."
                    ),
                    recommendedHelpline = targetHelpline
                )
            } else {
                ThreatAnalysisResult(
                    threatLevel = ThreatLevel.CRITICAL,
                    scorePercentage = 100,
                    summary = "🚨 EXTREME CRITICAL EMERGENCY (100% Danger Score): Sexual assault or lethal violence threat detected! Immediate physical escape and police intervention ($targetHelpline) required.",
                    immediateEscapeSteps = listOf(
                        "Step 1: Shout 'FIRE!' or 'POLICE!' at maximum volume to instantly command crowd intervention.",
                        "Step 2: If grabbed, drive a hard Palm Heel Strike straight into the attacker's nose or kick groin with full force, then sprint towards light.",
                        "Step 3: Trigger Sentinel AI SOS to auto-call $targetHelpline & sound the 110dB loud buzzer alarm.",
                        "Step 4: Enter the nearest 24/7 public establishment (Tea stall, Petrol bunk, Hospital, AWPS Police Station)."
                    ),
                    tacticalDeescalationAdvice = listOf(
                        if (isBelow18) "POCSO Act provides strict zero-tolerance protection for minors. Reach out via 1098 Childline immediately." else "Tamil Nadu All Women Police Stations (AWPS) provide 24/7 immediate protection under BNS & Women Safety Acts.",
                        "Dispatch 10s voice recording evidence to registered Guardians via Emergency SMS."
                    ),
                    recommendedHelpline = targetHelpline
                )
            }
        }

        // 2. Drugging & Spiked Drinks
        val isDrugged = lower.contains("spiked") || lower.contains("drug") || lower.contains("dizzy") ||
                lower.contains("unconscious") || lower.contains("மயக்க மருந்து") || lower.contains("மயக்கம்")

        if (isDrugged) {
            return if (containsTamil) {
                ThreatAnalysisResult(
                    threatLevel = ThreatLevel.CRITICAL,
                    scorePercentage = 95,
                    summary = "🚨 அவசர எச்சரிக்கை: மயக்க மருந்து அல்லது பானத்தில் நச்சு கலந்திருக்கலாம். சுயநினைவு இழக்கும் முன் உடனே பாதுகாப்பான இடத்திற்கு செல்லவும்.",
                    immediateEscapeSteps = listOf(
                        "உடனடியாக அந்த இடத்தை விட்டு வெளியேறி பொது வெளிச்சமுள்ள பகுதிக்கு செல்லவும்.",
                        "நம்பகமான நபரிடம் அல்லது கடைக்காரரிடம் உதவி கோரவும்.",
                        "உடனடியாக $targetHelpline அல்லது 108 ஆம்புலன்ஸ் எண்ணை அழைக்கவும்.",
                        "பாதுகாவலர்களுக்கு உடனடி நேரலை இருப்பிடத்தை பகிரவும்."
                    ),
                    tacticalDeescalationAdvice = listOf(
                        "சுயநினைவை இழக்காமல் இருக்க முகத்தில் குளிர்ந்த நீரை தெளித்துக் கொள்ளவும்.",
                        "அறிமுகமில்லாத நபர்கள் தரும் பானங்களை ஒருபோதும் உட்கொள்ள வேண்டாம்."
                    ),
                    recommendedHelpline = targetHelpline
                )
            } else {
                ThreatAnalysisResult(
                    threatLevel = ThreatLevel.CRITICAL,
                    scorePercentage = 95,
                    summary = "🚨 CRITICAL DRUGGING ALERT: Spiked drink or chemical incapacitation suspected. Act immediately before losing motor control.",
                    immediateEscapeSteps = listOf(
                        "Step 1: Immediately leave the venue into a well-lit public area or hotel reception.",
                        "Step 2: Inform staff or female patrons loudly that you suspect you have been drugged.",
                        "Step 3: Press SOS to auto-call $targetHelpline and dial 108 Ambulance.",
                        "Step 4: Share live location to Guardians instantly."
                    ),
                    tacticalDeescalationAdvice = listOf(
                        "Splash cold water on face to maintain consciousness.",
                        "Do NOT go anywhere alone with anyone offering to 'take you home' from the venue."
                    ),
                    recommendedHelpline = targetHelpline
                )
            }
        }

        // 3. Stalking & Pursuits
        val isStalking = lower.contains("follow") || lower.contains("chase") || lower.contains("behind") ||
                lower.contains("stalk") || lower.contains("corner") || lower.contains("பின்தொடர்") ||
                lower.contains("துரத்து") || lower.contains("பின்னாடி")

        if (isStalking) {
            return if (containsTamil) {
                ThreatAnalysisResult(
                    threatLevel = ThreatLevel.HIGH,
                    scorePercentage = 85,
                    summary = "⚠️ அதிக ஆபத்து: ஒருவன் உங்களை பின்தொடர்கிறான் அல்லது பின்தொடர்ந்து வருகிறான்.",
                    immediateEscapeSteps = listOf(
                        "சாலையின் எதிர்ப்பக்கத்திற்கு உடனே மாறி மக்கள் நடமாட்டம் உள்ள பகுதியை நோக்கி விரைவாக நடக்கவும்.",
                        "அருகிலுள்ள கடை அல்லது தேநீரகத்திற்குள் சென்று அங்கே நிற்கவும்.",
                        "$targetHelpline மகளிர் காவல் எண்ணை அழைக்கவும்.",
                        "ஆதாரத்திற்காக ஆடியோ ரெக்கார்டரை இயக்கவும்."
                    ),
                    tacticalDeescalationAdvice = listOf(
                        "போனில் உரக்கப் பேசுவது போல் பாசாங்கு செய்து காவல் நிலையத்தின் பெயரைச் சொல்லவும்.",
                        "அருகில் உள்ள அனைத்து மகளிர் காவல் நிலையத்திற்குச் செல்லவும்."
                    ),
                    recommendedHelpline = targetHelpline
                )
            } else {
                ThreatAnalysisResult(
                    threatLevel = ThreatLevel.HIGH,
                    scorePercentage = 85,
                    summary = "⚠️ HIGH THREAT ALERT: Active stalking or pursuit detected in close proximity.",
                    immediateEscapeSteps = listOf(
                        "Step 1: Cross the street immediately and walk briskly towards high-density commercial light.",
                        "Step 2: Enter an open store, supermarket, or tea stall and stay inside.",
                        "Step 3: Keep finger on the Sentinel AI SOS button ready to auto-dial $targetHelpline.",
                        "Step 4: Start 10s voice evidence recorder."
                    ),
                    tacticalDeescalationAdvice = listOf(
                        "Fake an assertive phone call describing your exact location and that police/family are waiting ahead.",
                        "Do not turn into narrow or unlit lanes."
                    ),
                    recommendedHelpline = targetHelpline
                )
            }
        }

        // 4. Cab / Transit Anomaly
        val isTransit = lower.contains("cab") || lower.contains("taxi") || lower.contains("auto") ||
                lower.contains("driver") || lower.contains("wrong route") || lower.contains("ஆட்டோ") ||
                lower.contains("டாக்ஸி") || lower.contains("டிரைவர்") || lower.contains("பாதை")

        if (isTransit) {
            return if (containsTamil) {
                ThreatAnalysisResult(
                    threatLevel = ThreatLevel.HIGH,
                    scorePercentage = 80,
                    summary = "⚠️ அதிக ஆபத்து: ஆட்டோ / டாக்ஸி தவறான பாதையில் செல்கிறது அல்லது ஆபத்தான சூழல் உள்ளது.",
                    immediateEscapeSteps = listOf(
                        "ஓட்டுநரிடம் தைரியமாக சொல்லுங்கள்: 'என் குடும்பத்தினரும் காவல்துறையும் இந்த பயணத்தை நேரலையாகக் கண்காணிக்கிறார்கள்'.",
                        "வண்டி மெதுவாக செல்லும் போது அல்லது நிற்கும் போது, உடனே கதவைத் திறந்து வெளிச்சமான இடத்தை நோக்கி இறங்கவும்.",
                        "பாதுகாவலர்களுக்கு உடனடி SMS எச்சரிக்கை அனுப்பவும்.",
                        "ஆதாரத்திற்காக ஒலிப்பதிவை (Audio Record) இயக்கவும்."
                    ),
                    tacticalDeescalationAdvice = listOf(
                        "குடும்பத்தினரிடம் பேசுவது போல் போலி அழைப்பு செய்து வாகனத்தின் பதிவு எண்ணை உரக்கச் சொல்லவும்.",
                        "கதவுக் பிடியை எப்போதும் தயாராகப் பிடித்துக் கொள்ளவும்."
                    ),
                    recommendedHelpline = targetHelpline
                )
            } else {
                ThreatAnalysisResult(
                    threatLevel = ThreatLevel.HIGH,
                    scorePercentage = 80,
                    summary = "⚠️ HIGH TRANSIT ALERT: Vehicle anomaly or route deviation detected.",
                    immediateEscapeSteps = listOf(
                        "Step 1: Assertively demand the driver stop: 'My family and police are live tracking this trip'.",
                        "Step 2: If vehicle slows down at a junction, unlock door immediately and exit towards public light.",
                        "Step 3: Send instant SMS alert with live location to Guardians.",
                        "Step 4: Keep one-tap Audio Evidence Recorder active."
                    ),
                    tacticalDeescalationAdvice = listOf(
                        "Loudly speak the vehicle registration number over a fake or real phone call.",
                        "Keep hand firmly on door lock handle."
                    ),
                    recommendedHelpline = targetHelpline
                )
            }
        }

        // Default Moderate Caution
        return if (containsTamil) {
            ThreatAnalysisResult(
                threatLevel = ThreatLevel.MEDIUM,
                scorePercentage = 50,
                summary = "🟡 மிதமான ஆபத்து: எச்சரிக்கையுடனும் விழிப்புடனும் இருக்க அறிவுறுத்தப்படுகிறது.",
                immediateEscapeSteps = listOf(
                    "அருகில் உள்ள மகளிர் காவல் நிலையம் (AWPS) அல்லது காவல் சாவடியைக் கண்டறியவும்.",
                    "சென்டினல் AI பயன்பாட்டை திறந்த நிலையில் வைத்திருக்கவும்.",
                    "எதிரே வரும் வாகனங்களை நோக்கியவாறு சுறுசுறுப்பாக நடக்கவும்."
                ),
                tacticalDeescalationAdvice = listOf(
                    "தனியாக நடக்கும் போது இயர்போன் அணிவதையோ போனை மட்டும் பார்த்து நடப்பதையோ தவிர்க்கவும்.",
                    "முதன்மை பாதுகாவலரின் எண்ணை விரைவு அழைப்பில் வைக்கவும்."
                ),
                recommendedHelpline = targetHelpline
            )
        } else {
            ThreatAnalysisResult(
                threatLevel = ThreatLevel.MEDIUM,
                scorePercentage = 50,
                summary = "🟡 MODERATE THREAT: Standard situational alertness and caution recommended.",
                immediateEscapeSteps = listOf(
                    "Step 1: Scan surroundings for nearest All Women Police Station (AWPS) or lit public booth.",
                    "Step 2: Keep phone unlocked on Sentinel AI home screen.",
                    "Step 3: Walk briskly facing incoming traffic in well-lit areas."
                ),
                tacticalDeescalationAdvice = listOf(
                    "Avoid wearing headphones or looking down at phone while walking alone.",
                    "Keep primary guardian on quick speed dial."
                ),
                recommendedHelpline = targetHelpline
            )
        }
    }
}

