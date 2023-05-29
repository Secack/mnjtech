package su.akari.mnjtech.ui.screen.index.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import su.akari.mnjtech.ui.component.Centered
import su.akari.mnjtech.ui.component.Md3TopBar
import su.akari.mnjtech.ui.local.LocalNavController
import su.akari.mnjtech.ui.local.LocalSelfData
import su.akari.mnjtech.ui.navigation.Destinations
import su.akari.mnjtech.util.getHourDisplay

@Composable
fun HomePage() {
    val list = listOf(
        FuncItem(
            title = "南工在线",
            route = Destinations.OlIndex
        ),
        FuncItem(
            title = "成绩查询",
            route = Destinations.Score
        ),
        FuncItem(
            title = "查空教室",
            route = Destinations.FreeRoom
        ),
        FuncItem(
            title = "教学评价",
            route = Destinations.Evaluation
        )
    )
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(16.dp),
            ) {

                items(list) { funcItem ->
                    FuncCard(funcItem = funcItem)
                }
            }

        }
    }
}

@Composable
fun FuncCard(
    funcItem: FuncItem
) {
    val navController = LocalNavController.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        onClick = {
            navController.navigate(funcItem.route)
        }
    ) {
        Centered(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Text(
                text = funcItem.title,
                textAlign = TextAlign.Center,
                fontSize = 24.sp,

                )
        }
    }
}

data class FuncItem(
    val title: String,
    val route: String
)
