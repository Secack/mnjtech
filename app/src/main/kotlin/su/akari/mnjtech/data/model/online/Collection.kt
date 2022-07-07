package su.akari.mnjtech.data.model.online

data class CollectPayload(
    val topicId: Int,
    val genre: String
)

data class CollectStatus(
    val isCollect: Boolean
)

data class Collection(
    val createdOn: Long,
    val genre: String,
    val id: Int,
    val modifiedOn: Long,
    val software: Any,
    val topicId: Int,
    val userId: Int,
    val video: Video
)