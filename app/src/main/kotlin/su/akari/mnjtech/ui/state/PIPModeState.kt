package su.akari.mnjtech.ui.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.core.app.PictureInPictureModeChangedInfo
import su.akari.mnjtech.ui.local.LocalActivity

@Composable
fun PipModeListener(handler: (PictureInPictureModeChangedInfo) -> Unit) {
    val activity = LocalActivity.current
    DisposableEffect(Unit) {
        activity.addOnPictureInPictureModeChangedListener(handler)
        onDispose {
            activity.removeOnPictureInPictureModeChangedListener(handler)
        }
    }
}