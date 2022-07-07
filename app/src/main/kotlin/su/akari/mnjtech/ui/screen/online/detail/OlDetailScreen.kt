package su.akari.mnjtech.ui.screen.online.detail

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastMap
import androidx.core.view.WindowCompat
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player.STATE_ENDED
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.viewModel
import org.koin.core.parameter.parametersOf
import su.akari.mnjtech.data.model.online.VideoTopic
import su.akari.mnjtech.ui.component.CardStyle
import su.akari.mnjtech.ui.component.CardX
import su.akari.mnjtech.ui.component.LoadingAnim
import su.akari.mnjtech.ui.component.pagerTabIndicatorOffset
import su.akari.mnjtech.ui.component.player.Episode
import su.akari.mnjtech.ui.component.player.PlayerController
import su.akari.mnjtech.ui.component.player.PlayerState
import su.akari.mnjtech.ui.component.player.VideoPlayer
import su.akari.mnjtech.ui.local.LocalActivity
import su.akari.mnjtech.ui.local.LocalDarkMode
import su.akari.mnjtech.ui.local.LocalNavController
import su.akari.mnjtech.ui.screen.online.detail.tabs.OlDetailCommentTab
import su.akari.mnjtech.ui.screen.online.detail.tabs.OlDetailSummaryTab
import su.akari.mnjtech.util.DataState
import su.akari.mnjtech.util.prettyDuration
import su.akari.mnjtech.util.rememberState
import su.akari.mnjtech.util.toast
import java.io.File
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@Composable
fun OlDetailScreen() {
    val navController = LocalNavController.current
    val viewModel by viewModel<OlDetailViewModel> {
        parametersOf(navController.currentBackStackEntry!!.arguments!!)
    }
    val record by viewModel.recordFlow.collectAsState()
    val videoDetail by viewModel.videoDetailFlow.collectAsState()
    val videoUrlList by viewModel.videoUrlListFlow.collectAsState()

    val allVideos by viewModel.allVideosFlow.collectAsState(initial = emptyList())

    val isLoaded =
        if (viewModel.cacheMode.not()) record is DataState.Success
                && videoDetail is DataState.Success && videoUrlList is DataState.Success
        else viewModel.topic != null

    val scope = rememberCoroutineScope()
    val activity = LocalActivity.current
    val view = LocalView.current
    val darkMode = LocalDarkMode.current

    var recordJob: Job? by rememberState(null)
    val episodeListState = rememberLazyListState()
    val episodeGridStates = rememberLazyGridState() to rememberLazyGridState()
    val hostState = remember {
        SnackbarHostState()
    }

    var pickerExpanded by rememberState(false)
    val togglePicker = { pickerExpanded = !pickerExpanded }

    val playerState = remember {
        PlayerState(
            context = activity,
            builder = {
                ExoPlayer.Builder(activity)
                    .setHandleAudioBecomingNoisy(true)
                    .build()
            }
        ).also {
            it.episodeListState = episodeListState
            it.episodeGridStates = episodeGridStates
        }
    }

    LaunchedEffect(pickerExpanded) {
        with(playerState.index.value - 1) {
            if (this > 0 && pickerExpanded) {
                playerState.episodeGridStates.first.scrollToItem(this)
            }
        }
    }

    fun saveRecord() {
        val position = playerState.player.currentPosition
        if (position != 0L)
            viewModel.saveRecord(
                position = position,
                videoId = viewModel.videoId,
                index = playerState.index.value,
                urlId = playerState.episodes.value.find { it.index == playerState.index.value }!!.id
            ) {
                activity.toast("保存失败")
            }
    }

    val playerComponent = remember {
        movableContentOf {
            VideoPlayer(
                modifier = Modifier
                    .padding(
                        if (playerState.fullScreen.value)
                            PaddingValues(0.dp)
                        else
                            WindowInsets.statusBars.asPaddingValues()
                    )
                    .then(
                        if (playerState.fullScreen.value) {
                            Modifier.fillMaxHeight()
                        } else {
                            Modifier.aspectRatio(16 / 9f)
                        }
                    ),
                state = playerState
            ) {
                PlayerController(
                    playerState = playerState,
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                if (playerState.fullScreen.value) {
                                    playerState.exitFullScreen(activity)
                                } else {
                                    saveRecord()
                                    navController.popBackStack()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ArrowBack,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    }
                )
            }
        }
    }

    BackHandler {
        if (pickerExpanded) {
            togglePicker()
        } else {
            saveRecord()
            navController.popBackStack()
        }
    }

    DisposableEffect(Unit) {
        runCatching {
            scope.launch {
                delay(0.5.seconds)
                WindowCompat.getInsetsController(activity.window, view).apply {
                    isAppearanceLightStatusBars = false
                }
            }
            onDispose {
                playerState.resumeBrightness()
                WindowCompat.getInsetsController(activity.window, view).apply {
                    isAppearanceLightStatusBars = !darkMode
                }
            }
        }.getOrDefault(onDispose { })
    }

    LaunchedEffect(playerState.isPlaying.value) {
        if (playerState.isPlaying.value) {
            recordJob = launch {
                while (true) {
                    delay(1.minutes)
                    saveRecord()
                }
            }
        } else {
            recordJob?.cancel()
        }
    }

    LaunchedEffect(playerState.playbackState.value) {
        if (playerState.playbackState.value == STATE_ENDED) {
            playerState.skipNext(activity)
        }
    }

    Crossfade(targetState = isLoaded) { loaded ->
        if (loaded) {
            CompatVideoPlayer(
                fullScreen = playerState.fullScreen.value,
                playerState = playerState,
                hostState = hostState,
                viewModel = viewModel,
                videoTopic =
                if (viewModel.cacheMode.not())
                    videoDetail.read().data.let {
                        VideoTopic(
                            id = it.id,
                            name = it.name,
                            cover = it.cover,
                            intro = it.intro ?: ""
                        )
                    } else viewModel.topic!!,
                pickerExpanded = pickerExpanded,
                onPickerClick = togglePicker,
                playerComponent = playerComponent
            )
            LaunchedEffect(Unit) {
                if (viewModel.cacheMode.not()) {
                    playerState.initEpisodes(
                        episodes = videoUrlList.read().data.list.filter { it.path.isNotEmpty() }
                            .fastMap {
                                Episode(
                                    index = it.index,
                                    id = it.id,
                                    name = it.name,
                                    mediaItem = MediaItem.fromUri(it.path)
                                )
                            }
                    )
                } else {
                    playerState.initEpisodes(
                        episodes = allVideos.fastMap {
                            Episode(
                                index = it.index,
                                id = it.id,
                                name = it.name,
                                mediaItem = MediaItem.fromUri(Uri.fromFile(File(it.filePath)))
                            )
                        }
                    )
                }
                viewModel.time.takeIf { it != -1L }?.let {
                    playerState.setCurrentEpisode(viewModel.index, it)
                } ?: run {
                    playerState.episodes.value.firstOrNull()?.let {
                        playerState.setCurrentEpisode(it.index, 0L, false)
                    }
                    if (viewModel.cacheMode.not()) {
                        record.read().data.let {
                            with(
                                (it.url?.index ?: 0) to (it.time?.toFloat() ?: 0f).times(1000)
                                    .toLong()
                            ) {
                                if (first != 0) {
                                    hostState.showSnackbar(
                                        "上次播放至第 $first 集 - ${second.prettyDuration()}",
                                        "点击跳转"
                                    )
                                        .takeIf { result -> result == SnackbarResult.ActionPerformed }
                                        ?.let {
                                            playerState.setCurrentEpisode(first, second)
                                        }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            LoadingAnim()
        }
    }
}

@Composable
private fun CompatVideoPlayer(
    fullScreen: Boolean,
    playerState: PlayerState,
    hostState: SnackbarHostState,
    viewModel: OlDetailViewModel,
    videoTopic: VideoTopic,
    pickerExpanded: Boolean,
    onPickerClick: () -> Unit,
    playerComponent: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        playerComponent()
        if (!fullScreen) {
            LaunchedEffect(playerState.index.value) {
                val index = playerState.index.value - 1
                if (index > 0) {
                    playerState.episodeListState.scrollToItem(index)
                }
            }
            val pagerState = rememberPagerState(0)
            val scope = rememberCoroutineScope()
            AnimatedVisibility(visible = pickerExpanded) {
                LazyVerticalGrid(
                    state = playerState.episodeGridStates.first,
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                        .navigationBarsPadding(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    item(span = {
                        GridItemSpan(maxCurrentLineSpan)
                    }) {
                        Row(
                            modifier = Modifier.height(40.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "选集 (${playerState.episodes.value.size})",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(onClick = onPickerClick) {
                                Icon(imageVector = Icons.Outlined.Close, contentDescription = null)
                            }
                        }
                    }
                    items(playerState.episodes.value) { episode ->
                        val isCurrent = playerState.index.value == episode.index
                        CardX(
                            modifier = Modifier.fillMaxSize(),
                            style = if (isCurrent) CardStyle.Filled else CardStyle.Outlined,
                            onClick = {
                                playerState.setCurrentEpisode(episode.index)
                            }
                        ) {
                            Text(
                                modifier = Modifier
                                    .aspectRatio(16 / 9f)
                                    .padding(8.dp),
                                text = episode.name
                            )
                        }
                    }
                }
            }
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                indicator = {
                    TabRowDefaults.Indicator(Modifier.pagerTabIndicatorOffset(pagerState, it))
                }
            ) {
                Tab(
                    selected = pagerState.currentPage == 0,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(0)
                        }
                    },
                    text = {
                        Text(text = "简介")
                    }
                )
                Tab(
                    selected = pagerState.currentPage == 1,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(1)
                        }
                    },
                    text = {
                        Text(text = "评论")
                    }
                )
            }
            HorizontalPager(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                state = pagerState,
                count = 2
            ) {
                when (it) {
                    0 -> OlDetailSummaryTab(
                        playerState = playerState,
                        hostState = hostState,
                        olDetailViewModel = viewModel,
                        videoTopic = videoTopic,
                        onPickerClick = onPickerClick
                    )
                    1 -> OlDetailCommentTab()
                }
            }
        }
    }
}
