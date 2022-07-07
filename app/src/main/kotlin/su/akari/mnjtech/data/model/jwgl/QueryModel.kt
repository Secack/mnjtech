package su.akari.mnjtech.data.model.jwgl

data class QueryModel(
    val currentPage: Int,
    val currentResult: Int,
    val entityOrField: Boolean,
    val limit: Int,
    val offset: Int,
    val pageNo: Int,
    val pageSize: Int,
    val showCount: Int,
    val sorts: List<Any>,
    val totalCount: Int,
    val totalPage: Int,
    val totalResult: Int
)
