package su.akari.mnjtech.ui.activity

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import su.akari.mnjtech.data.model.Network
import su.akari.mnjtech.data.model.profile.Profile
import su.akari.mnjtech.data.model.session.SessionManager
import su.akari.mnjtech.data.repo.NjtechRepo
import su.akari.mnjtech.data.repo.OlRepo
import su.akari.mnjtech.util.DataState
import su.akari.mnjtech.util.DataStateFlow
import su.akari.mnjtech.util.collectAsStateFlow

class MainViewModel(
    private val njtechRepo: NjtechRepo,
    private val olRepo: OlRepo
) : ViewModel() {
    var userData: Profile by mutableStateOf(Profile.GUEST)
    var userDataFetched by mutableStateOf(false)
    val loginJwglFlow = DataStateFlow<Unit>()
    var loginOnlineFlow = DataStateFlow<Unit>()
    val networkState by mutableStateOf(Network.OFFLINE)

    init {
        viewModelScope.launch {
            prepareUserData()
        }
    }

    suspend fun prepareUserData() {
        if (SessionManager.session.cookies.isNotEmpty()) {
            njtechRepo.getProfile().collectLatest {
                userData = if (it is DataState.Success) {
                    it.data.apply {
                        njtechRepo.id = id
                        loginJwglFlow.value = DataState.Empty
                        loginOnlineFlow.value = DataState.Empty
                    }
                } else {
                    Profile.GUEST
                }
            }
        }
        userDataFetched = true
    }

    fun loginJwgl() {
        njtechRepo.loginJwgl().collectAsStateFlow(viewModelScope, loginJwglFlow)
    }

    fun loginOnline() {
        olRepo.loginOnline().collectAsStateFlow(viewModelScope, loginOnlineFlow)
    }

    fun refreshNetworkState() {

    }
}

