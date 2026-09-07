package com.example.data.model

object AppStrings {

    fun get(language: AppLanguage): Strings {
        return when (language) {
            AppLanguage.ENGLISH -> EnglishStrings
            AppLanguage.TAMIL -> TamilStrings
        }
    }

    interface Strings {
        // App & Navigation
        val appTitle: String
        val topAppBarCall: String
        val selectLanguage: String
        val languageDialogTitle: String
        val languageDialogSubtitle: String
        val confirmLanguage: String
        val tabSos: String
        val tabAi: String
        val tabAwps: String
        val tabGuardians: String
        val tabGuide: String

        // SOS Screen
        val sosBannerTag: String
        val sosBannerTitle: String
        val sosBannerSubtitle: String
        val sosInstructionText: String
        val sosButtonDefault: String
        val sosButtonActive: String
        val quickControlsTitle: String
        val callPoliceAction: String
        val loudSirenAction: String
        val stopSirenAction: String
        val recordAudioAction: String
        val stopAudioAction: String
        val smsGuardiansAction: String
        val recordingEvidenceHeader: String
        val stopBtn: String
        val savedAudioHeader: String
        val helplinesTitle: String
        val helplineTnPolice: String
        val helplineTnPoliceDesc: String
        val helplineErs: String
        val helplineErsDesc: String
        val helplineChild: String
        val helplineChildDesc: String
        val helplineNcw: String
        val helplineNcwDesc: String

        // Guardians Screen
        val guardiansHeaderTitle: String
        val guardiansHeaderSubtitle: String
        val testSmsBtn: String
        val savedGuardiansTitle: String
        val addNewBtn: String
        val noGuardiansTitle: String
        val noGuardiansSubtitle: String
        val addFirstGuardianBtn: String
        val addGuardianDialogTitle: String
        val guardianNameLabel: String
        val guardianPhoneLabel: String
        val guardianRelationLabel: String
        val setPrimaryContact: String
        val saveGuardianBtn: String
        val cancelBtn: String
        val primaryLabel: String

        // AWPS Screen
        val awpsTitle: String
        val awpsSubtitle: String
        val awpsSearchPlaceholder: String
        val awpsFoundCount: String
        val jurisdictionPrefix: String
        val callStationBtn: String
        val directionsBtn: String

        // AI Assistant Screen
        val aiHeaderTitle: String
        val aiHeaderSubtitle: String
        val quickScenariosTitle: String
        val aiInputPlaceholder: String
        val analyzeBtnText: String
        val evaluatingText: String
        val dangerScorePrefix: String
        val threatAnalysisTitle: String
        val tacticalEscapeTitle: String
        val deescalationTitle: String
        val call1091Btn: String
        val alertSmsBtn: String
        val voiceInputBtn: String
        val voiceInputListening: String
        val autoVoiceGuardianBtn: String
        val speakResponseBtn: String
        val muteResponseBtn: String
        val parameterizedKeywordsTitle: String
        val sendOfflineSmsEvidenceAction: String
        val tamilVoiceBadgeText: String
        val scenarioStalking: String
        val scenarioCab: String
        val scenarioNight: String
        val scenarioWorkplace: String
        val scenarioCyber: String

        // Permissions & Scream Detector
        val screamDetectorTitle: String
        val screamDetectorSubtitle: String
        val screamListeningActive: String
        val screamListeningOff: String
        val screamDialogTitle: String
        val screamDialogMessage: String
        val captureIncidentPhotoAction: String
        val shareEvidenceGuardians: String
        val grantPermissionsBtn: String
        val permissionsHeader: String
        val permissionsDesc: String

        // Age Verification & Helpline Routing
        val ageDialogTitle: String
        val ageDialogSubtitle: String
        val ageBelow18Title: String
        val ageBelow18Desc: String
        val ageAbove18Title: String
        val ageAbove18Desc: String
        val ageExactLabel: String
        val confirmAgeBtn: String
        val ageModeChildBadge: String
        val ageModeWomenBadge: String
        val ageModeChildCardTitle: String
        val ageModeChildCardDesc: String
        val ageModeWomenCardTitle: String
        val ageModeWomenCardDesc: String
        val changeAgeBtn: String
        val primaryForYourAgeTag: String
        val callPrimaryActionChild: String
        val callPrimaryActionWomen: String

        // Shake & Motion Emergency Detector
        val shakeDetectorTitle: String
        val shakeDetectorSubtitle: String
        val shakeListeningActive: String
        val shakeListeningOff: String
        val shakeDialogTitle: String
        val shakeDialogMessage: String
        val shakeSensitivityLabel: String
        val shakeLevelLabel: String
        val shakeMaximumLevelText: String
        val shakeHighLevelText: String

