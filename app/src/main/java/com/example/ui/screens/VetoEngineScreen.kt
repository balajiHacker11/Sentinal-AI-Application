package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.db.VetoIncidentEntity
import com.example.data.model.AppLanguage
import com.example.data.veto.VetoDecisionResult
import com.example.data.veto.VetoTelemetryInput
import com.example.ui.components.VetoIncidentRowCard
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CrimsonPrimary
import com.example.ui.theme.MagentaSecondary
import com.example.ui.theme.SuccessGreen
import com.example.viewmodel.SafetyViewModel
import java.util.Locale

@Composable
fun VetoEngineScreen(
    viewModel: SafetyViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val isVetoGateActive by viewModel.isVetoGateActive.collectAsStateWithLifecycle()
    val latestDecision by viewModel.latestVetoDecision.collectAsStateWithLifecycle()
    val latestTelemetry by viewModel.latestTelemetryInput.collectAsStateWithLifecycle()
    val vetoIncidents by viewModel.vetoIncidents.collectAsStateWithLifecycle()
    val vetoCount by viewModel.vetoCount.collectAsStateWithLifecycle()
    val dispatchCount by viewModel.dispatchCount.collectAsStateWithLifecycle()
    val filterTab by viewModel.vetoFilterTab.collectAsStateWithLifecycle()
    val currentLanguage by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val isTamil = currentLanguage == AppLanguage.TAMIL

    var showPatentBriefing by remember { mutableStateOf(true) }
    var showRawJson by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("veto_engine_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Hero Card: Master Toggle & Operational State
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isVetoGateActive) SuccessGreen.copy(alpha = 0.12f) else CrimsonPrimary.copy(alpha = 0.08f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.5.dp,
                        if (isVetoGateActive) SuccessGreen.copy(alpha = 0.5f) else CrimsonPrimary.copy(alpha = 0.4f),
                        RoundedCornerShape(16.dp)
                    )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = if (isVetoGateActive) SuccessGreen else CrimsonPrimary,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (isTamil) "குறுக்கு-முறைமை SOS வீட்டோ இயந்திரம்" else "Cross-Modal SOS Veto Engine",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (isVetoGateActive) "SHIELD ACTIVE • 24/7 False Alarm Filter" else "FILTER DISABLED • Direct Emergency Dispatch",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = if (isVetoGateActive) SuccessGreen else CrimsonPrimary
                                )
                            }
                        }

                        Switch(
                            checked = isVetoGateActive,
                            onCheckedChange = { viewModel.toggleVetoGate() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = SuccessGreen,
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = MaterialTheme.colorScheme.outline
                            ),
                            modifier = Modifier.testTag("veto_master_switch")
                        )
                    }

                    Text(
                        text = if (isVetoGateActive)
                            "The Veto Gate intercepts hardware SOS triggers (5 power-button presses) and acoustic spikes. It evaluates a 2,000ms multi-sensor burst to programmatically suppress accidental pocket dials or TV screams before cellular 1091 baseband dispatch occurs."
                        else
                            "Warning: When disabled, all SOS events immediately dial Singappen Helpline 1091 without sensor validation. Enable to stop pocket dials from dispatching police desks.",
                        fontSize = 11.5.sp,
                        lineHeight = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                    )

                    // Operational Metrics Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Vetoed (Aborted)", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = SuccessGreen)
                                Text("$vetoCount", fontSize = 16.sp, fontWeight = FontWeight.Black, color = SuccessGreen)
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Confirmed 1091", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = CrimsonPrimary)
                                Text("$dispatchCount", fontSize = 16.sp, fontWeight = FontWeight.Black, color = CrimsonPrimary)
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Window Burst", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = MagentaSecondary)
                                Text("2,000 ms", fontSize = 14.sp, fontWeight = FontWeight.Black, color = MagentaSecondary)
                            }
                        }
                    }
                }
            }
        }

        // 2. Patent & Presentation Briefing Card
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Psychology, contentDescription = null, tint = MagentaSecondary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Patent & Presentation Architecture",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 13.5.sp,
                                color = MagentaSecondary
                            )
                        }

                        IconButton(
                            onClick = {
                                val briefingText = """
                                    SENTINEL AI: CROSS-MODAL SOS VETO ENGINE
                                    
                                    1. Core Concept (One-Liner):
                                    Instead of immediately dialing emergency services when an SOS trigger occurs, the Veto Engine intercepts the countdown and checks a 2-second burst of multiple phone sensors at once to verify whether the user is in real danger or if it is just an accidental trigger.
                                    
                                    2. The Three Sensor Modalities:
                                    - Kinetic Motion (Accelerometer/Gyroscope): Distinguishes erratic physical struggle from calm walking or stationary rest.
                                    - Optical Exposure (Proximity/Light): Detects open air vs. dark pocket or handbag containment.
                                    - Acoustic Frequencies (Microphone): Separates live biological distress vocalizations from loudspeaker/TV compression (<15kHz cutoff).
                                    
                                    3. Decision Matrix:
                                    - Pocket Dial (VETO): Prox covered + low jerk + no voice stress -> Abort 1091 call.
                                    - TV Scream (VETO): Audio spike + stationary IMU + codec cutoff -> Abort alert.
                                    - Genuine Emergency (DISPATCH): Violent jerk OR live distress scream -> Zero-countdown 1091 dispatch.
                                """.trimIndent()
                                clipboardManager.setText(AnnotatedString(briefingText))
                                Toast.makeText(context, "Specification copied for slides & reports!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy Architecture Brief", modifier = Modifier.size(16.dp))
                        }
                    }

                    // Pillar 1: Core Concept
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "1. Core Concept (One-Liner for Slides/Intro)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.5.sp,
                                color = CrimsonPrimary
                            )
                            Text(
                                text = "Instead of immediately dialing emergency services when an SOS trigger occurs, the Veto Engine intercepts the countdown and checks a 2-second burst of multiple phone sensors at once to verify whether the user is in real danger or if it is just an accidental trigger.",
                                fontSize = 11.sp,
                                lineHeight = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    // Pillar 2: The Three Sensor Modalities
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "2. The Three Sensor Modalities (\"Cross-Modal\" Fusion)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.5.sp,
                                color = CrimsonPrimary
                            )
                            
                            Row(verticalAlignment = Alignment.Top) {
                                Text("• ", fontWeight = FontWeight.Bold, color = CrimsonPrimary)
                                Column {
                                    Text("Kinetic Motion (Accelerometer/Gyroscope):", fontWeight = FontWeight.Bold, fontSize = 10.5.sp)
                                    Text("Measures physical force. It distinguishes between the erratic, high-impact motion of a physical struggle and the calm, rhythmic motion of walking or a phone resting flat on a table.", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }

                            Row(verticalAlignment = Alignment.Top) {
                                Text("• ", fontWeight = FontWeight.Bold, color = CrimsonPrimary)
                                Column {
                                    Text("Optical Exposure (Proximity/Light Sensor):", fontWeight = FontWeight.Bold, fontSize = 10.5.sp)
                                    Text("Detects whether the phone is in open air or trapped inside a dark pocket or handbag.", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }

                            Row(verticalAlignment = Alignment.Top) {
                                Text("• ", fontWeight = FontWeight.Bold, color = CrimsonPrimary)
                                Column {
                                    Text("Acoustic Frequencies (Microphone):", fontWeight = FontWeight.Bold, fontSize = 10.5.sp)
                                    Text("Analyzes sound waves to separate a live human vocal cord in distress from compressed audio coming out of a TV speaker or radio.", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Mathematical Formulation: CMCS = (w_k · K) + (w_o · O) + (w_a · A)",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MagentaSecondary,
                                    modifier = Modifier.padding(6.dp)
                                )
                            }
                        }
                    }

                    // Pillar 3: How It Makes Decisions
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "3. How It Makes Decisions (Real-World Scenarios)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.5.sp,
                                color = CrimsonPrimary
                            )

                            Text(
                                text = "Scenario A: Pocket Dial (VETO)\nTrigger: Power button pressed 5 times inside a tight pocket or bouncing bag.\nSensor Check: Optical = Dark/Covered; Kinetic = Normal walking rhythm; Acoustic = Muffled fabric, no vocal stress.\nAction: Silently aborts the emergency call to prevent false dispatch.",
                                fontSize = 10.sp,
                                lineHeight = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

                            Text(
                                text = "Scenario B: Movie/TV Scream (VETO)\nTrigger: Audio detector hears a loud scream from a television.\nSensor Check: Optical = Open air; Kinetic = Perfectly still (0 movement on a table); Acoustic = Loudspeaker frequency cutoffs detected.\nAction: Silently cancels the alert.",
                                fontSize = 10.sp,
                                lineHeight = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

                            Text(
                                text = "Scenario C: Genuine Emergency (DISPATCH)\nTrigger: Scream detected or power button pressed during an attack.\nSensor Check: Kinetic = Violent struggle or erratic impact; Acoustic = Live biological distress vocalization.\nAction: Bypasses all countdown delays and immediately triggers the siren, audio recording, and offline emergency alerts to guardians and police control rooms.",
                                fontSize = 10.sp,
                                lineHeight = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // 3. Interactive Scenario Testing Studio (Real-World Simulations)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "🧪 Interactive Decision Engine Test Bench",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp
                )
                Text(
                    text = "Trigger multi-sensor snapshots to observe deterministic arbitration and haptic feedback in real-time:",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                // Scenario A: Pocket Dial Button
                OutlinedButton(
                    onClick = { viewModel.simulatePocketBagDialVeto() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("test_scenario_a_pocket_dial"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SuccessGreen)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Work, contentDescription = null, modifier = Modifier.size(20.dp), tint = SuccessGreen)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Scenario A: Pocket Dial", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("Dark pocket + low jerk (0.42g/s) + no stress", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Surface(shape = RoundedCornerShape(6.dp), color = SuccessGreen.copy(alpha = 0.15f)) {
                            Text("ASSERT VETO", fontSize = 10.sp, fontWeight = FontWeight.Black, color = SuccessGreen, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
                }

                // Scenario B: TV / Movie Playback Button
                OutlinedButton(
                    onClick = { viewModel.simulateMediaTvPlaybackVeto() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("test_scenario_b_tv_scream"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AmberWarning)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Tv, contentDescription = null, modifier = Modifier.size(20.dp), tint = AmberWarning)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Scenario B: Movie/TV Scream", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("Audio spike + phone still on table + cutoff <= 15kHz", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Surface(shape = RoundedCornerShape(6.dp), color = AmberWarning.copy(alpha = 0.15f)) {
                            Text("ASSERT VETO", fontSize = 10.sp, fontWeight = FontWeight.Black, color = AmberWarning, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
                }

                // Scenario C: Genuine Emergency (Assault / High Jerk)
                OutlinedButton(
                    onClick = { viewModel.simulateConfirmedAssaultDispatch() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("test_scenario_c_assault"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CrimsonPrimary)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Speed, contentDescription = null, modifier = Modifier.size(20.dp), tint = CrimsonPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Scenario C1: Violent Struggle / Assault", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("High jerk (5.80g/s) + erratic rotational variance", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Surface(shape = RoundedCornerShape(6.dp), color = CrimsonPrimary.copy(alpha = 0.15f)) {
                            Text("CONFIRM 1091", fontSize = 10.sp, fontWeight = FontWeight.Black, color = CrimsonPrimary, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
                }

                // Scenario C2: Genuine Biological Scream
                OutlinedButton(
                    onClick = { viewModel.simulateGenuineScreamDispatch() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("test_scenario_c_scream"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CrimsonPrimary)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Mic, contentDescription = null, modifier = Modifier.size(20.dp), tint = CrimsonPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Scenario C2: Live Human Distress Scream", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("Voice stress TRUE + wideband cutoff (18.6kHz)", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Surface(shape = RoundedCornerShape(6.dp), color = CrimsonPrimary.copy(alpha = 0.15f)) {
                            Text("CONFIRM 1091", fontSize = 10.sp, fontWeight = FontWeight.Black, color = CrimsonPrimary, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
                }
            }
        }

        // 4. Decision Engine Outcome Card
        item {
            if (latestDecision != null) {
                val isVeto = latestDecision!!.action == "VETO"
                val bannerColor = if (isVeto) SuccessGreen else CrimsonPrimary

                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = bannerColor.copy(alpha = 0.08f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.5.dp, bannerColor.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                        .testTag("decision_outcome_card")
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    if (isVeto) Icons.Default.Shield else Icons.Default.FlashOn,
                                    contentDescription = null,
                                    tint = bannerColor,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "DECISION: ${latestDecision!!.action}",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp,
                                    color = bannerColor
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = bannerColor.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "${String.format(Locale.US, "%.0f%%", latestDecision!!.confidence_score * 100)} Confidence",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = bannerColor,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        LinearProgressIndicator(
                            progress = { latestDecision!!.confidence_score.coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = bannerColor,
                            trackColor = bannerColor.copy(alpha = 0.15f)
                        )

                        // Key columns row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            if (latestDecision!!.veto_reason != null) {
                                Surface(shape = RoundedCornerShape(6.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
                                    Text("Reason: ${latestDecision!!.veto_reason}", fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                                }
                            }
                            Surface(shape = RoundedCornerShape(6.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
                                Text("Haptic: ${latestDecision!!.haptic_feedback_pattern}", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                            }
                            Surface(shape = RoundedCornerShape(6.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
                                Text("Countdown: ${latestDecision!!.cancel_countdown_seconds}s", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                            }
                        }

                        Text(
                            text = "Technical Justification:\n${latestDecision!!.technical_justification}",
                            fontSize = 11.sp,
                            lineHeight = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        // Strict JSON Viewer Toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (showRawJson) "Hide Strict JSON Output" else "View Strict JSON Output",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.clickable { showRawJson = !showRawJson }
                            )

                            IconButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(latestDecision!!.toJsonString()))
                                    Toast.makeText(context, "Strict JSON copied to clipboard!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy JSON", modifier = Modifier.size(16.dp))
                            }
                        }

                        AnimatedVisibility(visible = showRawJson) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFF1E1E1E),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = latestDecision!!.toJsonString(),
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
                                    color = Color(0xFF80CBC4),
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 5. Multi-Sensor Telemetry Columns Matrix
        item {
            if (latestTelemetry != null) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "📊 2,000ms Multi-Sensor Telemetry Columns",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp
                        )

                        // Kinetic Column
                        Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.surface, modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text("🏃 Kinetic Data Column", fontWeight = FontWeight.Bold, fontSize = 10.5.sp, color = CrimsonPrimary)
                                Text("• mean_g: ${latestTelemetry!!.kinetic_data.mean_g} g", fontSize = 9.5.sp)
                                Text("• jerk: ${latestTelemetry!!.kinetic_data.jerk} g/s", fontSize = 9.5.sp)
                                Text("• rotational_variance: ${latestTelemetry!!.kinetic_data.rotational_variance} rad/s²", fontSize = 9.5.sp)
                                Text("• motion_type: ${latestTelemetry!!.kinetic_data.motion_type}", fontSize = 9.5.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        // Optical Column
                        Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.surface, modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text("👁️ Optical Data Column", fontWeight = FontWeight.Bold, fontSize = 10.5.sp, color = CrimsonPrimary)
                                Text("• proximity_covered: ${latestTelemetry!!.optical_data.proximity_covered}", fontSize = 9.5.sp)
                                Text("• ambient_lux: ${latestTelemetry!!.optical_data.ambient_lux} lx", fontSize = 9.5.sp)
                            }
                        }

                        // Acoustic Column
                        Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.surface, modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text("🎙️ Acoustic Data Column", fontWeight = FontWeight.Bold, fontSize = 10.5.sp, color = CrimsonPrimary)
                                Text("• thd_percentage: ${latestTelemetry!!.acoustic_data.thd_percentage}%", fontSize = 9.5.sp)
                                Text("• high_freq_cutoff_khz: ${latestTelemetry!!.acoustic_data.high_freq_cutoff_khz} kHz", fontSize = 9.5.sp)
                                Text("• voice_stress_detected: ${latestTelemetry!!.acoustic_data.voice_stress_detected}", fontSize = 9.5.sp)
                                Text("• source_type: ${latestTelemetry!!.acoustic_data.source_type}", fontSize = 9.5.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        // Trigger Source
                        Text(
                            text = "Trigger Event Source: ${latestTelemetry!!.trigger_source}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MagentaSecondary
                        )
                    }
                }
            }
        }

        // 6. Historical Veto Incidents Audit Log
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📜 Veto Engine Incident Logs (${vetoIncidents.size})",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp
                )

                if (vetoIncidents.isNotEmpty()) {
                    Text(
                        text = "Clear Logs",
                        fontSize = 11.sp,
                        color = CrimsonPrimary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable { viewModel.clearAllVetoLogs() }
                            .testTag("clear_veto_logs_button")
                    )
                }
            }
        }

        // Filter Tabs
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = filterTab == 0,
                    onClick = { viewModel.setVetoFilterTab(0) },
                    label = { Text("All (${vetoIncidents.size})", fontSize = 10.5.sp) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MaterialTheme.colorScheme.primaryContainer)
                )
                FilterChip(
                    selected = filterTab == 1,
                    onClick = { viewModel.setVetoFilterTab(1) },
                    label = { Text("Vetoed ($vetoCount)", fontSize = 10.5.sp) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = SuccessGreen.copy(alpha = 0.25f))
                )
                FilterChip(
                    selected = filterTab == 2,
                    onClick = { viewModel.setVetoFilterTab(2) },
                    label = { Text("Dispatched ($dispatchCount)", fontSize = 10.5.sp) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = CrimsonPrimary.copy(alpha = 0.25f))
                )
            }
        }

        val filteredLogs = when (filterTab) {
            1 -> vetoIncidents.filter { it.action == "VETO" }
            2 -> vetoIncidents.filter { it.action == "DISPATCH" }
            else -> vetoIncidents
        }

        if (filteredLogs.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "No incidents logged in this view. Use the test bench above or press the power button 5 times to trigger an evaluation snapshot.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.padding(14.dp)
                    )
                }
            }
        } else {
            items(filteredLogs.size) { index ->
                VetoIncidentRowCard(incident = filteredLogs[index])
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
