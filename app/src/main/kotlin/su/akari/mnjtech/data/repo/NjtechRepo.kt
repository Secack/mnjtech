package su.akari.mnjtech.data.repo

import su.akari.mnjtech.data.api.NjtechApi
import su.akari.mnjtech.util.dataStateFlow

class NjtechRepo constructor(
    private val njtechApi: NjtechApi
) {
    lateinit var id: String

    fun login(username: String, password: String, provider: Int, onWifiResp: (String?) -> Unit) =
        dataStateFlow {
            njtechApi.login(username, password, provider, onWifiResp)
        }

    fun logout() = dataStateFlow {
        njtechApi.logout()
    }

    fun loginJwgl() = dataStateFlow {
        njtechApi.loginJwgl()
    }

    fun getProfile() = dataStateFlow {
        njtechApi.getProfile()
    }

    fun getCurriculum(year: Int, term: Int?) = dataStateFlow {
        njtechApi.getCurriculum(
            year = year,
            term = term,
            id = id
        )
    }

    fun getScoreList(year: Int?, term: Int?) = dataStateFlow {
        njtechApi.getScoreList(
            id = id,
            year = year,
            term = term
        )
    }

    fun getEvaluationList() = dataStateFlow {
        njtechApi.getEvaluationList(id = id)
    }

    fun getSectionList(campus: Int, year: Int, term: Int) =
        dataStateFlow {
            njtechApi.getSectionList(campus = campus, year = year, term = term)
        }

    fun getRoomList(
        campus: Int,
        year: Int,
        term: Int,
        week: Int,
        dayOfWeek: Int,
        building: String,
        classes: IntRange
    ) = dataStateFlow {
        njtechApi.getRoomList(
            campus = campus,
            year = year,
            term = term,
            week = week,
            dayOfWeek = dayOfWeek,
            building = building,
            classes = classes
        )
    }

    fun get2DCode() = dataStateFlow {
        njtechApi.get2DCode()
    }
}
