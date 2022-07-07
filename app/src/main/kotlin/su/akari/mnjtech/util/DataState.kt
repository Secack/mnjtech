package su.akari.mnjtech.util

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import su.akari.mnjtech.data.model.online.OlResponse
import su.akari.mnjtech.ui.component.ErrorAnim
import su.akari.mnjtech.ui.component.LoadingAnim

sealed class DataState<out T> {
    object Empty : DataState<Nothing>()
    object Loading : DataState<Nothing>()

    data class Success<T>(
        val data: T
    ) : DataState<T>()

    data class Error(
        val msg: String = ""
    ) : DataState<Nothing>()

    fun read(): T = (this as Success<T>).data
    fun readSafely(): T? = if (this is Success<T>) read() else null
}

@Composable
fun <T> DataState<T>.onSuccess(
    content: @Composable (T) -> Unit
): DataState<T> {
    if (this is DataState.Success) content(data)
    return this
}

@Composable
fun <T> DataState<T>.onError(
    content: @Composable (String) -> Unit
): DataState<T> {
    if (this is DataState.Error) content(msg)
    return this
}

@Composable
fun <T> DataState<T>.onEmpty(
    content: @Composable () -> Unit
): DataState<T> {
    if (this == DataState.Empty) content()
    return this
}

@Composable
fun <T> DataState<T>.onLoading(
    content: @Composable () -> Unit
): DataState<T> {
    if (this == DataState.Loading) content()
    return this
}

@Composable
fun <T> DataState<T>.onEmptyOrLoading(
    content: @Composable () -> Unit
): DataState<T> {
    if (this == DataState.Empty || this == DataState.Loading) content()
    return this
}

@Composable
fun <T> DataState<T>.handlerWithLoadingAnim(
    errorMsg: String? = null,
    errorAction: () -> Unit,
    onSuccess: @Composable (T) -> Unit
) = onSuccess(onSuccess)
    .onEmptyOrLoading {
        LoadingAnim()
    }
    .onError {
        ErrorAnim(text = errorMsg ?: it, onClick = errorAction)
    }


@Suppress("FunctionName")
fun <T> DataStateFlow(): MutableStateFlow<DataState<T>> = MutableStateFlow(DataState.Empty)

inline fun <T : Any> dataStateFlow(crossinline block: suspend () -> T) = flow {
    emit(DataState.Loading)
    runCatching {
        emit(DataState.Success(block()))
    }.getOrElse { th ->
        Log.e(th.message)
        generateSequence(th) {
            it.cause
        }.find { it.cause == null }?.let {
            Log.e(it)
        }
        emit(DataState.Error(th.message ?: ""))
    }
}

@SuppressLint("ComposableNaming")
@Composable
fun <T> DataState<T>.observeState(
    needUpdate: Boolean = true,
    onSuccess:suspend (T) -> Unit = {},
    onError: suspend (String) -> Unit = {},
    onEmpty:suspend () -> Unit = {},
    onLoading: suspend () -> Unit = {},
) {
    LaunchedEffect(this) {
        if (needUpdate) {
            when (this@observeState) {
                is DataState.Success -> onSuccess(this@observeState.data)
                is DataState.Error -> onError(this@observeState.msg)
                DataState.Empty -> onEmpty()
                DataState.Loading -> onLoading()
            }
        }
    }
}

@SuppressLint("ComposableNaming")
@Composable
fun <T, K> bindDataState(
    parent: DataState<T>,
    vararg children: DataState<K>,
    initChildren: (T) -> Unit,
    parentError: suspend (String) -> Unit = {},
    childError: suspend (String) -> Unit = parentError,
) {
    parent.observeState(
        needUpdate = children.any { it !is DataState.Success },
        onSuccess = {
            initChildren(it)
        },
        onError = parentError
    )
    children.forEach {
        it.observeState(onError = childError)
    }
}

@Suppress("FunctionName")
fun <T> OlStateFlow() = DataStateFlow<OlResponse<T>>()