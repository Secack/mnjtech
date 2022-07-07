package su.akari.mnjtech.ui.screen.index.page

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import su.akari.mnjtech.ui.component.SearchBar
import su.akari.mnjtech.ui.local.LocalNavController
import su.akari.mnjtech.ui.navigation.Destinations
import su.akari.mnjtech.util.rememberState

data class FuncItem(
    val title: String,
    val route: String
)

@Composable
fun FuncPage() {
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
    var search by rememberState("")
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(16.dp),
    ) {
        stickyHeader {
            SearchBar(
                value = search,
                onValueChange = {
                    search = it
                },
                onClear = {
                    search = ""
                }
            )
        }

        items(list) { funcItem ->
            FuncCard(funcItem = funcItem)
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
            .fillMaxWidth(),
        onClick = {
            navController.navigate(funcItem.route)
        }
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 16.dp),
            text = funcItem.title,
            textAlign = TextAlign.Center
        )
    }
}