        // Power Button Double-Tap Emergency Guard
        val powerButtonTitle: String
        val powerButtonSubtitle: String
        val powerButtonActiveStatus: String
        val powerButtonInactiveStatus: String
        val powerButtonInstruction: String
        val powerButtonTestBtn: String
        val powerButtonDialogTitle: String
        val powerButtonDialogMessage: String
        val powerButtonAutoCalling: String
        val powerButtonBuzzerActive: String
        val powerButtonVoiceRecording: String
        val powerButtonSmsDispatched: String
        val powerButtonSimulateDoubleTap: String

        // Guide Screen
        val guideHeaderTitle: String
        val guideHeaderSubtitle: String
        val tabSelfDefense: String
        val tabTravelSafety: String
        val tabLegalRights: String
        val targetPrefix: String
        val executionStepsTitle: String
        val tipPrefix: String
    }

    private object EnglishStrings : Strings {
        override val appTitle = "Sentinel AI"
        override val topAppBarCall = "Call Police 1091"
        override val selectLanguage = "Language / மொழி"
        override val languageDialogTitle = "Select App Language / மொழியைத் தேர்ந்தெடுக்கவும்"
        override val languageDialogSubtitle = "Choose your preferred language for safety alerts and navigation"
        override val confirmLanguage = "Confirm Language / உறுதிப்படுத்துக"
        override val tabSos = "SOS"
        override val tabAi = "AI Escape"
        override val tabAwps = "AWPS Police"
        override val tabGuardians = "Guardians"
        override val tabGuide = "Guide"

        override val sosBannerTag = "SENTINEL AI DIGITAL GUARD"
        override val sosBannerTitle = "SENTINEL AI Women's Digital Safety Guard"
        override val sosBannerSubtitle = "24/7 AI Threat Monitor • Direct 1091 Helpline & Guardians Alert"
        override val sosInstructionText = "Press button to call 1091, alert Guardians, sound Siren & Record Audio"
        override val sosButtonDefault = "TAP FOR EMERGENCY SOS"
        override val sosButtonActive = "EMERGENCY ALERT SENT!"
        override val quickControlsTitle = "Emergency Quick Controls"
        override val callPoliceAction = "Call Police\n1091"
        override val loudSirenAction = "Loud Siren\nAlarm"
        override val stopSirenAction = "STOP\nSiren"
        override val recordAudioAction = "Record Audio\nEvidence"
        override val stopAudioAction = "Stop Audio\nRecord"
        override val smsGuardiansAction = "SMS Alert\nGuardians"
        override val recordingEvidenceHeader = "RECORDING EVIDENCE AUDIO..."
        override val stopBtn = "STOP"
        override val savedAudioHeader = "Saved Audio Evidence"
        override val helplinesTitle = "Tamil Nadu & National Helplines"
        override val helplineTnPolice = "TN Women Police Helpline"
        override val helplineTnPoliceDesc = "24/7 Immediate Tamil Nadu Women Distress Helpline"
        override val helplineErs = "Emergency Response System"
        override val helplineErsDesc = "National Emergency Number (Police, Ambulance, Fire)"
        override val helplineChild = "Childline / Girls Safety"
        override val helplineChildDesc = "24/7 Child and Girl Child Emergency Helpline"
        override val helplineNcw = "National Commission for Women"
        override val helplineNcwDesc = "24/7 NCW Helpline for Women in Distress"

        override val guardiansHeaderTitle = "Emergency Guardian Network"
        override val guardiansHeaderSubtitle = "Registered guardians will receive immediate SMS emergency alerts and evidence during SOS."
        override val testSmsBtn = "TEST EMERGENCY ALERT SMS NOW"
        override val savedGuardiansTitle = "Saved Guardians"
        override val addNewBtn = "Add New"
        override val noGuardiansTitle = "No Guardians Added Yet"
        override val noGuardiansSubtitle = "Add family members or trusted friends so they receive automatic evidence SMS during emergency."
        override val addFirstGuardianBtn = "+ Add First Guardian"
        override val addGuardianDialogTitle = "Add Emergency Guardian"
        override val guardianNameLabel = "Guardian Name"
        override val guardianPhoneLabel = "Mobile Number (e.g. +91 9876543210)"
        override val guardianRelationLabel = "Relationship (e.g. Mother, Father, Spouse)"
        override val setPrimaryContact = "Set as Primary Contact"
        override val saveGuardianBtn = "Save Guardian"
        override val cancelBtn = "Cancel"
        override val primaryLabel = "PRIMARY"

