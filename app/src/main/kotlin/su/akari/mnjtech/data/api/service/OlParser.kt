package su.akari.mnjtech.data.api.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import su.akari.mnjtech.data.api.URL_ONLINE
import su.akari.mnjtech.data.api.URL_ONLINE_AUTH
import su.akari.mnjtech.util.autoRetry
import su.akari.mnjtech.util.network.get
import su.akari.mnjtech.util.network.updateSession
import java.net.SocketTimeoutException

class OlParser(private val okHttpClient: OkHttpClient) {
    suspend fun loginOnline() = withContext(Dispatchers.IO) {
        with(okHttpClient) {
            autoRetry(
                onSuccess = { resp ->
                    if (resp.request.url.toString().contains(URL_ONLINE)) {
                        updateSession()
                        return@with
                    }
                },
                onError = {
                    require(it !is SocketTimeoutException) { "连接超时，可能未处于校园内网" }
                }
            ) {
                get(URL_ONLINE_AUTH)
            }
            require(false) { "" } // 设备超限后也会登录失败
        }
    }
}