package su.akari.mnjtech.ui.screen.online.search

import android.os.Bundle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import su.akari.mnjtech.data.model.online.FilterItem
import su.akari.mnjtech.data.model.online.QueryVideo
import su.akari.mnjtech.data.model.online.Video
import su.akari.mnjtech.data.repo.OlRepo
import su.akari.mnjtech.ui.navigation.DestinationArgs
import su.akari.mnjtech.util.DataState
import su.akari.mnjtech.util.OlStateFlow
import su.akari.mnjtech.util.collectAsStateFlow

class OlSearchViewModel(
    args: Bundle,
    private val olRepo: OlRepo
) : ViewModel() {
    val category by mutableStateOf(args.getString(DestinationArgs.Category))
    val videoConfigFlow =
        olRepo.getVideoConfig().stateIn(viewModelScope, SharingStarted.Eagerly, DataState.Empty)
    val filterItems = mutableListOf<FilterItem>()
    var queryResult = listOf<Video?>()
    val filters = mutableMapOf<String, String>()
    var name by mutableStateOf("")
    var queryVideosFlow = OlStateFlow<QueryVideo>()
    private val orders = listOf("id", "play_count", "score").map { "$it desc" }
    var orderIndex by mutableStateOf(0)

    fun initFilter(category: String?) {
        with(videoConfigFlow.value.read().data) {
            filterItems +=
                FilterItem(
                    label = "分类",
                    query = "category",
                    data = categories,
                    selectedIndex = categories.indexOfFirst {
                        it.name == category
                    }.takeIf { it != -1 }?.inc() ?: 0
                )
            filterItems +=
                FilterItem(
                    label = "地区",
                    query = "region",
                    data = regions
                )
            filterItems +=
                FilterItem(
                    label = "语言",
                    query = "language",
                    data = languages
                )
            filterItems +=
                FilterItem(
                    label = "年份",
                    query = "year",
                    data = years
                )
            filterItems +=
                FilterItem(
                    label = "标签",
                    query = "tag",
                    data = tags,
                    useId = true
                )
        }
    }

    fun getVideoByKeyword() {
        olRepo.getVideoByKeyword(name).collectAsStateFlow(viewModelScope, queryVideosFlow)
    }

    val videoPager = Pager(
        config = PagingConfig(
            pageSize = 36,
            prefetchDistance = 72
        )
    ) {
        VideoPagingSource(
            olRepo = olRepo,
            filters = filters,
            name = name,
            order = orders[orderIndex]
        )
    }.flow.cachedIn(viewModelScope)

    class VideoPagingSource(
        val olRepo: OlRepo,
        val filters: Map<String, String>,
        val name: String,
        val order: String,
    ) : PagingSource<Int, Video>() {
        override fun getRefreshKey(state: PagingState<Int, Video>): Int = 1

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Video> {
            val page = params.key ?: 1
            var result: LoadResult<Int, Video>? = null
            olRepo.getVideoList(
                filters = filters,
                name = name.takeIf { it.isNotEmpty() },
                order = order,
                page = page,
                pageSize = 36
            ).collect { value ->
                when (value) {
                    is DataState.Success -> {
                        val data = value.read().data
                        result = LoadResult.Page(
                            data = data.list,
                            prevKey = if (page > 1) page - 1 else null,
                            nextKey = if (data.total / data.pageSize >= page) page + 1 else null
                        )
                    }
                    is DataState.Error -> {
                        result = LoadResult.Error(throwable = Throwable(value.msg))
                    }
                    else -> {}
                }
            }
            return result!!
        }
    }
}