package su.akari.mnjtech.data.model.jwgl

data class SectionList(
    val jcList: List<Jc>,
    val lhList: List<Lh>
)

data class Jc(
    val JCMC: Int,
    val RSDMC: String
)

data class Lh(
    val JXLDM: String,
    val JXLMC: String,
    val XQH_ID: String
)

data class RoomListPage(
    val currentPage: Int,
    val currentResult: Int,
    val entityOrField: Boolean,
    val items: List<RoomItem>,
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

data class RoomItem(
    val cd_id: String,
    val cdbh: String,
    val cdlb_id: String,
    val cdlbmc: String,
    val cdmc: String,
    val cdxqxx_id: String,
    val date: String,
    val dateDigit: String,
    val dateDigitSeparator: String,
    val day: String,
    val jgpxzd: String,
    val jxlmc: String,
    val kszws1: String,
    val lh: String,
    val listnav: String,
    val localeKey: String,
    val month: String,
    val pageable: Boolean,
    val queryModel: QueryModel,
    val rangeable: Boolean,
    val row_id: String,
    val totalResult: String,
    val userModel: UserModel,
    val xqh_id: String,
    val xqmc: String,
    val year: String,
    val zws: String
)