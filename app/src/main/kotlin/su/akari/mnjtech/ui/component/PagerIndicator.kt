package su.akari.mnjtech.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.TabPosition
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import com.google.accompanist.pager.PagerState
import kotlin.math.absoluteValue

fun Modifier.pagerTabIndicatorOffset(
    pagerState: PagerState,
    tabPositions: List<TabPosition>,
): Modifier = composed {
    if (pagerState.pageCount == 0) return@composed this
    val targetIndicatorOffset: Dp
    val indicatorWidth: Dp
    val currentTab = tabPositions[minOf(tabPositions.lastIndex, pagerState.currentPage)]
    val targetPage = pagerState.currentPage
    val targetTab = tabPositions.getOrNull(targetPage)
    if (targetTab != null) {
        val targetDistance = (targetPage - pagerState.currentPage).absoluteValue
        val fraction =
            (pagerState.currentPageOffset / kotlin.math.max(targetDistance, 1)).absoluteValue
        targetIndicatorOffset = lerp(currentTab.left, targetTab.left, fraction)
        indicatorWidth = lerp(currentTab.width, targetTab.width, fraction).absoluteValue
    } else {
        targetIndicatorOffset = currentTab.left
        indicatorWidth = currentTab.width
    }
    fillMaxWidth()
        .wrapContentSize(Alignment.BottomStart)
        .offset(x = targetIndicatorOffset)
        .width(indicatorWidth)
}

private inline val Dp.absoluteValue: Dp
    get() = value.absoluteValue.dp