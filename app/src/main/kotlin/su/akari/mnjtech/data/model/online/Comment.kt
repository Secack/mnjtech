package su.akari.mnjtech.data.model.online

data class Comment(
    val commentType: Int,
    val content: String,
    val createdOn: Long,
    val downCount: Int,
    val fromUser: FromUser,
    val fromUserId: Int,
    val genre: String,
    val id: Int,
    val modifiedOn: Long,
    val replies: List<Reply>,
    val topicId: Int,
    val upCount: Int,
    val userAgent: String
)

data class FromUser(
    val avatarUrl: String,
    val id: Int,
    val isAdmin: Int,
    val nickname: String
)

data class Reply(
    val commentId: Int,
    val commentType: Int,
    val content: String,
    val createdOn: Long,
    val downCount: Int,
    val fromUser: FromUserX,
    val fromUserId: Int,
    val id: Int,
    val modifiedOn: Long,
    val replyId: Int,
    val replyType: Int,
    val toUser: ToUser,
    val toUserId: Int,
    val upCount: Int,
    val userAgent: String
)

data class FromUserX(
    val avatarUrl: String,
    val id: Int,
    val isAdmin: Int,
    val nickname: String
)

data class ToUser(
    val avatarUrl: String,
    val id: Int,
    val isAdmin: Int,
    val nickname: String
)