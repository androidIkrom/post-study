package com.example.poststudy.presentation.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Modifier.hoverEffect(
    scale: Float = 1.05f, // Increased default scale
    yOffset: Float = -12f, // Lift up effect
    durationMillis: Int = 250
): Modifier {
    var isHovered by remember { mutableStateOf(false) }
    
    val animatedScale by animateFloatAsState(
        targetValue = if (isHovered) scale else 1.0f,
        animationSpec = tween(durationMillis)
    )
    
    val animatedOffset by animateFloatAsState(
        targetValue = if (isHovered) yOffset else 0f,
        animationSpec = tween(durationMillis)
    )

    return this
        .onPointerEvent(PointerEventType.Enter) { isHovered = true }
        .onPointerEvent(PointerEventType.Exit) { isHovered = false }
        .graphicsLayer {
            scaleX = animatedScale
            scaleY = animatedScale
            translationY = animatedOffset
        }
}
