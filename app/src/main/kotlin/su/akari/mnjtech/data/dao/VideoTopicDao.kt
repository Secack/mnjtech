package su.akari.mnjtech.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import su.akari.mnjtech.data.model.online.VideoTopic

@Dao
interface VideoTopicDao {
    @Query("SELECT * FROM ol_topic")
    fun getAllTopicsFlow(): Flow<List<VideoTopic>>

    @Query("SELECT * FROM ol_topic WHERE id=:id LIMIT 1")
    suspend fun getTopicById(id: Int): VideoTopic?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopic(topic: VideoTopic)

    @Delete
    suspend fun deleteTopic(topic: VideoTopic)
}