        override val awpsTitle = "TN All Women Police Stations (AWPS)"
        override val awpsSubtitle = "Search and contact 24/7 AWPS stations across Tamil Nadu"
        override val awpsSearchPlaceholder = "Search by station, district, or pincode..."
        override val awpsFoundCount = "AWPS Station(s) Found"
        override val jurisdictionPrefix = "Jurisdiction: "
        override val callStationBtn = "Call Station"
        override val directionsBtn = "Directions"

        override val aiHeaderTitle = "Sentinel AI Voice Escape & Threat Analyzer"
        override val aiHeaderSubtitle = "Communicate via voice or text. Parameterized keywords analyze real-time threats and provide instant tactical escape routes."
        override val quickScenariosTitle = "Quick Threat Scenarios"
        override val aiInputPlaceholder = "Speak or type e.g. Someone is following me near Chennai central station..."
        override val analyzeBtnText = "ANALYZE THREAT & ESCAPE"
        override val evaluatingText = "Evaluating Threat & Calculating Escape Route..."
        override val dangerScorePrefix = "Threat Level Score: "
        override val threatAnalysisTitle = "Situational Threat Assessment"
        override val tacticalEscapeTitle = "Immediate Tactical Escape Steps"
        override val deescalationTitle = "De-escalation & Defense Advice"
        override val call1091Btn = "CALL 1091"
        override val alertSmsBtn = "ALERT SMS"
        override val voiceInputBtn = "Voice Assistant"
        override val voiceInputListening = "Listening to your voice..."
        override val autoVoiceGuardianBtn = "Auto Voice Record to Guardians"
        override val speakResponseBtn = "Speak Guidance"
        override val muteResponseBtn = "Mute Voice"
        override val parameterizedKeywordsTitle = "Parameterized Threat Detector"
        override val sendOfflineSmsEvidenceAction = "Offline SMS Evidence\nஆஃப்லைன் SMS அனுப்பவும்"
        override val tamilVoiceBadgeText = "தமிழ்ப் பேச்சைக் கவனிக்கிறது"
        override val scenarioStalking = "ஒருவன் என்னை பின்தொடர்கிறான் (Someone is following me)"
        override val scenarioCab = "ஆட்டோ/டாக்ஸி வழி மாறுகிறது (Cab/Auto wrong route)"
        override val scenarioNight = "இருட்டான சாலையில் பயமாக உள்ளது (Isolated dark street)"
        override val scenarioWorkplace = "அலுவலகத்தில் மிரட்டல் (Workplace harassment)"
        override val scenarioCyber = "ஆன்லைன் பிளாக்மெயில் / அச்சுறுத்தல் (Cyber threat)"

        override val screamDetectorTitle = "24/7 Scream & Loud Voice SOS Trigger"
        override val screamDetectorSubtitle = "Detects loud screams or 'HELP' / 'காப்பாத்துங்க' to auto-trigger haptic alarm, incident photo, and prompt 1091 call"
        override val screamListeningActive = "Listening for Scream / HELP / உதவி..."
        override val screamListeningOff = "Tap to Enable Scream SOS Detection"
        override val screamDialogTitle = "🚨 SCREAM / DISTRESS DETECTED!"
        override val screamDialogMessage = "Loud scream or emergency help voice detected! Strong haptic alert triggered and incident photo captured. Call 1091 now?"
        override val captureIncidentPhotoAction = "Capture Incident\nPhoto"
        override val shareEvidenceGuardians = "Share Evidence to Guardians"
        override val grantPermissionsBtn = "Grant Camera & Mic Permissions"
        override val permissionsHeader = "Camera & Mic Permissions Required"
        override val permissionsDesc = "Grant permissions to capture incident camera evidence, record audio, and send emergency SMS to guardians."

