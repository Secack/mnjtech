package su.akari.mnjtech.ui.component

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.SwipeableState
import androidx.compose.material.swipeable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset

@Composable
fun SwipeableItem(
    modifier: Modifier = Modifier,
    swipeState: SwipeableState<Int>,
    isShowChild: Boolean = true,
    swipeStyle: SwipeStyle = SwipeStyle.EndToStart,
    childContent: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    var childWidth by remember {
        mutableStateOf(1)
    }

    var contentHeight by remember {
        mutableStateOf(1)
    }

    Box(
        modifier.swipeable(
            state = swipeState,
            anchors = mapOf(childWidth.toFloat() to 1, 0.7f to 0),
            thresholds = { _, _ ->
                FractionalThreshold(0.7f)
            },
            reverseDirection = swipeStyle == SwipeStyle.EndToStart,
            orientation = Orientation.Horizontal
        )
    ) {
        Box(modifier = Modifier
            .onGloballyPositioned {
                childWidth = it.size.width
            }
            .height(with(LocalDensity.current) { contentHeight.toDp() })
            .align(if (swipeStyle == SwipeStyle.EndToStart) Alignment.CenterEnd else Alignment.CenterStart)
        ) {
            childContent()
        }
        Box(modifier = Modifier
            .fillMaxWidth()
            .onGloballyPositioned {
                contentHeight = it.size.height
            }
            .offset {
                IntOffset(
                    if (isShowChild) {
                        if (swipeStyle == SwipeStyle.EndToStart) {
                            -swipeState.offset.value.toInt()
                        } else swipeState.offset.value.toInt()
                    } else {
                        0
                    }, 0
                )
            }
        ) {
            content()
        }
    }
}

enum class SwipeStyle {
    StartToEnd,
    EndToStart
}