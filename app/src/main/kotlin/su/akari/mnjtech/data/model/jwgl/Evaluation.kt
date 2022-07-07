package su.akari.mnjtech.data.model.jwgl

data class EvaluationListPage(
    val currentPage: Int,
    val currentResult: Int,
    val entityOrField: Boolean,
    val items: List<EvaluationItem>,
    val limit: Int,
    val offset: Int,
    val pageNo: Int,
    val pageSize: Int,
    val showCount: Int,
    val sortName: String,
    val sortOrder: String,
    val sorts: List<Any>,
    val totalCount: Int,
    val totalPage: Int,
    val totalResult: Int
)

data class EvaluationItem(
    val date: String,
    val dateDigit: String,
    val dateDigitSeparator: String,
    val day: String,
    val jgh_id: String,
    val jgpxzd: String,
    val jxb_id: String,
    val jxbmc: String,
    val jzgmc: String,
    val kch_id: String,
    val kcmc: String,
    val listnav: String,
    val localeKey: String,
    val month: String,
    val pageable: Boolean,
    val pjmbmcb_id: String,
    val pjzt: String,
    val queryModel: QueryModel,
    val rangeable: Boolean,
    val row_id: String,
    val sfcjlrjs: String,
    val tjzt: String,
    val tjztmc: String,
    val totalResult: String,
    val userModel: UserModel,
    val xnm: String,
    val xqm: String,
    val xsdm: String,
    val xsmc: String,
    val year: String
)

data class EvaluationChoose(
    val pfdjdmxmb_id: String,
    val pjzbxm_id: String,
    val pfdjdmb_id: String,
    val zsmbmcb_id: String
)