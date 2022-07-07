package su.akari.mnjtech.data.model.jwgl

import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "course")
data class Course(
    @PrimaryKey val name: String,
    val venue: String,
    val weekStart: Int,
    val weekEnd: Int,
    val dayOfWeek: Int,
    val sectionStart: Int,
    val sectionEnd: Int,
    val teacher: String,
    val color: String
)

data class CourseItem(
    val name: String,
    val venue: String,
    val weeks: IntRange,
    val dayOfWeek: Int,
    val sections: IntRange,
    val teacher: String,
    val color: Color
)

data class SectionTime(
    val headTime: String,
    val sections: IntRange
)

data class Curriculum(
    val djdzList: List<Any>,
    val jxhjkcList: List<Any>,
    val kbList: List<Kb>,
    val kblx: Int,
    val qsxqj: String,
    val rqazcList: List<Any>,
    val sfxsd: String,
    val sjkList: List<Sjk>,
    val xkkg: Boolean,
    val xnxqsfkz: String,
    val xqbzxxszList: List<Any>,
    val xqjmcMap: XqjmcMap,
    val xsbjList: List<Xsbj>,
    val xskbsfxstkzt: String,
    val xsxx: Xsxx,
    val zckbsfxssj: String
)

data class Kb(
    val cd_id: String,
    val cdmc: String,
    val cxbj: String,
    val cxbjmc: String,
    val date: String,
    val dateDigit: String,
    val dateDigitSeparator: String,
    val day: String,
    val jc: String,
    val jcor: String,
    val jcs: String,
    val jgh_id: String,
    val jgpxzd: String,
    val jxb_id: String,
    val jxbmc: String,
    val jxbsftkbj: String,
    val jxbzc: String,
    val kcbj: String,
    val kch: String,
    val kch_id: String,
    val kclb: String,
    val kcmc: String,
    val kcxszc: String,
    val kcxz: String,
    val khfsmc: String,
    val kkzt: String,
    val listnav: String,
    val localeKey: String,
    val month: String,
    val oldjc: String,
    val oldzc: String,
    val pageable: Boolean,
    val pkbj: String,
    val queryModel: QueryModel,
    val rangeable: Boolean,
    val rk: String,
    val rsdzjs: Int,
    val skfsmc: String,
    val sxbj: String,
    val totalResult: String,
    val userModel: UserModel,
    val xf: String,
    val xkbz: String,
    val xm: String,
    val xnm: String,
    val xqdm: String,
    val xqh1: String,
    val xqh_id: String,
    val xqj: Int,
    val xqjmc: String,
    val xqm: String,
    val xqmc: String,
    val xsdm: String,
    val xslxbj: String,
    val year: String,
    val zcd: String,
    val zcmc: String,
    val zfjmc: String,
    val zhxs: String,
    val zxs: String,
    val zxxx: String,
    val zyfxmc: String,
    val zzrl: String
)

data class Sjk(
    val date: String,
    val dateDigit: String,
    val dateDigitSeparator: String,
    val day: String,
    val jgpxzd: String,
    val jsxm: String,
    val kcmc: String,
    val listnav: String,
    val localeKey: String,
    val month: String,
    val pageable: Boolean,
    val qsjsz: String,
    val qtkcgs: String,
    val queryModel: QueryModelX,
    val rangeable: Boolean,
    val rsdzjs: Int,
    val sfsjk: String,
    val sjkcgs: String,
    val totalResult: String,
    val userModel: UserModelX,
    val xf: String,
    val xksj: String,
    val xnmc: String,
    val xqmmc: String,
    val year: String
)

data class XqjmcMap(
    val `1`: String,
    val `2`: String,
    val `3`: String,
    val `4`: String,
    val `5`: String,
    val `6`: String,
    val `7`: String
)

data class Xsbj(
    val xsdm: String,
    val xslxbj: String,
    val xsmc: String,
    val ywxsmc: String
)

data class Xsxx(
    val BJMC: String,
    val JSXM: String,
    val KCMS: Int,
    val KXKXXQ: String,
    val XH: String,
    val XH_ID: String,
    val XKKG: String,
    val XKKGXQ: String,
    val XM: String,
    val XNM: String,
    val XNMC: String,
    val XQM: String,
    val XQMMC: String
)

data class QueryModelX(
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

data class UserModelX(
    val monitor: Boolean,
    val roleCount: Int,
    val roleKeys: String,
    val roleValues: String,
    val status: Int,
    val usable: Boolean
)