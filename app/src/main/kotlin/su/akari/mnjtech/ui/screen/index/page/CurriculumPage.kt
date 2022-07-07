package su.akari.mnjtech.ui.screen.index.page

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import su.akari.mnjtech.data.model.jwgl.CourseItem
import su.akari.mnjtech.data.model.profile.Profile
import su.akari.mnjtech.ui.component.DashedDivider
import su.akari.mnjtech.ui.component.ScrollableTabRow
import su.akari.mnjtech.ui.component.ThinDivider
import su.akari.mnjtech.ui.local.LocalActivity
import su.akari.mnjtech.ui.screen.index.IndexViewModel
import su.akari.mnjtech.ui.screen.login.awaitJwglLogin
import su.akari.mnjtech.util.*
import java.time.LocalDate
import kotlin.math.roundToInt

@Composable
fun CurriculumPage(viewModel: IndexViewModel) {
    val activity = LocalActivity.current
    val loginJwgl by activity.viewModel.loginJwglFlow.collectAsState()
    val curriculum by viewModel.curriculum.collectAsState()
    val term = activity.viewModel.userData.term!!
    if (viewModel.localCurriculumFetched && viewModel.localCurriculum.isEmpty()) {
        LaunchedEffect(loginJwgl) {
            when (loginJwgl) {
                is DataState.Empty -> activity.viewModel.loginJwgl()
                is DataState.Success -> viewModel.queryCurriculum(term.year, term.term)
                else -> {}
            }
        }
        curriculum.awaitJwglLogin {
            viewModel.saveCurriculum(it, term.week)
        }
    } else {
        Content(viewModel = viewModel, courses = viewModel.localCurriculum, term = term)
    }
}

@Composable
fun Content(viewModel: IndexViewModel, courses: List<CourseItem>, term: Profile.SchoolTerm) {
    val scope = rememberCoroutineScope()
    val startDate by viewModel.startDate.collectAsState()
    val pagerState = rememberPagerState(initialPage = term.week.dec())
    var expanded by rememberSaveable { mutableStateOf(false) }
    var selectedWeek by rememberState(-1)

    if (pagerState.isScrollInProgress.not()) {
        selectedWeek = pagerState.currentPage.inc()
    }
    Column(modifier = Modifier.statusBarsPadding()) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .align(Alignment.Center)
                    .noRippleClickable {
                        expanded = expanded.not()
                    }, verticalAlignment = Alignment.CenterVertically
            ) {
                val isCurrentWeek = selectedWeek == term.week
                Text(
                    text = "第${selectedWeek}周${if (isCurrentWeek) "" else " (非本周)"}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isCurrentWeek) MaterialTheme.colorScheme.onSecondaryContainer else Color.Red
                )
                Icon(
                    modifier = Modifier.animateRotate(if (expanded) 180f else 0f),
                    imageVector = Icons.Outlined.ArrowDropDown,
                    contentDescription = null
                )
            }
            IconButton(modifier = Modifier.align(Alignment.CenterEnd), onClick = {
                viewModel.curriculum.value = DataState.Empty
                viewModel.localCurriculum.clear()
            }) {
                Icon(
                    imageVector = Icons.Outlined.Refresh, contentDescription = null
                )
            }
        }
        ThinDivider()
        AnimatedVisibility(visible = expanded) {
            ScrollableTabRow(
                selectedTabIndex = selectedWeek.dec(),
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ) {
                (1..20).forEach { week ->
                    ThumbCard(
                        week = week,
                        isTermWeek = week == term.week,
                        isSelectedWeek = week == selectedWeek,
                    ) {
                        selectedWeek = it
                        scope.launch {
                            pagerState.animateScrollToPage(it.dec())
                        }
                    }
                }
            }
        }
        HorizontalPager(
            count = 20, state = pagerState
        ) { index ->
            val week = index.inc()
            WeekItem(courses = courses.filter { week in it.weeks },
                week = week,
                isTermWeek = week == term.week,
                startDate = startDate!!,
                showWeekend = courses.any { it.dayOfWeek in 6..7 })
        }
    }
}

