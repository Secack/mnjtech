package su.akari.mnjtech.ui.screen.online.record

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.viewModel
import su.akari.mnjtech.data.model.online.Record
import su.akari.mnjtech.ui.component.Centered
import su.akari.mnjtech.ui.component.LazyListScaffold
import su.akari.mnjtech.ui.component.VideoCard
import su.akari.mnjtech.ui.component.VideoPosition
import su.akari.mnjtech.util.format
import su.akari.mnjtech.util.handlerWithLoadingAnim
import su.akari.mnjtech.util.prettyDuration
import su.akari.mnjtech.util.toast

@Composable
fun OlRecordScreen() {
    val context = LocalContext.current
    val viewModel by viewModel<OlRecordViewModel>()
    val recordList by viewModel.recordListFlow.collectAsState()

    LazyListScaffold(
        title = {
            Text(text = "历史记录")
        }
    ) { padding, state ->
        LaunchedEffect(Unit) {
            viewModel.getRecordList()
            state.scrollToItem(0)
        }
        recordList.handlerWithLoadingAnim(
            errorAction = {
                viewModel.getRecordList()
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
                        it.modifiedOn!!.format()
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
                        items(it.value) { record ->
                            RecordCard(record = record) { videoId ->
                                viewModel.deleteRecord(videoId) {
                                    context.toast("删除失败")
                                }
                            }
                        }
                    }
                }
            } else {
                Centered(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(text = "暂无历史记录")
                }
            }
        }
    }
}

@Composable
private fun RecordCard(record: Record, onRemove: (Int) -> Unit) {
    val pos = VideoPosition(
        index = record.url!!.index,
        time = record.time!!.toFloat().times(1000).toLong()
    )
    VideoCard(
        videoId = record.video!!.id,
        cover = record.video.cover,
        extraRoute = "?index=${pos.index}&time=${pos.time}",
        onDelete = onRemove
    ) {
        Column {
            Text(
                text = record.video.name,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(text = "播放至第${pos.index}集 ${pos.time.prettyDuration()}")
            Text(text = record.modifiedOn!!.format(detail = true))
        }
    }
}