package su.akari.mnjtech.ui.screen.login

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.WebView
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.google.accompanist.web.AccompanistWebViewClient
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewState
import org.koin.androidx.compose.viewModel
import su.akari.mnjtech.PR
import su.akari.mnjtech.R
import su.akari.mnjtech.data.api.URL_I_NJTECH
import su.akari.mnjtech.ui.component.BackIcon
import su.akari.mnjtech.ui.component.Md3TopBar
import su.akari.mnjtech.ui.local.LocalActivity
import su.akari.mnjtech.ui.local.LocalNavController
import su.akari.mnjtech.ui.navigation.Destinations
import su.akari.mnjtech.util.setBlocking
import su.akari.mnjtech.util.stringResource
import su.akari.mnjtech.util.toast

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun LoginWebScreen() {
    val viewModel by viewModel<LoginWebViewModel>()
    val activity = LocalActivity.current
    val navController = LocalNavController.current
    Scaffold(topBar = {
        Md3TopBar(
            title = {
                Text(text = "登录")
            },
            navigationIcon = {
                BackIcon()
            }
        )
    }) { padding ->
        val state = rememberWebViewState(URL_I_NJTECH)
        val client = remember {
            object : AccompanistWebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    url?.takeIf { it.contains(URL_I_NJTECH) }?.let {
                        viewModel.login(activity.viewModel) {
                            PR.saveSession.setBlocking(true)
                            activity.toast(activity.stringResource(R.string.login_successful))
                            navController.navigate(Destinations.Index)
                        }
                    }
                }
            }
        }
        WebView(
            state = state,
            modifier = Modifier.padding(padding),
            onCreated = {
                it.settings.javaScriptEnabled = true
            },
            client = client
        )
    }
}