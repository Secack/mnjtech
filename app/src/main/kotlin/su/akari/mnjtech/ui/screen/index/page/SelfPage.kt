package su.akari.mnjtech.ui.screen.index.page

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Job
import su.akari.mnjtech.R
import su.akari.mnjtech.ui.local.LocalNavController
import su.akari.mnjtech.ui.navigation.DestinationArgs
import su.akari.mnjtech.ui.navigation.Destinations
import su.akari.mnjtech.ui.screen.index.IndexViewModel
import su.akari.mnjtech.util.DataState
import su.akari.mnjtech.util.ResourceText
import su.akari.mnjtech.util.observeState
import su.akari.mnjtech.util.onLoading
import su.akari.mnjtech.util.rememberState
import su.akari.mnjtech.util.toast

@Composable
fun SelfPage(viewModel: IndexViewModel) {
    val navController = LocalNavController.current
    val context = LocalContext.current
    val logout by viewModel.logoutFlow.collectAsState()
    var logoutJob: Job? by rememberState(null)
    var isRotated by rememberState(value = false)
    val rotationAngle by animateFloatAsState(
        targetValue = if (isRotated) 360F else 0F,
        animationSpec = tween(durationMillis = 500, easing = FastOutLinearInEasing)
    )

    logout.onLoading {
        AlertDialog(title = {
            Text(text = "登出中")
        }, text = {
            ResourceText(R.string.wait_for_a_while)
        }, icon = {
            CircularProgressIndicator(Modifier.size(30.dp))
        }, onDismissRequest = {
            logoutJob?.cancel()
            viewModel.logoutFlow.value = DataState.Empty
        }, confirmButton = {})
    }

    logout.observeState(onSuccess = {
        navController.navigate("${Destinations.Login}?${DestinationArgs.AutoLogin}=${0}") {
            popUpTo(0)
        }
    }, onError = {
        context.toast(it)
    })

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .rotate(rotationAngle)
                    .clickable {
                        isRotated = !isRotated
                    },
                painter = painterResource(id = R.drawable.avatar_icon),
                contentDescription = null,
            )
            Spacer(modifier = Modifier.height(15.dp))
            Text(
                text = "杨凯 个人开发", fontSize = 15.sp, fontWeight = FontWeight.Bold
            )
            Text(
                text = "Copyright © 2022-2023 Secack",
                fontSize = 10.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(35.dp))
            AboutCardWidget(modifier = Modifier.fillMaxHeight(0.11f), onClick = {
                Intent(
                    Intent.ACTION_VIEW, Uri.parse("https://github.com/Secack/mnjtech")
                ).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }.let(context::startActivity)
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_github),
                    contentDescription = null,
                    modifier = Modifier.size(42.dp)
                )
                Spacer(modifier = Modifier.width(15.dp))
                Text(
                    text = "项目源码", fontWeight = FontWeight.Bold, fontSize = 16.sp
                )
            }
            Spacer(modifier = Modifier.height(35.dp))
            Button(onClick = {
                logoutJob = viewModel.logout()
            }) {
                Text(text = "注销登录")
            }
        }
    }
}

@Composable
fun AboutCardWidget(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    onClick: () -> Unit = {},
    content: @Composable () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth(1.0f)
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.elevatedCardElevation(),
    ) {
        Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
            Spacer(modifier = Modifier.width(25.dp))
            content()
        }
    }
}