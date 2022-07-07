package su.akari.mnjtech.data.model.online

data class OlResponse<T>(
    val data: T,
    val code: Int,
    val msg: String
)
