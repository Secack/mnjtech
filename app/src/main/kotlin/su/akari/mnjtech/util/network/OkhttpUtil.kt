package su.akari.mnjtech.util.network

import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

suspend fun OkHttpClient.request(url: String, builderBlock: Request.Builder.() -> Unit) =
    Request.Builder().url(url).apply(builderBlock).build().let { newCall(it) }.run {
        suspendCancellableCoroutine<Response> {
            enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    if (it.isCancelled) return
                    it.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    it.resume(response)
                }
            })
            it.invokeOnCancellation {
                runCatching {
                    cancel()
                }
            }
        }
    }

suspend fun OkHttpClient.get(url: String) = request(url) { get() }

suspend fun OkHttpClient.getString(url: String) =
    get(url).body.string()

suspend fun OkHttpClient.post(url: String, body: RequestBody) = request(url) { post(body) }

suspend fun OkHttpClient.postString(url: String, body: RequestBody) =
    post(url, body).let {
        it to it.body.string()
    }

suspend fun OkHttpClient.postJson(url: String, json: String) =
    post(url, json.toRequestBody("application/json; charset=utf-8".toMediaType()))

suspend fun OkHttpClient.postMultipart(url: String, data: String) =
    post(
        url,
        MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("data", data).build()
    )


