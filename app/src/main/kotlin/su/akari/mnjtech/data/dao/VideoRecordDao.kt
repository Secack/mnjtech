package su.akari.mnjtech.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import su.akari.mnjtech.data.model.online.VideoRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface VideoRecordDao {
    @Query("SELECT * FROM ol_record WHERE topicId = :topicId LIMIT 1")
    fun getRecordById(topicId: Int): Flow<VideoRecord?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(videoRecord: VideoRecord)
}