        // Age Verification & Helpline Routing
        override val ageDialogTitle = "Age Verification & Helpline Routing"
        override val ageDialogSubtitle = "Select age to automatically route all emergency calls & SOS triggers to the right helpline"
        override val ageBelow18Title = "Below 18 Years (Child / Minor Safety)"
        override val ageBelow18Desc = "Directly routes all emergency calls, panic triggers, and shake alerts to Child Safety Helpline 1098 (24/7 Childline & Minor Protection)"
        override val ageAbove18Title = "18 Years & Above (Adult / Women Safety)"
        override val ageAbove18Desc = "Directly routes all emergency calls, panic triggers, and shake alerts to TN Women Police Helpline 1091 (24/7 Kavalan SOS)"
        override val ageExactLabel = "Exact Age (Years)"
        override val confirmAgeBtn = "Confirm Age & Helpline"
        override val ageModeChildBadge = "👶 <18 Child Safety (1098)"
        override val ageModeWomenBadge = "👩 18+ Women Safety (1091)"
        override val ageModeChildCardTitle = "Child Safety Mode Active (Below 18)"
        override val ageModeChildCardDesc = "All direct calls, double-tap & shake SOS triggers are routed directly to Child Safety Helpline 1098"
        override val ageModeWomenCardTitle = "Women Safety Mode Active (18+)"
        override val ageModeWomenCardDesc = "All direct calls, double-tap & shake SOS triggers are routed directly to Women Police Helpline 1091"
        override val changeAgeBtn = "Change Age"
        override val primaryForYourAgeTag = "PRIMARY HELPLINE FOR YOUR AGE"
        override val callPrimaryActionChild = "Childline\n1098"
        override val callPrimaryActionWomen = "Women Helpline\n1091"

        override val shakeDetectorTitle = "Violent Shake & Motion Emergency Guard"
        override val shakeDetectorSubtitle = "When phone is shaken at maximum violent level, automatically triggers direct 1091 call, siren alarm, offline SMS to guardians, and audio recording"
        override val shakeListeningActive = "Motion / Shake Guard Active"
        override val shakeListeningOff = "Tap to Enable Shake SOS Guard"
        override val shakeDialogTitle = "🚨 VIOLENT SHAKE / MOTION DETECTED!"
        override val shakeDialogMessage = "Maximum violent shake detected! Automated Emergency SOS is active: Direct call to 1091, loud siren sounding, offline SMS dispatched to guardians, and audio evidence recording started."
        override val shakeSensitivityLabel = "Shake Sensitivity Level"
        override val shakeLevelLabel = "Live Shake Intensity Level"
        override val shakeMaximumLevelText = "Maximum Violent Shake"
        override val shakeHighLevelText = "High Shake"

        override val powerButtonTitle = "⚡ Power Button Double-Tap Danger Guard"
        override val powerButtonSubtitle = "Tap the power button 2 times quickly (even with screen locked or in pocket) to automatically make an emergency call, sound piercing buzzer, and record voice evidence."
        override val powerButtonActiveStatus = "Active: Double-Tap Power Button Anytime for SOS"
        override val powerButtonInactiveStatus = "Tap to Enable Power Button Danger Guard"
        override val powerButtonInstruction = "When in danger, press phone's power button 2 times rapidly. Immediate action will automatically trigger without unlocking the phone."
        override val powerButtonTestBtn = "⚡ Simulate Power Button Double-Tap"
        override val powerButtonDialogTitle = "🚨 POWER BUTTON DANGER TRIGGERED!"
        override val powerButtonDialogMessage = "Power button double-tap detected! Emergency actions active: Direct phone call dispatched, emergency siren buzzer sounding, and ambient voice evidence recording."
        override val powerButtonAutoCalling = "Auto Calling Police / Helpline..."
        override val powerButtonBuzzerActive = "Loud Buzzer Siren Sounding"
        override val powerButtonVoiceRecording = "Voice Evidence Recording in Progress..."
        override val powerButtonSmsDispatched = "Guardian SMS Dispatched with Evidence"
        override val powerButtonSimulateDoubleTap = "Test Danger Trigger"

        override val guideHeaderTitle = "Women Tactical Safety Guide"
        override val guideHeaderSubtitle = "Essential moves, transit rules, and legal protections"
        override val tabSelfDefense = "Self-Defense Moves"
        override val tabTravelSafety = "Travel Safety"
        override val tabLegalRights = "Rights & Laws"
        override val targetPrefix = "Target: "
        override val executionStepsTitle = "Key Execution Steps:"
        override val tipPrefix = "Tip: "
    }

    private object TamilStrings : Strings {
        override val appTitle = "சென்டினல் AI"
        override val topAppBarCall = "மகளிர் காவல் 1091"
        override val selectLanguage = "மொழி / Language"
        override val languageDialogTitle = "பயன்பாட்டு மொழியைத் தேர்ந்தெடுக்கவும்"
        override val languageDialogSubtitle = "பாதுகாப்பு எச்சரிக்கைகள் மற்றும் வழிமுறைகளுக்கு உங்கள் விருப்ப மொழியைத் தேர்ந்தெடுக்கவும்"
        override val confirmLanguage = "மொழியை உறுதிப்படுத்துக"
        override val tabSos = "SOS உதவி"
        override val tabAi = "AI உதவி"
        override val tabAwps = "மகளிர் காவல்"
        override val tabGuardians = "பாதுகாவலர்"
        override val tabGuide = "வழிகாட்டி"

