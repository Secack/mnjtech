package su.akari.mnjtech.data.model.online

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ol_video")
data class DownloadedVideo(
    @PrimaryKey val id: Int,
    val topicId: Int,
    val index: Int,
    val name: String,
    val filePath: String,
    val fileSize: Long
)