package su.akari.mnjtech.ui.screen.login

import android.webkit.CookieManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import su.akari.mnjtech.data.api.URL_I_NJTECH
import su.akari.mnjtech.data.api.URL_U_NJTECH
import su.akari.mnjtech.data.model.profile.Profile
import su.akari.mnjtech.data.model.session.SessionManager
import su.akari.mnjtech.ui.activity.MainViewModel

class LoginWebViewModel : ViewModel() {
    fun login(viewModel: MainViewModel, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val mapCookie = { dest: MutableMap<String, String>, src: String ->
                src.split(';').map { cookie ->
                    cookie.split('=').let {
                        dest.put(it[0].trim(), it[1].trim())
                    }
                }
            }
            mutableMapOf<String, String>().let {
                with(CookieManager.getInstance()) {
                    mapCookie(it, getCookie(URL_U_NJTECH))
                    mapCookie(it, getCookie(URL_I_NJTECH))
                }
                SessionManager.update(it)
            }

            viewModel.prepareUserData()
            if (viewModel.userData != Profile.GUEST) {
                onSuccess()
            }
        }
    }
}