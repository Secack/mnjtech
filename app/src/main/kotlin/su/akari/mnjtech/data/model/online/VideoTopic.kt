package su.akari.mnjtech.data.model.online

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "ol_topic")
data class VideoTopic(
    @PrimaryKey val id: Int,
    val name: String,
    val cover: String,
    val intro: String,
) : Parcelable
