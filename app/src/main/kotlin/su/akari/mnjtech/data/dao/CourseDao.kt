package su.akari.mnjtech.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import su.akari.mnjtech.data.model.jwgl.Course

@Dao
interface CourseDao {
    @Query("SELECT * FROM course")
    fun getAllCoursesFlow(): Flow<List<Course>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourse(course: Course)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourses(courses: List<Course>)

    @Update
    suspend fun updateCourse(course: Course)

    @Delete
    suspend fun deleteCourse(course: Course)

    @Query("DELETE FROM course")
    suspend fun deleteAllCourses()
}