@Composable
fun WeekItem(
    courses: List<CourseItem>,
    week: Int,
    isTermWeek: Boolean,
    startDate: LocalDate,
    showWeekend: Boolean
) {
    BoxWithConstraints {
//        val weekSize = if (showWeekEnd) 7 else 5
        val weekSize = 7
        val sectionSize = 10
        val (headerColHeight, headerRowWidth) = 50.dp to 40.dp
        val itemHeight = maxHeight.minus(headerColHeight) / sectionSize
        val itemWidth = maxWidth.minus(headerRowWidth) / weekSize
        CompositionLocalProvider(
            LocalTextStyle provides MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSecondaryContainer, textAlign = TextAlign.Center
            )
        ) {
            Column {
                (1..sectionSize.inc()).forEach { row ->
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        (1..weekSize.inc()).forEach { col ->
                            val (firstRow, firstCol) = (row == 1) to (col == 1)
                            if (firstRow && firstCol) {
                                Column(
                                    modifier = Modifier.size(
                                        width = headerRowWidth, height = headerColHeight
                                    ), horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(modifier = Modifier.align(Alignment.Center),
                                            text = buildAnnotatedString {
                                                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                                    append(remember {
                                                        startDate.plusWeeks(
                                                            week.toLong().dec()
                                                        ).monthValue.toString()
                                                    })
                                                }
                                                append('\n')
                                                append('月')
                                            })
                                    }
                                    ThinDivider()
                                }
                            } else if (firstRow) {
                                val dayOfWeek = col.dec()
                                Column(
                                    modifier = Modifier.size(
                                        width = itemWidth, height = headerColHeight
                                    )
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxWidth()
                                            .padding(2.dp)
                                            .clip(shape = RoundedCornerShape(10))
                                            .background(
                                                color = if (isTermWeek && LocalDate.now().dayOfWeek.value == dayOfWeek) MaterialTheme.colorScheme.tertiaryContainer
                                                else MaterialTheme.colorScheme.secondaryContainer
                                            )
                                    ) {
                                        Text(modifier = Modifier
                                            .align(Alignment.Center)
                                            .padding(horizontal = 8.dp),
                                            text = buildAnnotatedString {
                                                append(getDayOfWeekDisplay(dayOfWeek, true))
                                                append('\n')
                                                withStyle(SpanStyle(fontSize = 10.sp)) {
                                                    with(remember {
                                                        startDate.plusWeeks(week.toLong().dec())
                                                            .plusDays(dayOfWeek.toLong().dec())
                                                    }) {
                                                        append(
                                                            monthValue.toString().padStart(2, '0')
                                                        )
                                                        append('/')
                                                        append(
                                                            dayOfMonth.toString().padStart(2, '0')
                                                        )
                                                    }
                                                }
                                            })
                                    }
                                    ThinDivider()
                                }
                            } else if (firstCol) {
                                val section = row.dec()
                                Box(
                                    modifier = Modifier.size(
                                        width = headerRowWidth, height = itemHeight
                                    )
                                ) {
                                    Text(modifier = Modifier.align(Alignment.Center),
                                        text = buildAnnotatedString {
                                            append(section.toString())
                                            append('\n')
                                            withStyle(SpanStyle(fontSize = 8.sp)) {
                                                append("09:05")
                                                append('\n')
                                                append("09:50")
                                            }
                                        })
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(itemHeight)
                                ) {
                                    DashedDivider(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .align(Alignment.BottomCenter)
                                    )
                                }
                            }
                        }
                    }
                }
            }
            val density = LocalDensity.current
            courses.forEach {
                Surface(modifier = Modifier
                    .size(
                        width = itemWidth, height = itemHeight * it.sections.toList().size
                    )
                    .align { _, _, _ ->
                        with(density) {
                            IntOffset(
                                x = (itemWidth * it.dayOfWeek.dec() + headerRowWidth)
                                    .toPx()
                                    .roundToInt(),
                                y = (itemHeight * it.sections.first.dec() + headerColHeight)
                                    .toPx()
                                    .roundToInt()
                            )
                        }
                    }
                    .padding(2.dp), shape = RoundedCornerShape(10), color = it.color) {
                    Text(
                        modifier = Modifier.padding(2.dp),
                        text = "${it.name}★\n@${it.venue}",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        lineHeight = 14.sp,
                        textAlign = TextAlign.Left
                    )
                }
            }
        }
    }
}

@Composable
fun ThumbCard(
    week: Int, isTermWeek: Boolean, isSelectedWeek: Boolean, onSelected: (Int) -> Unit
) {
    Surface(
        modifier = Modifier
            .height(80.dp)
            .padding(2.dp)
            .noRippleClickable {
                onSelected(week)
            }, color = if (isSelectedWeek) MaterialTheme.colorScheme.onPrimary
        else if (isTermWeek) MaterialTheme.colorScheme.tertiaryContainer
        else MaterialTheme.colorScheme.secondaryContainer, shape = RoundedCornerShape(10)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CompositionLocalProvider(
                LocalTextStyle provides TextStyle.Default.copy(
                    fontSize = 12.sp, color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                Text(modifier = Modifier.padding(horizontal = 2.dp), text = buildAnnotatedString {
                    append("第 ")
                    withStyle(SpanStyle(fontSize = 20.sp)) {
                        append(week.toString())
                    }
                    append(" 周")
                })
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                )
                if (isTermWeek) {
                    Text(
                        text = "(本周)",
                    )
                }
            }
        }
    }
}
