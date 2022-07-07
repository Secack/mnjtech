package su.akari.mnjtech.data.model.session

import su.akari.mnjtech.data.api.URI_NJTECH
import okhttp3.Cookie

data class Session constructor(val cookies: MutableMap<String, String>) {
    constructor(iterable: Iterable<Cookie>) : this(iterable.associate { it.name to it.value }
        .toMutableMap())

    fun toCookies() = cookies.map {
        Cookie.Builder()
            .name(it.key)
            .value(it.value)
            .domain(URI_NJTECH)
            .build()
    }

    override fun toString(): String =
        cookies.map { "${it.key}=${it.value}" }.joinToString(";")
}