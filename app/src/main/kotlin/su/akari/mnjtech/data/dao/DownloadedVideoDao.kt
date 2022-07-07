package su.akari.mnjtech.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import su.akari.mnjtech.data.model.online.DownloadedVideo

@Dao
interface DownloadedVideoDao {
    @Query("SELECT * FROM ol_video WHERE topicId = :topicId ORDER BY `index`")
    fun getAllVideosFlow(topicId: Int): Flow<List<DownloadedVideo>>

    @Query("SELECT * FROM ol_video WHERE topicId = :topicId ORDER BY `index`")
    suspend fun getAllVideos(topicId: Int): List<DownloadedVideo>

    @Query("SELECT * FROM ol_video WHERE id = :id LIMIT 1")
    suspend fun getVideoById(id: Int): DownloadedVideo?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideo(downloadedVideo: DownloadedVideo)

    @Delete
    suspend fun deleteVideo(video: DownloadedVideo)
}