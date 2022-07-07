package su.akari.mnjtech.data.model.online

data class Announcement(
    val content: String,
    val createdBy: String,
    val createdOn: Long,
    val id: Int,
    val isTop: Int,
    val modifiedOn: Long,
    val title: String
)