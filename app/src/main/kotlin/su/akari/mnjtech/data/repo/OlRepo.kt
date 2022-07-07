package su.akari.mnjtech.data.repo

import su.akari.mnjtech.data.api.OlApi
import su.akari.mnjtech.util.dataStateFlow

class OlRepo(private val olApi: OlApi) {
    fun loginOnline() = dataStateFlow {
        olApi.loginOnline()
    }

    fun getCarouselList() = dataStateFlow {
        olApi.getCarouselList()
    }

    fun getWeeklyHottest() = dataStateFlow {
        olApi.getWeeklyHottest()
    }

    fun getLatestVideo() = dataStateFlow {
        olApi.getLatestVideo()
    }

    fun getUserDetail() = dataStateFlow {
        olApi.getUserDetail()
    }

    fun getProfileCount() = dataStateFlow {
        olApi.getProfileCount()
    }

    fun getVideoDetail(videoId: Int) = dataStateFlow {
        olApi.getVideoDetail(videoId)
    }

    fun getVideoUrlList(videoId: Int) = dataStateFlow {
        olApi.getVideoUrlList(videoId)
    }

    fun getCollectionList() = dataStateFlow {
        olApi.getCollectionList()
    }

    suspend fun addCollection(videoId: Int) =
        olApi.addCollection(videoId)

    suspend fun delCollection(videoId: Int) =
        olApi.delCollection(videoId)

    fun isCollect(videoId: Int) = dataStateFlow {
        olApi.isCollect(videoId)
    }

    fun getRecordList() = dataStateFlow {
        olApi.getRecordList()
    }

    fun getRecord(videoId: Int) = dataStateFlow {
        olApi.getRecord(videoId = videoId)
    }

    suspend fun modifyRecord(time: String, videoId: Int, urlId: Int) =
        olApi.modifyRecord(
            time = time,
            videoId = videoId,
            urlId = urlId
        )

    suspend fun deleteRecord(videoId: Int) =
        olApi.deleteRecord(videoId)

    fun getVideoConfig() = dataStateFlow {
        olApi.getVideoConfig()
    }

    fun getVideoList(
        filters: Map<String, String>,
        name: String?,
        order: String,
        page: Int,
        pageSize: Int
    ) = dataStateFlow {
        olApi.getVideoList(filters, name, order, page, pageSize)
    }

    fun getVideoByKeyword(keyword: String) = dataStateFlow {
        olApi.getVideoByKeyword(keyword)
    }

    fun getCommentList() = dataStateFlow {
        olApi.getCommentList()
    }

    fun getLatestAnnouncement() = dataStateFlow {
        olApi.getLatestAnnouncement()
    }

    fun getAnnouncementList() = dataStateFlow {
        olApi.getAnnouncementList()
    }

    suspend fun markNotify(id: Int) =
        olApi.markNotify(id)

    fun getNotificationList() = dataStateFlow {
        olApi.getNotificationList()
    }
}