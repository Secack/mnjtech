package su.akari.mnjtech.ui.screen.online.detail.tabs

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import su.akari.mnjtech.ui.component.Centered

@Composable
fun OlDetailCommentTab() {
    Centered(
        modifier = Modifier.fillMaxSize()
    ) {
        Text("评论暂时关闭")
    }
}