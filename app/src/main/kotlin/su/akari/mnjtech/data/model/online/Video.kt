package su.akari.mnjtech.data.model.online

import com.google.gson.annotations.SerializedName

private typealias VideoList = List<Video>

data class Video(
    val actors: String?,
    val category: String?,
    val cover: String,
    val createdBy: String?,
    val createdOn: Long?,
    val danmakuToken: String?,
    val directors: String?,
    val doubanId: Int?,
    val id: Int,
    val intro: String?,
    val isEnable: Int?,
    val language: String?,
    val modifiedBy: String?,
    val modifiedOn: Long?,
    val name: String,
    val playCount: Int?,
    val region: String?,
    val score: Score?,
    val status: Int?,
    val tags: ArrayList<Tag>?,
    val year: String?,
    val genre: String?,
    val topic: Topic?,
    val value: Int?
)

data class Score(
    val average: String?,
    val createdOn: Int,
    val id: Int,
    val max: Int,
    val modifiedOn: Int,
    val numRaters: Int,
    val videoId: Int
)

data class Topic(
    val id: Int,
    val name: String
)

data class WeeklyHottest(
    val count: List<Int>,
    val video: VideoList
)

data class LatestVideo(
    val movie: VideoList,
    val teleplay: VideoList,
    val cartoon: VideoList,
    val variety: VideoList,
    val fact: VideoList,
    val study: VideoList,
    @SerializedName("anti-fraud")
    val anti_fraud: VideoList
)

data class QueryVideo(
    val videos: List<Video?>
)