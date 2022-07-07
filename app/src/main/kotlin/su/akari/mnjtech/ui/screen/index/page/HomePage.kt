package su.akari.mnjtech.ui.screen.index.page

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import su.akari.mnjtech.ui.component.Md3TopBar
import su.akari.mnjtech.ui.component.NoThumbSlider
import su.akari.mnjtech.ui.component.Centered
import su.akari.mnjtech.ui.local.LocalSelfData
import su.akari.mnjtech.util.getHourDisplay

@Composable
fun HomePage() {

    val userData = LocalSelfData.current
    val date = remember {
        getHourDisplay()
    }
    Scaffold(
        topBar = {
            Md3TopBar(
                title = {
                    Text(text = "${date}好，${userData.name}")
                }
            )
        }
    ) { padding ->
        Centered(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "正在做了",
                    style = MaterialTheme.typography.headlineLarge
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "进度 ")
                    NoThumbSlider(
                        value = 0f,
                        onValueChange = {}
                    )
                }
            }

        }
    }
}