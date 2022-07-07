package su.akari.mnjtech.ui.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
inline fun <reified T> rememberSystemService(): T {
    val context = LocalContext.current
    return remember(context) {
        context.getSystemService(T::class.java)
    }
}