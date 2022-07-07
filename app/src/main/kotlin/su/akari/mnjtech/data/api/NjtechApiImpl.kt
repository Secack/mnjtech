package su.akari.mnjtech.data.api

import okhttp3.FormBody
import okhttp3.ResponseBody
import su.akari.mnjtech.data.api.service.JwglService
import su.akari.mnjtech.data.api.service.NjtechParser
import su.akari.mnjtech.data.api.service.NjtechService
import su.akari.mnjtech.data.model.jwgl.CourseItem
import su.akari.mnjtech.data.model.jwgl.RoomListPage
import su.akari.mnjtech.data.model.jwgl.ScoreListPage
import su.akari.mnjtech.data.model.jwgl.SectionList

class NjtechApiImpl(
    private val njtechParser: NjtechParser,
    private val njtechService: NjtechService,
    private val jwglService: JwglService
) : NjtechApi {
    override suspend fun login(
        username: String,
        password: String,
        provider: Int,
        onWifiResp: (String?) -> Unit
    ) = njtechParser.login(username, password, provider, onWifiResp)

    override suspend fun logout() =
        njtechParser.logout()

    override suspend fun loginJwgl() =
        njtechParser.loginJwgl()

    override suspend fun getProfile() =
        njtechParser.getProfile()

    override suspend fun getCurriculum(
        id: String,
        year: Int,
        term: Int?
    ): List<CourseItem> =
        njtechParser.getCurriculum(
            func = "N253508",
            id = id,
            year = year,
            term = term
        )

    override suspend fun getScoreList(
        id: String,
        year: Int?,
        term: Int?
    ): ScoreListPage =
        njtechParser.getScoreList(
            doType = "query",
            func = "N305005",
            id = id,
            year = year,
            term = term
        )

    override suspend fun getEvaluationList(id: String) =
        njtechParser.getEvaluationList(
            doType = "query",
            func = "N401605",
            id = id
        )

    override suspend fun doEvaluation(id: String, payload: FormBody): ResponseBody =
        jwglService.doEvaluation(
            gnmkdm = "N401605",
            su = id,
            payload = payload
        )

    override suspend fun getSectionList(
        campus: Int,
        year: Int,
        term: Int,
    ): SectionList =
        jwglService.getSectionList(
            gnmkdm = "N2155", xqh_id = campus, xnm = year, xqm = term
        )

    override suspend fun getRoomList(
        campus: Int,
        year: Int,
        term: Int,
        week: Int,
        dayOfWeek: Int,
        building: String,
        classes: IntRange
    ): RoomListPage =
        njtechParser.getRoomList(
            doType = "query",
            func = "N2155",
            campus = campus,
            year = year,
            term = term,
            week = week,
            dayOfWeek = dayOfWeek,
            building = building,
            classes = classes
        )

    override suspend fun get2DCode() =
        njtechParser.get2DCode()

}