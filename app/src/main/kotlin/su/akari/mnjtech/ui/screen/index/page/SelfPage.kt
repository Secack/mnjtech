package su.akari.mnjtech.ui.screen.index.page

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Job
import su.akari.mnjtech.R
import su.akari.mnjtech.ui.component.Centered
import su.akari.mnjtech.ui.local.LocalNavController
import su.akari.mnjtech.ui.navigation.DestinationArgs
import su.akari.mnjtech.ui.navigation.Destinations
import su.akari.mnjtech.ui.screen.index.IndexViewModel
import su.akari.mnjtech.util.*

@Composable
fun SelfPage(viewModel: IndexViewModel) {
    val navController = LocalNavController.current
    val context = LocalContext.current
    val logout by viewModel.logoutFlow.collectAsState()
    var logoutJob: Job? by rememberState(null)

    logout.onLoading {
        AlertDialog(
            title = {
                Text(text = "登出中")
            },
            text = {
                ResourceText(R.string.wait_for_a_while)
            },
            icon = {
                CircularProgressIndicator(Modifier.size(30.dp))
            },
            onDismissRequest = {
                logoutJob?.cancel()
                viewModel.logoutFlow.value = DataState.Empty
            },
            confirmButton = {}
        )
    }

    logout.observeState(
        onSuccess = {
            navController.navigate("${Destinations.Login}?${DestinationArgs.AutoLogin}=${0}") {
                popUpTo(0)
            }
        },
        onError = {
            context.toast(it)
        }
    )

    Scaffold { padding ->
        Centered(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Button(
                onClick = {
                    logoutJob = viewModel.logout()
                }
            ) {
                Text(text = "注销登录")
            }
        }
    }
}