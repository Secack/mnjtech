package su.akari.mnjtech.data.model.session

import su.akari.mnjtech.PR
import su.akari.mnjtech.util.fromJsonMap
import su.akari.mnjtech.util.getBlocking
import su.akari.mnjtech.util.setBlocking
import su.akari.mnjtech.util.toJson

object SessionManager {
    val session: Session by lazy {
        Session(
            runCatching {
                require(PR.saveSession.getBlocking())
                PR.cookies.getBlocking().fromJsonMap<String, String>().toMutableMap()
            }.getOrElse {
                PR.cookies.setBlocking("{}")
                mutableMapOf()
            }
        )
    }

    fun update(cookies: MutableMap<String, String>) {
        session.cookies.putAll(cookies)
        PR.cookies.setBlocking(cookies.toJson())
    }

    fun clear() {
        session.cookies.clear()
        PR.cookies.setBlocking("{}")
    }
}