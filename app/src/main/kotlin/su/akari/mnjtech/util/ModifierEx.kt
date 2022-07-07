package su.akari.mnjtech.util

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.rotate

fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = composed {
    clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() }
    ) {
        onClick()
    }
}

fun Modifier.animateRotate(targetValue: Float) = composed {
    val rotationAngle by animateFloatAsState(
        targetValue = targetValue,
        animationSpec = tween(
            durationMillis = 150,
            easing = FastOutLinearInEasing
        )
    )
    rotate(rotationAngle)
}