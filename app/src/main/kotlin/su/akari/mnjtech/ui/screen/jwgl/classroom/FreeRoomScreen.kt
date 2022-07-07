package su.akari.mnjtech.ui.screen.jwgl.classroom

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastMap
import com.dokar.sheets.rememberBottomSheetState
import kotlinx.coroutines.launch
import org.koin.androidx.compose.viewModel
import su.akari.mnjtech.PR
import su.akari.mnjtech.ui.component.*
import su.akari.mnjtech.ui.local.LocalActivity
import su.akari.mnjtech.ui.local.LocalSelfData
import su.akari.mnjtech.ui.screen.login.awaitJwglLogin
import su.akari.mnjtech.ui.screen.login.initJwglState
import su.akari.mnjtech.util.*
import java.time.LocalDate

@Composable
fun FreeRoomScreen() {
    val viewModel by viewModel<FreeRoomViewModel>()
    val userData = LocalSelfData.current
    val scope = rememberCoroutineScope()

    val activity = LocalActivity.current
    val mainViewModel = activity.viewModel
    val campus by rememberState(1)
    val year by rememberState(userData.term!!.year)
    val term by rememberState(userData.term!!.term.value)
    var week by rememberState(userData.term!!.week)
    var dayOfWeek by rememberState(LocalDate.now().dayOfWeek.value)
    val querySheetState = rememberBottomSheetState()
    val building by PR.building.collectAsState()
    val classMin by PR.classMin.collectAsState()
    val classMax by PR.classMax.collectAsState()
    val sectionList by viewModel.sectionListFlow.collectAsState()
    val roomList by viewModel.roomListFlow.collectAsState()
    val getRoomList = {
        scope.launch {
            viewModel.getRoomList(
                campus = campus,
                year = year,
                term = term,
                week = week,
                dayOfWeek = dayOfWeek,
                building = building,
                classes = classMin..classMax
            )
            querySheetState.expand()
        }
    }

    initJwglState(sectionList) {
        viewModel.getSectionList(campus = campus, year = year, term = term)
    }

    Scaffold(
        topBar = {
            Md3TopBar(title = {
                Text(text = "空教室")
            }, navigationIcon = {
                BackIcon()
            })
        }
    ) { padding ->
        sectionList.awaitJwglLogin {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SelectionRow(
                    label = "周次",
                    items = (1..20).map { "第${it}周" to it },
                    defValue = week,
                ) {
                    week = it
                }
                SelectionRow(
                    label = "星期",
                    items = remember {
                        (1..7).map {
                            getDayOfWeekDisplay(it) to it
                        }
                    },
                    defValue = dayOfWeek,
                ) {
                    dayOfWeek = it
                }

                SelectionRow(
                    label = "教学楼",
                    items = remember {
                        sectionList.read().lhList.sortedBy { it.JXLDM }
                            .fastMap { it.JXLMC to it.JXLDM }
                    },
                    defValue = building
                ) {
                    PR.building.setBlocking(it)
                }
                var showSlider by rememberState(false)
                var range by rememberState(classMin.toFloat()..classMax.toFloat())
                SelectionRow(
                    expanded = showSlider,
                    label = "节次",
                    text = "第${range.start.toInt()}节 到 第${range.endInclusive.toInt()}节"
                ) {
                    showSlider = showSlider.not()
                }
                AnimatedVisibility(visible = showSlider) {
                    RangeSlider(
                        values = range,
                        onValueChange = {
                            range = it
                        },
                        valueRange = 1f..10f,
                        steps = 8,
                        onValueChangeFinished = {
                            scope.launch {
                                PR.classMin.set(range.start.toInt())
                                PR.classMax.set(range.endInclusive.toInt())
                            }
                        },
                        colors = SliderDefaults.colors(
                            activeTickColor = Color.Transparent,
                        )
                    )
                }

                Spacer(modifier = Modifier.height(64.dp))
                Button(modifier = Modifier.fillMaxWidth(), onClick = {
                    getRoomList()
                }) {
                    Text(
                        modifier = Modifier.padding(4.dp),
                        text = "立即查询",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                SimpleBottomSheet(
                    state = querySheetState,
                    title = {
                        Text(
                            text = "空教室",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    },
                    onClose = {
                        scope.launch {
                            querySheetState.collapse()
                        }
                    }
                ) {
                    roomList.handlerWithLoadingAnim(
                        errorAction = {
                            mainViewModel.loginJwgl()
                        }
                    ) { resp ->
                        val list = resp.items
                        if (list.isNotEmpty()) {
                            val headers = listOf(
                                "教室" to 0.4f, "类型" to 0.45f, "座位" to 0.15f
                            )
                            SimpleTable(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp),
                                rowSize = list.size,
                                header = headers,
                                border = true,
                                stripe = true
                            ) { row, col ->
                                with(list[row]) {
                                    when (col) {
                                        0 -> Text(
                                            modifier = Modifier.fillMaxWidth(),
                                            text = cdmc,
                                            textAlign = TextAlign.Center
                                        )
                                        1 -> {
                                            Text(
                                                modifier = Modifier.fillMaxWidth(),
                                                text = cdlbmc.replace('（', '(').replace('）', ')'),
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                        2 -> Text(text = zws)
                                    }
                                }
                            }
                        } else {
                            Centered(modifier = Modifier.fillMaxSize()) {
                                Text(text = "暂无空教室")
                            }
                        }
                    }
                }
            }
        }
    }
}