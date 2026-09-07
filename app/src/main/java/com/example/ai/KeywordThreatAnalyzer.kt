package com.example.ai

data class ThreatParameterMatch(
    val categoryName: String,
    val matchedKeywords: List<String>,
    val riskWeight: Int
)

data class ParameterizedAnalysisResult(
    val threatLevel: ThreatLevel,
    val scorePercentage: Int,
    val primaryCategory: String,
    val detectedParameters: List<ThreatParameterMatch>,
    val summary: String,
    val immediateEscapeSteps: List<String>,
    val tacticalDeescalationAdvice: List<String>,
    val recommendedActions: List<String>,
    val isExtremeEmergency: Boolean = false
)

object KeywordThreatAnalyzer {

    // 1. EXTREME CRITICAL: Rape & Sexual Assault Keywords (Score 100)
    private val SEXUAL_ASSAULT_RAPE_KEYWORDS = listOf(
        "rape", "rapist", "raping", "attempted rape", "molest", "molestation", "molesting", "molested",
        "sexual assault", "sexually assaulted", "forced sex", "groping", "grope", "groped", "stripping",
        "undressing", "forced to undress", "touching private parts", "private parts", "unwanted touch",
        "non consensual", "pocso", "child molestation", "child abuse", "forced kiss", "indecent assault",
        "sexual harassment", "flashing", "exposing himself", "lewd touch", "forced intercourse", "attempt to rape",
        "கற்பழிப்பு", "பாலியல் வன்கொடுமை", "வன்புணர்வு", "பாலியல் தொல்லை", "பாலியல் சீண்டல்",
        "பாலியல் பலாத்காரம்", "ஆடை கிழித்தல்", "உடை அவிழ்க்க", "தவறாக தொடுகிறான்", "அந்தரங்க உறுப்பு",
        "தொட்டு இழுக்கிறான்", "பாலியல் அத்துமீறல்", "போக்சோ", "குழந்தை பாலியல் வன்கொடுமை",
        "குழந்தை துன்புறுத்தல்", "வலுக்கட்டாயமாக", "கட்டாய உறவு", "பாலியல் அச்சுறுத்தல்", "கற்பழிக்க முயற்சி"
    )

    // 2. EXTREME CRITICAL: Kidnap, Hostage & Lethal Weapon Threat (Score 100)
    private val KIDNAP_LETHAL_WEAPON_KEYWORDS = listOf(
        "kidnap", "kidnapped", "kidnapping", "abduct", "abduction", "hostage", "ransom",
        "acid attack", "acid threat", "knife at throat", "gunpoint", "kill", "killing", "threat to kill",
        "murder", "strangle", "strangling", "choke", "choking", "stab", "stabbing", "beheading",
        "throat cut", "burning", "locked inside", "captive", "dragged into van", "pulled into car",
        "கடத்தல்", "கடத்திட்டாங்க", "கடத்துகிறான்", "ஆசிட் வீச்சு", "ஆசிட்", "கழுத்தை நெரிக்கிறான்",
        "கத்தி வைத்து மிரட்டுகிறான்", "துப்பாக்கி முனை", "கொன்று விடுவேன்", "கொலை மிரட்டல்",
        "குத்த வர்றான்", "பிணைக் கைதி", "அறையில் பூட்டிவிட்டான்", "தீ வைத்து", "உயிருக்கு ஆபத்து",
        "காரில் கடத்துகிறான்", "கழுத்தை பிடிக்குறான்"
    )

    // 3. CRITICAL: Drugging, Spiked Drinks & Incapacitation (Score 95)
    private val DRUG_SPIKE_KEYWORDS = listOf(
        "spiked", "spiked drink", "drugged", "sedative", "poisoned", "feeling dizzy",
        "losing consciousness", "unconscious", "forced to drink", "powder in drink", "smelled chloroform",
        "passed out", "blackout", "blurred vision", "can't move", "paralyzed", "loss of balance",
        "மயக்க மருந்து", "விஷம்", "மயக்கம் வருது", "கண் தெரியல", "தண்ணீரில் ஏதோ கலந்திருக்கிறான்",
        "மயங்கி விழுந்தேன்", "சுயநினைவு இழக்கிறேன்", "எழுந்திருக்க முடியல", "மயக்கமடைய செய்கிறான்"
    )

