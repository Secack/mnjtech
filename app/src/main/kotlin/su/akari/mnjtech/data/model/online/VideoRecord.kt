package su.akari.mnjtech.data.model.online

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ol_record")
data class VideoRecord(
    @PrimaryKey val topicId: Int,
    val index: Int,
    val time: Long
)