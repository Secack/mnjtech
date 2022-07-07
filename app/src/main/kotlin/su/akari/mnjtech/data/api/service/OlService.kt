package su.akari.mnjtech.data.api.service

import retrofit2.http.*
import su.akari.mnjtech.data.model.online.*

interface OlService {
    @GET("carousel/list")
    suspend fun getCarouselList(
        @Query("pageSize") pageSize: Int,
        @Query("page") page: Int,
        @Query("genre") genre: String
    ): OlResponse<VideoListPage>

    @GET("video/hottest/weekly")
    suspend fun getWeeklyHottest(
        @Query("pageSize") pageSize: Int
    ): OlResponse<WeeklyHottest>

    @GET("video/latest")
    suspend fun getLatestVideo(
        @Query("pageSize") pageSize: Int
    ): OlResponse<LatestVideo>

    @GET("user/detail")
    suspend fun getUserDetail(
    ): OlResponse<User>

    @GET("user/profile/count")
    suspend fun getProfileCount(
    ): OlResponse<ProfileCount>

    @GET("video/detail")
    suspend fun getVideoDetail(
        @Query("videoId") videoId: Int
    ): OlResponse<Video>

    @GET("url/list")
    suspend fun getUrlList(
        @Query("topicId") topicId: Int,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
        @Query("genre") genre: String,
        @Query("sort") sort: String
    ): OlResponse<VideoUrlListPage>

    @GET("collection/list")
    suspend fun getCollectionList(
        @Query("pageSize") pageSize: Int,
        @Query("page") page: Int,
        @Query("genre") genre: String,
    ): OlResponse<CollectionListPage>

    @POST("collection/add")
    suspend fun addCollection(
        @Body payload: CollectPayload
    ): OlResponse<Any>

    @GET("collection/delete")
    suspend fun delCollection(
        @Query("genre") genre: String,
        @Query("topicId") topicId: Int
    ): OlResponse<Any>

    @GET("collection/status")
    suspend fun isCollect(
        @Query("genre") genre: String,
        @Query("topicId") topicId: Int
    ): OlResponse<CollectStatus>

    @GET("record/list")
    suspend fun getRecordList(
        @Query("pageSize") pageSize: Int,
        @Query("page") page: Int,
        @Query("genre") genre: String,
    ): OlResponse<RecordListPage>

    @GET("record/detail")
    suspend fun getRecord(
        @Query("genre") genre: String,
        @Query("topicId") topicId: Int
    ): OlResponse<Record>

    @POST("record/modify")
    suspend fun modifyRecord(
        @Body payload: RecordPayload
    ): OlResponse<Record>

    @GET("record/delete")
    suspend fun deleteRecord(
        @Query("genre") genre: String,
        @Query("topicId") topicId: Int
    ): OlResponse<Any>

    @GET("video/config")
    suspend fun getVideoConfig(
    ): OlResponse<VideoConfig>

    @GET("video/list")
    suspend fun getVideoList(
        @QueryMap(encoded = true) filters: Map<String, String>,
        @Query("name", encoded = true) name: String?,
        @Query("order", encoded = true) order: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): OlResponse<VideoListPage>

    @GET("video/query")
    suspend fun getVideoByKeyword(
        @Query("keyword", encoded = true) keyword: String
    ): OlResponse<QueryVideo>

    @GET("comment/list")
    suspend fun getCommentList(
        @Query("pageSize") pageSize: Int,
        @Query("page") page: Int,
        @Query("genre") genre: String,
        @Query("order") order: String
    ): OlResponse<CommentListPage>

    @GET("announcement/detail")
    suspend fun getLatestAnnouncement(
    ): OlResponse<Announcement>

    @GET("announcement/list")
    suspend fun getAnnouncementList(
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): OlResponse<AnnouncementListPage>

    @GET("notify/mark")
    suspend fun markNotify(
        @Query("id") id: Int
    ): OlResponse<Any>

    @GET("notify/list")
    suspend fun getNotificationList(
        @Query("pageSize") pageSize: Int,
        @Query("page") page: Int
    ): OlResponse<NotificationListPage>
}

