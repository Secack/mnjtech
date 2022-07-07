package su.akari.mnjtech.util.network

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

const val USER_AGENT =
    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/101.0.4951.54 Safari/537.36"

object UserAgentInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val userAgentRequest: Request = chain.request()
            .newBuilder()
            .header("User-Agent", USER_AGENT)
            .build()
        return chain.proceed(userAgentRequest)
    }
}