package su.akari.mnjtech.ui.component

import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import kotlinx.coroutines.launch

@Composable
fun LazyListScaffold(
    title: @Composable () -> Unit,
    navigationIcon: @Composable () -> Unit = { BackIcon() },
    content: @Composable (PaddingValues, LazyListState) -> Unit
) {
    val scope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        decayAnimationSpec = rememberSplineBasedDecay(),
        state = rememberTopAppBarScrollState()
    )
    Scaffold(
        topBar = {
            Md3TopBar(
                title = title,
                navigationIcon = navigationIcon,
                appBarStyle = AppBarStyle.Large,
                scrollBehavior = scrollBehavior
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        floatingActionButton = {
            Back2TopFab(lazyListState) { onBack ->
                scope.launch {
                    onBack()
                }
            }
        },
        content = { content(it, lazyListState) }
    )
}