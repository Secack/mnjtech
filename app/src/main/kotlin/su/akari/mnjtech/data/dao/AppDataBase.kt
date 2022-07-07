package su.akari.mnjtech.data.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import su.akari.mnjtech.data.model.jwgl.Course
import su.akari.mnjtech.data.model.online.DownloadedVideo
import su.akari.mnjtech.data.model.online.VideoRecord
import su.akari.mnjtech.data.model.online.VideoTopic

@Database(
    entities = [Course::class, VideoTopic::class, DownloadedVideo::class, VideoRecord::class],
    version = 1
)
abstract class AppDataBase : RoomDatabase() {
    abstract fun courseDao(): CourseDao

    abstract fun videoTopicDao(): VideoTopicDao

    abstract fun downloadedVideoDao(): DownloadedVideoDao

    abstract fun recordDao(): VideoRecordDao
}