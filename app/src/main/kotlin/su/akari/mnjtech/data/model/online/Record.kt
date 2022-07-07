package su.akari.mnjtech.data.model.online

data class RecordPayload(
    val genre: String,
    val time: String,
    val topicId: Int,
    val urlId: Int
)

class Record(
    val createdOn: Long?,
    val genre: String?,
    val id: Int?,
    val modifiedOn: Long?,
    val time: String?,
    val topicId: Int?,
    val url: VideoUrl?,
    val urlId: Int?,
    val userId: Int?,
    val video: Video?
)