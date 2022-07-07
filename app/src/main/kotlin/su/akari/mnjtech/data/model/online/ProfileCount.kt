package su.akari.mnjtech.data.model.online

data class ProfileCount(
    val collectionCount: Int,
    val messageCount: Int,
    val notifyCount: Int,
    val onlineCount: Int,
    val recordCount: Int,
    val unReadNotifyCount: Int
)