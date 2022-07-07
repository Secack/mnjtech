package su.akari.mnjtech.ui.screen.online.index

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.viewModel
import su.akari.mnjtech.PR
import su.akari.mnjtech.R
import su.akari.mnjtech.data.model.online.Video
import su.akari.mnjtech.ui.component.*
import su.akari.mnjtech.ui.local.LocalActivity
import su.akari.mnjtech.ui.local.LocalNavController
import su.akari.mnjtech.ui.navigation.DestinationArgs
import su.akari.mnjtech.ui.navigation.Destinations
import su.akari.mnjtech.ui.screen.login.initLoginState
import su.akari.mnjtech.util.*
import kotlin.time.Duration.Companion.seconds

var exceedDeviceLimit = ""

@Composable
fun OlIndexScreen() {
    val viewModel by viewModel<OlIndexViewModel>()
    val activity = LocalActivity.current
    val navController = LocalNavController.current
    val scope = rememberCoroutineScope()
    val loginOnline by activity.viewModel.loginOnlineFlow.collectAsState()
    val carouselList by viewModel.carouselListFlow.collectAsState()
    val hottestWeekly by viewModel.weeklyHottestFlow.collectAsState()
    val latestVideo by viewModel.latestVideoFlow.collectAsState()
    val latestAnnouncement by viewModel.latestAnnouncementFlow.collectAsState()
    val lazyGridState = rememberLazyGridState()
    val isLoaded =
        carouselList is DataState.Success && hottestWeekly is DataState.Success && latestVideo is DataState.Success
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    BackHandler {
        if (drawerState.isOpen) {
            scope.launch {
                drawerState.close()
            }
        } else {
            navController.popBackStack()
        }
    }

    initLoginState(
        loginFlow = activity.viewModel.loginOnlineFlow,
        initLogin = {
            activity.viewModel.loginOnline()
        },
        initError = { msg ->
            if (msg.isEmpty()) {
                exceedDeviceLimit.takeIf { it.isEmpty() }?.let {
                    activity.toast("会话过期，请重新登录")
                    navController.navigate("${Destinations.Login}?${DestinationArgs.AutoLogin}=${1}") {
                        popUpTo(0)
                    }
                }
            }
        },
        loginOnline, carouselList, hottestWeekly, latestVideo, latestAnnouncement,
        initChildren = {
            viewModel.init()
        }
    )

    LaunchedEffect(Unit) {
        while (true) {
            if (loginOnline is DataState.Success) {
                viewModel.getProfileCount()
            }
            delay(5.seconds)
        }
    }

    var announcementDialog by rememberState(false)
    latestAnnouncement.observeState(
        onSuccess = {
            if (it.data.id != PR.announcement.get()) {
                announcementDialog = true
            }
        }
    )

    if (announcementDialog) {
        AlertDialog(
            title = {
                Text(text = latestAnnouncement.read().data.title)
            },
            text = {
                HtmlText(
                    text = latestAnnouncement.read().data.content,
                    imageFillWidth = true
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        PR.announcement.setBlocking(latestAnnouncement.read().data.id)
                        announcementDialog = false
                    }
                ) {
                    ResourceText(R.string.ok)
                }
            },
            onDismissRequest = {},
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            OlIndexDrawer(
                viewModel = viewModel,
                drawerState = drawerState
            )
        }
    ) {
        Scaffold(topBar =
        {
            Md3TopBar(
                title = {
                    Text(
                        text = "南工在线",
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    MenuIcon(drawerState)
                },
                actions = {
                    if (loginOnline is DataState.Success) {
                        IconButton(onClick = {
                            navController.navigate(Destinations.OlSearch)
                        }) {
                            Icon(Icons.Outlined.Search, null)
                        }
                    }
                },
                appBarStyle = AppBarStyle.CenterAligned
            )
        })
        { padding ->
            Crossfade(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .navigationBarsPadding(),
                targetState = isLoaded
            ) { loaded ->
                if (loaded) {
                    mutableMapOf<String, List<Video>>().apply {
                        hottestWeekly.onSuccess { resp ->
                            put("一周热播", resp.data.video)
                        }
                        latestVideo.onSuccess { resp ->
                            with(resp.data) {
                                putAll(
                                    mapOf(
                                        "电影" to movie,
                                        "电视剧" to teleplay,
                                        "动漫" to cartoon,
                                        "纪录片" to fact,
                                        "学习" to study,
                                        "网络安全专题" to anti_fraud
                                    )
                                )
                            }
                        }
                    }.run {
                        LazyVerticalGrid(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            columns = GridCells.Fixed(3),
                            state = lazyGridState,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            item(span = {
                                GridItemSpan(maxCurrentLineSpan)
                            }) {
                                carouselList.onSuccess { resp ->
                                    CarousePager(resp.data.list)
                                }
                            }
                            forEach {
                                item(span = {
                                    GridItemSpan(maxCurrentLineSpan)
                                }) {
                                    Row(
                                        modifier = Modifier
                                            .heightIn(min = 48.dp)
                                            .padding(horizontal = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = it.key,
                                            fontSize = 20.sp
                                        )
                                        it.value.first().category
                                            ?: ElevatedAssistChip(
                                                onClick = {
                                                    navController.navigate("${Destinations.OlSearch}?${DestinationArgs.Category}=${it.key}")
                                                },
                                                label = {
                                                    Text(
                                                        text = "更多"
                                                    )
                                                }
                                            )
                                    }
                                }
                                items(it.value) { video ->
                                    VideoPreviewCard(video = video)
                                }
                            }
                        }
                    }
                } else {
                    if (loginOnline is DataState.Error) {
                        ErrorAnim(text = exceedDeviceLimit.takeIf { it.isNotEmpty() }
                            ?: (loginOnline as DataState.Error).msg) {
                            activity.viewModel.loginOnline()
                        }
                    } else {
                        LoadingAnim()
                    }
                }
            }
        }
    }
}

