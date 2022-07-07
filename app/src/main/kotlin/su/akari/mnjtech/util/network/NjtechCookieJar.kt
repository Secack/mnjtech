package su.akari.mnjtech.util.network

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import su.akari.mnjtech.data.api.URI_NJTECH
import su.akari.mnjtech.data.model.session.Session
import su.akari.mnjtech.data.model.session.SessionManager

object NjtechCookieJar : CookieJar, Iterable<Cookie> {
    private var cookies = ArrayList<Cookie>()

    override fun loadForRequest(url: HttpUrl): List<Cookie> =
        if (url.host.contains(URI_NJTECH)) cookies else arrayListOf()

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        if (url.host.contains(URI_NJTECH)) {
            arrayListOf<Cookie>().apply {
                addAll(cookies)
                addAll(this@NjtechCookieJar.cookies)
            }.distinctBy { it.name }
                .let {
                    this.cookies = ArrayList(it)
                }
        }
    }

    override fun iterator(): Iterator<Cookie> = cookies.iterator()

    override fun toString(): String = cookies.toString()

    fun init() {
        cookies = ArrayList(SessionManager.session.toCookies())
    }

    fun update() {
        SessionManager.update(Session(this).cookies)
    }

    fun clear() {
        cookies.clear()
        SessionManager.clear()
    }
}

fun OkHttpClient.cookie() = this.cookieJar as NjtechCookieJar

fun OkHttpClient.initSession() = cookie().init()

fun OkHttpClient.updateSession() = cookie().update()

fun OkHttpClient.clearSession() = cookie().clear()
