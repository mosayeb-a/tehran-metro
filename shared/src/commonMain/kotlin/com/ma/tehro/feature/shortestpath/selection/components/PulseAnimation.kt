package com.ma.tehro.feature.shortestpath.selection.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch

data class PulseAnimation(
    val scale: Animatable<Float, *>,
    val trigger: () -> Unit,
    val isAnimating: Boolean
)

@Composable
fun rememberPulseAnimation(
    onPulseComplete: () -> Unit = {}
): PulseAnimation {
    val scope = rememberCoroutineScope()
    val scale = remember { Animatable(1f) }
    var isAnimating by remember { mutableStateOf(false) }

    val trigger: () -> Unit = {
        if (!isAnimating) {
            isAnimating = true
            scope.launch {
                scale.animateTo(
                    targetValue = 1.3f,
                    animationSpec = spring(dampingRatio = 0.8f, stiffness = 2000f)
                )
                scale.animateTo(
                    targetValue = 0.8f,
                    animationSpec = spring(dampingRatio = 0.8f, stiffness = 2000f)
                )
                scale.animateTo(
                    targetValue = 1f,
                    animationSpec = spring(dampingRatio = 0.8f, stiffness = 2000f)
                )
                isAnimating = false
                onPulseComplete()
            }
        }
    }

    return PulseAnimation(scale, trigger, isAnimating)
}