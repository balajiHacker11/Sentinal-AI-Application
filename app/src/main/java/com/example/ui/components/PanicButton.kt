package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CrimsonLight
import com.example.ui.theme.CrimsonPrimary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PanicButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    mainText: String = "SOS",
    subText: String = "TAP FOR HELP"
) {
    val coroutineScope = rememberCoroutineScope()
    var isPressedManually by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "sos_pulse")
    val scalePulse by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sos_scale"
    )

    val outerRingAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sos_ring_alpha"
    )

    val buttonScale by animateFloatAsState(
        targetValue = if (isPressedManually) 0.92f else 1.0f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "button_press_scale"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(240.dp)
    ) {
        // Outer animated ripple aura
        Box(
            modifier = Modifier
                .size(230.dp)
                .scale(scalePulse)
                .clip(CircleShape)
                .background(CrimsonLight.copy(alpha = outerRingAlpha))
        )

        // Middle aura ring
        Box(
            modifier = Modifier
                .size(195.dp)
                .clip(CircleShape)
                .background(CrimsonPrimary.copy(alpha = 0.22f))
        )

        // Core tactile interactive panic button
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(165.dp)
                .scale(buttonScale)
                .shadow(20.dp, CircleShape, spotColor = CrimsonPrimary, ambientColor = CrimsonPrimary)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFF5252),
                            CrimsonPrimary,
                            Color(0xFF800000)
                        )
                    )
                )
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            isPressedManually = true
                            tryAwaitRelease()
                            isPressedManually = false
                        },
                        onTap = {
                            coroutineScope.launch {
                                isPressedManually = true
                                delay(120)
                                isPressedManually = false
                                onClick()
                            }
                        }
                    )
                }
                .testTag("sos_panic_button")
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "SOS Alert",
                    tint = Color.White,
                    modifier = Modifier.size(42.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = mainText,
                    color = Color.White,
                    fontSize = if (mainText.length > 5) 20.sp else 28.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.2.sp
                )
                Text(
                    text = subText,
                    color = Color.White.copy(alpha = 0.92f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

