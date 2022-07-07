package su.akari.mnjtech.data.model.online

interface PageList<T> {
    var list: List<T>
    val pageSize: Int
    val total: Int
}

data class AnnouncementListPage(
    override var list: List<Announcement>,
    override val pageSize: Int,
    override val total: Int
) : PageList<Announcement>

data class RecordListPage(
    override var list: List<Record>,
    override val pageSize: Int,
    override val total: Int
) : PageList<Record>

data class CollectionListPage(
    override var list: List<Collection>,
    override val pageSize: Int,
    override val total: Int
) : PageList<Collection>

data class CommentListPage(
    override var list: List<Comment>,
    override val pageSize: Int,
    override val total: Int
) : PageList<Comment>

data class NotificationListPage(
    override var list: List<Notification>,
    override val pageSize: Int,
    override val total: Int
) : PageList<Notification>

data class VideoListPage(
    override var list: List<Video>,
    override val pageSize: Int,
    override val total: Int
) : PageList<Video>

data class VideoUrlListPage(
    override var list: List<VideoUrl>,
    override val pageSize: Int,
    override val total: Int
) : PageList<VideoUrl>