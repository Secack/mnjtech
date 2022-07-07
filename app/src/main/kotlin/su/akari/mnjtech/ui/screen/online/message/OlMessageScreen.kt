package su.akari.mnjtech.ui.screen.online.message

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import su.akari.mnjtech.ui.component.Centered

@Composable
fun OlMessageScreen() {
    Centered(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(text = "留言已关闭")
    }
}