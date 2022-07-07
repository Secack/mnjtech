package su.akari.mnjtech.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import su.akari.mnjtech.util.hasBack2Top

@Composable
fun Back2TopFab(
    state: LazyListState,
    modifier: Modifier = Modifier,
    onClick: (onBack: suspend () -> Unit) -> Unit = {}
) {
    AnimatedVisibility(
        visible = state.hasBack2Top().not(),
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it })
    ) {
        FloatingActionButton(
            modifier = modifier.navigationBarsPadding(),
            onClick = {
                onClick {
                    state.animateScrollToItem(0)
                }
            }
        ) {
            Icon(imageVector = Icons.Outlined.ArrowUpward, contentDescription = null)
        }
    }
}

@Composable
fun Back2TopFab(
    state: LazyGridState,
    modifier: Modifier = Modifier,
    onClick: (onBack: suspend () -> Unit) -> Unit = {}
) {
    AnimatedVisibility(
        visible = state.hasBack2Top().not(),
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it })
    ) {
        FloatingActionButton(
            modifier = modifier.navigationBarsPadding(),
            onClick = {
                onClick {
                    state.animateScrollToItem(0)
                }
            }
        ) {
            Icon(imageVector = Icons.Outlined.ArrowUpward, contentDescription = null)
        }
    }
}