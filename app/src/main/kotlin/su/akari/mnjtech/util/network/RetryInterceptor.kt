package su.akari.mnjtech.util.network

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import java.net.SocketTimeoutException

class Retry(
    private val maxRetryTimes: Int = 2
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val newRequest = {
            request.newBuilder().build()
        }

        repeat(maxRetryTimes) {
            runCatching {
                chain.proceed(newRequest())
            }.onSuccess {
                return it
            }.onFailure {
                if (it is SocketTimeoutException) {
                    Log.d("Retry", "Retry: ${it.message}")
                } else {
                    throw it
                }
            }
        }
        return chain.proceed(newRequest())
    }
}