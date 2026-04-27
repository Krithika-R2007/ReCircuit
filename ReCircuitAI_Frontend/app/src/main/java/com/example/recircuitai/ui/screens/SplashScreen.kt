package com.example.recircuitai.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.recircuitai.ui.theme.BackgroundGreen
import com.example.recircuitai.ui.theme.PrimaryGreen
import com.example.recircuitai.ui.theme.SecondaryGreen
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onAnimationFinished: () -> Unit) {
    val leafCount = 40
    val leaves = remember {
        List(leafCount) {
            LeafParticle(
                startX = (0..1000).random().toFloat() / 1000f,
                startY = (0..1000).random().toFloat() / 1000f,
                size = (10..70).random().dp,
                rotation = (0..360).random().toFloat(),
                delay = (0..400).random()
            )
        }
    }

    var animateLeaves by remember { mutableStateOf(false) }
    var showLogo by remember { mutableStateOf(false) }

    val logoAlpha by animateFloatAsState(
        targetValue = if (showLogo) 1f else 0f,
        animationSpec = tween(1200, easing = EaseInOutCubic),
        label = "logoAlpha"
    )

    val logoScale by animateFloatAsState(
        targetValue = if (showLogo) 1f else 0.8f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "logoScale"
    )

    LaunchedEffect(Unit) {
        delay(200)
        animateLeaves = true
        delay(1200)
        showLogo = true
        delay(2800)
        onAnimationFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEFFBE0)),
        contentAlignment = Alignment.Center
    ) {
        // Particles Layer
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val screenWidth = maxWidth
            val screenHeight = maxHeight
            
            leaves.forEach { leaf ->
                val progress = animateFloatAsState(
                    targetValue = if (animateLeaves) 1f else 0f,
                    animationSpec = tween(2200, delayMillis = leaf.delay, easing = EaseInOutQuart),
                    label = "leafProgress"
                )

                // Complex path: Random -> Near Center (blurred) -> Edge (Clear)
                val x = if (progress.value < 0.5f) {
                    // Converging
                    lerp(leaf.startX * screenWidth.value, screenWidth.value / 2, progress.value * 2).dp
                } else {
                    // Dispersing - wider spread
                    val angle = (leaf.startX * 2 * Math.PI).toFloat()
                    lerp(screenWidth.value / 2, (screenWidth.value / 2) + Math.cos(angle.toDouble()).toFloat() * screenWidth.value * 1.5f, (progress.value - 0.5f) * 2).dp
                }

                val y = if (progress.value < 0.5f) {
                    lerp(leaf.startY * screenHeight.value, screenHeight.value / 2, progress.value * 2).dp
                } else {
                    val angle = (leaf.startX * 2 * Math.PI).toFloat()
                    lerp(screenHeight.value / 2, (screenHeight.value / 2) + Math.sin(angle.toDouble()).toFloat() * screenHeight.value * 1.5f, (progress.value - 0.5f) * 2).dp
                }

                val blurVal = if (progress.value < 0.4f) 8.dp else if (progress.value > 0.6f) 0.dp else 4.dp
                val alphaVal = if (progress.value > 0.9f) 1f - ((progress.value - 0.9f) * 10) else 1f

                Icon(
                    imageVector = Icons.Default.Eco,
                    contentDescription = null,
                    modifier = Modifier
                        .offset(x = x, y = y)
                        .rotate(leaf.rotation + (progress.value * 360f))
                        .size(leaf.size)
                        .blur(blurVal)
                        .alpha(alphaVal.coerceIn(0f, 1f)),
                    tint = SecondaryGreen.copy(alpha = 0.5f)
                )
            }
        }

        // Central Logo Reveal
        AsyncImage(
            model = "android.resource://com.example.recircuitai/drawable/logo",
            contentDescription = "ReCircuit AI Logo",
            modifier = Modifier
                .size(220.dp)
                .alpha(logoAlpha)
                .scale(logoScale)
        )
    }
}

private fun lerp(start: Float, stop: Float, fraction: Float): Float {
    return start + (stop - start) * fraction
}

data class LeafParticle(
    val startX: Float,
    val startY: Float,
    val size: androidx.compose.ui.unit.Dp,
    val rotation: Float,
    val delay: Int
)
