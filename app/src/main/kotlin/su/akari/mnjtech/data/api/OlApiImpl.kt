package su.akari.mnjtech.data.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import su.akari.mnjtech.data.api.service.OlParser
import su.akari.mnjtech.data.api.service.OlService
import su.akari.mnjtech.data.model.online.*

const val PAGE_SIZE = 400

class OlApiImpl(
    private val olParser: OlParser,
    private val olService: OlService
) : OlApi {

    private inline fun <T, reified U : PageList<T>> getAllPageList(
        func: (page: Int, pageSize: Int) -> OlResponse<U>
    ): OlResponse<U> = mutableListOf<T>().also { ret ->
        with(func(1, PAGE_SIZE).data) {
            ret += list
            total.minus(PAGE_SIZE).takeIf { it > 0 }?.run {
                repeat(div(PAGE_SIZE).inc()) {
                    ret += func(it.plus(2), PAGE_SIZE).data.list
                }
            }
        }
    }.let {
        OlResponse(
            data = U::class.java.getDeclaredConstructor(
                List::class.java,
                Int::class.java,
                Int::class.java
            ).newInstance(it, PAGE_SIZE, it.size),
            code = 200,
            msg = ""
        )
    }

    override suspend fun loginOnline() = olParser.loginOnline()

    override suspend fun getCarouselList() =
        olService.getCarouselList(pageSize = 8, page = 1, genre = "video")

    override suspend fun getWeeklyHottest() = olService.getWeeklyHottest(pageSize = 6)

    override suspend fun getLatestVideo() = olService.getLatestVideo(pageSize = 6)

    override suspend fun getUserDetail() = olService.getUserDetail()

    override suspend fun getProfileCount(): OlResponse<ProfileCount> = olService.getProfileCount()

    override suspend fun getVideoDetail(videoId: Int) = olService.getVideoDetail(videoId)

    override suspend fun getVideoUrlList(videoId: Int): OlResponse<VideoUrlListPage> =
        getAllPageList { page, pageSize ->
            withContext(Dispatchers.IO) {
                olService.getUrlList(
                    topicId = videoId,
                    page = page,
                    pageSize = pageSize,
                    genre = "video",
                    sort = "asc"
                )
            }
        }

    override suspend fun getCollectionList(): OlResponse<CollectionListPage> =
        getAllPageList { page, pageSize ->
            olService.getCollectionList(
                page = page, pageSize = pageSize, genre = "video"
            )
        }

    override suspend fun addCollection(videoId: Int) = olService.addCollection(
        CollectPayload(
            topicId = videoId, genre = "video"
        )
    )

    override suspend fun delCollection(videoId: Int) = olService.delCollection(
        topicId = videoId, genre = "video"
    )

    override suspend fun isCollect(videoId: Int) = olService.isCollect(
        topicId = videoId,
        genre = "video",
    )

    override suspend fun getRecordList(): OlResponse<RecordListPage> =
        getAllPageList { page, pageSize ->
            olService.getRecordList(
                page = page, pageSize = pageSize, genre = "video"
            )
        }

    override suspend fun getRecord(videoId: Int) = olService.getRecord(
        topicId = videoId, genre = "video"
    )

    override suspend fun modifyRecord(time: String, videoId: Int, urlId: Int) =
        olService.modifyRecord(
            RecordPayload(
                time = time, topicId = videoId, urlId = urlId, genre = "video"
            )
        )

    override suspend fun deleteRecord(videoId: Int) = olService.deleteRecord(
        topicId = videoId, genre = "video"
    )

    override suspend fun getVideoConfig() = olService.getVideoConfig()

    override suspend fun getVideoList(
        filters: Map<String, String>, name: String?, order: String, page: Int, pageSize: Int
    ) = olService.getVideoList(
        filters = filters, name = name, order = order, page = page, pageSize = pageSize
    )

    override suspend fun getVideoByKeyword(keyword: String) = olService.getVideoByKeyword(keyword)

    override suspend fun getCommentList() = getAllPageList { page, pageSize ->
        olService.getCommentList(
            page = page, pageSize = pageSize, genre = "message", order = "id desc"
        )
    }

    override suspend fun getLatestAnnouncement(): OlResponse<Announcement> =
        olService.getLatestAnnouncement()

    override suspend fun getAnnouncementList(): OlResponse<AnnouncementListPage> =
        getAllPageList { page, pageSize ->
            olService.getAnnouncementList(
                page = page, pageSize = pageSize
            )
        }

    override suspend fun markNotify(id: Int): OlResponse<Any> = olService.markNotify(id)

    override suspend fun getNotificationList(): OlResponse<NotificationListPage> =
        getAllPageList { page, pageSize ->
            olService.getNotificationList(
                page = page, pageSize = pageSize,
            )
        }
}