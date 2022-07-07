package su.akari.mnjtech.ui.component.player

import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import su.akari.mnjtech.ui.component.Centered
import su.akari.mnjtech.ui.state.OnLifecycleEvent
import su.akari.mnjtech.util.rememberState
import com.google.android.exoplayer2.ui.StyledPlayerView
import java.lang.ref.WeakReference

@Composable
private fun VideoPlayerSurface(
    state: PlayerState
) {
    val context = LocalContext.current
    AndroidView(
        factory = {
            StyledPlayerView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }.also {
                state.surfaceView = WeakReference(it)
                it.player = state.player
                it.useController = false
            }
        },
        modifier = Modifier.fillMaxSize()
    )
    DisposableEffect(state) {
        onDispose {
            state.player.release()
        }
    }
}

@Composable
fun VideoPlayer(
    modifier: Modifier = Modifier,
    state: PlayerState,
    controller: @Composable () -> Unit = {}
) {
    var isPlaying by rememberState(false)
    CompositionLocalProvider(
        LocalContentColor provides Color.White
    ) {
        Centered(
            modifier = Modifier
                .background(Color.Black)
                .then(modifier)
        ) {
            VideoPlayerSurface(
                state = state
            )
            controller()
        }

        OnLifecycleEvent { _, event ->
            when (event) {
                Lifecycle.Event.ON_STOP -> {
                    isPlaying = state.isPlaying.value
                    state.player.pause()
                }
                Lifecycle.Event.ON_START -> {
                    if (isPlaying)
                        state.player.play()
                }
                else -> {}
            }
        }
    }
}