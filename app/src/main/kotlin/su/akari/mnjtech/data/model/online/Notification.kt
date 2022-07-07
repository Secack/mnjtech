package su.akari.mnjtech.data.model.online

data class Notification(
    val commentId: Int,
    val content: String,
    val createdOn: Long,
    val genre: String,
    val id: Int,
    val modifiedOn: Long,
    val origin: String,
    val replyId: Int,
    val sender: Sender,
    val senderId: Int,
    val target: Target,
    val targetType: String,
    val userNotify: UserNotify
)

data class Sender(
    val avatarUrl: String,
    val id: Int,
    val isAdmin: Int,
    val nickname: String
)

class Target

data class UserNotify(
    val commentId: Int,
    val createdOn: Int,
    val id: Int,
    val isRead: Int,
    val modifiedOn: Int,
    val notifyId: Int,
    val replyId: Int,
    val userId: Int
)