@SuppressLint("UnsafeOptInUsageError")
@Composable
fun CarousePager(item: List<Video>) {
    val navController = LocalNavController.current
    val pagerState = rememberPagerState()
    Box(
        modifier = Modifier
            .aspectRatio(16 / 9f)
            .padding(bottom = 8.dp)
            .clip(MaterialTheme.shapes.small)
            .clickable {
                navController.navigate("${Destinations.OlDetail}/${item[pagerState.currentPage].topicId}")
            },
        contentAlignment = Alignment.BottomCenter
    ) {
        HorizontalPager(
            count = item.size,
            state = pagerState
        ) { page ->
            AsyncImage(
                modifier = Modifier.fillMaxSize(),
                model = item[page].cover,
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
        }
        HorizontalPagerIndicator(
            pagerState = pagerState,
            modifier = Modifier
                .padding(16.dp),
            activeColor = MaterialTheme.colorScheme.primary,
            inactiveColor = MaterialTheme.colorScheme.background
        )
    }

    var underDragging by rememberState(false)
    LaunchedEffect(Unit) {
        pagerState.interactionSource.interactions.collect { interaction ->
            underDragging = when (interaction) {
                is PressInteraction.Press -> true
                is PressInteraction.Release -> false
                is PressInteraction.Cancel -> false
                is DragInteraction.Start -> true
                is DragInteraction.Stop -> false
                is DragInteraction.Cancel -> false
                else -> false
            }
        }
    }
    LaunchedEffect(underDragging) {
        if (!underDragging) {
            runCatching {
                while (true) {
                    delay(2.seconds)
                    val nextPage = pagerState.currentPage + 1
                    pagerState.animateScrollToPage(nextPage.takeIf { nextPage < pagerState.pageCount }
                        ?: 0)
                }
            }
        }
    }
}
