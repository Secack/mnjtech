package su.akari.mnjtech.util

import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

suspend inline fun <T> autoRetry(
    onSuccess: (T) -> Unit,
    onError: (Throwable) -> Unit = {},
    times: Int = 3,
    duration: Duration = 3.seconds,
    block: () -> T,
) {
    repeat(times) {
        runCatching(block).onSuccess(onSuccess).onFailure(onError)
        delay(duration)
    }
}