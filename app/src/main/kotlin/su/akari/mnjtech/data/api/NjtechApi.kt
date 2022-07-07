package su.akari.mnjtech.data.api

import su.akari.mnjtech.data.model.jwgl.CourseItem
import su.akari.mnjtech.data.model.jwgl.RoomListPage
import su.akari.mnjtech.data.model.jwgl.ScoreListPage
import su.akari.mnjtech.data.model.jwgl.SectionList
import su.akari.mnjtech.data.model.profile.Profile
import okhttp3.FormBody
import okhttp3.ResponseBody

interface NjtechApi {
    /**
     * 登录i南工
     * @param username 用户名
     * @param password 密码
     * @param provider 运营商
     * @param onWifiResp Wifi登录回调
     */
    suspend fun login(
        username: String, password: String, provider: Int, onWifiResp: (String?) -> Unit
    )

    suspend fun logout()

    /**
     * 登录教务系统
     */
    suspend fun loginJwgl()

    /**
     * 获取基本信息
     * @return Response<Profile>
     */
    suspend fun getProfile(): Profile

    /**
     * 获取课表
     * @param id String
     * @param year Int
     * @param term Int?
     * @return List<CourseItem>
     */
    suspend fun getCurriculum(
        id: String, year: Int, term: Int?
    ): List<CourseItem>

    /**
     * 获取成绩
     * @param id String
     * @param year Int?
     * @param term Int?
     * @return ScoreListPage
     */
    suspend fun getScoreList(
        id: String,
        year: Int?,
        term: Int?,
    ): ScoreListPage

    /**
     * 获取评教列表
     * @param id String
     * @return Map<String, suspend () -> ResponseBody>
     */
    suspend fun getEvaluationList(
        id: String
    ): Map<String, suspend () -> ResponseBody>

    suspend fun doEvaluation(
        id: String, payload: FormBody
    ): ResponseBody

    suspend fun getSectionList(
        campus: Int, year: Int, term: Int
    ): SectionList

    suspend fun getRoomList(
        campus: Int,
        year: Int,
        term: Int,
        week: Int,
        dayOfWeek: Int,
        building: String,
        classes: IntRange
    ): RoomListPage

    suspend fun get2DCode()
}