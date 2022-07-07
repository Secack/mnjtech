package su.akari.mnjtech.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import me.rerere.md3compat.basicColorSchemeList
import me.rerere.md3compat.dynamicColorSchemeList
import su.akari.mnjtech.ui.local.LocalActivity
import su.akari.mnjtech.ui.local.LocalDarkMode

@Composable
fun MNjtechTheme(
    content: @Composable () -> Unit
) {
    val darkTheme = isSystemInDarkTheme()

    CompositionLocalProvider(
        LocalDarkMode provides darkTheme
    ) {
        ApplyBarColor()
        MaterialTheme(
            colorScheme = dynamicColorSchemeList(darkTheme).firstOrNull()
                ?: basicColorSchemeList().first(), typography = Typography
        ) {
            androidx.compose.material.MaterialTheme(
                colors = MaterialTheme.colorScheme.toLegacyColor(darkTheme), content = content
            )
        }
    }
}

@Composable
fun ApplyBarColor(darkTheme: Boolean = LocalDarkMode.current) {
    val view = LocalView.current
    val activity = LocalActivity.current
    SideEffect {
        activity.window.apply {
            statusBarColor = Color.Transparent.toArgb()
            navigationBarColor = Color.Transparent.toArgb()
        }
        WindowCompat.getInsetsController(activity.window, view).apply {
            isAppearanceLightNavigationBars = !darkTheme
            isAppearanceLightStatusBars = !darkTheme
        }
    }
}