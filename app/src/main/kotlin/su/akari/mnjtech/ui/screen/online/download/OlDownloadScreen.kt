package su.akari.mnjtech.ui.screen.online.download

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.arialyy.aria.core.Aria
import com.arialyy.aria.core.download.DownloadEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.viewModel
import su.akari.mnjtech.R
import su.akari.mnjtech.data.model.online.DownloadedVideo
import su.akari.mnjtech.data.model.online.VideoRecord
import su.akari.mnjtech.data.model.online.VideoTopic
import su.akari.mnjtech.service.VideoDownloadService
import su.akari.mnjtech.ui.component.Centered
import su.akari.mnjtech.ui.component.LazyListScaffold
import su.akari.mnjtech.ui.component.VideoCard
import su.akari.mnjtech.ui.navigation.DestinationArgs
import su.akari.mnjtech.util.FileSize
import su.akari.mnjtech.util.ResourceText
import su.akari.mnjtech.util.rememberState
import kotlin.time.Duration.Companion.seconds

@Composable
fun OlDownloadScreen() {
    val viewModel by viewModel<OlDownloadViewModel>()
    val context = LocalContext.current
    val downloading by rememberDownloadingTasks()
    val allTopics by viewModel.allTopicsFlow.collectAsState(initial = emptyList())

    var cancelDialog by rememberState(false)
    if (cancelDialog) {
        AlertDialog(
            title = {
                ResourceText(R.string.ask)
            },
            text = {
                Text(text = "停止所有任务？")
            },
            onDismissRequest = {
                cancelDialog = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        Aria.download(context).stopAllTask()
                        cancelDialog = false
                    }
                ) {
                    ResourceText(R.string.yes)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        cancelDialog = false
                    }
                ) {
                    ResourceText(R.string.cancel)
                }
            }
        )
    }

    LazyListScaffold(
        title = {
            Text(text = "缓存")
        }
    ) { padding, state ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .navigationBarsPadding(),
            state = state
        ) {
            downloading.firstOrNull()?.let {
                stickyHeader {
                    Surface(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            modifier = Modifier.padding(
                                horizontal = 16.dp,
                                vertical = 8.dp
                            ),
                            text = "正在缓存 (${downloading.size})",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                item(it) {
                    Card(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                            .clickable {
                                cancelDialog = true
                            }
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                        ) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = it.fileName
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                LinearProgressIndicator(
                                    progress = it.percent / 100.0f,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = "${it.percent}%"
                                )
                            }
                        }

                    }
                }
            }
            if (allTopics.isNotEmpty()) {
                items(allTopics) { topic ->
                    val videoRecord by viewModel.getVideoRecord(topic.id)
                        .collectAsState(initial = null)
                    val allVideos by viewModel.getAllVideosFlow(topic.id)
                        .collectAsState(initial = emptyList())
                    TopicCard(
                        viewModel = viewModel,
                        videoTopic = topic,
                        videoRecord = videoRecord,
                        allVideos = allVideos
                    )
                }
            } else {
                item {
                    Centered(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(text = "暂无已缓存的视频")
                    }
                }
            }
        }
    }
}

@Composable
private fun TopicCard(
    viewModel: OlDownloadViewModel,
    videoTopic: VideoTopic,
    videoRecord: VideoRecord?,
    allVideos: List<DownloadedVideo>
) {
    val totalSize = allVideos.fold(0L) { acc, video ->
        acc + video.fileSize
    }
    VideoCard(
        videoId = videoTopic.id,
        cover = videoTopic.cover,
        extraRoute = "?${
            videoRecord?.run {
                allVideos.find { it.index == index }?.let {
                    "${DestinationArgs.Index}=${it.index}&${DestinationArgs.Time}=${time}&"
                }
            } ?: ""
        }${DestinationArgs.CacheMode}=${true}",
        onDelete = {
            viewModel.deleteTopic(videoTopic)
        }
    ) {
        Column {
            Text(
                text = videoTopic.name,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(text = "共 ${allVideos.size} 集 | ${FileSize(totalSize)}")
        }
    }
}


@Composable
fun rememberDownloadingTasks(): State<List<DownloadEntity>> {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val state = remember {
        mutableStateOf<List<DownloadEntity>>(emptyList(), neverEqualPolicy())
    }
    DisposableEffect(Unit) {
        val connection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder) {
                scope.launch {
                    while (true) {
                        state.value = (service as VideoDownloadService.DownloadBinder)
                            .getDownloadingTasks()
                        delay(1.seconds)
                    }
                }
            }

            override fun onServiceDisconnected(name: ComponentName?) {
            }
        }

        context.bindService(
            Intent(context, VideoDownloadService::class.java),
            connection,
            Context.BIND_AUTO_CREATE
        )

        onDispose {
            context.unbindService(connection)
        }
    }
    return state
}