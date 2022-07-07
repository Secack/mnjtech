package su.akari.mnjtech.data.model.online

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DownloadEntry(
    val id: Int,
    val index: Int,
    val name: String,
    val path: String,
    val videoTopic: VideoTopic
) : Parcelable