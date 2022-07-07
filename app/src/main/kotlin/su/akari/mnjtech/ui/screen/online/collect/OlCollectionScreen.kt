package su.akari.mnjtech.ui.screen.online.collect

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.viewModel
import su.akari.mnjtech.data.model.online.Collection
import su.akari.mnjtech.ui.component.Centered
import su.akari.mnjtech.ui.component.LazyListScaffold
import su.akari.mnjtech.ui.component.VideoCard
import su.akari.mnjtech.util.format
import su.akari.mnjtech.util.handlerWithLoadingAnim

@Composable
fun OlCollectionScreen() {
    val viewModel by viewModel<OlCollectionViewModel>()
    val collectionList by viewModel.collectionListFlow.collectAsState()

    LazyListScaffold(
        title = {
            Text(text = "收藏")
        }
    ) { padding, state ->
        LaunchedEffect(Unit) {
            viewModel.getCollectionList()
            state.scrollToItem(0)
        }
        collectionList.handlerWithLoadingAnim(
            errorAction = {
                viewModel.getCollectionList()
            }
        ) { resp ->
            val list = resp.data.list
            if (list.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .padding(padding)
                        .navigationBarsPadding(),
                    state = state
                ) {
                    list.groupBy {
                        it.modifiedOn.format()
                    }.forEach {
                        stickyHeader {
                            Surface(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    modifier = Modifier.padding(
                                        horizontal = 16.dp,
                                        vertical = 8.dp
                                    ),
                                    text = it.key,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        items(it.value) { collection ->
                            CollectionCard(collection = collection) { videoId ->
                                viewModel.delCollection(videoId)
                            }
                        }
                    }
                }
            } else {
                Centered(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(text = "暂无收藏")
                }
            }
        }
    }
}

@Composable
private fun CollectionCard(collection: Collection, onDelete: (Int) -> Unit) {
    VideoCard(
        videoId = collection.video.id,
        cover = collection.video.cover,
        onDelete = onDelete
    ) {
        Column {
            Text(
                text = collection.video.name,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(text = "收藏于 ${collection.modifiedOn.format(detail = true)}")
        }
    }
}