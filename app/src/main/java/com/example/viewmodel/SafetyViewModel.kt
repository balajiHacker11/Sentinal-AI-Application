package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import com.example.ai.GeminiThreatAssistant
import com.example.ai.KeywordThreatAnalyzer
import com.example.ai.ParameterizedAnalysisResult
import com.example.ai.ThreatAnalysisResult
import com.example.ai.ThreatLevel
import com.example.data.db.AppDatabase
import com.example.data.db.AudioRecordingEntity
import com.example.data.db.GuardianEntity
import com.example.data.db.IncidentEvidenceEntity
import com.example.data.model.AppLanguage
import com.example.data.model.PoliceStation
import com.example.data.model.PoliceStationProvider
import com.example.service.AudioRecorder
import com.example.service.CameraCaptureManager
import com.example.service.PowerButtonEmergencyDetector
import com.example.service.PowerButtonSosService
import com.example.service.ScreamDetector
import com.example.service.ShakeMotionDetector
import com.example.service.ShakeSensitivity
import com.example.service.SirenPlayer
import com.example.service.SosManager
import com.example.service.SpeechToTextManager
import com.example.service.TextToSpeechManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File

class SafetyViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val guardianDao = db.guardianDao()
    private val audioDao = db.audioRecordingDao()
    private val evidenceDao = db.evidenceDao()

    private val prefs = application.getSharedPreferences("tn_safety_prefs", Context.MODE_PRIVATE)

    // Language State
    private val initialLangCode = prefs.getString("selected_language", null)
    private val _currentLanguage = MutableStateFlow(
        if (initialLangCode == "ta") AppLanguage.TAMIL else AppLanguage.ENGLISH
    )
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    private val _showLanguageDialog = MutableStateFlow(initialLangCode == null)
    val showLanguageDialog: StateFlow<Boolean> = _showLanguageDialog.asStateFlow()

    // Age Verification & Emergency Helpline Routing State
    private val initialAge = prefs.getInt("user_age", -1)
    private val initialIsBelow18 = prefs.getBoolean("is_below_18", false)
    private val hasAgeBeenSet = prefs.contains("is_below_18")

    private val _userAge = MutableStateFlow<Int?>(if (initialAge > 0) initialAge else null)
    val userAge: StateFlow<Int?> = _userAge.asStateFlow()

    private val _isBelow18 = MutableStateFlow(initialIsBelow18)
    val isBelow18: StateFlow<Boolean> = _isBelow18.asStateFlow()

    private val _showAgeDialog = MutableStateFlow(!hasAgeBeenSet && initialLangCode != null)
    val showAgeDialog: StateFlow<Boolean> = _showAgeDialog.asStateFlow()

    // Dynamic Under 15 Check:
    // Under 15 (< 15) -> 1098 (Childline / Child Safety Helpline)
    // 15 & Above (>= 15) -> 1091 (Women Police / Kavalan SOS Helpline)
    val isUnder15: StateFlow<Boolean> = combine(_userAge, _isBelow18) { age, isMinor ->
        if (age != null) age < 15 else isMinor
    }.stateIn(viewModelScope, SharingStarted.Eagerly, (initialAge in 1..14) || (initialAge == -1 && initialIsBelow18))

    val primaryEmergencyNumber: StateFlow<String> = isUnder15.map { under15 ->
        if (under15) "1098" else "1091"
    }.stateIn(viewModelScope, SharingStarted.Eagerly, if ((initialAge in 1..14) || (initialAge == -1 && initialIsBelow18)) "1098" else "1091")

    val primaryHelplineName: StateFlow<String> = isUnder15.map { under15 ->
        if (under15) "Child Safety 1098" else "Women Helpline 1091"
    }.stateIn(viewModelScope, SharingStarted.Eagerly, if ((initialAge in 1..14) || (initialAge == -1 && initialIsBelow18)) "Child Safety 1098" else "Women Helpline 1091")

    fun selectLanguage(language: AppLanguage) {
        _currentLanguage.value = language
        prefs.edit().putString("selected_language", language.code).apply()
        _showLanguageDialog.value = false
        // If age has not been set yet, show age selection right after language selection
        if (!prefs.contains("is_below_18")) {
            _showAgeDialog.value = true
        }
        val msg = if (language == AppLanguage.TAMIL) "மொழி தமிழுக்கு மாற்றப்பட்டது ✅" else "Language set to English ✅"
        showNotice(msg)
    }

    fun openLanguageSelection() {
        _showLanguageDialog.value = true
    }

    fun closeLanguageSelection() {
        _showLanguageDialog.value = false
    }

    fun setAgeCategory(isMinor: Boolean, exactAge: Int? = null) {
        val ageVal = exactAge ?: if (isMinor) 14 else 22
        val under15 = ageVal < 15
        _isBelow18.value = isMinor
        _userAge.value = ageVal
        _powerButtonEmergencyContact.value = if (under15) "1098" else "1091"

        prefs.edit()
            .putBoolean("is_below_18", isMinor)
            .putInt("user_age", ageVal)
            .putBoolean("is_under_15", under15)
            .apply()

        _showAgeDialog.value = false
        val helplineNum = if (under15) "1098 (Childline / Child Safety Helpline)" else "1091 (Women Safety Helpline)"
        val notice = if (_currentLanguage.value == AppLanguage.TAMIL) {
            if (under15) "15 வயதுக்கு கீழ்: குழந்தை பாதுகாப்பு முறை செயலில் உள்ளது (1098)" else "பெண்கள் பாதுகாப்பு முறை செயலில் உள்ளது (1091)"
        } else {
            "Emergency calls routed directly to $helplineNum"
        }
        showNotice(notice)
    }

    fun openAgeSelection() {
        _showAgeDialog.value = true
    }

    fun closeAgeSelection() {
        _showAgeDialog.value = false
    }

    val sosManager = SosManager(application)
    val sirenPlayer = SirenPlayer(application)
    val audioRecorder = AudioRecorder(application)
    val geminiAssistant = GeminiThreatAssistant()
    val screamDetector = ScreamDetector(application)
    val shakeMotionDetector = ShakeMotionDetector(application)
    val cameraCaptureManager = CameraCaptureManager(application)
    val speechToTextManager = SpeechToTextManager(application)
    val textToSpeechManager = TextToSpeechManager(application)
    val powerButtonDetector = PowerButtonEmergencyDetector(application)

    // Speech & Voice State
    val isVoiceListening: StateFlow<Boolean> = speechToTextManager.isListening
    val isSpeakingTts: StateFlow<Boolean> = textToSpeechManager.isSpeaking

    // Power Button Double-Tap Danger State
    val isPowerButtonGuardActive: StateFlow<Boolean> = powerButtonDetector.isListening
    val powerButtonTapCount: StateFlow<Int> = powerButtonDetector.currentTapCount

    private val _showPowerButtonDangerDialog = MutableStateFlow(false)
    val showPowerButtonDangerDialog: StateFlow<Boolean> = _showPowerButtonDangerDialog.asStateFlow()

    private val _powerButtonEmergencyContact = MutableStateFlow("1091")
    val powerButtonEmergencyContact: StateFlow<String> = _powerButtonEmergencyContact.asStateFlow()

    // Parameterized Threat Analysis State
    private val _parameterizedResult = MutableStateFlow<ParameterizedAnalysisResult?>(null)
    val parameterizedResult: StateFlow<ParameterizedAnalysisResult?> = _parameterizedResult.asStateFlow()

    // Database flows
    val guardiansList: StateFlow<List<GuardianEntity>> = guardianDao.getAllGuardians()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val audioRecordingsList: StateFlow<List<AudioRecordingEntity>> = audioDao.getAllRecordings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val incidentEvidencesList: StateFlow<List<IncidentEvidenceEntity>> = evidenceDao.getAllEvidences()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Scream detection & alert dialog state
    private val _isScreamListening = MutableStateFlow(false)
    val isScreamListening: StateFlow<Boolean> = _isScreamListening.asStateFlow()

    private val _showScreamAlertDialog = MutableStateFlow(false)
    val showScreamAlertDialog: StateFlow<Boolean> = _showScreamAlertDialog.asStateFlow()

    // Shake & Motion detection state
    val isShakeListening: StateFlow<Boolean> = shakeMotionDetector.isListening
    val shakeGForce: StateFlow<Float> = shakeMotionDetector.currentGForce
    val shakeMotionPercent: StateFlow<Float> = shakeMotionDetector.currentMotionPercent
    val shakeSensitivity: StateFlow<ShakeSensitivity> = shakeMotionDetector.sensitivity

    private val _showShakeAlertDialog = MutableStateFlow(false)
    val showShakeAlertDialog: StateFlow<Boolean> = _showShakeAlertDialog.asStateFlow()

    init {
        screamDetector.setScreamListener {
            onScreamOrDistressDetected()
        }

        shakeMotionDetector.setShakeListener {
            onShakeEmergencyDetected()
        }

        val isGuardEnabled = prefs.getBoolean("power_button_guard_enabled", true)
        val isShakeEnabled = prefs.getBoolean("shake_guard_enabled", true)
        val isScreamEnabled = prefs.getBoolean("scream_guard_enabled", true)
        val triggerOnOff = prefs.getBoolean("power_button_trigger_on_off", true)

        powerButtonDetector.setTriggerOnPowerOff(triggerOnOff)
        powerButtonDetector.setDangerListener { reason ->
            onPowerButtonDangerDetected(reason)
        }

        if (isGuardEnabled) {
            powerButtonDetector.startListening()
        }
        if (isShakeEnabled) {
            shakeMotionDetector.startListening()
        }
        if (isScreamEnabled && isAudioPermissionGranted()) {
            screamDetector.startListening()
            _isScreamListening.value = true
        }

        if (isGuardEnabled || isShakeEnabled || isScreamEnabled) {
            try {
                PowerButtonSosService.start(application)
            } catch (_: Exception) {}
        }
    }

    private fun isAudioPermissionGranted(): Boolean {
        return androidx.core.content.ContextCompat.checkSelfPermission(
            getApplication(),
            android.Manifest.permission.RECORD_AUDIO
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
    }

    // Siren state
    private val _isSirenActive = MutableStateFlow(false)
    val isSirenActive: StateFlow<Boolean> = _isSirenActive.asStateFlow()

    // Audio recording state
    private val _isRecordingAudio = MutableStateFlow(false)
    val isRecordingAudio: StateFlow<Boolean> = _isRecordingAudio.asStateFlow()

    private val _recordingTimerSeconds = MutableStateFlow(0)
    val recordingTimerSeconds: StateFlow<Int> = _recordingTimerSeconds.asStateFlow()

    private var recordingTimerJob: Job? = null

    // Audio playback state
    private val _playingAudioPath = MutableStateFlow<String?>(null)
    val playingAudioPath: StateFlow<String?> = _playingAudioPath.asStateFlow()

    // Police stations filter
    private val _stationSearchQuery = MutableStateFlow("")
    val stationSearchQuery: StateFlow<String> = _stationSearchQuery.asStateFlow()

    private val _selectedDistrict = MutableStateFlow("All Districts")
    val selectedDistrict: StateFlow<String> = _selectedDistrict.asStateFlow()

    val filteredPoliceStations: StateFlow<List<PoliceStation>> = combine(
        _stationSearchQuery,
        _selectedDistrict
    ) { query, district ->
        PoliceStationProvider.tnPoliceStations.filter { station ->
            val matchesDistrict = district == "All Districts" || station.district.equals(district, ignoreCase = true)
            val matchesQuery = query.isBlank() ||
                    station.name.contains(query, ignoreCase = true) ||
                    station.district.contains(query, ignoreCase = true) ||
                    station.address.contains(query, ignoreCase = true) ||
                    station.pincode.contains(query)
            matchesDistrict && matchesQuery
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        PoliceStationProvider.tnPoliceStations
    )

    // AI Threat Assistant state
    private val _threatPrompt = MutableStateFlow("")
    val threatPrompt: StateFlow<String> = _threatPrompt.asStateFlow()

    private val _isEvaluatingThreat = MutableStateFlow(false)
    val isEvaluatingThreat: StateFlow<Boolean> = _isEvaluatingThreat.asStateFlow()

    private val _threatResult = MutableStateFlow<ThreatAnalysisResult?>(null)
    val threatResult: StateFlow<ThreatAnalysisResult?> = _threatResult.asStateFlow()

    // User notice state
    private val _userNotice = MutableStateFlow<String?>(null)
    val userNotice: StateFlow<String?> = _userNotice.asStateFlow()

    fun showNotice(message: String) {
        _userNotice.value = message
    }

    fun clearNotice() {
        _userNotice.value = null
    }

    // --- SOS ACTIONS ---

    fun triggerFullMasterSosAlert() {
        val targetPhone = primaryEmergencyNumber.value
        val helplineDesc = if (_isBelow18.value) "Child Safety Helpline ($targetPhone)" else "Women Helpline ($targetPhone)"

        // 1. Direct call target helpline based on age (1098 for <18, 1091 for 18+)
        sosManager.triggerDirectCall(targetPhone)

        // 2. Automatically send SMS emergency alert to guardians
        viewModelScope.launch {
            val guardians = guardiansList.value
            val result = sosManager.sendEmergencySmsToGuardians(guardians)
            showNotice("🚨 FULL SOS ACTIVATED!\n• Direct call to $helplineDesc initiated\n• $result\n• Siren sounding\n• Audio evidence recording started")
        }

        // 3. Automatically enable loud buzzer alarm if not already active
        if (!_isSirenActive.value) {
            sirenPlayer.startSiren()
            _isSirenActive.value = true
        }

        // 4. Automatically enable audio evidence recording if not already active
        if (!_isRecordingAudio.value) {
            startAudioEvidenceRecording()
        }
    }

    fun triggerEmergencyCall(phone: String? = null) {
        val target = phone ?: primaryEmergencyNumber.value
        val label = when (target) {
            "1098" -> "Child Safety Helpline (1098)"
            "1091" -> "Women Helpline (1091)"
            else -> target
        }
        sosManager.triggerDirectCall(target)
        showNotice("Initiating direct call to $label")
    }

    fun sendSosSmsToGuardians() {
        viewModelScope.launch {
            val guardians = guardiansList.value
            val result = sosManager.sendEmergencySmsToGuardians(guardians)
            showNotice(result)
        }
    }

    fun sendOfflineSmsWithEvidence() {
        viewModelScope.launch {
            val guardians = guardiansList.value
            val latestPhoto = incidentEvidencesList.value.firstOrNull { it.mediaType == "PHOTO" }?.title
                ?: incidentEvidencesList.value.firstOrNull()?.title
            val latestAudio = audioRecordingsList.value.firstOrNull()?.title
            val result = sosManager.sendOfflineEvidenceSmsToGuardians(guardians, latestPhoto, latestAudio)
            showNotice(result)
        }
    }

    fun toggleSiren() {
        if (_isSirenActive.value) {
            sirenPlayer.stopSiren()
            _isSirenActive.value = false
            showNotice("Loud Siren Stopped")
        } else {
            sirenPlayer.startSiren()
            _isSirenActive.value = true
            showNotice("🚨 LOUD SIREN ACTIVATED!")
        }
    }

    // --- AUDIO RECORDING ACTIONS ---

    fun toggleAudioRecording() {
        if (_isRecordingAudio.value) {
            stopAudioEvidenceRecording()
        } else {
            startAudioEvidenceRecording()
        }
    }

    private fun startAudioEvidenceRecording() {
        val file = audioRecorder.startRecording()
        if (file != null) {
            _isRecordingAudio.value = true
            _recordingTimerSeconds.value = 0
            showNotice("🎙️ Audio Evidence Recording Started...")

            recordingTimerJob = viewModelScope.launch {
                while (_isRecordingAudio.value) {
                    delay(1000)
                    _recordingTimerSeconds.value += 1
                }
            }
        } else {
            showNotice("Unable to access microphone for recording")
        }
    }

    fun stopAudioEvidenceRecording(): File? {
        recordingTimerJob?.cancel()
        recordingTimerJob = null

        val duration = _recordingTimerSeconds.value
        val file: File? = audioRecorder.stopRecording()
        _isRecordingAudio.value = false

        if (file != null && file.exists()) {
            val count = audioRecordingsList.value.size + 1
            val entity = AudioRecordingEntity(
                title = "Evidence Audio #$count",
                filePath = file.absolutePath,
                durationSeconds = duration.coerceAtLeast(1)
            )
            viewModelScope.launch {
                audioDao.insertRecording(entity)
                showNotice("✅ Evidence Audio saved ($duration sec)")
            }
        } else {
            showNotice("Audio recording stopped")
        }
        return file
    }

    fun playRecording(recording: AudioRecordingEntity) {
        if (_playingAudioPath.value == recording.filePath) {
            audioRecorder.stopPlayback()
            _playingAudioPath.value = null
        } else {
            _playingAudioPath.value = recording.filePath
            audioRecorder.playAudio(recording.filePath) {
                _playingAudioPath.value = null
            }
        }
    }

    fun deleteRecording(recording: AudioRecordingEntity) {
        viewModelScope.launch {
            try {
                val file = File(recording.filePath)
                if (file.exists()) file.delete()
            } catch (e: Exception) {
                // Ignore file delete errors
            }
            audioDao.deleteRecording(recording)
            showNotice("Deleted recording")
        }
    }

    // --- GUARDIANS ACTIONS ---

    fun addGuardian(name: String, phone: String, relationship: String, isPrimary: Boolean) {
        if (name.isBlank() || phone.isBlank()) {
            showNotice("Please provide both name and phone number")
            return
        }

        viewModelScope.launch {
            val entity = GuardianEntity(
                name = name.trim(),
                phone = phone.trim(),
                relationship = if (relationship.isBlank()) "Family" else relationship.trim(),
                isPrimary = isPrimary
            )
            guardianDao.insertGuardian(entity)
            showNotice("Saved guardian contact: ${entity.name}")
        }
    }

    fun deleteGuardian(guardian: GuardianEntity) {
        viewModelScope.launch {
            guardianDao.deleteGuardian(guardian)
            showNotice("Removed ${guardian.name}")
        }
    }

    // --- POLICE STATIONS FILTERS ---

    fun updateStationSearch(query: String) {
        _stationSearchQuery.value = query
    }

    fun selectDistrict(district: String) {
        _selectedDistrict.value = district
    }

    // --- AI THREAT ASSISTANT ---

    fun updateThreatPrompt(text: String) {
        _threatPrompt.value = text
    }

    fun startVoiceInput() {
        textToSpeechManager.stop()
        speechToTextManager.startListening(_currentLanguage.value) { spokenText ->
            _threatPrompt.value = spokenText
            showNotice("🎙️ Heard: \"$spokenText\"")
            evaluateThreat(spokenText)
        }
    }

    fun stopVoiceInput() {
        speechToTextManager.stopListening()
    }

    fun speakTacticalGuidance(text: String) {
        textToSpeechManager.speak(text, _currentLanguage.value)
    }

    fun speakThreatResult(result: ThreatAnalysisResult? = _threatResult.value) {
        if (result == null) return
        val isTa = _currentLanguage.value == AppLanguage.TAMIL || result.summary.any { it.code in 0x0B80..0x0BFF }
        val fullSpeechText = textToSpeechManager.buildSolutionSpeechScript(result, isTamil = isTa)
        textToSpeechManager.speak(fullSpeechText, if (isTa) AppLanguage.TAMIL else AppLanguage.ENGLISH)
    }

    fun toggleSpeakThreatResult() {
        if (isSpeakingTts.value) {
            stopTtsSpeech()
        } else {
            speakThreatResult()
        }
    }

    fun stopTtsSpeech() {
        textToSpeechManager.stop()
    }

    fun evaluateThreat(scenarioOverride: String? = null) {
        val prompt = scenarioOverride ?: _threatPrompt.value
        if (prompt.isBlank()) {
            showNotice("Please speak or type your current situation, or select a quick scenario.")
            return
        }

        _isEvaluatingThreat.value = true
        _threatPrompt.value = prompt

        val isTa = _currentLanguage.value == AppLanguage.TAMIL
        val isMinor = _isBelow18.value
        val paramResult = KeywordThreatAnalyzer.analyze(prompt, isTamil = isTa, isBelow18 = isMinor)
        _parameterizedResult.value = paramResult

        if (paramResult.isExtremeEmergency) {
            screamDetector.triggerHapticFeedback()
        }

        viewModelScope.launch {
            val geminiResult = geminiAssistant.evaluateAttackThreat(prompt, isTamil = isTa, isBelow18 = isMinor)
            _threatResult.value = geminiResult
            _isEvaluatingThreat.value = false

            // Auto-speak all solution tips and tactical escape route with flow
            speakThreatResult(geminiResult)
        }
    }

    fun autoRecordVoiceAlertToGuardians(context: Context) {
        if (_isRecordingAudio.value) {
            stopAudioEvidenceRecording()
            showNotice("Voice recording stopped.")
            return
        }

        startAudioEvidenceRecording()
        showNotice("🎙️ Recording 10-sec Voice Alert for Guardians...")

        viewModelScope.launch {
            delay(10000) // Record 10 seconds
            if (_isRecordingAudio.value) {
                val recordedFile = stopAudioEvidenceRecording()
                val guardians = guardiansList.value
                val smsResult = sosManager.sendEmergencySmsToGuardians(
                    guardians,
                    customMessage = "🚨 AUTOMATIC VOICE EMERGENCY ALERT DETECTED!"
                )

                if (recordedFile != null && recordedFile.exists()) {
                    shareEvidenceToGuardians(context, recordedFile.absolutePath)
                } else {
                    showNotice("🚨 Emergency SMS sent to guardians: $smsResult")
                }
            }
        }
    }

    // --- SCREAM DETECTOR & INCIDENT PHOTO ACTIONS ---

    fun toggleScreamDetection() {
        if (_isScreamListening.value) {
            screamDetector.stopListening()
            _isScreamListening.value = false
            prefs.edit().putBoolean("scream_guard_enabled", false).apply()
            PowerButtonSosService.updateConfig(getApplication())
            showNotice("Scream Detector Paused")
        } else {
            screamDetector.startListening()
            _isScreamListening.value = true
            prefs.edit().putBoolean("scream_guard_enabled", true).apply()
            PowerButtonSosService.start(getApplication())
            PowerButtonSosService.updateConfig(getApplication())
            showNotice("🎙️ 24/7 Scream & HELP Voice Detector Active (Runs Even When Closed)!")
        }
    }

    fun onScreamOrDistressDetected() {
        val under15 = isUnder15.value
        val contactToCall = if (under15) "1098" else primaryEmergencyNumber.value.ifBlank { "1091" }
        val helplineLabel = if (under15) "Child Safety 1098" else "Women Helpline $contactToCall"

        // 1. Trigger haptic vibration feedback
        screamDetector.triggerHapticFeedback()

        // 2. Direct emergency call (1098 for <15, 1091 for 15+)
        sosManager.triggerDirectCall(contactToCall)

        // 3. Blast loud emergency police siren alarm
        if (!_isSirenActive.value) {
            sirenPlayer.startSiren()
            _isSirenActive.value = true
        }

        // 4. Automatically capture instant camera photo evidence
        val photoFile = cameraCaptureManager.captureIncidentPhoto("Distress Scream Detected")
        if (photoFile != null) {
            val entity = IncidentEvidenceEntity(
                title = "Scream Triggered Incident Photo",
                mediaType = "PHOTO",
                filePath = photoFile.absolutePath
            )
            viewModelScope.launch {
                evidenceDao.insertEvidence(entity)
            }
        }

        // 5. Start audio recording if not already active
        if (!_isRecordingAudio.value) {
            startAudioEvidenceRecording()
        }

        // 6. Send emergency SMS with GPS coordinates to guardians
        viewModelScope.launch {
            val guardians = guardiansList.value
            val alertMsg = if (under15) {
                "🚨 EMERGENCY CHILD SAFETY ALERT (<15 Age)!\nChild in danger! Distress scream/voice detected.\nDirect call to Childline 1098 dispatched. Urgent assistance needed!"
            } else {
                "🚨 EMERGENCY DANGER SOS ALERT!\nUrgent danger! Distress scream/voice detected.\nDirect call to Women Helpline 1091 dispatched. Urgent assistance needed!"
            }
            val smsResult = sosManager.sendEmergencySmsToGuardians(guardians, customMessage = alertMsg)
            showNotice("🎙️ DISTRESS SCREAM DETECTED!\n• Direct Call: $helplineLabel ($contactToCall)\n• Loud Siren Alarm Sounding\n• Audio Evidence Recording Active\n• $smsResult")
        }

        // 7. Show high priority emergency dialog on screen
        _showScreamAlertDialog.value = true
    }

    fun triggerScreamAlertFromBackground() {
        if (!_isSirenActive.value) {
            _isSirenActive.value = true
        }
        if (!_isRecordingAudio.value) {
            _isRecordingAudio.value = true
        }
        _showScreamAlertDialog.value = true
    }

    fun triggerShakeAlertFromBackground() {
        if (!_isSirenActive.value) {
            _isSirenActive.value = true
        }
        if (!_isRecordingAudio.value) {
            _isRecordingAudio.value = true
        }
        _showShakeAlertDialog.value = true
    }

    fun testScreamDangerTrigger() {
        showNotice("🎙️ Simulating Distress Scream Voice Detection...")
        onScreamOrDistressDetected()
    }

    fun testShakeDangerTrigger() {
        showNotice("🚨 Simulating Violent Shake Detection...")
        onShakeEmergencyDetected()
    }

    fun dismissScreamDialog() {
        _showScreamAlertDialog.value = false
        screamDetector.resetScreamState()
    }

    // --- SHAKE / MOTION EMERGENCY ACTIONS ---

    fun toggleShakeDetection() {
        if (shakeMotionDetector.isListening.value) {
            shakeMotionDetector.stopListening()
            prefs.edit().putBoolean("shake_guard_enabled", false).apply()
            PowerButtonSosService.updateConfig(getApplication())
            showNotice("Shake & Motion Emergency Guard Paused")
        } else {
            shakeMotionDetector.startListening()
            prefs.edit().putBoolean("shake_guard_enabled", true).apply()
            PowerButtonSosService.start(getApplication())
            PowerButtonSosService.updateConfig(getApplication())
            showNotice("🚨 24/7 Violent Shake Emergency Guard Active (Runs Even When Closed)!")
        }
    }

    fun setShakeSensitivity(sensitivity: ShakeSensitivity) {
        shakeMotionDetector.setSensitivity(sensitivity)
        val label = if (_currentLanguage.value == AppLanguage.TAMIL) sensitivity.tamilLabel else sensitivity.label
        showNotice("Shake sensitivity: $label")
    }

    fun onShakeEmergencyDetected() {
        val under15 = isUnder15.value
        val contactToCall = if (under15) "1098" else primaryEmergencyNumber.value.ifBlank { "1091" }
        val helplineDesc = if (under15) "Child Safety Helpline 1098" else "Women Helpline $contactToCall"

        // 1. Trigger direct call to helpline based on age (1098 for <15, 1091 for 15+)
        sosManager.triggerDirectCall(contactToCall)

        // 2. Automatically trigger loud alarm siren
        if (!_isSirenActive.value) {
            sirenPlayer.startSiren()
            _isSirenActive.value = true
        }

        // 3. Automatically start audio recording
        if (!_isRecordingAudio.value) {
            startAudioEvidenceRecording()
        }

        // 4. Capture photo evidence
        val photoFile = cameraCaptureManager.captureIncidentPhoto("Violent Shake Incident")
        if (photoFile != null) {
            val entity = IncidentEvidenceEntity(
                title = "Violent Shake Incident Photo",
                mediaType = "PHOTO",
                filePath = photoFile.absolutePath
            )
            viewModelScope.launch {
                evidenceDao.insertEvidence(entity)
            }
        }

        // 5. Automatically send offline SMS with incident evidence / location to guardians
        viewModelScope.launch {
            val guardians = guardiansList.value
            val alertMsg = if (under15) {
                "🚨 EMERGENCY CHILD SAFETY ALERT (<15 Age)!\nChild in danger! Violent shake detected.\nDirect call to Childline 1098 dispatched. Urgent assistance needed!"
            } else {
                "🚨 EMERGENCY DANGER SOS ALERT!\nUrgent danger! Violent shake detected.\nDirect call to Women Helpline 1091 dispatched. Urgent assistance needed!"
            }
            val smsResult = sosManager.sendEmergencySmsToGuardians(guardians, customMessage = alertMsg)
            showNotice("🚨 MAXIMUM VIOLENT SHAKE DETECTED!\n• Direct Call: $helplineDesc ($contactToCall)\n• Siren Sounding\n• Audio Evidence Recording Active\n• $smsResult")
        }

        // 6. Trigger alert dialog on screen
        _showShakeAlertDialog.value = true
    }

    fun dismissShakeDialog() {
        _showShakeAlertDialog.value = false
        shakeMotionDetector.resetDetection()
    }

    fun captureIncidentPhoto() {
        sosManager.vibrateAlert()
        val file = cameraCaptureManager.captureIncidentPhoto("Live Incident Snapshot")
        if (file != null && file.exists()) {
            val count = incidentEvidencesList.value.size + 1
            val entity = IncidentEvidenceEntity(
                title = "Camera Snapshot #$count",
                mediaType = "PHOTO",
                filePath = file.absolutePath
            )
            viewModelScope.launch {
                evidenceDao.insertEvidence(entity)
                showNotice("📸 Incident Camera Photo Captured & Saved!")
            }
        } else {
            showNotice("Unable to capture camera snapshot")
        }
    }

    fun shareEvidenceToGuardians(context: Context, filePath: String) {
        try {
            val file = File(filePath)
            if (!file.exists()) {
                showNotice("Evidence file not found!")
                return
            }

            val uri = try {
                FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
            } catch (e: Exception) {
                Uri.fromFile(file)
            }

            val isPhoto = filePath.endsWith(".jpg") || filePath.endsWith(".png")
            val mimeType = if (isPhoto) "image/*" else "audio/*"
            val text = "🚨 SENTINEL AI INCIDENT EVIDENCE ALERT!\nRecorded: ${file.name}"

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = mimeType
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_TEXT, text)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }

            val chooser = Intent.createChooser(intent, "Share Incident Evidence to Guardians")
            chooser.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(chooser)
        } catch (e: Exception) {
            // Fallback: SMS share text to guardians
            val guardians = guardiansList.value
            val result = sosManager.sendEmergencySmsToGuardians(guardians, customMessage = "🚨 INCIDENT EVIDENCE RECORDED! File: ${File(filePath).name}")
            showNotice(result)
        }
    }

    fun deleteEvidence(evidence: IncidentEvidenceEntity) {
        viewModelScope.launch {
            try {
                val file = File(evidence.filePath)
                if (file.exists()) file.delete()
            } catch (e: Exception) {
                // Ignore delete errors
            }
            evidenceDao.deleteEvidence(evidence)
            showNotice("Deleted incident evidence")
        }
    }

    // --- POWER BUTTON DOUBLE-TAP DANGER GUARD ACTIONS ---

    val isPowerButtonTriggerOnOffActive: StateFlow<Boolean> = powerButtonDetector.triggerOnPowerOff

    fun togglePowerButtonGuard() {
        if (powerButtonDetector.isListening.value) {
            powerButtonDetector.stopListening()
            PowerButtonSosService.stop(getApplication())
            prefs.edit().putBoolean("power_button_guard_enabled", false).apply()
            showNotice("Special Power Button SOS Guard Paused")
        } else {
            powerButtonDetector.startListening()
            PowerButtonSosService.start(getApplication())
            prefs.edit().putBoolean("power_button_guard_enabled", true).apply()
            showNotice("⚡ Special Power Button SOS Guard Active (Runs 24/7 Even When Closed)!")
        }
    }

    fun togglePowerButtonTriggerOnOff() {
        val nextState = !isPowerButtonTriggerOnOffActive.value
        powerButtonDetector.setTriggerOnPowerOff(nextState)
        prefs.edit().putBoolean("power_button_trigger_on_off", nextState).apply()
        val notice = if (nextState) {
            "📴 Power Off Mode: SOS triggers whenever power button turns screen off!"
        } else {
            "⚡ Double-Tap Mode: SOS triggers on 2x rapid power button presses."
        }
        showNotice(notice)
    }

    fun testPowerButtonDangerTrigger() {
        showNotice("⚡ Simulating Power Button Double-Tap (2x)...")
        powerButtonDetector.simulateDoubleTap()
    }

    fun testPowerButtonOffTrigger() {
        showNotice("📴 Simulating Power Button Screen Off Trigger...")
        powerButtonDetector.simulatePowerOffTap()
    }

    fun setPowerButtonEmergencyContact(phone: String) {
        if (phone.isNotBlank()) {
            _powerButtonEmergencyContact.value = phone.trim()
            showNotice("Emergency contact set to $phone")
        }
    }

    fun onPowerButtonDangerDetected(reason: String = "POWER_TRIGGER") {
        val under15 = isUnder15.value
        val contactToCall = if (under15) "1098" else _powerButtonEmergencyContact.value.ifBlank { "1091" }
        val helplineLabel = if (under15) "Child Safety 1098" else "Women Helpline 1091"

        // 1. Automatically initiate direct call (1098 for <15, 1091 for 15+)
        sosManager.triggerDirectCall(contactToCall)

        // 2. Automatically blast loud police buzzer / siren alarm
        if (!_isSirenActive.value) {
            sirenPlayer.startSiren()
            _isSirenActive.value = true
        }

        // 3. Automatically start continuous audio evidence voice recording
        if (!_isRecordingAudio.value) {
            startAudioEvidenceRecording()
        }

        // 4. Automatically dispatch emergency SMS with evidence to all guardians
        viewModelScope.launch {
            val guardians = guardiansList.value
            val alertMsg = if (under15) {
                "🚨 EMERGENCY CHILD SAFETY ALERT (<15 Age)!\nChild in danger! Special Power Button SOS Triggered ($reason).\nDirect call to Childline 1098 dispatched. Urgent assistance needed!"
            } else {
                "🚨 EMERGENCY DANGER SOS ALERT!\nUrgent danger! Special Power Button SOS Triggered ($reason).\nDirect call to Women Helpline 1091 dispatched. Urgent assistance needed!"
            }
            val smsResult = sosManager.sendEmergencySmsToGuardians(guardians, customMessage = alertMsg)
            val triggerText = if (reason == "POWER_BUTTON_OFF") "Power Button Screen Off" else "Power Button 2x Tap"
            showNotice("🚨 SPECIAL POWER BUTTON SOS TRIGGERED!\n• Mode: $triggerText\n• Direct Call: $helplineLabel ($contactToCall)\n• Loud Siren Alarm Sounding\n• Audio Evidence Recording Active\n• $smsResult")
        }

        // 5. Open high-priority danger dialog
        _showPowerButtonDangerDialog.value = true
    }

    fun dismissPowerButtonDangerDialog() {
        _showPowerButtonDangerDialog.value = false
    }

    override fun onCleared() {
        super.onCleared()
        sirenPlayer.stopSiren()
        audioRecorder.stopPlayback()
        screamDetector.stopListening()
        shakeMotionDetector.stopListening()
        powerButtonDetector.stopListening()
        speechToTextManager.stopListening()
        textToSpeechManager.shutdown()
        if (_isRecordingAudio.value) {
            audioRecorder.stopRecording()
        }
    }
}
