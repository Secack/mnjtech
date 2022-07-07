package su.akari.mnjtech.ui.component

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

suspend fun <T> ListIterator<T>.doWhenHasNextOrPrevious(
    duration: Duration = 3.seconds,
    doWork: suspend (T) -> Unit
) {
    while (hasNext() || hasPrevious()) {
        while (hasNext()) {
            delay(duration)
            doWork(next())
        }
        while (hasPrevious()) {
            delay(duration)
            doWork(previous())
        }
    }
}

@Composable
fun AnimatedPlaceholder(
    hints: List<String>,
    textColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
) {
    val target by produceState(initialValue = hints.first()) {
        hints.listIterator().doWhenHasNextOrPrevious {
            value = it
        }
    }
    AnimatedContent(
        targetState = target,
        transitionSpec = {
            slideInVertically(
                initialOffsetY = { 50 },
                animationSpec = tween()
            ) + fadeIn() with slideOutVertically(
                targetOffsetY = { -50 },
                animationSpec = tween()
            ) + fadeOut()
        }
    ) {
        Text(
            text = it,
            color = textColor,
            fontSize = 14.sp
        )
    }
}