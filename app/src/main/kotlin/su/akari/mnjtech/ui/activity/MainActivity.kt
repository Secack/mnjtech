package su.akari.mnjtech.ui.activity

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import kotlinx.coroutines.delay
import org.koin.androidx.viewmodel.ext.android.viewModel
import su.akari.mnjtech.ui.local.LocalActivity
import su.akari.mnjtech.ui.local.LocalNavController
import su.akari.mnjtech.ui.local.LocalNetworkState
import su.akari.mnjtech.ui.local.LocalSelfData
import su.akari.mnjtech.ui.navigation.NavGraph
import su.akari.mnjtech.ui.theme.MNjtechTheme
import kotlin.time.Duration.Companion.seconds

class MainActivity : AppCompatActivity() {
    val viewModel by viewModel<MainViewModel>()

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        requestedOrientation =
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT //TODO: adapt to horizontal screen

        super.onCreate(savedInstanceState)

        savedInstanceState ?: run {
            installSplashScreen().setKeepOnScreenCondition {
                !viewModel.userDataFetched
            }
        }

        setContent {
            LaunchedEffect(Unit) {
                while (true) {
                    delay(2.seconds)
                    viewModel.refreshNetworkState()
                }
            }
            val navController = rememberAnimatedNavController()
            CompositionLocalProvider(
                LocalActivity provides this@MainActivity,
                LocalNavController provides navController,
                LocalSelfData provides viewModel.userData,
                LocalNetworkState provides viewModel.networkState
            ) {
                MNjtechTheme {
                    NavGraph()
                }
            }
        }
    }
}
