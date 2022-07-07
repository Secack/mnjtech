package su.akari.mnjtech.ui.screen.online.detail

import android.os.Bundle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import su.akari.mnjtech.data.dao.AppDataBase
import su.akari.mnjtech.data.model.online.*
import su.akari.mnjtech.data.repo.OlRepo
import su.akari.mnjtech.ui.navigation.DestinationArgs
import su.akari.mnjtech.util.OlStateFlow
import su.akari.mnjtech.util.collectAsStateFlow

class OlDetailViewModel(
    args: Bundle,
    private val olRepo: OlRepo,
    private val dataBase: AppDataBase
) : ViewModel() {
    val videoId by mutableStateOf(args.getInt(DestinationArgs.VideoId))
    val index by mutableStateOf(args.getInt(DestinationArgs.Index))
    val time by mutableStateOf(args.getLong(DestinationArgs.Time))
    var cacheMode by mutableStateOf(args.getBoolean(DestinationArgs.CacheMode))

    val videoDetailFlow = OlStateFlow<Video>()
    val videoUrlListFlow = OlStateFlow<VideoUrlListPage>()
    val recordFlow = OlStateFlow<Record>()
    var isCollect by mutableStateOf(false)

    var topic by mutableStateOf<VideoTopic?>(null)
    val allVideosFlow = dataBase.downloadedVideoDao().getAllVideosFlow(videoId)

    init {
        viewModelScope.also { scope ->
            if (cacheMode.not()) {
                with(olRepo) {
                    getRecord(videoId).collectAsStateFlow(scope, recordFlow)
                    getVideoDetail(videoId).collectAsStateFlow(scope, videoDetailFlow)
                    getVideoUrlList(videoId).collectAsStateFlow(scope, videoUrlListFlow)
                }
                runCatching {
                    scope.launch {
                        olRepo.isCollect(videoId).collect { value ->
                            value.readSafely()?.let {
                                isCollect = it.data.isCollect
                            }
                        }
                    }
                }
            } else {
                scope.launch {
                    topic = dataBase.videoTopicDao().getTopicById(videoId)
                }
            }
        }
    }

    fun toggleCollection(videoId: Int) {
        viewModelScope.launch {
            runCatching {
                if (isCollect)
                    olRepo.delCollection(videoId)
                else
                    olRepo.addCollection(videoId)
                isCollect = !isCollect
            }
        }
    }

    fun saveRecord(
        position: Long,
        videoId: Int,
        index: Int,
        urlId: Int,
        onError: (Throwable) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            runCatching {
                if (cacheMode.not()) {
                    olRepo.modifyRecord(
                        time = (position / 1000.0).toString(),
                        videoId = videoId,
                        urlId = urlId
                    )
                } else {
                    dataBase.recordDao().insertRecord(
                        VideoRecord(
                            topicId = videoId,
                            index = index,
                            time = position
                        )
                    )
                }
            }.getOrElse(onError)
        }
    }
}