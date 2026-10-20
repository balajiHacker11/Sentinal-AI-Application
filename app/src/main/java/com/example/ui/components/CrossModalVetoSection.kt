package com.example.ui.components

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Hearing
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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.VetoIncidentEntity
import com.example.data.veto.VetoDecisionResult
import com.example.data.veto.VetoTelemetryInput
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CrimsonPrimary
import com.example.ui.theme.SuccessGreen
import com.example.viewmodel.SafetyViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CrossModalVetoSection(
    viewModel: SafetyViewModel,
    isVetoGateActive: Boolean,
    latestDecision: VetoDecisionResult?,
    latestTelemetry: VetoTelemetryInput?,
    vetoIncidents: List<VetoIncidentEntity>,
    vetoCount: Int,
    dispatchCount: Int,
    filterTab: Int,
    isTamil: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var showRawJson by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1. Header & Active Toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Shield,
                        contentDescription = null,
                        tint = if (isVetoGateActive) SuccessGreen else CrimsonPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isTamil) "குறுக்கு-முறைமை SOS வீட்டோ இயந்திரம்" else "Cross-Modal SOS Veto Engine",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp
                    )
                }
                Text(
                    text = if (isVetoGateActive) "24/7 False Alarm Filter Active (Prevents Accidental 1091 Calls)" else "Filter Disabled (Direct Unfiltered Dispatch)",
                    fontSize = 10.5.sp,
                    color = if (isVetoGateActive) SuccessGreen else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    fontWeight = FontWeight.SemiBold
                )
            }

            Switch(
                checked = isVetoGateActive,
                onCheckedChange = { viewModel.toggleVetoGate() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = SuccessGreen
                )
            )
        }

        // 2. Operational Rules Summary Banner
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "⚙️ Deterministic 2,000ms Cross-Modal Arbitration Rules:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = CrimsonPrimary
                )
                Text(
                    text = "1. Pocket Dial Veto: Proximity Covered + Jerk < 2.0 g/s + No Voice Stress -> VETO (0.95+ conf)",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "2. Media/TV Playback Veto: Audio Spike + Jerk < 0.2 g/s + Cutoff <= 15kHz -> VETO (0.90+ conf)",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "3. Emergency Dispatch: Jerk > 3.0 g/s OR Voice Stress with Cutoff > 16kHz -> DISPATCH to 1091",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // 3. One-Tap Simulation Scenarios Grid
        Text(
            text = "🧪 Test Cross-Modal Arbitration Scenarios:",
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = { viewModel.simulatePocketBagDialVeto() },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = SuccessGreen)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Work, contentDescription = null, modifier = Modifier.size(16.dp))
                    Text("🎒 Pocket Dial", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text("Assert VETO", fontSize = 9.sp, color = SuccessGreen)
                }
            }

            OutlinedButton(
                onClick = { viewModel.simulateMediaTvPlaybackVeto() },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = AmberWarning)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Tv, contentDescription = null, modifier = Modifier.size(16.dp))
                    Text("📺 TV Playback", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text("Assert VETO", fontSize = 9.sp, color = AmberWarning)
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = { viewModel.simulateConfirmedAssaultDispatch() },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = CrimsonPrimary)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Speed, contentDescription = null, modifier = Modifier.size(16.dp))
                    Text("🚨 Assault / Jerk", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text("CONFIRM 1091", fontSize = 9.sp, color = CrimsonPrimary)
                }
            }

            OutlinedButton(
                onClick = { viewModel.simulateGenuineScreamDispatch() },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = CrimsonPrimary)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Mic, contentDescription = null, modifier = Modifier.size(16.dp))
                    Text("🎙️ Real Scream", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text("CONFIRM 1091", fontSize = 9.sp, color = CrimsonPrimary)
                }
            }
        }

        // 4. Latest Decision Outcome Card
        if (latestDecision != null) {
            val isVeto = latestDecision.action == "VETO"
            val bannerColor = if (isVeto) SuccessGreen else CrimsonPrimary

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = bannerColor.copy(alpha = 0.10f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.5.dp, bannerColor.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
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
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "DECISION: ${latestDecision.action}",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp,
                                color = bannerColor
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = bannerColor.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "${String.format(Locale.US, "%.0f%%", latestDecision.confidence_score * 100)} Confidence",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = bannerColor,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    LinearProgressIndicator(
                        progress = { latestDecision.confidence_score.coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = bannerColor,
                        trackColor = bannerColor.copy(alpha = 0.2f)
                    )

                    // Specific Column Option Badges
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (latestDecision.veto_reason != null) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = "Reason: ${latestDecision.veto_reason}",
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = "Haptic: ${latestDecision.haptic_feedback_pattern}",
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = "Countdown: ${latestDecision.cancel_countdown_seconds}s",
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }

                    // Technical Justification
                    Text(
                        text = "Technical Justification:\n${latestDecision.technical_justification}",
                        fontSize = 10.5.sp,
                        lineHeight = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Toggle Raw JSON viewer
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (showRawJson) "Hide JSON Output" else "View Strict JSON Output",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable { showRawJson = !showRawJson }
                        )

                        IconButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(latestDecision.toJsonString()))
                                Toast.makeText(context, "JSON copied to clipboard!", Toast.LENGTH_SHORT).show()
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
                                text = latestDecision.toJsonString(),
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.5.sp,
                                color = Color(0xFF80CBC4),
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            }
        }

        // 5. Specific Telemetry Columns Breakdown Card
        if (latestTelemetry != null) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "📊 2,000ms Multi-Sensor Telemetry Columns",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.5.sp
                    )

                    // Row 1: Kinetic Columns
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(
                            text = "Kinetic: mean_g=${latestTelemetry.kinetic_data.mean_g} | jerk=${latestTelemetry.kinetic_data.jerk} g/s | rot_var=${latestTelemetry.kinetic_data.rotational_variance}",
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Row 2: Optical Columns
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(
                            text = "Optical: prox_covered=${latestTelemetry.optical_data.proximity_covered} | ambient_lux=${latestTelemetry.optical_data.ambient_lux} lux",
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Row 3: Acoustic Columns
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(
                            text = "Acoustic: thd=${latestTelemetry.acoustic_data.thd_percentage}% | cutoff=${latestTelemetry.acoustic_data.high_freq_cutoff_khz} kHz | voice_stress=${latestTelemetry.acoustic_data.voice_stress_detected}",
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Row 4: Trigger Source & Motion Type
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(
                            text = "Trigger Source: ${latestTelemetry.trigger_source} | Motion: ${latestTelemetry.kinetic_data.motion_type}",
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = CrimsonPrimary
                        )
                    }
                }
            }
        }

        // 6. Historical Veto Incidents Log
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "📜 Veto Engine Logs (${vetoIncidents.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )

            if (vetoIncidents.isNotEmpty()) {
                Text(
                    text = "Clear Logs",
                    fontSize = 10.sp,
                    color = CrimsonPrimary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { viewModel.clearAllVetoLogs() }
                )
            }
        }

        // Filter Tabs: All, Vetoed Only, Dispatched Only
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FilterChip(
                selected = filterTab == 0,
                onClick = { viewModel.setVetoFilterTab(0) },
                label = { Text("All (${vetoIncidents.size})", fontSize = 10.sp) },
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MaterialTheme.colorScheme.primaryContainer)
            )
            FilterChip(
                selected = filterTab == 1,
                onClick = { viewModel.setVetoFilterTab(1) },
                label = { Text("Vetoed ($vetoCount)", fontSize = 10.sp) },
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = SuccessGreen.copy(alpha = 0.2f))
            )
            FilterChip(
                selected = filterTab == 2,
                onClick = { viewModel.setVetoFilterTab(2) },
                label = { Text("Dispatched ($dispatchCount)", fontSize = 10.sp) },
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = CrimsonPrimary.copy(alpha = 0.2f))
            )
        }

        val filteredLogs = when (filterTab) {
            1 -> vetoIncidents.filter { it.action == "VETO" }
            2 -> vetoIncidents.filter { it.action == "DISPATCH" }
            else -> vetoIncidents
        }

        if (filteredLogs.isEmpty()) {
            Text(
                text = "No recorded incidents in this category. Use test buttons above to simulate veto arbitration.",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                filteredLogs.take(6).forEach { incident ->
                    VetoIncidentRowCard(incident = incident)
                }
            }
        }
    }
}

