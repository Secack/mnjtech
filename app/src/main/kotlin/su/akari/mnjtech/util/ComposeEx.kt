package su.akari.mnjtech.util

import androidx.annotation.StringRes
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.stringResource

@Composable
fun <T> rememberState(value: T) = remember {
    mutableStateOf(value)
}

fun <T> stateSaver() = Saver<MutableState<T>, Any>(
    save = { state -> state.value ?: "null" },
    restore = { value ->
        @Suppress("UNCHECKED_CAST")
        mutableStateOf((if (value == "null") null else value) as T)
    }
)

@Composable
fun ResourceText(@StringRes id: Int) = Text(stringResource(id))

@Composable
private fun ScrollableState.hasBack2Top(firstVisibleItemIndexState: State<Int>): Boolean {
    val firstVisibleItemIndex by firstVisibleItemIndexState
    var hasScrolled by rememberSaveable(this) { mutableStateOf(false) }
    var previousIndex by rememberSaveable(this) { mutableStateOf(firstVisibleItemIndex) }
    return remember(this) {
        derivedStateOf {
            hasScrolled.not().or(
                (previousIndex >= firstVisibleItemIndex).and(firstVisibleItemIndex == 0)
            ).also {
                if (firstVisibleItemIndex != previousIndex) {
                    hasScrolled = true
                }
                previousIndex = firstVisibleItemIndex
            }
        }
    }.value
}

@Composable
fun LazyListState.hasBack2Top(): Boolean =
    hasBack2Top(remember { derivedStateOf { firstVisibleItemIndex } })

@Composable
fun LazyGridState.hasBack2Top(): Boolean =
    hasBack2Top(remember { derivedStateOf { firstVisibleItemIndex } })