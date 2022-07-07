package su.akari.mnjtech.ui.screen.setting

import androidx.compose.runtime.Composable
import org.koin.androidx.compose.viewModel

@Composable
fun SettingScreen() {
    val viewModel by viewModel<SettingViewModel>()
    
}