@Composable
fun VetoIncidentRowCard(incident: VetoIncidentEntity) {
    val isVeto = incident.action == "VETO"
    val badgeColor = if (isVeto) SuccessGreen else CrimsonPrimary
    val dateFormat = remember { SimpleDateFormat("HH:mm:ss dd/MM", Locale.getDefault()) }
    var expanded by remember { mutableStateOf(false) }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = badgeColor.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = incident.action,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = badgeColor,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = incident.triggerSource,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Text(
                    text = dateFormat.format(Date(incident.timestamp)),
                    fontSize = 9.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }

            if (incident.vetoReason != null) {
                Text(
                    text = "Reason: ${incident.vetoReason}",
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = badgeColor
                )
            }

            // Quick column preview
            Text(
                text = "Jerk: ${String.format(Locale.US, "%.2f", incident.jerk)} | Prox: ${if (incident.proximityCovered) "Covered" else "Open"} | THD: ${incident.thdPercentage}% | Cutoff: ${incident.highFreqCutoffKhz}kHz",
                fontSize = 8.5.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (expanded) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Justification: ${incident.technicalJustification}",
                    fontSize = 9.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFF1E1E1E),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = incident.rawJsonOutput,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 8.5.sp,
                        color = Color(0xFF80CBC4),
                        modifier = Modifier.padding(6.dp)
                    )
                }
            }
        }
    }
}
