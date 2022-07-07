package su.akari.mnjtech.ui.screen.online.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.window.PopupProperties
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.launch
import org.koin.androidx.compose.viewModel
import org.koin.core.parameter.parametersOf
import su.akari.mnjtech.data.model.online.Config
import su.akari.mnjtech.data.model.online.Video
import su.akari.mnjtech.ui.component.*
import su.akari.mnjtech.ui.local.LocalNavController
import su.akari.mnjtech.ui.navigation.Destinations
import su.akari.mnjtech.util.DataState
import su.akari.mnjtech.util.handlerWithLoadingAnim
import su.akari.mnjtech.util.observeState
import su.akari.mnjtech.util.rememberState
import kotlin.collections.set

@Composable
fun OlSearchScreen() {
    val navController = LocalNavController.current
    val viewModel by viewModel<OlSearchViewModel> {
        parametersOf(navController.currentBackStackEntry!!.arguments!!)
    }
    val scope = rememberCoroutineScope()
    val lazyGridState = rememberLazyGridState()
    val videoList = viewModel.videoPager.collectAsLazyPagingItems()
    val queryVideos by viewModel.queryVideosFlow.collectAsState()
    var expanded by rememberSaveable {
        mutableStateOf(true)
    }
    var menuExpanded by rememberState(false)
    val videoConfig by viewModel.videoConfigFlow.collectAsState()

    queryVideos.observeState(
        onSuccess = {
            viewModel.queryResult = it.data.videos
            menuExpanded = true
        },
        onEmpty = {
            viewModel.queryResult = listOf()
            menuExpanded = false
        }
    )

    val focusRequester = remember {
        FocusRequester()
    }
    LaunchedEffect(Unit) {
        if (viewModel.category == null && viewModel.filterItems.isEmpty()) {
            focusRequester.requestFocus()
        }
    }

    videoConfig.observeState(
        needUpdate = viewModel.filterItems.isEmpty(),
        onSuccess = {
            viewModel.initFilter(category = viewModel.category)
        }
    )

    Scaffold(
        topBar = {
            Md3TopBar(
                title = {
                    val showDropdown = {
                        viewModel.name.takeIf { it.isNotEmpty() }?.let {
                            viewModel.getVideoByKeyword()
                        } ?: run {
                            viewModel.queryVideosFlow.value = DataState.Empty
                        }
                        videoList.refresh()
                    }
                    SearchBar(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 4.dp)
                            .focusRequester(focusRequester)
                            .onFocusChanged {
                                if (it.isFocused) {
                                    showDropdown()
                                }
                            },
                        value = viewModel.name,
                        onValueChange = {
                            viewModel.name = it
                            showDropdown()
                        },
                        onClear = {
                            viewModel.name = ""
                            videoList.refresh()
                        },
                        onDone = {}
                    )
                    with(viewModel.queryResult) {
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = {
                                viewModel.queryVideosFlow.value = DataState.Empty
                                menuExpanded = false
                            },
                            properties = PopupProperties()
                        ) {
                            fastForEach { video ->
                                video?.let {
                                    DropdownMenuItem(
                                        text = {
                                            Text(it.name)
                                        },
                                        onClick = {
                                            viewModel.queryVideosFlow.value = DataState.Empty
                                            navController.navigate("${Destinations.OlDetail}/${it.id}")
                                        }
                                    )
                                }
                            }
                        }
                    }
                },
                navigationIcon = {
                    BackIcon()
                }
            )
        },
        floatingActionButton = {
            Back2TopFab(lazyGridState) { onBack ->
                scope.launch {
                    videoList.refresh()
                    onBack()
                    expanded = true
                }
            }
        }
    ) { padding ->
        val filterCard = remember {
            movableContentOf {
                FilterCard(
                    viewModel = viewModel,
                    videoList = videoList,
                    expanded = expanded
                ) {
                    expanded = !expanded
                }
            }
        }
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(8.dp)
                    .navigationBarsPadding()
            ) {
                when (videoList.itemCount) {
                    0 -> {
                        Column {
                            filterCard()
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = "什么也没找到～",
                                fontSize = 20.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    else -> {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            state = lazyGridState,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            item(
                                span = {
                                    GridItemSpan(maxCurrentLineSpan)
                                }
                            ) {
                                filterCard()
                            }
                            when (videoList.loadState.refresh) {
                                is LoadState.Error -> {
                                    item(span = {
                                        GridItemSpan(maxCurrentLineSpan)
                                    }) {
                                        Text(
                                            modifier = Modifier
                                                .clickable {
                                                    videoList.refresh()
                                                },
                                            text = "加载失败，点击重试",
                                            fontSize = 20.sp,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                                else -> {
                                    items(videoList.itemCount) {
                                        VideoPreviewCard(videoList[it]!!)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }


@Composable
fun FilterCard(
    viewModel: OlSearchViewModel,
    videoList: LazyPagingItems<Video>,
    expanded: Boolean,
    toggleExpanded: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        Column {
            viewModel.filterItems.fastForEach {
                var selectedIndex by rememberSaveable {
                    mutableStateOf(it.selectedIndex)
                }
                LaunchedEffect(selectedIndex) {
                    if (selectedIndex == 0) {
                        viewModel.filters.remove(it.query)
                    } else {
                        viewModel.filters[it.query] = it.data[selectedIndex.dec()].run {
                            if (it.useId) id.toString() else name
                        }
                    }
                    videoList.refresh()
                }
                val lazyListState = rememberSaveable(saver = LazyListState.Saver) {
                    it.lazyListState
                }
                AnimatedVisibility(
                    visible = expanded,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = it.label
                        )
                        LazyRow(
                            state = lazyListState,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            itemsIndexed(it.data.toMutableList().apply {
                                add(0, Config(0, "全部"))
                            }) { index, config ->
                                ElevatedFilterChip(
                                    selected = selectedIndex == index,
                                    onClick = {
                                        selectedIndex = index
                                        it.selectedIndex = index
                                        it.lazyListState = lazyListState
                                    },
                                    label = {
                                        Text(text = config.name)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
        LaunchedEffect(viewModel.orderIndex) {
            videoList.refresh()
        }
        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "排序方式"
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                itemsIndexed(listOf("最新", "最热", "评分")) { index, order ->
                    ElevatedFilterChip(
                        selected = viewModel.orderIndex == index,
                        onClick = {
                            viewModel.orderIndex = index
                        },
                        label = {
                            Text(text = order)
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = toggleExpanded) {
                Icon(
                    if (!expanded) Icons.Outlined.ExpandMore else Icons.Outlined.ExpandLess,
                    null
                )
            }
        }
    }
}
