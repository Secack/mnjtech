package su.akari.mnjtech.data.model.online

import androidx.compose.foundation.lazy.LazyListState

data class FilterItem(
    val label: String,
    val query: String,
    var data: List<Config>,
    val useId: Boolean = false,
    var selectedIndex: Int = 0,
    var lazyListState: LazyListState = LazyListState()
)