        override val sosBannerTag = "சென்டினல் AI டிஜிட்டல் கார்டு"
        override val sosBannerTitle = "சென்டினல் AI பெண்கள் டிஜிட்டல் பாதுகாப்பு கார்டு"
        override val sosBannerSubtitle = "24/7 AI அச்சுறுத்தல் கண்காணிப்பு • 1091 மகளிர் காவல் மற்றும் பாதுகாவலர் எச்சரிக்கை"
        override val sosInstructionText = "1091 ஐ அழைக்கவும், பாதுகாவலர்களை எச்சரிக்கவும், சைரன் மற்றும் ஒலிப்பதிவு செய்ய அழுத்தவும்"
        override val sosButtonDefault = "அவசர உதவிக்கு அழுத்தவும்"
        override val sosButtonActive = "அவசர எச்சரிக்கை அனுப்பப்பட்டது!"
        override val quickControlsTitle = "அவசரக்கால கட்டுப்பாடுகள்"
        override val callPoliceAction = "காவல்துறை\n1091 அழை"
        override val loudSirenAction = "சத்தமான சைரன்\nஎச்சரிக்கை"
        override val stopSirenAction = "சைரன்\nநிறுத்து"
        override val recordAudioAction = "ஒலி ஆதாரங்களை\nபதிவு செய்"
        override val stopAudioAction = "பதிவை\nநிறுத்து"
        override val smsGuardiansAction = "SMS மூலம்\nஎச்சரி"
        override val recordingEvidenceHeader = "ஒலி ஆதாரங்கள் பதிவு செய்யப்படுகின்றன..."
        override val stopBtn = "நிறுத்து"
        override val savedAudioHeader = "சேமிக்கப்பட்ட ஒலி ஆதாரங்கள்"
        override val helplinesTitle = "தமிழ்நாடு மற்றும் தேசிய உதவி எண்கள்"
        override val helplineTnPolice = "தமிழ்நாடு மகளிர் காவல் உதவி எண்"
        override val helplineTnPoliceDesc = "24/7 உடனடி பெண்கள் அவசர பாதுகாப்பு உதவி எண்"
        override val helplineErs = "தேசிய அவசரக்கால பதில் எண் (112)"
        override val helplineErsDesc = "காவல்துறை, ஆம்புலன்ஸ், தீயணைப்பு அவசர எண்"
        override val helplineChild = "குழந்தைகள் மற்றும் பெண்கள் பாதுகாப்பு"
        override val helplineChildDesc = "24/7 குழந்தைகள் மற்றும் சிறுமிகள் பாதுகாப்பு உதவி எண்"
        override val helplineNcw = "தேசிய மகளிர் ஆணையம்"
        override val helplineNcwDesc = "24/7 பெண்கள் பாதுகாப்பு தேசிய ஆணையத்தின் உதவி எண்"

        override val guardiansHeaderTitle = "அவசரகால பாதுகாவலர் பிணையம்"
        override val guardiansHeaderSubtitle = "SOS இன் போது பதிவுசெய்த பாதுகாவலர்கள் அவசரநிலை SMS மற்றும் ஆதாரங்களை உடனடியாக பெறுவார்கள்."
        override val testSmsBtn = "அவசர எச்சரிக்கை SMS ஐ சோதனை செய்"
        override val savedGuardiansTitle = "சேமிக்கப்பட்ட பாதுகாவலர்கள்"
        override val addNewBtn = "+ புதிய பாதுகாவலர்"
        override val noGuardiansTitle = "பாதுகாவலர்கள் இன்னும் சேர்க்கப்படவில்லை"
        override val noGuardiansSubtitle = "குடும்ப உறுப்பினர்கள் அல்லது நம்பகமான நண்பர்களைச் சேர்க்கவும், அவசரக்காலத்தில் தானியங்கி SMS செல்லும்."
        override val addFirstGuardianBtn = "+ முதல் பாதுகாவலரைச் சேர்"
        override val addGuardianDialogTitle = "அவசரகால பாதுகாவலரைச் சேர்"
        override val guardianNameLabel = "பாதுகாவலர் பெயர்"
        override val guardianPhoneLabel = "கைபேசி எண் (எ.கா. +91 9876543210)"
        override val guardianRelationLabel = "உறவுமுறை (எ.கா. தாய், தந்தை, கணவர்)"
        override val setPrimaryContact = "முதன்மை தொடர்பாக அமை"
        override val saveGuardianBtn = "பாதுகாவலரை சேமிக்கவும்"
        override val cancelBtn = "ரத்துசெய்"
        override val primaryLabel = "முதன்மை"

