package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocalPolice
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.model.AppLanguage
import com.example.data.model.AppStrings
import com.example.ui.components.AgeSelectionDialog
import com.example.ui.components.LanguageSelectionDialog
import com.example.ui.screens.AiAssistantScreen
import com.example.ui.screens.GuardiansScreen
import com.example.ui.screens.PoliceStationsScreen
import com.example.ui.screens.SosHomeScreen
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CrimsonPrimary
import com.example.ui.theme.MagentaSecondary
import com.example.viewmodel.SafetyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContainer(
    viewModel: SafetyViewModel
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val snackbarHostState = remember { SnackbarHostState() }

    val isSirenActive by viewModel.isSirenActive.collectAsStateWithLifecycle()
    val isRecordingAudio by viewModel.isRecordingAudio.collectAsStateWithLifecycle()
    val recordingTimerSeconds by viewModel.recordingTimerSeconds.collectAsStateWithLifecycle()
    val audioRecordings by viewModel.audioRecordingsList.collectAsStateWithLifecycle()
    val playingAudioPath by viewModel.playingAudioPath.collectAsStateWithLifecycle()

    val guardians by viewModel.guardiansList.collectAsStateWithLifecycle()

    val stationSearchQuery by viewModel.stationSearchQuery.collectAsStateWithLifecycle()
    val selectedDistrict by viewModel.selectedDistrict.collectAsStateWithLifecycle()
    val filteredStations by viewModel.filteredPoliceStations.collectAsStateWithLifecycle()

    val threatPrompt by viewModel.threatPrompt.collectAsStateWithLifecycle()
    val isEvaluatingThreat by viewModel.isEvaluatingThreat.collectAsStateWithLifecycle()
    val threatResult by viewModel.threatResult.collectAsStateWithLifecycle()

    val userNotice by viewModel.userNotice.collectAsStateWithLifecycle()

    val currentLanguage by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val showLanguageDialog by viewModel.showLanguageDialog.collectAsStateWithLifecycle()

    val isBelow18 by viewModel.isBelow18.collectAsStateWithLifecycle()
    val userAge by viewModel.userAge.collectAsStateWithLifecycle()
    val showAgeDialog by viewModel.showAgeDialog.collectAsStateWithLifecycle()
    val primaryEmergencyNumber by viewModel.primaryEmergencyNumber.collectAsStateWithLifecycle()

    val strings = remember(currentLanguage) { AppStrings.get(currentLanguage) }

    // Ambient pulsing animation for the interactive Tamil Nadu background logo
    val infiniteTransition = rememberInfiniteTransition(label = "tn_emblem_pulse")
    val ambientPulseScale by infiniteTransition.animateFloat(
        initialValue = if (isSirenActive) 0.95f else 1.0f,
        targetValue = if (isSirenActive) 1.08f else 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isSirenActive) 600 else 3500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ambient_scale"
    )
    val ambientAlpha by infiniteTransition.animateFloat(
        initialValue = if (isSirenActive) 0.18f else 0.08f,
        targetValue = if (isSirenActive) 0.32f else 0.14f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isSirenActive) 600 else 3500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ambient_alpha"
    )

    LaunchedEffect(userNotice) {
        userNotice?.let { notice ->
            snackbarHostState.showSnackbar(notice)
            viewModel.clearNotice()
        }
    }

    if (showLanguageDialog) {
        LanguageSelectionDialog(
            currentLanguage = currentLanguage,
            onLanguageSelected = { lang -> viewModel.selectLanguage(lang) },
            onDismiss = { viewModel.closeLanguageSelection() }
        )
    }

    if (showAgeDialog) {
        AgeSelectionDialog(
            currentLanguage = currentLanguage,
            initialIsBelow18 = isBelow18,
            initialAge = userAge,
            onAgeConfirmed = { isMinor, exactAge ->
                viewModel.setAgeCategory(isMinor, exactAge)
            },
            onDismiss = { viewModel.closeAgeSelection() }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.tn_govt_emblem_icon_1787302927871),
                            contentDescription = "Tamil Nadu Government Emblem",
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = strings.appTitle,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            color = if (isBelow18) AmberWarning else CrimsonPrimary,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = primaryEmergencyNumber,
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                },
                actions = {
                    // Age Checking Switcher Chip Button
                    Surface(
                        onClick = { viewModel.openAgeSelection() },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isBelow18) AmberWarning.copy(alpha = 0.15f) else CrimsonPrimary.copy(alpha = 0.15f),
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .testTag("top_app_bar_age_checker")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isBelow18) "👶 <18" else "👩 18+",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isBelow18) AmberWarning else CrimsonPrimary
                            )
                        }
                    }

                    // Language Switcher Chip Button
                    Surface(
                        onClick = { viewModel.openLanguageSelection() },
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .padding(end = 2.dp)
                            .testTag("top_app_bar_language_switcher")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = strings.selectLanguage,
                                tint = CrimsonPrimary,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = if (currentLanguage == AppLanguage.TAMIL) "தமிழ்" else "EN",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = CrimsonPrimary
                            )
                        }
                    }

                    IconButton(
                        onClick = { viewModel.triggerEmergencyCall() },
                        modifier = Modifier.testTag("top_app_bar_call_emergency")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = if (isBelow18) "Call Childline 1098" else strings.topAppBarCall,
                            tint = if (isBelow18) AmberWarning else CrimsonPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.AutoAwesome, contentDescription = strings.tabAi) },
                    label = { Text(strings.tabAi, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MagentaSecondary,
                        selectedTextColor = MagentaSecondary,
                        indicatorColor = MagentaSecondary.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("nav_tab_ai")
                )

                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.Warning, contentDescription = strings.tabSos) },
                    label = { Text(strings.tabSos, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CrimsonPrimary,
                        selectedTextColor = CrimsonPrimary,
                        indicatorColor = CrimsonPrimary.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("nav_tab_sos")
                )

                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.LocalPolice, contentDescription = strings.tabAwps) },
                    label = { Text(strings.tabAwps, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CrimsonPrimary,
                        selectedTextColor = CrimsonPrimary,
                        indicatorColor = CrimsonPrimary.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("nav_tab_awps")
                )

                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(Icons.Default.People, contentDescription = strings.tabGuardians) },
                    label = { Text(strings.tabGuardians, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CrimsonPrimary,
                        selectedTextColor = CrimsonPrimary,
                        indicatorColor = CrimsonPrimary.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("nav_tab_guardians")
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Interactive Tamil Nadu Emblem Background Watermark Layer
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.tn_govt_watermark_bg_1787302969559),
                    contentDescription = "Tamil Nadu Government Emblem Background",
                    modifier = Modifier
                        .fillMaxSize()
                        .scale(ambientPulseScale)
                        .alpha(ambientAlpha),
                    contentScale = ContentScale.Crop
                )

                // Soft subtle gradient scrim to ensure complete contrast and accessibility
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.background.copy(alpha = 0.85f),
                                    MaterialTheme.colorScheme.background.copy(alpha = 0.70f),
                                    MaterialTheme.colorScheme.background.copy(alpha = 0.90f)
                                )
                            )
                        )
                )
            }

            // Foreground Screen Content
            when (selectedTab) {
                0 -> AiAssistantScreen(
                    viewModel = viewModel,
                    threatPrompt = threatPrompt,
                    isEvaluatingThreat = isEvaluatingThreat,
                    threatResult = threatResult
                )
                1 -> SosHomeScreen(
                    viewModel = viewModel,
                    isSirenActive = isSirenActive,
                    isRecordingAudio = isRecordingAudio,
                    recordingTimerSeconds = recordingTimerSeconds,
                    audioRecordings = audioRecordings,
                    playingAudioPath = playingAudioPath
                )
                2 -> PoliceStationsScreen(
                    viewModel = viewModel,
                    searchQuery = stationSearchQuery,
                    selectedDistrict = selectedDistrict,
                    filteredStations = filteredStations
                )
                3 -> GuardiansScreen(
                    viewModel = viewModel,
                    guardians = guardians
                )
            }
        }
    }
}
