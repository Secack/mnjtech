package su.akari.mnjtech.data.api.service

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastForEachIndexed
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import org.jsoup.Jsoup
import org.jsoup.nodes.Attribute
import org.jsoup.nodes.Element
import su.akari.mnjtech.R
import su.akari.mnjtech.RES
import su.akari.mnjtech.data.api.URL_I_NJTECH
import su.akari.mnjtech.data.api.URL_JWGL
import su.akari.mnjtech.data.model.jwgl.CourseItem
import su.akari.mnjtech.data.model.profile.Profile
import su.akari.mnjtech.ui.theme.getMd3Colors
import su.akari.mnjtech.util.Log
import su.akari.mnjtech.util.autoRetry
import su.akari.mnjtech.util.fromJsonElement
import su.akari.mnjtech.util.get
import su.akari.mnjtech.util.getRawTextFile
import su.akari.mnjtech.util.network.clearSession
import su.akari.mnjtech.util.network.get
import su.akari.mnjtech.util.network.getString
import su.akari.mnjtech.util.network.initSession
import su.akari.mnjtech.util.network.post
import su.akari.mnjtech.util.network.updateSession
import java.security.MessageDigest
import kotlin.math.pow

class NjtechParser(
    private val okHttpClient: OkHttpClient,
    private val njtechService: NjtechService,
    private val jwglService: JwglService
) {
    suspend fun login(
        username: String, password: String, provider: Int, onWifiResp: (String?) -> Unit
    ) = withContext(Dispatchers.IO) {
        with(okHttpClient) {
            clearSession()
            val resp = get(URL_I_NJTECH)

            val keys = RES.getRawTextFile(R.raw.login_keys)
            suspend fun disCaptcha(i: Int): String {
                if (i > 20) return "0000";
                val sha1 = MessageDigest.getInstance("SHA-1")
                    .apply { update(get("https://u.njtech.edu.cn/cas/captcha.jpg").body.bytes()) }
                    .digest().joinToString(separator = "") { "%02x".format(it) }
                return keys.firstOrNull { it.split(' ')[0] == sha1 }?.let { it.split(' ')[1] }
                    ?: run {
                        disCaptcha(i + 1)
                    }
            }

            val captcha = disCaptcha(0)
            with(FormBody.Builder()) {
                Jsoup.parse(resp.body.string()).run {
                    select("div.login-submit").first()?.run {
                        children().fastForEach {
                            addEncoded(it.attr("name"), it.attr("value"))
                        }
                    }
                    select("div.channel-option").first()?.run {
                        children()[provider].let {
                            addEncoded("channel", it.attr("value"))
                            addEncoded("channelshow", it.attr("valuechinese"))
                        }
                    }
                    add("username", username)
                    add("password", password)
                    add("captcha", captcha)
                    build()
                }
            }.let { body ->
                post(
                    url = resp.request.url.toString(), body = body
                ).run {
                    val text = this.body.string()
                    if (request.url.toString().contains(URL_I_NJTECH).not()) {
                        Jsoup.parse(text).select("div.helpful-message")[0].children()[0].text()
                            .let {
                                require(it.isEmpty()) { it }
                            }
                    }
                    runCatching {
                        Regex("v46ip=\'(.*?)\'").find(getString("http://10.50.255.11"))?.let {
                            val ip = it.groupValues[1]
                            val channel = when (provider) {
                                1 -> "@cmcc"
                                2 -> "@telecom"
                                else -> ""
                            }
                            post(
                                url = "http://10.50.255.11:801/eportal/?c=ACSetting&a=Login&protocol=http:&hostname=10.50.255.11&iTermType=2&wlanuserip=$ip&wlanacip=null&wlanacname=null&mac=00-00-00-00-00-00&ip=$ip&enAdvert=0&queryACIP=0&jsVersion=2.4.3&loginMethod=1",
                                body = FormBody.Builder().add("DDDDD", ",0,$username$channel")
                                    .add("upass", password).add("R1", "0").add("R2", "0")
                                    .add("R3", "0")
                                    .add("R6", "0").add("para", "00").add("0MKKey", "123456")
                                    .build()
                            )
                        }
                    }
                    getString("$URL_I_NJTECH/njtech/wifiresponseinfo").fromJsonElement().run {
                        runCatching {
                            get("data")[get("ip").asString]["wifi_response"]["msg"].asString
                        }.getOrNull().let {
                            withContext(Dispatchers.Main) {
                                onWifiResp(it)
                            }
                        }
                    }
                    updateSession()
                }
            }
        }
    }

    suspend fun logout() = withContext(Dispatchers.IO) {
        with(okHttpClient) {
            get("https://u.njtech.edu.cn/oauth2/logout?redirect_uri=https://i.njtech.edu.cn/index.php/njtech/logout")
            updateSession()
        }
    }

    suspend fun loginJwgl() = withContext(Dispatchers.IO) {
        with(okHttpClient) {
            autoRetry(onSuccess = { return@with }) {
                if (get("https://jwgl.njtech.edu.cn/xtgl/index_cxNews.html").request.url.toString()
                        .contains("login")
                ) {
                    get("https://jwgl.njtech.edu.cn/sso/ktiotlogin").takeIf {
                        it.request.url.toString().contains(URL_JWGL)
                    }?.let {
                        updateSession()
                        return@with
                    }
                }
            }
        }
    }

    suspend fun getProfile() = withContext(Dispatchers.IO) {
        with(okHttpClient) {
            initSession()
            getString(URL_I_NJTECH).let {
                Jsoup.parse(it)
            }.select("script")[0].data().fromJsonElement().let {
                val (user, term) = it["user"] to it["schoolTerm"]
                Profile(
                    id = user["xgh"].asString,
                    name = user["name"].asString,
                    term = Profile.SchoolTerm(
                        year = term["academicYear"].asString.substringBefore('-').toInt(),
                        term = when (term["academicTerm"].asString) {
                            "第一学期" -> Profile.Term.FIRST
                            "第二学期" -> Profile.Term.SECOND
                            "第三学期" -> Profile.Term.THIRD
                            else -> throw IllegalArgumentException()
                        },
                        week = term["currentWeek"].asInt
                    )
                )
            }
        }
    }

    suspend fun getCurriculum(
        func: String, id: String, year: Int, term: Int?
    ): List<CourseItem> = withContext(Dispatchers.IO) {
        mutableMapOf<String, Int>().let { colorMap ->
            var i = 0
            jwglService.getCurriculum(
                gnmkdm = func, su = id, xnm = year, xqm = term, kzlx = "ck"
            ).kbList.onEach {
                colorMap[it.kcmc] ?: colorMap.putIfAbsent(it.kcmc, i++)
            }.map { kb ->
                fun String.toRange(dropN: Int = 0) = dropLast(dropN).split('-').run {
                    first().toInt()..last().toInt()
                }

                val name = kb.kcmc
                CourseItem(name = name,
                    venue = kb.cdmc,
                    weeks = kb.zcd.toRange(1),
                    dayOfWeek = kb.xqj,
                    sections = kb.jcs.toRange(),
                    teacher = kb.xm,
                    color = colorMap[name]?.let { getMd3Colors()[it] } ?: Color(name.hashCode()))
            }
        }
    }

    suspend fun getEvaluationList(doType: String, func: String, id: String) =
        withContext(Dispatchers.IO) {
            buildMap {
                jwglService.getEvaluationIndex(
                    doType = doType,
                    gnmkdm = func,
                    su = id,
                    _search = false,
                    nd = System.currentTimeMillis(),
                    showCount = 10000,
                    currentPage = 1,
                    sortName = "",
                    sortOrder = "asc",
                    time = 1
                ).items
                    //.filter { it.tjzt != "1" }
                    .map { item ->
                        val formBody = FormBody.Builder().run {
                            Jsoup.parse(
                                jwglService.getEvaluationDisplay(
                                    gnmkdm = func,
                                    su = id,
                                    jxb_id = item.jxb_id,
                                    kch_id = item.kch_id,
                                    jgh_id = item.jgh_id,
                                    xsdm = item.xsdm,
                                    tjzt = item.tjzt,
                                    sfcjlrjs = item.sfcjlrjs,
                                    pjmbmcb_id = ""
                                ).string()
                            ).run {
                                val add2modelList = { k: String, v: String ->
                                    addEncoded("modelList[0].$k", v)
                                }
                                val add2xspjList = { i1: Int, k: String, v: String ->
                                    add2modelList("xspjList[$i1].$k", v)
                                }
                                val add2childXspjList = { i1: Int, i2: Int, k: String, v: String ->
                                    add2xspjList(i1, "childXspjList[$i2].$k", v)
                                }

                                fun Element.filterData(
                                    con: (Attribute) -> Boolean,
                                    callback: (k: String, v: String) -> Unit
                                ) {
                                    attributes().filter {
                                        it.key.startsWith("data-") && con(it)
                                    }.fastForEach {
                                        callback(it.key.removePrefix("data-"), it.value)
                                    }
                                }

                                select("div.xspj-body")[0].apply {
                                    filterData({ it.value.isNotEmpty() }) { k, v ->
                                        add(k, v)
                                    }
                                    add("tjzt", "1")
                                }
                                select("div.panel-pjdx")[0].apply {
                                    filterData({ !it.key.endsWith("ypfj") }) { k, v ->
                                        add2modelList(k, v)
                                    }
                                    add2modelList("py", "")
                                    add2modelList("pjzt", "1")
                                }

                                select("table.table-xspj").fastForEachIndexed { idx1, xspj ->
                                    add2xspjList(idx1, "pjzbxm_id", xspj.attr("data-pjzbxm_id"))
                                    xspj.select("tr.tr-xspj")
                                        .fastForEachIndexed { idx2, childXspj ->
                                            childXspj.filterData({ it.key.endsWith("id") }) { k, v ->
                                                add2childXspjList(idx1, idx2, k, v)
                                            }
                                            childXspj.select("input.radio-pjf")
                                                .find { it.attr("data-dyf") == "100" }?.let { pjf ->
                                                    pjf.filterData({ it.key.endsWith("id") }) { k, v ->
                                                        add2childXspjList(idx1, idx2, k, v)
                                                    }
                                                } ?: add2childXspjList(
                                                idx1,
                                                idx2,
                                                "pfdjdmxmb_id",
                                                "26B1FFCCDF8D8F16E050007F01003D67"
                                            )
                                        }
                                }
                            }
                            build()
                        }

                        put(item.kcmc, suspend {
                            jwglService.doEvaluation(
                                gnmkdm = func, su = id, payload = formBody
                            )
                        })
                    }
            }
        }

    suspend fun getScoreList(doType: String, func: String, id: String, year: Int?, term: Int?) =
        withContext(Dispatchers.IO) {
            jwglService.getScoreList(
                doType = doType,
                gnmkdm = func,
                su = id,
                yearTerm = mapOf(
                    "xnm" to (year?.toString() ?: ""), "xqm" to (term?.toString() ?: "")
                ),
                _search = false,
                nd = System.currentTimeMillis(),
                showCount = 10000,
                currentPage = 1,
                sortName = "",
                sortOrder = "asc",
                time = 1
            )
        }

    suspend fun getRoomList(
        doType: String,
        func: String,
        campus: Int,
        year: Int,
        term: Int,
        week: Int,
        dayOfWeek: Int,
        building: String,
        classes: IntRange
    ) = withContext(Dispatchers.IO) {
        jwglService.getRoomList(
            doType = doType,
            gnmkdm = func,
            fwzt = "cx",
            xqh_id = campus,
            xnm = year,
            xqm = term,
            zcd = 2f.pow(week.dec()).toLong(),
            xqj = dayOfWeek,
            lh = building,
            jcd = classes.fold(0L) { acc, i -> acc + 2f.pow(i.dec()).toLong() },
            jyfs = 0,
            _search = false,
            nd = System.currentTimeMillis(),
            showCount = 10000,
            currentPage = 1,
            sortName = "cdbh",
            sortOrder = "asc",
            time = 1
        )
    }

    suspend fun get2DCode() = withContext(Dispatchers.IO) {
        with(okHttpClient) {
            Log.e(get("https://hub.17wanxiao.com/bsacs/qyweixin.action?flag=shanxizyy_njgydx-qywx-bk&paytype=qyweixin&ecardFunc=HB_2DCode").isRedirect)
        }
    }
}

