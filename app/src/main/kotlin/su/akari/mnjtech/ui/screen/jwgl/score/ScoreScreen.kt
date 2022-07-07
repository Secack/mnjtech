package su.akari.mnjtech.ui.screen.jwgl.score

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import org.koin.androidx.compose.viewModel
import su.akari.mnjtech.data.model.jwgl.ScoreItem
import su.akari.mnjtech.ui.component.BackIcon
import su.akari.mnjtech.ui.component.Md3TopBar
import su.akari.mnjtech.ui.component.Centered
import su.akari.mnjtech.ui.local.LocalActivity
import su.akari.mnjtech.ui.screen.login.awaitJwglLogin
import su.akari.mnjtech.ui.screen.login.initJwglState
import su.akari.mnjtech.util.DataState

@Composable
fun ScoreScreen() {
    val viewModel by viewModel<ScoreViewModel>()
    val activity = LocalActivity.current
    val mainViewModel = activity.viewModel
    val loginJwgl by mainViewModel.loginJwglFlow.collectAsState()
    val scoreList by viewModel.scoreListFlow.collectAsState()

    val refreshState =
        rememberSwipeRefreshState(isRefreshing = loginJwgl is DataState.Loading || scoreList is DataState.Loading)

    initJwglState(scoreList) {
        viewModel.getScoreList()
    }

    Scaffold(topBar = {
        Md3TopBar(title = {
            Text(text = "成绩")
        }, navigationIcon = {
            BackIcon()
        })
    }) { padding ->
        SwipeRefresh(modifier = Modifier
            .padding(padding)
            .navigationBarsPadding(),
            state = refreshState,
            onRefresh = {
                viewModel.getScoreList()
            }) {
            scoreList.awaitJwglLogin { resp ->
                val list =
                    resp.items.sortedWith(compareByDescending<ScoreItem> { it.xnm }.thenByDescending { it.xqmmc }
                        .thenByDescending { it.tjsj }).groupBy { it.xnmmc to it.xqmmc }
                        .mapKeys { "${it.key.first}-${it.key.second}" }
                if (list.isNotEmpty()) {
                    LazyColumn {
                        list.forEach {
                            stickyHeader {
                                Surface(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        modifier = Modifier.padding(
                                            horizontal = 16.dp, vertical = 8.dp
                                        ),
                                        text = it.key,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                            item {
                                ScoreGroup(it.value)
                            }
                        }
                    }
                } else {
                    Centered(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(text = "暂无成绩")
                    }
                }
            }
        }
    }
}

@Composable
fun ScoreGroup(termScoreList: List<ScoreItem>) {
    ElevatedCard(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp)
        .clickable {

        }) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            termScoreList.forEach {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = it.kcmc,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
//                    Text(text = it.kcxzmc)
//                    Text(text = it.xf.toDouble().toInt().toString())
                    Text(text = it.cj)
                }
            }
        }
    }
}