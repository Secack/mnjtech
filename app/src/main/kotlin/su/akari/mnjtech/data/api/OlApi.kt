package su.akari.mnjtech.data.api

import su.akari.mnjtech.data.model.online.*

interface OlApi {
    /**
     * 登录南工在线
     */
    suspend fun loginOnline()

    /**
     * 获取轮播图
     * @return OlResponse<VideoListPage>
     */
    suspend fun getCarouselList(): OlResponse<VideoListPage>

    /**
     * 获取一周热播
     * @return OlResponse<WeeklyHottest>
     */
    suspend fun getWeeklyHottest(): OlResponse<WeeklyHottest>

    /**
     * 获取首页视频
     * @return OlResponse<LatestVideo>
     */
    suspend fun getLatestVideo(): OlResponse<LatestVideo>

    /**
     * 获取用户信息
     * @return OlResponse<OlUser>
     */
    suspend fun getUserDetail(): OlResponse<User>

    /**
     * 获取简介数量
     * @return OlResponse<ProfileCount>
     */
    suspend fun getProfileCount(): OlResponse<ProfileCount>

    /**
     * 获取视频信息
     * @param videoId Int
     * @return OlResponse<Video>
     */
    suspend fun getVideoDetail(videoId: Int): OlResponse<Video>

    /**
     * 获取下载列表
     * @param videoId Int
     * @return OlResponse<VideoUrlList>
     */
    suspend fun getVideoUrlList(videoId: Int): OlResponse<VideoUrlListPage>

    /**
     * 获取收藏列表
     * @return OlResponse<CollectionListPage>
     */
    suspend fun getCollectionList(): OlResponse<CollectionListPage>

    /**
     * 添加收藏
     * @param videoId Int
     * @return OlResponse<Any>
     */
    suspend fun addCollection(videoId: Int): OlResponse<Any>

    /**
     * 删除收藏
     * @param videoId Int
     * @return OlResponse<Any>
     */
    suspend fun delCollection(videoId: Int): OlResponse<Any>

    /**
     * 查询收藏
     * @param videoId Int
     * @return OlResponse<CollectStatus>
     */
    suspend fun isCollect(videoId: Int): OlResponse<CollectStatus>

    /**
     * 获取历史记录列表
     * @return OlResponse<RecordListPage>
     */
    suspend fun getRecordList(): OlResponse<RecordListPage>

    /**
     * 获取历史记录
     * @param videoId Int
     * @return OlResponse<Record>
     */
    suspend fun getRecord(videoId: Int): OlResponse<Record>

    /**
     * 修改历史记录
     * @param time String
     * @param videoId Int
     * @param urlId Int
     * @return OlResponse<Record>
     */
    suspend fun modifyRecord(
        time: String,
        videoId: Int,
        urlId: Int
    ): OlResponse<Record>

    /**
     * 删除历史记录
     * @param videoId Int
     * @return OlResponse<Any>
     */
    suspend fun deleteRecord(videoId: Int): OlResponse<Any>

    /**
     * 获取排行榜配置
     * @return OlResponse<VideoConfig>
     */
    suspend fun getVideoConfig(): OlResponse<VideoConfig>

    /**
     * 获取排行榜
     * @param filters Map<String, String>
     * @param name String?
     * @param order String
     * @param page Int
     * @param pageSize Int
     * @return OlResponse<VideoListPage>
     */
    suspend fun getVideoList(
        filters: Map<String, String>,
        name: String?,
        order: String,
        page: Int,
        pageSize: Int
    ): OlResponse<VideoListPage>

    /**
     * 关键词查询视频
     * @param keyword String
     * @return OlResponse<QueryVideo>
     */
    suspend fun getVideoByKeyword(keyword: String): OlResponse<QueryVideo>

    /**
     * 获取留言
     * @return OlResponse<CommentListPage>
     */
    suspend fun getCommentList(): OlResponse<CommentListPage>

    /**
     * 获取最近公告
     * @return OlResponse<Announcement>
     */
    suspend fun getLatestAnnouncement(): OlResponse<Announcement>

    /**
     * 获取公告
     * @return OlResponse<AnnouncementListPage>
     */
    suspend fun getAnnouncementList(): OlResponse<AnnouncementListPage>

    /**
     * 标记通知已读
     * @param id Int
     * @return OlResponse<Any>
     */
    suspend fun markNotify(id: Int): OlResponse<Any>

    /**
     * 获取通知
     * @return OlResponse<NotificationListPage>
     */
    suspend fun getNotificationList(): OlResponse<NotificationListPage>
}