    // 4. PHYSICAL ASSAULT & DISTRESS (Score 90)
    private val PHYSICAL_ASSAULT_KEYWORDS = listOf(
        "attack", "hit", "slap", "grab", "force", "weapon", "knife", "gun", "blood",
        "beat", "beating", "touch", "groped", "scream", "help", "pulled down", "cornered",
        "pushed down", "iron rod", "broken bottle", "bleeding", "bruises", "punched", "kicked",
        "அடிக்கிறான்", "தாக்குறான்", "கத்தி", "துப்பாக்கி", "ரத்தம்", "காப்பாத்துங்க", "உதவி",
        "அடிக்க வர்றான்", "பிடிச்சி இழுக்குறான்", "கீழே தள்ளுறான்", "கம்பால் அடிக்கிறான்", "பாட்டிலால் குத்துகிறான்"
    )

    // 5. ACTIVE STALKING & PURSUIT (Score 82)
    private val STALKING_KEYWORDS = listOf(
        "follow", "following", "chase", "chasing", "behind", "shadow", "stalk", "stalker",
        "trailing", "corner", "staring", "tailgating", "watching", "circling", "following on bike",
        "car following", "taking photos secretly", "lurking", "blocking way",
        "துரத்துறாங்க", "பின்னால வர்றான்", "பின்தொடர்", "நிழல்", "வழி மறிக்கிறான்", "தொடர்கிறார்",
        "பின்தொடர்கிறார்கள்", "துரத்துகிறான்", "பின்னடியே வாரான்", "ஓடி வர்றான்", "பைக்கில் பின்தொடர்கிறான்",
        "ரகசியமாக படம் எடுக்கிறான்", "வழி அடைக்கிறான்"
    )

    // 6. TRANSIT & CAB VEHICLE ANOMALY (Score 78)
    private val TRANSIT_CAB_KEYWORDS = listOf(
        "cab", "taxi", "uber", "ola", "auto", "driver", "wrong route", "door lock", "isolated",
        "autorickshaw", "bus", "night travel", "train", "expressway", "refusing to stop",
        "driver looking back", "child lock on", "tinted glass", "speeding wrong way",
        "ஆட்டோ", "டாக்ஸி", "டிரைவர்", "வழி மாறுறான்", "கதவை பூட்டுறான்", "தனி வழி", "தவறான பாதை",
        "பஸ்", "பேருந்து", "ரயில்", "வண்டியை நிறுத்த மாட்டேங்கிறான்", "கதவு திறக்க முடியல"
    )

    // 7. WORKPLACE & DOMESTIC THREAT (Score 75)
    private val DOMESTIC_WORKPLACE_KEYWORDS = listOf(
        "office", "boss", "colleague", "workplace", "lock room", "threaten job", "posh", "husband",
        "in laws", "domestic violence", "marital abuse", "beaten at home", "dowry harassment",
        "salary withheld", "locked at home", "threatened dismissal",
        "அலுவலகம்", "அறை பூட்டு", "வேலை மிரட்டல்", "வீட்டு வன்முறை", "கணவன் அடிக்கிறான்",
        "மாமியார் கொடுமை", "வரதட்சணை கொடுமை", "வீட்டில் அடைத்து வைத்துள்ளார்கள்"
    )

    // 8. CYBER BLACKMAIL & EXTORTION (Score 70)
    private val CYBER_ONLINE_THREAT_KEYWORDS = listOf(
        "blackmail", "photo leak", "online threat", "video threat", "fake profile", "cyber stalking",
        "morphing", "deepfake", "nude video", "whatsapp threat", "extortion", "cyber bullying",
        "recording in trial room", "spy camera", "hidden camera",
        "பிளாக்மெயில்", "புகைப்படம்", "வீடியோ மிரட்டல்", "ஆன்லைன் அச்சுறுத்தல்", "போலி கணக்கு",
        "மார்பிங்", "அந்தரங்க வீடியோ", "ட்ரையல் ரூம் ரகசிய கேமரா"
    )

    // 9. VERBAL HARASSMENT & EVE TEASING (Score 60)
    private val VERBAL_HARASSMENT_KEYWORDS = listOf(
        "harass", "stare", "comments", "shout", "shouting", "drunk", "threaten", "abusing",
        "catcall", "eve teasing", "obscene gestures", "whistling", "lewd language", "singing songs",
        "வேணும்னே பேசுறான்", "மிரட்டுறான்", "குடிபோதை", "முறைக்கிறான்", "கேலி", "அசிங்கமா பேசுறான்",
        "கிண்டல்", "கலாட்டா", "விசில் அடிக்கிறான்", "அசிங்கமான சைகை"
    )

