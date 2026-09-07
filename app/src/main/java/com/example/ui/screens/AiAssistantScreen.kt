package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ai.ThreatAnalysisResult
import com.example.ai.ThreatLevel
import com.example.data.model.AppLanguage
import com.example.data.model.AppStrings
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CrimsonPrimary
import com.example.ui.theme.MagentaSecondary
import com.example.ui.theme.RiskCriticalRed
import com.example.ui.theme.RiskHighOrange
import com.example.ui.theme.RiskLowGreen
import com.example.ui.theme.RiskMediumYellow
import com.example.ui.theme.SuccessGreen
import com.example.viewmodel.SafetyViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AiAssistantScreen(
    viewModel: SafetyViewModel,
    threatPrompt: String,
    isEvaluatingThreat: Boolean,
    threatResult: ThreatAnalysisResult?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentLanguage by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val strings = remember(currentLanguage) { AppStrings.get(currentLanguage) }

    val isVoiceListening by viewModel.isVoiceListening.collectAsStateWithLifecycle()
    val isSpeakingTts by viewModel.isSpeakingTts.collectAsStateWithLifecycle()
    val isRecordingAudio by viewModel.isRecordingAudio.collectAsStateWithLifecycle()
    val parameterizedResult by viewModel.parameterizedResult.collectAsStateWithLifecycle()

    val isBelow18 by viewModel.isBelow18.collectAsStateWithLifecycle()
    val primaryEmergencyNumber by viewModel.primaryEmergencyNumber.collectAsStateWithLifecycle()
    val primaryHelplineName by viewModel.primaryHelplineName.collectAsStateWithLifecycle()

    val quickScenarios = if (currentLanguage == AppLanguage.TAMIL) listOf(
        "பாலியல் வன்கொடுமை முயற்சி / ஆடை கிழித்து அச்சுறுத்தல் (Rape & Sexual Assault Threat)",
        "இரவு 10 மணிக்கு ஆள் நடமாட்டமில்லாத தெருவில் என்னை யாரோ பின்தொடர்கிறார்கள்",
        "ஆட்டோ/டாக்ஸி ஓட்டுநர் தவறான வழியில் சென்று வண்டியை நிறுத்த மறுக்கிறார்",
        "கத்தி வைத்து கடத்தல் மற்றும் கொலை மிரட்டல் (Kidnap & Weapon Threat)",
        "சென்னையில் இருட்டான வழியில் பைக் என்னை நெருக்கமாக பின்தொடர்கிறது",
        "அலுவலகத்தில் சக ஊழியர்/மேலதிகாரி அச்சுறுத்தும் வகையில் துன்புறுத்துகிறார்",
        "சமூக வலைத்தளத்தில் புகைப்படங்களை வெளியிட்டு பிளாக்மெயில் செய்கிறார்கள்"
    ) else listOf(
        "Attempted rape and sexual assault by cornering in isolated room",
        "Walking alone at 10 PM in a quiet lane with someone following behind",
        "Auto/Cab driver took an unfamiliar wrong route and refuses to stop",
        "Kidnapping threat at knife point in deserted parking lot",
        "Bike tailgating me closely in a dimly lit alley in Chennai",
        "Workplace superior threatening job and violating personal boundaries",
        "Cyber blackmail and unauthorized photo leak threats on social media"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        // AI Sentinel Header Banner
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MagentaSecondary.copy(alpha = 0.12f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.5.dp, MagentaSecondary.copy(alpha = 0.4f), RoundedCornerShape(18.dp))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(MagentaSecondary, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = "Sentinel AI",
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = strings.aiHeaderTitle,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Text(
                                    text = strings.aiHeaderSubtitle,
                                    fontSize = 11.sp,
                                    lineHeight = 14.sp,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                                )
                            }
                        }

                        // Mute / Speaker Toggle Button
                        IconButton(
                            onClick = {
                                if (isSpeakingTts) {
                                    viewModel.stopTtsSpeech()
                                } else {
                                    val textToSay = threatResult?.summary ?: strings.aiHeaderSubtitle
                                    viewModel.speakTacticalGuidance(textToSay)
                                }
                            },
                            modifier = Modifier.testTag("ai_tts_toggle_button")
                        ) {
                            Icon(
                                imageVector = if (isSpeakingTts) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
                                contentDescription = "Speaker",
                                tint = if (isSpeakingTts) CrimsonPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Voice Assistant Mic Pulse & Auto Record Controls
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        color = CrimsonPrimary.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (currentLanguage == AppLanguage.TAMIL) "🗣️ தமிழ் & English இருமொழி குரல் பகுப்பாய்வு" else "🗣️ Bilingual Voice Safety Assistant",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = CrimsonPrimary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Voice Assistant Mic Button
                        VoiceMicPulseButton(
                            isListening = isVoiceListening,
                            onMicClick = {
                                if (isVoiceListening) {
                                    viewModel.stopVoiceInput()
                                } else {
                                    viewModel.startVoiceInput()
                                }
                            }
                        )

                        // Auto Voice Alert to Guardians Button
                        Surface(
                            onClick = { viewModel.autoRecordVoiceAlertToGuardians(context) },
                            shape = RoundedCornerShape(14.dp),
                            color = if (isRecordingAudio) CrimsonPrimary else SuccessGreen,
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 14.dp)
                                .testTag("auto_voice_guardian_alert_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = if (isRecordingAudio) Icons.Default.RecordVoiceOver else Icons.Default.Mic,
                                    contentDescription = "Voice Record to Guardians",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isRecordingAudio) "RECORDING..." else strings.autoVoiceGuardianBtn,
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }

                    if (isVoiceListening) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.GraphicEq,
                                contentDescription = null,
                                tint = CrimsonPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = strings.voiceInputListening,
                                color = CrimsonPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        // Quick Preset Scenario Chips (Interactive 1-Tap Analyzers)
        item {
            Text(
                text = strings.quickScenariosTitle,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                quickScenarios.forEach { scenario ->
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                        modifier = Modifier.clickable {
                            viewModel.evaluateThreat(scenario)
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CompassCalibration,
                                contentDescription = null,
                                tint = MagentaSecondary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (scenario.length > 32) scenario.take(32) + "..." else scenario,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // Input prompt box & analyze button
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = threatPrompt,
                    onValueChange = { viewModel.updateThreatPrompt(it) },
                    placeholder = { Text(strings.aiInputPlaceholder, fontSize = 13.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("ai_threat_input"),
                    shape = RoundedCornerShape(14.dp),
                    minLines = 2,
                    maxLines = 4
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { viewModel.evaluateThreat() },
                    enabled = !isEvaluatingThreat && threatPrompt.isNotBlank(),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("ai_analyze_button")
                ) {
                    if (isEvaluatingThreat) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(strings.evaluatingText, fontSize = 13.sp)
                    } else {
                        Icon(Icons.Default.DirectionsRun, contentDescription = "Escape Route", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(strings.analyzeBtnText, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }

        // Parameterized Keyword Threat Matrix Badge
        if (parameterizedResult != null && parameterizedResult!!.detectedParameters.isNotEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = CrimsonPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = strings.parameterizedKeywordsTitle,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = CrimsonPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            parameterizedResult!!.detectedParameters.forEach { param ->
                                Surface(
                                    color = CrimsonPrimary.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "⚡ ${param.categoryName} (${param.matchedKeywords.joinToString(",")})",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CrimsonPrimary,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // AI Threat Analysis Result
        if (threatResult != null) {
            item {
                ThreatResultCard(
                    result = threatResult!!,
                    isSpeaking = isSpeakingTts,
                    strings = strings,
                    isBelow18 = isBelow18,
                    primaryEmergencyNumber = primaryEmergencyNumber,
                    primaryHelplineName = primaryHelplineName,
                    onToggleSpeak = { viewModel.toggleSpeakThreatResult() },
                    onCallPolice = { viewModel.triggerEmergencyCall(primaryEmergencyNumber) },
                    onSendSms = { viewModel.sendSosSmsToGuardians() },
                    onSoundSiren = { viewModel.triggerFullMasterSosAlert() }
                )
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
private fun VoiceMicPulseButton(
    isListening: Boolean,
    onMicClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "mic_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isListening) 1.22f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Box(contentAlignment = Alignment.Center) {
        if (isListening) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .scale(pulseScale)
                    .background(CrimsonPrimary.copy(alpha = 0.3f), CircleShape)
            )
        }

        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(if (isListening) CrimsonPrimary else MagentaSecondary)
                .clickable { onMicClick() }
                .testTag("ai_voice_assistant_mic_button"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isListening) Icons.Default.MicOff else Icons.Default.Mic,
                contentDescription = "Voice Mic",
                tint = Color.White,
                modifier = Modifier.size(26.dp)
            )
        }
    }
}

@Composable
private fun ThreatResultCard(
    result: ThreatAnalysisResult,
    isSpeaking: Boolean,
    strings: AppStrings.Strings,
    isBelow18: Boolean,
    primaryEmergencyNumber: String,
    primaryHelplineName: String,
    onToggleSpeak: () -> Unit,
    onCallPolice: () -> Unit,
    onSendSms: () -> Unit,
    onSoundSiren: () -> Unit
) {
    val (levelColor, levelTitle) = when (result.threatLevel) {
        ThreatLevel.LOW -> RiskLowGreen to "LOW RISK"
        ThreatLevel.MEDIUM -> RiskMediumYellow to "MODERATE RISK"
        ThreatLevel.HIGH -> RiskHighOrange to "HIGH RISK"
        ThreatLevel.CRITICAL -> RiskCriticalRed to "CRITICAL EMERGENCY RISK"
    }

    // Interactive completed checklist state for tactical steps
    val checkedSteps = remember(result) { mutableStateMapOf<Int, Boolean>() }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, levelColor.copy(alpha = 0.8f), RoundedCornerShape(18.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Threat Level Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    color = levelColor,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = levelTitle,
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp
                        )
                    }
                }

                Text(
                    text = "Danger Score: ${result.scorePercentage}%",
                    fontWeight = FontWeight.Bold,
                    color = levelColor,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { result.scorePercentage / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape),
                color = levelColor,
                trackColor = levelColor.copy(alpha = 0.2f)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Spoken Voice Solution Tips Player Banner
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isSpeaking) CrimsonPrimary.copy(alpha = 0.12f) else MagentaSecondary.copy(alpha = 0.10f),
                border = BorderStroke(
                    1.dp,
                    if (isSpeaking) CrimsonPrimary.copy(alpha = 0.5f) else MagentaSecondary.copy(alpha = 0.3f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleSpeak() }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(if (isSpeaking) CrimsonPrimary else MagentaSecondary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isSpeaking) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
                                contentDescription = "Voice Guide",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = if (isSpeaking) strings.muteResponseBtn else strings.speakResponseBtn,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = if (isSpeaking) CrimsonPrimary else MagentaSecondary
                            )
                            Text(
                                text = if (isSpeaking) "Speaking solutions..." else "Tap to hear all solution tips with flow",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Surface(
                        color = if (isSpeaking) CrimsonPrimary else MagentaSecondary,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = if (isSpeaking) "MUTE" else "PLAY",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Threat Assessment Summary",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = result.summary,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Interactive Tactical Escape Route Steps Checklist
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.DirectionsRun,
                    contentDescription = null,
                    tint = CrimsonPrimary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Immediate Tactical Escape Steps (Interactive Checklist)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = CrimsonPrimary
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                result.immediateEscapeSteps.forEachIndexed { idx, step ->
                    val isChecked = checkedSteps[idx] == true
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isChecked) SuccessGreen.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                checkedSteps[idx] = !isChecked
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isChecked) Icons.Default.CheckBox else Icons.Default.CheckBoxOutlineBlank,
                                contentDescription = "Check step",
                                tint = if (isChecked) SuccessGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${idx + 1}. $step",
                                fontSize = 12.sp,
                                lineHeight = 16.sp,
                                textDecoration = if (isChecked) TextDecoration.LineThrough else TextDecoration.None,
                                color = if (isChecked) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            if (result.tacticalDeescalationAdvice.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = AmberWarning,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "De-escalation & Defense Advice",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = AmberWarning
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    result.tacticalDeescalationAdvice.forEach { advice ->
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = SuccessGreen,
                                modifier = Modifier
                                    .size(14.dp)
                                    .padding(top = 2.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = advice,
                                fontSize = 12.sp,
                                lineHeight = 16.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Triggers inside AI Result
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Button(
                    onClick = onCallPolice,
                    colors = ButtonDefaults.buttonColors(containerColor = if (isBelow18) AmberWarning else CrimsonPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Call, contentDescription = "Call", modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(primaryEmergencyNumber, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }

                Button(
                    onClick = onSendSms,
                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Send, contentDescription = "SMS", modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("SMS", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }

                Button(
                    onClick = onSoundSiren,
                    colors = ButtonDefaults.buttonColors(containerColor = MagentaSecondary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Warning, contentDescription = "Siren", modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("SIREN", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }
        }
    }
}