        override val awpsTitle = "தமிழ்நாடு அனைத்து மகளிர் காவல் நிலையங்கள் (AWPS)"
        override val awpsSubtitle = "தமிழ்நாடு முழுவதும் உள்ள 24/7 மகளிர் காவல் நிலையங்களைத் தொடர்புகொள்ளவும்"
        override val awpsSearchPlaceholder = "காவல் நிலையம், மாவட்டம் அல்லது பின்கோட் மூலம் தேடுக..."
        override val awpsFoundCount = "மகளிர் காவல் நிலையங்கள் கண்டறியப்பட்டன"
        override val jurisdictionPrefix = "எல்லை / பகுதி: "
        override val callStationBtn = "நிலையத்திற்கு அழை"
        override val directionsBtn = "வழித்தடம்"

        override val aiHeaderTitle = "சென்டினல் AI குரல் உதவி & தப்பிக்கும் வழி"
        override val aiHeaderSubtitle = "குரல் அல்லது தட்டச்சு மூலம் தொடர்பு கொள்ளுங்கள். சிறப்பு அச்சுறுத்தல் சொற்கள் மூலம் ஆபத்து உடனடியாக பகுப்பாய்வு செய்யப்பட்டு தப்பிக்கும் வழி சொல்லப்படும்."
        override val quickScenariosTitle = "விரைவு அச்சுறுத்தல் சூழ்நிலைகள்"
        override val aiInputPlaceholder = "பேசவும் அல்லது தட்டச்சு செய்யவும்: எ.கா. சென்னை சென்ட்ரல் அருகே ஒருவன் பின்தொடர்கிறான்..."
        override val analyzeBtnText = "ஆபத்தை ஆராய்ந்து தப்பிக்கும் வழியைப் பெறுக"
        override val evaluatingText = "ஆபத்து நிலை ஆராயப்படுகிறது..."
        override val dangerScorePrefix = "ஆபத்து அளவு: "
        override val threatAnalysisTitle = "அச்சுறுத்தல் பகுப்பாய்வு"
        override val tacticalEscapeTitle = "உடனடி தப்பிக்கும் வழிமுறைகள்"
        override val deescalationTitle = "தற்காப்பு மற்றும் தப்பிக்கும் ஆலோசனைகள்"
        override val call1091Btn = "1091 ஐ அழை"
        override val alertSmsBtn = "SMS அனுப்பு"
        override val voiceInputBtn = "குரல் உதவி"
        override val voiceInputListening = "உங்கள் குரலைக் கவனிக்கிறது..."
        override val autoVoiceGuardianBtn = "குரல் பதிவு செய்து பாதுகாவலருக்கு அனுப்பு"
        override val speakResponseBtn = "வழிகாட்டலை பேசு"
        override val muteResponseBtn = "குரலை நிறுத்து"
        override val parameterizedKeywordsTitle = "சிறப்பு அச்சுறுத்தல் கணிப்பான்"
        override val sendOfflineSmsEvidenceAction = "ஆஃப்லைன் SMS ஆதாரங்கள்\nபாதுகாவலர்களுக்கு அனுப்பு"
        override val tamilVoiceBadgeText = "தமிழ் குரல் வழிகாட்டி இயங்குகிறது"
        override val scenarioStalking = "ஒருவன் என்னை பின்தொடர்கிறான்"
        override val scenarioCab = "ஆட்டோ/டாக்ஸி தவறான பாதை செல்கிறது"
        override val scenarioNight = "இருட்டான தெருவில் தனியாக பயமாக உள்ளது"
        override val scenarioWorkplace = "அலுவலகத்தில் மிரட்டல் / துன்புறுத்தல்"
        override val scenarioCyber = "ஆன்லைன் பிளாக்மெயில் மற்றும் போலி கணக்கு"

