package su.akari.mnjtech.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

fun <T : Any> Flow<T>.collectAsStateFlow(
    scope: CoroutineScope,
    target: MutableStateFlow<T>
) = scope.launch {
    this@collectAsStateFlow.stateIn(this).collect {
        target.value = it
    }
}

