package su.akari.mnjtech.ui.screen.online.detail.tabs

import android.content.Intent
import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEach
import su.akari.mnjtech.data.model.online.DownloadEntry
import su.akari.mnjtech.data.model.online.VideoTopic
import su.akari.mnjtech.service.VideoDownloadService
import su.akari.mnjtech.ui.component.ButtonStyle
import su.akari.mnjtech.ui.component.ButtonX
import su.akari.mnjtech.ui.component.player.PlayerState
import su.akari.mnjtech.ui.local.LocalNavController
import su.akari.mnjtech.ui.navigation.Destinations
import su.akari.mnjtech.ui.screen.online.detail.OlDetailViewModel

@Composable
fun OlDetailSummaryTab(
    playerState: PlayerState,
    hostState: SnackbarHostState,
    olDetailViewModel: OlDetailViewModel,
    videoTopic: VideoTopic,
    onPickerClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
    ) {
        VideoDetail(
            videoTopic = videoTopic,
            viewModel = olDetailViewModel,
            playerState = playerState,
        )
        EpisodePicker(
            playerState = playerState,
            onPickerClick = onPickerClick
        )
        SnackbarHost(hostState = hostState)
    }
}

@Composable
fun VideoDetail(
    videoTopic: VideoTopic,
    viewModel: OlDetailViewModel,
    playerState: PlayerState
) {
    val context = LocalContext.current
    val navController = LocalNavController.current
    val view = LocalView.current

    val allVideos by viewModel.allVideosFlow.collectAsState(initial = emptyList())
    val downloaded = allVideos.size == playerState.episodes.value.size
    var expanded by rememberSaveable {
        mutableStateOf(true)
    }
    ElevatedCard(
        modifier = Modifier.padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = videoTopic.name,
                    style = MaterialTheme.typography.headlineSmall,
                    maxLines = if (expanded) 3 else 1
                )
                if (viewModel.cacheMode.not()) {
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            if (!expanded) Icons.Outlined.ExpandMore else Icons.Outlined.ExpandLess,
                            null
                        )
                    }
                }
            }
            Text(
                text = videoTopic.intro.takeIf { it.isNotEmpty() } ?: "暂无简介",
                maxLines = if (expanded) Int.MAX_VALUE else 5,
                style = MaterialTheme.typography.bodySmall
            )

            if (viewModel.cacheMode.not()) {
                AnimatedVisibility(visible = expanded) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        ButtonX(
                            modifier = Modifier.padding(end = 8.dp),
                            style = ButtonStyle.Outlined,
                            onClick = {
                                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                                val list =
                                    viewModel.videoUrlListFlow.value.read().data.list.filter { it.path.isNotEmpty() }
                                list.fastForEach {
                                    context.startService(
                                        Intent(context, VideoDownloadService::class.java).apply {
                                            putExtra(
                                                "entry", DownloadEntry(
                                                    id = it.id,
                                                    name = it.name,
                                                    index = it.index,
                                                    path = it.path,
                                                    videoTopic = videoTopic
                                                )
                                            )
                                        }
                                    )
                                }
                                navController.navigate(Destinations.OlDownload)
                            },
                            enabled = downloaded.not()
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Download,
                                contentDescription = null
                            )
                            Text(
                                text = if (downloaded) "已缓存" else "缓存"
                            )
                        }
                        Crossfade(targetState = viewModel.isCollect) { isCollect ->
                            ButtonX(
                                style = if (isCollect) ButtonStyle.Outlined else ButtonStyle.Filled,
                                onClick = {
                                    view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                                    viewModel.toggleCollection(videoTopic.id)
                                }) {
                                Icon(Icons.Outlined.Favorite, null)
                                Text(
                                    text = if (isCollect) " 已收藏" else " 收藏"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EpisodePicker(
    playerState: PlayerState,
    onPickerClick: () -> Unit
) {
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clickable(onClick = onPickerClick),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "选集",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "更新至第 ${playerState.episodes.value.lastOrNull()?.index ?: 0} 集 >",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
        LazyRow(
            modifier = Modifier
                .height(50.dp)
                .padding(horizontal = 8.dp),
            state = playerState.episodeListState
        ) {
            items(playerState.episodes.value) { episode ->
                ButtonX(
                    modifier = if (playerState.episodes.value.size == episode.index) Modifier
                    else Modifier.padding(end = 8.dp),
                    style = if (playerState.index.value == episode.index) ButtonStyle.Filled else ButtonStyle.Outlined,
                    onClick = {
                        playerState.setCurrentEpisode(episode.index)
                    }
                ) {
                    Text(text = episode.name)
                }
            }
        }
    }
}