        override val screamDetectorTitle = "24/7 அலறல் மற்றும் அவசரக் குரல் SOS தூண்டி"
        override val screamDetectorSubtitle = "உரத்த அலறல் அல்லது 'HELP' / 'காப்பாத்துங்க' குரல் ஒலித்தால் அதிர்வு எச்சரிக்கை, கேமரா படம் மற்றும் 1091 அழைப்பு தானாகத் தோன்றும்"
        override val screamListeningActive = "அலறல் / HELP / உதவி குரல் கவனிக்கப்படுகிறது..."
        override val screamListeningOff = "அலறல் SOS கணிப்பை இயக்க அழுத்தவும்"
        override val screamDialogTitle = "🚨 உரத்த அலறல் / அவசர உதவி கண்டறியப்பட்டது!"
        override val screamDialogMessage = "உரத்த அலறல் அல்லது அவசர உதவி குரல் கண்டறியப்பட்டது! அதிர்வு எச்சரிக்கை தூண்டப்பட்டு கேமரா படம் எடுக்கப்பட்டது. 1091 ஐ அழைக்கவா?"
        override val captureIncidentPhotoAction = "சம்பவ படம்\nஎடு"
        override val shareEvidenceGuardians = "ஆதாரங்களை பாதுகாவலர்களுக்கு அனுப்பு"
        override val grantPermissionsBtn = "கேமரா & மைக் அனுமதிகளை வழங்கு"
        override val permissionsHeader = "கேமரா மற்றும் மைக் அனுமதிகள் தேவை"
        override val permissionsDesc = "சம்பவ படங்கள் எடுக்கவும், குரல் பதிவு செய்யவும், பாதுகாவலர்களுக்கு அவசர SMS அனுப்பவும் அனுமதிகளை வழங்கவும்."

        // Age Verification & Helpline Routing
        override val ageDialogTitle = "வயது சரிபார்ப்பு & உதவி எண் தேர்வு"
        override val ageDialogSubtitle = "அவசர அழைப்புகள் மற்றும் SOS தூண்டல்களை சரியான உதவி எண்ணுக்கு இணைக்க உங்கள் வயதைத் தேர்வுசெய்யவும்"
        override val ageBelow18Title = "18 வயதுக்குட்பட்டோர் (குழந்தைகள் / சிறுமிகள் பாதுகாப்பு)"
        override val ageBelow18Desc = "அனைத்து அவசர அழைப்புகள், பவர் பட்டன் மற்றும் அதிர்வு SOS எச்சரிக்கைகள் நேரடியாக குழந்தைகள் உதவி எண் 1098-க்கு இணைக்கப்படும் (24/7 Childline)"
        override val ageAbove18Title = "18 வயது மற்றும் அதற்கு மேற்பட்டோர் (பெண்கள் பாதுகாப்பு)"
        override val ageAbove18Desc = "அனைத்து அவசர அழைப்புகள், பவர் பட்டன் மற்றும் அதிர்வு SOS எச்சரிக்கைகள் நேரடியாக தமிழ்நாடு பெண்கள் உதவி எண் 1091-க்கு இணைக்கப்படும்"
        override val ageExactLabel = "சரியான வயது (வருடங்கள்)"
        override val confirmAgeBtn = "வயதை உறுதிப்படுத்துக"
        override val ageModeChildBadge = "👶 <18 குழந்தைகள் உதவி (1098)"
        override val ageModeWomenBadge = "👩 18+ பெண்கள் உதவி (1091)"
        override val ageModeChildCardTitle = "குழந்தைகள் பாதுகாப்பு முறை செயலில் உள்ளது (<18)"
        override val ageModeChildCardDesc = "அனைத்து நேரடி அழைப்புகள் மற்றும் SOS தூண்டல்கள் குழந்தைகள் உதவி எண் 1098-க்கு அனுப்பப்படும்"
        override val ageModeWomenCardTitle = "பெண்கள் பாதுகாப்பு முறை செயலில் உள்ளது (18+)"
        override val ageModeWomenCardDesc = "அனைத்து நேரடி அழைப்புகள் மற்றும் SOS தூண்டல்கள் பெண்கள் உதவி எண் 1091-க்கு அனுப்பப்படும்"
        override val changeAgeBtn = "வயதை மாற்று"
        override val primaryForYourAgeTag = "உங்கள் வயதுக்கான முதன்மை உதவி எண்"
        override val callPrimaryActionChild = "குழந்தைகள் உதவி\n1098"
        override val callPrimaryActionWomen = "மகளிர் காவல்\n1091"

