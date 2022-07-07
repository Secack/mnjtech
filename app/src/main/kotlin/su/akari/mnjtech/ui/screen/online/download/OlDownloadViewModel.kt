package su.akari.mnjtech.ui.screen.online.download

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import su.akari.mnjtech.data.dao.AppDataBase
import su.akari.mnjtech.data.model.online.VideoTopic
import java.io.File

class OlDownloadViewModel(
    database: AppDataBase
) : ViewModel() {
    private val topicDao = database.videoTopicDao()
    private val videoDao = database.downloadedVideoDao()
    private val recordDao = database.recordDao()
    val allTopicsFlow = topicDao.getAllTopicsFlow()

    fun getVideoRecord(topicId: Int) =
        recordDao.getRecordById(topicId)

    fun getAllVideosFlow(topicId: Int) =
        videoDao.getAllVideosFlow(topicId)

    fun deleteTopic(videoTopic: VideoTopic) {
        viewModelScope.apply {
            launch {
                videoDao.getAllVideos(videoTopic.id).onEach { video ->
                    File(video.filePath).delete()
                    videoDao.deleteVideo(video)
                }.last().filePath.let {
                    File(it).parentFile!!.delete() //目录为空再删除 不删除分块文件
                }
            }
            launch {
                topicDao.deleteTopic(videoTopic)
            }
        }
    }
}