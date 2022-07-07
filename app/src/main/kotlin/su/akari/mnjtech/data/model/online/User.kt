package su.akari.mnjtech.data.model.online

data class User(
    val avatarUrl: String,
    val bg: String,
    val changed: Int,
    val createdOn: Int,
    val disableTime: Int,
    val email: String,
    val id: Int,
    val isAdmin: Int,
    val lastLoginIp: String,
    val lastLoginTime: Long,
    val modifiedOn: Int,
    val nickname: String,
    val openId: String,
    val realname: String,
    val status: Int
)