    // 10. ISOLATED DARK ZONE (Score 50)
    private val ISOLATED_NIGHT_KEYWORDS = listOf(
        "dark", "alone", "night", "no street light", "scared", "empty road", "deserted",
        "subway", "alley", "bus stop empty", "forest path", "abandoned building",
        "இருட்டு", "தனியா", "இரவு", "ஆள் நடமாட்டம் இல்லை", "பயமா இருக்கு", "யாருமில்லை",
        "தெரு விளக்கு இல்லை", "பாழடைந்த கட்டிடம்"
    )

    fun analyze(
        prompt: String,
        isTamil: Boolean = false,
        isBelow18: Boolean = false
    ): ParameterizedAnalysisResult {
        val clean = prompt.lowercase()
        val detected = mutableListOf<ThreatParameterMatch>()

        // 1. Sexual Assault & Rape check (Extreme Priority - Score 100)
        val rapeMatches = SEXUAL_ASSAULT_RAPE_KEYWORDS.filter { clean.contains(it) }
        if (rapeMatches.isNotEmpty()) {
            detected.add(ThreatParameterMatch("SEXUAL_ASSAULT_RAPE_EMERGENCY", rapeMatches, 100))
        }

        // 2. Kidnap & Lethal Weapon check (Extreme Priority - Score 100)
        val kidnapMatches = KIDNAP_LETHAL_WEAPON_KEYWORDS.filter { clean.contains(it) }
        if (kidnapMatches.isNotEmpty()) {
            detected.add(ThreatParameterMatch("KIDNAP_WEAPON_LETHAL_CRITICAL", kidnapMatches, 100))
        }

        // 3. Drugging & Spiked Drinks check (Score 95)
        val drugMatches = DRUG_SPIKE_KEYWORDS.filter { clean.contains(it) }
        if (drugMatches.isNotEmpty()) {
            detected.add(ThreatParameterMatch("DRUG_SPIKE_INCAPACITATION", drugMatches, 95))
        }

        // 4. Physical Assault check (Score 90)
        val assaultMatches = PHYSICAL_ASSAULT_KEYWORDS.filter { clean.contains(it) }
        if (assaultMatches.isNotEmpty()) {
            detected.add(ThreatParameterMatch("PHYSICAL_ATTACK_DISTRESS", assaultMatches, 90))
        }

        // 5. Stalking check (Score 82)
        val stalkingMatches = STALKING_KEYWORDS.filter { clean.contains(it) }
        if (stalkingMatches.isNotEmpty()) {
            detected.add(ThreatParameterMatch("ACTIVE_STALKING_FOLLOWING", stalkingMatches, 82))
        }

        // 6. Transit cab check (Score 78)
        val transitMatches = TRANSIT_CAB_KEYWORDS.filter { clean.contains(it) }
        if (transitMatches.isNotEmpty()) {
            detected.add(ThreatParameterMatch("CAB_TRANSIT_ANOMALY", transitMatches, 78))
        }

        // 7. Domestic & Workplace threat check (Score 75)
        val domesticMatches = DOMESTIC_WORKPLACE_KEYWORDS.filter { clean.contains(it) }
        if (domesticMatches.isNotEmpty()) {
            detected.add(ThreatParameterMatch("WORKPLACE_DOMESTIC_THREAT", domesticMatches, 75))
        }

        // 8. Cyber threat check (Score 70)
        val cyberMatches = CYBER_ONLINE_THREAT_KEYWORDS.filter { clean.contains(it) }
        if (cyberMatches.isNotEmpty()) {
            detected.add(ThreatParameterMatch("CYBER_HARASSMENT_BLACKMAIL", cyberMatches, 70))
        }

        // 9. Verbal Harassment check (Score 60)
        val harassmentMatches = VERBAL_HARASSMENT_KEYWORDS.filter { clean.contains(it) }
        if (harassmentMatches.isNotEmpty()) {
            detected.add(ThreatParameterMatch("VERBAL_HARASSMENT", harassmentMatches, 60))
        }

        // 10. Isolated Night check (Score 50)
        val nightMatches = ISOLATED_NIGHT_KEYWORDS.filter { clean.contains(it) }
        if (nightMatches.isNotEmpty()) {
            detected.add(ThreatParameterMatch("ISOLATED_DARK_ZONE", nightMatches, 50))
        }

        val baseRisk = detected.maxOfOrNull { it.riskWeight } ?: 35
        // Compound danger score for multiple threat signals (e.g. stalked + night + grab)
        val extraMultipliers = (detected.size - 1).coerceAtLeast(0) * 5
        val finalScore = (baseRisk + extraMultipliers).coerceIn(0, 100)

        val hasExtremeCritical = rapeMatches.isNotEmpty() || kidnapMatches.isNotEmpty()
        val finalCalculatedScore = if (hasExtremeCritical) 100 else finalScore

        val level = when {
            finalCalculatedScore >= 88 || hasExtremeCritical -> ThreatLevel.CRITICAL
            finalCalculatedScore >= 72 -> ThreatLevel.HIGH
            finalCalculatedScore >= 45 -> ThreatLevel.MEDIUM
            else -> ThreatLevel.LOW
        }

        val primaryCat = detected.firstOrNull()?.categoryName ?: "GENERAL_SAFETY_QUERY"
        val targetHelpline = if (isBelow18) "1098 (Childline Helpline)" else "1091 (TN Women Helpline)"

        val summary = if (isTamil) {
            when {
                hasExtremeCritical -> "🚨 தீவிர அவசர எச்சரிக்கை: பாலியல் அச்சுறுத்தல் அல்லது கடத்தல் ஆபத்து! உடனடியாக 100% தற்காப்பு மற்றும் காவல்துறை உதவி ($targetHelpline) தேவை."
                level == ThreatLevel.CRITICAL -> "🚨 அதிகபட்ச ஆபத்து கண்டறியப்பட்டது! உடனடி தற்காப்பு மற்றும் $targetHelpline உதவி தேவை."
                level == ThreatLevel.HIGH -> "⚠️ அதிக ஆபத்து எச்சரிக்கை: பாதுகாப்பான மக்கள் நடமாட்டம் உள்ள பகுதிக்கு செல்லவும்."
                level == ThreatLevel.MEDIUM -> "🟡 மிதமான ஆபத்து: சுற்றிலும் கவனமாக இருக்கவும்."
                else -> "🟢 குறைந்த ஆபத்து: விழிப்புடன் இருங்கள்."
            }
        } else {
            when {
                hasExtremeCritical -> "🚨 EXTREME CRITICAL EMERGENCY: Sexual assault or severe physical threat identified! Immediate police intervention ($targetHelpline) and physical escape required."
                level == ThreatLevel.CRITICAL -> "🚨 CRITICAL THREAT DETECTED: Immediate physical safety and emergency dispatch ($targetHelpline) required!"
                level == ThreatLevel.HIGH -> "⚠️ HIGH THREAT ALERT: Elevated risk of interception or hostile confrontation."
                level == ThreatLevel.MEDIUM -> "🟡 MODERATE THREAT: Elevated risk detected. Move to public lit spaces."
                else -> "🟢 LOW THREAT: Standard situational awareness recommended."
            }
        }

        val escapeSteps = if (isTamil) {
            when {
                hasExtremeCritical -> listOf(
                    "உடனடியாக உரத்த குரலில் 'தீ!' அல்லது 'காப்பாத்துங்க!' என்று கத்தி பொதுமக்களின் கவனத்தை ஈர்க்கவும்.",
                    "தாக்க முயன்றால் மூக்கு எலும்பில் உள்ளங்கை அடி (Palm Heel Strike) அல்லது கண், பிறப்புறுப்பில் உதைத்து விட்டு தப்பவும்.",
                    "உடனடியாக $targetHelpline எண்ணை அழைக்கவும் அல்லது அவசர SOS பொத்தானை அழுத்தவும்.",
                    "அருகிலுள்ள தேநீர் கடை, பெட்ரோல் பங்க் அல்லது அனைத்து மகளிர் காவல் நிலையத்திற்குள் (AWPS) நுழையவும்."
                )
                level == ThreatLevel.CRITICAL -> listOf(
                    "உடனடியாக அருகில் உள்ள கடை, தேனீர் கடை அல்லது மக்கள் நடமாட்டம் உள்ள இடத்திற்கு செல்லவும்.",
                    "$targetHelpline அவசர எண்ணை உடனடியாக அழைக்கவும்.",
                    "உரத்த சைரன் அலாரத்தை இயக்கி பொதுமக்களை ஈர்க்கவும்.",
                    "பாதுகாவலர்களுக்கு அவசர குரல் பதிவு மற்றும் இருப்பிடத்தை அனுப்பவும்."
                )
                level == ThreatLevel.HIGH -> listOf(
                    "சாலையின் எதிர்ப்பக்கத்திற்கு மாறி வெளிச்சமான பகுதியை நோக்கி நடக்கவும்.",
                    "அருகில் உள்ள அனைத்து மகளிர் காவல் நிலையத்தை நோக்கி செல்லவும்.",
                    "கைபேசியில் அவசர SOS பொத்தானை தயாராக வைக்கவும்."
                )
                else -> listOf(
                    "வெளிச்சமான பாதையில் நடக்கவும்.",
                    "ஹெட்போன் அணிவதை தவிர்க்கவும்.",
                    "பாதுகாவலருக்கு தற்போதைய இருப்பிடத்தை பகிரவும்."
                )
            }
        } else {
            when {
                hasExtremeCritical -> listOf(
                    "Step 1: Instantly shout 'FIRE!' or 'POLICE!' at maximum volume to command instant public intervention.",
                    "Step 2: If physically grabbed, execute Palm-Heel strike to the nose or hard Groin Kick, then sprint toward crowds.",
                    "Step 3: Auto-dial $targetHelpline & sound the 110dB loud buzzer alarm immediately.",
                    "Step 4: Enter the nearest 24/7 establishment (Tea stall, Petrol bunk, Hospital, AWPS Police Station)."
                )
                level == ThreatLevel.CRITICAL -> listOf(
                    "Step 1: Instantly enter the nearest open store, hotel, or crowd cluster.",
                    "Step 2: Press SOS to auto-call $targetHelpline.",
                    "Step 3: Trigger the 110dB loud siren alarm to command public intervention.",
                    "Step 4: Dispatch instant 10s voice recording evidence to registered Guardians."
                )
                level == ThreatLevel.HIGH -> listOf(
                    "Step 1: Immediately cross the street towards light & commercial activity.",
                    "Step 2: Fake an urgent voice phone call loudly mentioning police tracking and location.",
                    "Step 3: Keep finger on the Sentinel AI voice recorder button."
                )
                else -> listOf(
                    "Step 1: Walk facing oncoming traffic in well-lit areas.",
                    "Step 2: Avoid looking down at your phone or using headphones.",
                    "Step 3: Keep primary guardian speed dial ready."
                )
            }
        }

        val tacticalAdvice = if (isTamil) {
            if (isBelow18) {
                listOf(
                    "குழந்தைகள் பாதுகாப்பு சட்டம் (POCSO Act) கடுமையான பாதுகாப்பு வழங்குகிறது. உடனடியாக 1098 Childline-ஐ அழைக்கவும்.",
                    "அருகிலுள்ள பெரியவர்கள், ஆசிரியர்கள் அல்லது காவல் நிலையத்திடம் தைரியமாக உதவி கேட்கவும்."
                )
            } else {
                listOf(
                    "பயப்படாதீர்கள்! தமிழ்நாடு அனைத்து மகளிர் காவல் நிலையங்கள் (AWPS) 24/7 உடனடி பாதுகாப்பு தரும்.",
                    "1091 மகளிர் காவல் உதவி எண் மற்றும் காவலன் SOS செயலியைப் பயன்படுத்தவும்."
                )
            }
        } else {
            if (isBelow18) {
                listOf(
                    "POCSO Act provides strict zero-tolerance legal protection. Immediate support via 1098 Childline.",
                    "Reach out loudly to the nearest adult, shopkeeper, or police officer."
                )
            } else {
                listOf(
                    "Yell 'FIRE!' or 'POLICE!' instead of 'help' to immediately compel crowd intervention.",
                    "Utilize Sentinel AI AWPS Directory to locate the nearest 24/7 Tamil Nadu All Women Police Station."
                )
            }
        }

        val actions = listOf(
            if (isBelow18) "CALL_1098" else "CALL_1091",
            "RECORD_VOICE_GUARDIANS",
            "SOUND_SIREN",
            "SHARE_EVIDENCE"
        )

        return ParameterizedAnalysisResult(
            threatLevel = level,
            scorePercentage = finalCalculatedScore,
            primaryCategory = primaryCat,
            detectedParameters = detected,
            summary = summary,
            immediateEscapeSteps = escapeSteps,
            tacticalDeescalationAdvice = tacticalAdvice,
            recommendedActions = actions,
            isExtremeEmergency = hasExtremeCritical
        )
    }
}