        override val shakeDetectorTitle = "அதிவேக அதிர்வு & இயக்க அவசர பாதுகாப்பு"
        override val shakeDetectorSubtitle = "தொலைபேசி அதிகபட்ச தீவிரமாக குலுக்கப்பட்டால், தானாகவே 1091 மகளிர் காவல் அழைப்பு, அலாரம் ஒலி, பாதுகாவலர்களுக்கு ஆஃப்லைன் SMS மற்றும் ஆடியோ பதிவு தொடங்கும்"
        override val shakeListeningActive = "இயக்க / அதிர்வு பாதுகாப்பு செயலில் உள்ளது"
        override val shakeListeningOff = "அதிர்வு SOS பாதுகாப்பை இயக்க அழுத்தவும்"
        override val shakeDialogTitle = "🚨 தீவிர அதிர்வு / ஆபத்து இயக்கம் கண்டறியப்பட்டது!"
        override val shakeDialogMessage = "அதிகபட்ச தீவிர அதிர்வு கண்டறியப்பட்டது! தானியங்கி அவசர SOS செயல்படுகிறது: 1091 நேரடி அழைப்பு, பலத்த அலாரம் ஒலி, பாதுகாவலர்களுக்கு ஆஃப்லைன் SMS மற்றும் ஆடியோ ஆதாரப் பதிவு தொடங்கப்பட்டுள்ளது."
        override val shakeSensitivityLabel = "அதிர்வு உணர்திறன் நிலை"
        override val shakeLevelLabel = "நேரலை அதிர்வு தீவிரம்"
        override val shakeMaximumLevelText = "அதிகபட்ச தீவிர அதிர்வு"
        override val shakeHighLevelText = "அதிக அதிர்வு"

        override val powerButtonTitle = "⚡ பவர் பட்டன் இரட்டைத் தட்டு அவசரப் பாதுகாப்பு"
        override val powerButtonSubtitle = "ஆபத்தில் இருக்கும்போது பவர் பட்டனை 2 முறை வேகமாகத் தட்டினால் (போன் லாக் செய்யப்பட்டிருந்தாலும்) தானாகவே அவசர அழைப்பு செல்லும், பலத்த அலாரம் ஒலிக்கும் மற்றும் குரல் பதிவு செய்யப்படும்."
        override val powerButtonActiveStatus = "செயலில் உள்ளது: பவர் பட்டனை 2 முறை தட்டி SOS ஐப் பெறவும்"
        override val powerButtonInactiveStatus = "பவர் பட்டன் அவசரப் பாதுகாப்பை இயக்க அழுத்தவும்"
        override val powerButtonInstruction = "ஆபத்தில் இருக்கும்போது உங்கள் போனின் பவர் பட்டனை 2 முறை வேகமாக அழுத்தவும். போனை அன்லாக் செய்யாமலேயே உடனடி உதவி செயல்படும்."
        override val powerButtonTestBtn = "⚡ பவர் பட்டன் இரட்டைத் தட்டைச் சோதிக்கவும்"
        override val powerButtonDialogTitle = "🚨 பவர் பட்டன் அவசர உதவி தூண்டப்பட்டது!"
        override val powerButtonDialogMessage = "பவர் பட்டன் இரட்டைத் தட்டு கண்டறியப்பட்டது! அவசர நடவடிக்கைகள் இயங்குகின்றன: மகளிர் காவல் அழைப்பு, அலாரம் ஒலி மற்றும் சுற்றுப்புறக் குரல் பதிவு செய்யப்படுகிறது."
        override val powerButtonAutoCalling = "காவல்துறை / உதவி எண்ணுக்கு அழைக்கிறது..."
        override val powerButtonBuzzerActive = "பலத்த அலாரம் சைரன் ஒலிக்கிறது"
        override val powerButtonVoiceRecording = "குரல் ஆதாரங்கள் பதிவு செய்யப்படுகின்றன..."
        override val powerButtonSmsDispatched = "பாதுகாவலர்களுக்கு SMS அனுப்பப்பட்டது"
        override val powerButtonSimulateDoubleTap = "சோதனை செய்"

        override val guideHeaderTitle = "பெண்கள் பாதுகாப்பு மற்றும் உரிமை வழிகாட்டி"
        override val guideHeaderSubtitle = "முக்கிய தற்காப்பு முறைகள், பயண விதிகள் மற்றும் சட்ட உரிமைகள்"
        override val tabSelfDefense = "தற்காப்பு முறைகள்"
        override val tabTravelSafety = "பயண பாதுகாப்பு"
        override val tabLegalRights = "சட்ட உரிமைகள்"
        override val targetPrefix = "இலக்கு: "
        override val executionStepsTitle = "முக்கிய வழிமுறைகள்:"
        override val tipPrefix = "குறிப்பு: "
    }
}
