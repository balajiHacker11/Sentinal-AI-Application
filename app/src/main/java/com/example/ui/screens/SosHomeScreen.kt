package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.db.AudioRecordingEntity
import com.example.data.model.AppLanguage
import com.example.data.model.AppStrings
import com.example.service.ShakeSensitivity
import com.example.ui.components.PanicButton
import com.example.ui.components.PermissionStatusCard
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CrimsonPrimary
import com.example.ui.theme.MagentaSecondary
import com.example.ui.theme.SuccessGreen
import com.example.viewmodel.SafetyViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SosHomeScreen(
    viewModel: SafetyViewModel,
    isSirenActive: Boolean,
    isRecordingAudio: Boolean,
    recordingTimerSeconds: Int,
    audioRecordings: List<AudioRecordingEntity>,
    playingAudioPath: String?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentLanguage by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val strings = remember(currentLanguage) { AppStrings.get(currentLanguage) }

    val isScreamListening by viewModel.isScreamListening.collectAsStateWithLifecycle()
    val showScreamAlertDialog by viewModel.showScreamAlertDialog.collectAsStateWithLifecycle()
    val incidentEvidences by viewModel.incidentEvidencesList.collectAsStateWithLifecycle()
    val currentAmplitude by viewModel.screamDetector.currentAmplitude.collectAsStateWithLifecycle()

    val isShakeListening by viewModel.isShakeListening.collectAsStateWithLifecycle()
    val shakeGForce by viewModel.shakeGForce.collectAsStateWithLifecycle()
    val shakeMotionPercent by viewModel.shakeMotionPercent.collectAsStateWithLifecycle()
    val shakeSensitivity by viewModel.shakeSensitivity.collectAsStateWithLifecycle()
    val showShakeAlertDialog by viewModel.showShakeAlertDialog.collectAsStateWithLifecycle()

    val isPowerButtonGuardActive by viewModel.isPowerButtonGuardActive.collectAsStateWithLifecycle()
    val powerButtonTapCount by viewModel.powerButtonTapCount.collectAsStateWithLifecycle()
    val showPowerButtonDangerDialog by viewModel.showPowerButtonDangerDialog.collectAsStateWithLifecycle()
    val powerButtonEmergencyContact by viewModel.powerButtonEmergencyContact.collectAsStateWithLifecycle()

    val isBelow18 by viewModel.isBelow18.collectAsStateWithLifecycle()
    val userAge by viewModel.userAge.collectAsStateWithLifecycle()
    val isUnder15 by viewModel.isUnder15.collectAsStateWithLifecycle()
    val isPowerButtonTriggerOnOffActive by viewModel.isPowerButtonTriggerOnOffActive.collectAsStateWithLifecycle()
    val primaryEmergencyNumber by viewModel.primaryEmergencyNumber.collectAsStateWithLifecycle()
    val primaryHelplineName by viewModel.primaryHelplineName.collectAsStateWithLifecycle()

    var sensorTabSelected by remember { mutableIntStateOf(0) }

    val requiredPermissions = arrayOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.CALL_PHONE,
        Manifest.permission.SEND_SMS
    )

    fun isPermissionGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) {
        viewModel.triggerFullMasterSosAlert()
    }

    // Special Power Button SOS Alert Dialog
    if (showPowerButtonDangerDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissPowerButtonDangerDialog() },
            icon = {
                Icon(
                    Icons.Default.PowerSettingsNew,
                    contentDescription = "Power Button Danger Alert",
                    tint = CrimsonPrimary,
                    modifier = Modifier.size(40.dp)
                )
            },
            title = {
                Text(
                    text = if (isUnder15) "🚨 CHILD SAFETY SOS (<15 Age)" else "🚨 SPECIAL POWER BUTTON SOS",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 17.sp,
                    color = CrimsonPrimary
                )
            },
            text = {
                Column {
                    Text(
                        text = if (isUnder15) {
                            "Special Power Button emergency activated! Direct call to Childline 1098 dispatched, loud siren sounded, continuous audio recording started, and alert SMS dispatched to all guardians."
                        } else {
                            "Special Power Button emergency activated! Direct call to Women Safety Helpline 1091 dispatched, loud siren sounded, continuous audio recording started, and alert SMS dispatched to all guardians."
                        },
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        color = CrimsonPrimary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Call, contentDescription = null, tint = CrimsonPrimary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Automated Direct Call: ${if (isUnder15) "1098 (Childline)" else "1091 (Women Safety)"}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CrimsonPrimary)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.VolumeUp, contentDescription = null, tint = AmberWarning, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Loud Police Siren Alarm Sounding", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AmberWarning)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Mic, contentDescription = null, tint = MagentaSecondary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Continuous Audio Evidence Recording Active", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MagentaSecondary)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Send, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Offline SOS SMS Dispatched to All Guardians", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SuccessGreen)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.dismissPowerButtonDangerDialog()
                        viewModel.triggerEmergencyCall(if (isUnder15) "1098" else primaryEmergencyNumber)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = if (isUnder15) AmberWarning else CrimsonPrimary)
                ) {
                    Icon(Icons.Default.Call, contentDescription = "Call", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isUnder15) "Call Childline (1098)" else strings.call1091Btn, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (isSirenActive) {
                        Button(
                            onClick = { viewModel.toggleSiren() },
                            colors = ButtonDefaults.buttonColors(containerColor = AmberWarning)
                        ) {
                            Text(strings.stopSirenAction.replace("\n", " "), fontSize = 11.sp)
                        }
                    }
                    OutlinedButton(
                        onClick = { viewModel.dismissPowerButtonDangerDialog() }
                    ) {
                        Text(strings.cancelBtn, fontSize = 11.sp)
                    }
                }
            }
        )
    }

    // Violent Shake / Motion Emergency Alert Dialog
    if (showShakeAlertDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissShakeDialog() },
            icon = {
                Icon(
                    Icons.Default.Vibration,
                    contentDescription = "Violent Shake Alert",
                    tint = CrimsonPrimary,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = strings.shakeDialogTitle,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 17.sp,
                    color = CrimsonPrimary
                )
            },
            text = {
                Column {
                    Text(
                        text = strings.shakeDialogMessage,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        color = CrimsonPrimary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Call, contentDescription = null, tint = CrimsonPrimary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Automated Call Dispatched ($primaryEmergencyNumber - $primaryHelplineName)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CrimsonPrimary)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.VolumeUp, contentDescription = null, tint = AmberWarning, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Loud Alarm Siren Sounding", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AmberWarning)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Send, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Offline SOS SMS Dispatched to Guardians", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SuccessGreen)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Mic, contentDescription = null, tint = MagentaSecondary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Audio Evidence Recording Active", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MagentaSecondary)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.dismissShakeDialog()
                        viewModel.triggerEmergencyCall(primaryEmergencyNumber)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = if (isBelow18) AmberWarning else CrimsonPrimary)
                ) {
                    Icon(Icons.Default.Call, contentDescription = "Call", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isBelow18) strings.callPrimaryActionChild else strings.call1091Btn, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button(
                        onClick = {
                            viewModel.dismissShakeDialog()
                            viewModel.sendOfflineSmsWithEvidence()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                    ) {
                        Text("Re-send SMS", fontSize = 11.sp)
                    }
                    OutlinedButton(
                        onClick = { viewModel.dismissShakeDialog() }
                    ) {
                        Text(strings.cancelBtn, fontSize = 11.sp)
                    }
                }
            }
        )
    }

    // Scream Emergency Trigger Dialog
    if (showScreamAlertDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissScreamDialog() },
            icon = {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = "Distress Alert",
                    tint = CrimsonPrimary,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = strings.screamDialogTitle,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 17.sp,
                    color = CrimsonPrimary
                )
            },
            text = {
                Column {
                    Text(
                        text = strings.screamDialogMessage,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        color = CrimsonPrimary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Vibration, contentDescription = "Haptic", tint = CrimsonPrimary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Haptic Emergency Alarm Activated", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CrimsonPrimary)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.dismissScreamDialog()
                        viewModel.triggerEmergencyCall(primaryEmergencyNumber)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = if (isBelow18) AmberWarning else CrimsonPrimary)
                ) {
                    Icon(Icons.Default.Call, contentDescription = "Call", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isBelow18) strings.callPrimaryActionChild else strings.call1091Btn, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button(
                        onClick = {
                            viewModel.dismissScreamDialog()
                            viewModel.sendSosSmsToGuardians()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                    ) {
                        Text(strings.alertSmsBtn, fontSize = 11.sp)
                    }
                    OutlinedButton(
                        onClick = { viewModel.dismissScreamDialog() }
                    ) {
                        Text(strings.cancelBtn, fontSize = 11.sp)
                    }
                }
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        // Camera & Mic Permissions Request Banner
        item {
            PermissionStatusCard(
                strings = strings,
                onPermissionsGranted = {
                    viewModel.showNotice("Permissions granted successfully!")
                }
            )
        }

        // Age Verification & Active Helpline Banner
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isBelow18) AmberWarning.copy(alpha = 0.12f) else CrimsonPrimary.copy(alpha = 0.10f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.5.dp,
                        color = if (isBelow18) AmberWarning.copy(alpha = 0.6f) else CrimsonPrimary.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .testTag("age_safety_mode_card")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .background(
                                    if (isBelow18) AmberWarning.copy(alpha = 0.25f) else CrimsonPrimary.copy(alpha = 0.2f),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isBelow18) Icons.Default.ChildCare else Icons.Default.Female,
                                contentDescription = null,
                                tint = if (isBelow18) AmberWarning else CrimsonPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = if (isBelow18) strings.ageModeChildBadge else strings.ageModeWomenBadge,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 13.sp,
                                    color = if (isBelow18) AmberWarning else CrimsonPrimary
                                )
                                Surface(
                                    color = if (isBelow18) AmberWarning else CrimsonPrimary,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = primaryEmergencyNumber,
                                        color = Color.White,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 10.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isBelow18) {
                                    if (currentLanguage == AppLanguage.TAMIL) "வயது 18-க்கு கீழ்: அனைத்து அவசர அழைப்புகளும் 1098-க்கு இணைக்கப்படும்" else "Age Under 18: All SOS calls route to 1098 Childline"
                                } else {
                                    if (currentLanguage == AppLanguage.TAMIL) "வயது 18 & அதற்கு மேல்: அனைத்து அவசர அழைப்புகளும் 1091-க்கு இணைக்கப்படும்" else "Age 18+: All SOS calls route to 1091 Women Helpline"
                                },
                                fontSize = 10.sp,
                                lineHeight = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Surface(
                        onClick = { viewModel.openAgeSelection() },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isBelow18) AmberWarning else CrimsonPrimary,
                        modifier = Modifier.testTag("change_age_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.SwapHoriz,
                                contentDescription = "Change Age",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = strings.changeAgeBtn,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        // Interactive 24/7 Smart Shield Status Bar
        item {
            val allActive = isScreamListening && isShakeListening && isPowerButtonGuardActive
            val anyActive = isScreamListening || isShakeListening || isPowerButtonGuardActive
            val shieldColor by animateColorAsState(
                targetValue = if (allActive) SuccessGreen else if (anyActive) AmberWarning else CrimsonPrimary,
                label = "shield_status_color"
            )

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = shieldColor.copy(alpha = 0.10f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.5.dp, shieldColor.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(shieldColor)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (allActive) {
                                    if (currentLanguage == AppLanguage.TAMIL) "24/7 முழுமையான பாதுகாப்பு இயங்குகிறது" else "24/7 FULL GUARD ACTIVE"
                                } else if (anyActive) {
                                    if (currentLanguage == AppLanguage.TAMIL) "பாதுகாப்பு பகுதி இயங்குகிறது" else "PARTIAL SHIELD ACTIVE"
                                } else {
                                    if (currentLanguage == AppLanguage.TAMIL) "தானியங்கி பாதுகாப்பு நிறுத்தப்பட்டது" else "AUTO SENSORS PAUSED"
                                },
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 12.sp,
                                color = shieldColor
                            )
                            Text(
                                text = if (currentLanguage == AppLanguage.TAMIL) "குரல், அதிர்வு & பவர் பட்டன் (2x) கண்காணிக்கப்படுகிறது" else "Monitoring Voice, Shake & Power Button 2x",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }

                    // 1-Tap Master Toggle
                    Surface(
                        onClick = {
                            if (allActive) {
                                if (isScreamListening) viewModel.toggleScreamDetection()
                                if (isShakeListening) viewModel.toggleShakeDetection()
                                if (isPowerButtonGuardActive) viewModel.togglePowerButtonGuard()
                            } else {
                                if (!isScreamListening && isPermissionGranted(Manifest.permission.RECORD_AUDIO)) {
                                    viewModel.toggleScreamDetection()
                                } else if (!isScreamListening) {
                                    permissionLauncher.launch(requiredPermissions)
                                }
                                if (!isShakeListening) {
                                    viewModel.toggleShakeDetection()
                                }
                                if (!isPowerButtonGuardActive) {
                                    viewModel.togglePowerButtonGuard()
                                }
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        color = shieldColor,
                        modifier = Modifier.padding(start = 6.dp)
                    ) {
                        Text(
                            text = if (allActive) "Pause" else "Arm All",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Giant Tactile Pulsing SOS Button
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PanicButton(
                    mainText = if (currentLanguage == AppLanguage.TAMIL) "அவசரம்" else "SOS",
                    subText = if (currentLanguage == AppLanguage.TAMIL) "உதவிக்கு அழுத்தவும்" else "TAP FOR HELP",
                    onClick = {
                        if (requiredPermissions.all { isPermissionGranted(it) }) {
                            viewModel.triggerFullMasterSosAlert()
                        } else {
                            permissionLauncher.launch(requiredPermissions)
                        }
                    }
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = strings.sosInstructionText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }

        // Special Power Button Master Option Card (Works When App Is Closed)
        item {
            val pulseTransition = rememberInfiniteTransition(label = "power_pulse")
            val powerPulseAlpha by pulseTransition.animateFloat(
                initialValue = 0.6f,
                targetValue = 1.0f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1200, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "power_pulse_alpha"
            )

            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isPowerButtonGuardActive) CrimsonPrimary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.5.dp,
                        if (isPowerButtonGuardActive) CrimsonPrimary.copy(alpha = 0.6f) else MaterialTheme.colorScheme.outlineVariant,
                        RoundedCornerShape(18.dp)
                    )
                    .testTag("special_power_button_master_card")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Header Row with glowing icon, title and ON/OFF switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isPowerButtonGuardActive) CrimsonPrimary.copy(alpha = powerPulseAlpha) else MaterialTheme.colorScheme.outline
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.PowerSettingsNew,
                                    contentDescription = "Special Power Button SOS",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (currentLanguage == AppLanguage.TAMIL) "⚡ சிறப்பு பவர் பட்டன் SOS" else "⚡ Special Power Button SOS",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 14.sp,
                                    color = if (isPowerButtonGuardActive) CrimsonPrimary else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (currentLanguage == AppLanguage.TAMIL) "பயன்பாடு மூடியிருக்கும் போதும் இயங்கும்" else "Active Even When App Is Closed",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    color = if (isPowerButtonGuardActive) SuccessGreen else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }

                        Switch(
                            checked = isPowerButtonGuardActive,
                            onCheckedChange = { viewModel.togglePowerButtonGuard() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = CrimsonPrimary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Status and automated actions breakdown
                    Surface(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = if (currentLanguage == AppLanguage.TAMIL) "தானியங்கி அவசர செயல்பாடுகள்:" else "Automated Emergency Protocol:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                            )

                            // 1. Direct Call (Age-routed: 1098 if <15, 1091 if 15+)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Call, contentDescription = null, tint = if (isUnder15) AmberWarning else CrimsonPrimary, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isUnder15) {
                                        "Direct Call: 1098 (Childline / Child Safety Helpline) [<15 Age]"
                                    } else {
                                        "Direct Call: 1091 (Women Police / SOS Helpline) [15+ Age]"
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (isUnder15) AmberWarning else CrimsonPrimary
                                )
                            }

                            // 2. Message to Guardian
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Send, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Message to Guardians: Offline SMS + GPS location alert",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SuccessGreen
                                )
                            }

                            // 3. Siren
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.VolumeUp, contentDescription = null, tint = AmberWarning, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Loud Police Siren: Blasts at maximum device volume",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AmberWarning
                                )
                            }

                            // 4. Record Audio
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Mic, contentDescription = null, tint = MagentaSecondary, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Record Audio: Continuous background evidence recording",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MagentaSecondary
                                )
                            }
                        }
                    }

                    if (isPowerButtonGuardActive) {
                        Spacer(modifier = Modifier.height(10.dp))

                        // Trigger Option: Power Button Try To Off (Screen Off / Lock)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "📴 Trigger When Power Button Tries To Turn Off Screen",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Triggers SOS as soon as power button locks/turns screen off",
                                    fontSize = 9.5.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }
                            Switch(
                                checked = isPowerButtonTriggerOnOffActive,
                                onCheckedChange = { viewModel.togglePowerButtonTriggerOnOff() },
                                modifier = Modifier.scale(0.85f),
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = CrimsonPrimary
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Trigger Option: Power Button 2x Tap
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "⚡ Power 2x Rapid Tap: ",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CrimsonPrimary
                                )
                                Text(
                                    text = "$powerButtonTapCount / 2",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (powerButtonTapCount > 0) CrimsonPrimary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Surface(
                                    onClick = { viewModel.testPowerButtonOffTrigger() },
                                    shape = RoundedCornerShape(6.dp),
                                    color = AmberWarning.copy(alpha = 0.15f)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "Simulate Power Off",
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AmberWarning
                                        )
                                    }
                                }

                                Surface(
                                    onClick = { viewModel.testPowerButtonDangerTrigger() },
                                    shape = RoundedCornerShape(6.dp),
                                    color = CrimsonPrimary.copy(alpha = 0.15f)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                                    ) {
                                        Icon(Icons.Default.FlashOn, contentDescription = null, tint = CrimsonPrimary, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text(
                                            text = "Simulate 2x Tap",
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = CrimsonPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Action Quick Access Buttons Grid (2x2)
        item {
            Text(
                text = strings.quickControlsTitle,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickActionCard(
                    title = if (isBelow18) strings.callPrimaryActionChild else strings.callPoliceAction,
                    icon = Icons.Default.Call,
                    containerColor = if (isBelow18) AmberWarning else CrimsonPrimary,
                    modifier = Modifier.weight(1f),
                    testTag = "action_call_police",
                    onClick = {
                        if (isPermissionGranted(Manifest.permission.CALL_PHONE)) {
                            viewModel.triggerEmergencyCall(primaryEmergencyNumber)
                        } else {
                            permissionLauncher.launch(requiredPermissions)
                        }
                    }
                )

                val sirenBg by animateColorAsState(
                    targetValue = if (isSirenActive) CrimsonPrimary else AmberWarning,
                    label = "siren_color"
                )
                QuickActionCard(
                    title = if (isSirenActive) strings.stopSirenAction else strings.loudSirenAction,
                    icon = if (isSirenActive) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                    containerColor = sirenBg,
                    modifier = Modifier.weight(1f),
                    testTag = "action_toggle_siren",
                    onClick = { viewModel.toggleSiren() }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickActionCard(
                    title = strings.sendOfflineSmsEvidenceAction,
                    icon = Icons.Default.Send,
                    containerColor = SuccessGreen,
                    modifier = Modifier.weight(1f),
                    testTag = "action_send_offline_sms_evidence",
                    onClick = {
                        if (isPermissionGranted(Manifest.permission.SEND_SMS)) {
                            viewModel.sendOfflineSmsWithEvidence()
                        } else {
                            permissionLauncher.launch(requiredPermissions)
                        }
                    }
                )

                val micBg by animateColorAsState(
                    targetValue = if (isRecordingAudio) CrimsonPrimary else MagentaSecondary,
                    label = "mic_color"
                )
                QuickActionCard(
                    title = if (isRecordingAudio) strings.stopAudioAction else strings.recordAudioAction,
                    icon = if (isRecordingAudio) Icons.Default.MicOff else Icons.Default.Mic,
                    containerColor = micBg,
                    modifier = Modifier.weight(1f),
                    testTag = "action_record_audio",
                    onClick = {
                        if (isPermissionGranted(Manifest.permission.RECORD_AUDIO)) {
                            viewModel.toggleAudioRecording()
                        } else {
                            permissionLauncher.launch(requiredPermissions)
                        }
                    }
                )
            }
        }

        // Active Audio Recording Status Card
        item {
            AnimatedVisibility(
                visible = isRecordingAudio,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CrimsonPrimary.copy(alpha = 0.12f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, CrimsonPrimary, RoundedCornerShape(16.dp))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(CrimsonPrimary)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = strings.recordingEvidenceHeader,
                                    color = CrimsonPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                val mins = recordingTimerSeconds / 60
                                val secs = recordingTimerSeconds % 60
                                Text(
                                    text = String.format(Locale.getDefault(), "%02d:%02d", mins, secs),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }

                        Button(
                            onClick = { viewModel.toggleAudioRecording() },
                            colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Stop, contentDescription = "Stop", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(strings.stopBtn, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Unified Smart Automatic Triggers Hub (Tabbed & Simplified)
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = null,
                                tint = CrimsonPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (currentLanguage == AppLanguage.TAMIL) "தானியங்கி அவசர உணரிகளமைப்பு" else "Smart Automatic Triggers",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Tab Selector
                    TabRow(
                        selectedTabIndex = sensorTabSelected,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        contentColor = CrimsonPrimary,
                        modifier = Modifier.clip(RoundedCornerShape(10.dp))
                    ) {
                        Tab(
                            selected = sensorTabSelected == 0,
                            onClick = { sensorTabSelected = 0 },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Hearing, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (currentLanguage == AppLanguage.TAMIL) "அலறல் குரல்" else "Scream Voice",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        )
                        Tab(
                            selected = sensorTabSelected == 1,
                            onClick = { sensorTabSelected = 1 },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Vibration, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (currentLanguage == AppLanguage.TAMIL) "அதிர்வு" else "Shake",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        )
                        Tab(
                            selected = sensorTabSelected == 2,
                            onClick = { sensorTabSelected = 2 },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.ElectricBolt, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (currentLanguage == AppLanguage.TAMIL) "பவர் பட்டன் 2x" else "Power 2x",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Tab 0: Scream Voice
                    if (sensorTabSelected == 0) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = strings.screamDetectorTitle,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = if (isScreamListening) strings.screamListeningActive else strings.screamListeningOff,
                                    fontSize = 11.sp,
                                    color = if (isScreamListening) SuccessGreen else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                    fontWeight = if (isScreamListening) FontWeight.Bold else FontWeight.Normal
                                )
                            }

                            Button(
                                onClick = {
                                    if (isPermissionGranted(Manifest.permission.RECORD_AUDIO)) {
                                        viewModel.toggleScreamDetection()
                                    } else {
                                        permissionLauncher.launch(requiredPermissions)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isScreamListening) CrimsonPrimary else MaterialTheme.colorScheme.primary
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = if (isScreamListening) "Active" else "Enable",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        if (isScreamListening) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Mic Amplitude: ",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                val normalizedAmp = (currentAmplitude / 32767f).coerceIn(0f, 1f)
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(fraction = normalizedAmp.coerceAtLeast(0.04f))
                                            .height(8.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(if (normalizedAmp > 0.5f) CrimsonPrimary else SuccessGreen)
                                    )
                                }
                            }
                        }
                    }

                    // Tab 1: Violent Shake
                    if (sensorTabSelected == 1) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = strings.shakeDetectorTitle,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = if (isShakeListening) strings.shakeListeningActive else strings.shakeListeningOff,
                                    fontSize = 11.sp,
                                    color = if (isShakeListening) SuccessGreen else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                    fontWeight = if (isShakeListening) FontWeight.Bold else FontWeight.Normal
                                )
                            }

                            Button(
                                onClick = { viewModel.toggleShakeDetection() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isShakeListening) CrimsonPrimary else MaterialTheme.colorScheme.primary
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = if (isShakeListening) "Active" else "Enable",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        if (isShakeListening) {
                            Spacer(modifier = Modifier.height(10.dp))

                            // Sensitivity Selector
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = strings.shakeSensitivityLabel,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                                )

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    val isMax = shakeSensitivity == ShakeSensitivity.MAXIMUM
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isMax) CrimsonPrimary else MaterialTheme.colorScheme.surfaceVariant,
                                        modifier = Modifier.clickable {
                                            viewModel.setShakeSensitivity(ShakeSensitivity.MAXIMUM)
                                        }
                                    ) {
                                        Text(
                                            text = if (currentLanguage == AppLanguage.TAMIL) "அதிகபட்சம் (Max)" else "Maximum (Violent)",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isMax) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }

                                    val isHigh = shakeSensitivity == ShakeSensitivity.HIGH
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isHigh) CrimsonPrimary else MaterialTheme.colorScheme.surfaceVariant,
                                        modifier = Modifier.clickable {
                                            viewModel.setShakeSensitivity(ShakeSensitivity.HIGH)
                                        }
                                    ) {
                                        Text(
                                            text = if (currentLanguage == AppLanguage.TAMIL) "அதிகம் (High)" else "High",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isHigh) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Live Shake Meter
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${strings.shakeLevelLabel}: ${"%.1f".format(shakeGForce)} m/s²",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (shakeMotionPercent > 0.8f) CrimsonPrimary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))
                                ) {
                                    val progress = shakeMotionPercent.coerceIn(0f, 1f)
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(fraction = progress.coerceAtLeast(0.04f))
                                            .height(8.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(
                                                if (progress > 0.8f) CrimsonPrimary
                                                else if (progress > 0.4f) AmberWarning
                                                else SuccessGreen
                                            )
                                    )
                                }
                            }
                        }
                    }

                    // Tab 2: Power Button Double-Tap Danger Guard
                    if (sensorTabSelected == 2) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = strings.powerButtonTitle,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = if (isPowerButtonGuardActive) strings.powerButtonActiveStatus else strings.powerButtonInactiveStatus,
                                        fontSize = 11.sp,
                                        color = if (isPowerButtonGuardActive) SuccessGreen else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                        fontWeight = if (isPowerButtonGuardActive) FontWeight.Bold else FontWeight.Normal
                                    )
                                }

                                Button(
                                    onClick = { viewModel.togglePowerButtonGuard() },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isPowerButtonGuardActive) CrimsonPrimary else MaterialTheme.colorScheme.primary
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text(
                                        text = if (isPowerButtonGuardActive) "Active" else "Enable",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Explanatory info box
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(10.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = strings.powerButtonSubtitle,
                                        fontSize = 11.sp,
                                        lineHeight = 16.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    // Sequence bullets
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Call, contentDescription = null, tint = CrimsonPrimary, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("1. Auto Dials Women Helpline (1091) or Primary Guardian", fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.VolumeUp, contentDescription = null, tint = AmberWarning, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("2. Blasts Loud Piercing Alarm Buzzer / Siren", fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Mic, contentDescription = null, tint = MagentaSecondary, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("3. Records Voice Evidence & Dispatches Guardian SMS", fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Live Tap Counter & Test Simulation Button
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.TouchApp,
                                        contentDescription = null,
                                        tint = if (powerButtonTapCount > 0) CrimsonPrimary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Taps: $powerButtonTapCount / 2",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 12.sp,
                                        color = if (powerButtonTapCount > 0) CrimsonPrimary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                }

                                OutlinedButton(
                                    onClick = { viewModel.testPowerButtonDangerTrigger() },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CrimsonPrimary)
                                ) {
                                    Icon(Icons.Default.FlashOn, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = strings.powerButtonSimulateDoubleTap,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Saved Evidence Audio List
        if (audioRecordings.isNotEmpty()) {
            item {
                Text(
                    text = "${strings.savedAudioHeader} (${audioRecordings.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            items(audioRecordings) { recording ->
                AudioRecordingCard(
                    recording = recording,
                    isPlaying = playingAudioPath == recording.filePath,
                    onPlay = { viewModel.playRecording(recording) },
                    onShare = { viewModel.shareEvidenceToGuardians(context, recording.filePath) },
                    onDelete = { viewModel.deleteRecording(recording) }
                )
            }
        }

        // Quick Emergency Helplines Section
        item {
            Text(
                text = strings.helplinesTitle,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (isBelow18) {
                    // Childline 1098 first with special highlight for minors
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = AmberWarning.copy(alpha = 0.15f)),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, AmberWarning),
                        modifier = Modifier.fillMaxWidth().clickable { viewModel.triggerEmergencyCall("1098") }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        text = strings.helplineChild,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 13.sp,
                                        color = AmberWarning
                                    )
                                    Surface(color = AmberWarning, shape = RoundedCornerShape(4.dp)) {
                                        Text(strings.primaryForYourAgeTag, color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                    }
                                }
                                Text(
                                    text = strings.helplineChildDesc,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = { viewModel.triggerEmergencyCall("1098") },
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AmberWarning)
                            ) {
                                Icon(Icons.Default.Call, contentDescription = "Call", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "1098", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                } else {
                    // Women Police 1091 first with special highlight for 18+
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = CrimsonPrimary.copy(alpha = 0.12f)),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, CrimsonPrimary),
                        modifier = Modifier.fillMaxWidth().clickable { viewModel.triggerEmergencyCall("1091") }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        text = strings.helplineTnPolice,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 13.sp,
                                        color = CrimsonPrimary
                                    )
                                    Surface(color = CrimsonPrimary, shape = RoundedCornerShape(4.dp)) {
                                        Text(strings.primaryForYourAgeTag, color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                    }
                                }
                                Text(
                                    text = strings.helplineTnPoliceDesc,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = { viewModel.triggerEmergencyCall("1091") },
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary)
                            ) {
                                Icon(Icons.Default.Call, contentDescription = "Call", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "1091", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }

                if (!isBelow18) {
                    HelplineBarItem(
                        name = strings.helplineChild,
                        number = "1098",
                        description = strings.helplineChildDesc,
                        onClick = { viewModel.triggerEmergencyCall("1098") }
                    )
                } else {
                    HelplineBarItem(
                        name = strings.helplineTnPolice,
                        number = "1091",
                        description = strings.helplineTnPoliceDesc,
                        onClick = { viewModel.triggerEmergencyCall("1091") }
                    )
                }

                HelplineBarItem(
                    name = strings.helplineErs,
                    number = "112",
                    description = strings.helplineErsDesc,
                    onClick = { viewModel.triggerEmergencyCall("112") }
                )
                HelplineBarItem(
                    name = strings.helplineNcw,
                    number = "7827170170",
                    description = strings.helplineNcwDesc,
                    onClick = { viewModel.triggerEmergencyCall("7827170170") }
                )
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
private fun QuickActionCard(
    title: String,
    icon: ImageVector,
    containerColor: Color,
    modifier: Modifier = Modifier,
    testTag: String,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .height(84.dp)
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp,
                    lineHeight = 16.sp
                )
            }
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color.White,
                modifier = Modifier.size(26.dp)
            )
        }
    }
}

@Composable
private fun AudioRecordingCard(
    recording: AudioRecordingEntity,
    isPlaying: Boolean,
    onPlay: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit
) {
    val dateStr = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(recording.timestamp))

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                IconButton(
                    onClick = onPlay,
                    modifier = Modifier
                        .size(38.dp)
                        .background(MagentaSecondary, CircleShape)
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                        contentDescription = "Play Audio",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = recording.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "$dateStr • ${recording.durationSeconds}s",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row {
                IconButton(onClick = onShare) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share Evidence",
                        tint = SuccessGreen
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Recording",
                        tint = CrimsonPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun HelplineBarItem(
    name: String,
    number: String,
    description: String,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Text(
                    text = description,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedButton(
                onClick = onClick,
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = CrimsonPrimary)
            ) {
                Icon(Icons.Default.Call, contentDescription = "Call", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = number, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}
