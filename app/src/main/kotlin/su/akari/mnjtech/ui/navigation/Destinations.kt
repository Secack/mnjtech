package su.akari.mnjtech.ui.navigation

object Destinations {
    const val Index = "index"

    const val Login = "login"

    const val LoginWeb = "login_web"

    const val Setting = "setting"

    const val Evaluation = "evaluation"

    const val Score = "score"

    const val FreeRoom = "free_room"

    const val OlIndex = "online_index"

    const val OlDetail = "online_detail"

    const val OlSearch = "online_search"

    const val OlAnnouncement = "online_announcement"

    const val OlNotification = "online_notification"

    const val OlCollection = "online_collection"

    const val OlRecord = "online_record"

    const val OlComment = "online_comment"

    const val OlMessage = "online_message"

    const val OlDownload = "online_download"
}

object DestinationArgs {
    const val AutoLogin = "auto_login"

    const val VideoId = "video_id"

    const val Index = "index"

    const val Time = "time"

    const val CacheMode = "cache_mode"

    const val Category = "category"
}

object DestinationDeepLink {
    private const val BaseUri = "mnjtech://"

    const val DownloadPattern = "$BaseUri${Destinations.OlDownload}"
}