package su.akari.mnjtech.ui.screen.login

import android.os.Bundle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import su.akari.mnjtech.PR
import su.akari.mnjtech.data.repo.NjtechRepo
import su.akari.mnjtech.ui.navigation.DestinationArgs
import su.akari.mnjtech.util.DataStateFlow
import su.akari.mnjtech.util.collectAsStateFlow

class LoginViewModel(
    args: Bundle,
    private val njtechRepo: NjtechRepo
) : ViewModel() {
    val autoLogin by mutableStateOf(args.getInt(DestinationArgs.AutoLogin))
    var loginFlow = DataStateFlow<Unit>()

    fun login(
        username: String,
        password: String,
        provider: Int,
        onWifiResp: (String?) -> Unit
    ): Job {
        viewModelScope.launch {
            PR.username.set(username)
            PR.password.set(password)
        }
        return njtechRepo.login(username, password, provider, onWifiResp)
            .collectAsStateFlow(viewModelScope, loginFlow)
    }
}