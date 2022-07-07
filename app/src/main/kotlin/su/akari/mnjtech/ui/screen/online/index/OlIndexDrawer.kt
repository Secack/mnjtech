package su.akari.mnjtech.ui.screen.online.index

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import kotlinx.coroutines.launch
import su.akari.mnjtech.data.api.URL_ONLINE
import su.akari.mnjtech.data.model.online.User
import su.akari.mnjtech.ui.local.LocalActivity
import su.akari.mnjtech.ui.local.LocalNavController
import su.akari.mnjtech.ui.navigation.Destinations
import su.akari.mnjtech.util.DataState
import su.akari.mnjtech.util.onSuccess

@Composable
fun OlIndexDrawer(
    viewModel: OlIndexViewModel,
    drawerState: DrawerState
) {
    val activity = LocalActivity.current
    val loginOnline by activity.viewModel.loginOnlineFlow.collectAsState()
    val navController = LocalNavController.current
    val scope = rememberCoroutineScope()

    val userDetail by viewModel.userDetailFlow.collectAsState()
    val profileCount by viewModel.profileCountFlow.collectAsState()

    fun <T> userProp(callback: (User) -> T): T? =
        userDetail.readSafely()?.let { callback(it.data) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(top = 18.dp),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Bottom
            ) {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 32.dp)
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current).run {
                            userProp { user ->
                                user.avatarUrl.replace(user.email, user.openId).let {
                                    it.takeIf {
                                        it.startsWith('/')
                                    }?.run {
                                        decoderFactory(SvgDecoder.Factory())
                                        URL_ONLINE + it
                                    } ?: it
                                }.let {
                                    data(it)
                                }
                                crossfade(true)
                                build()
                            }
                        },
                        contentScale = ContentScale.Crop,
                        contentDescription = null
                    )
                }
                Column(modifier = Modifier.padding(horizontal = 32.dp, vertical = 16.dp)) {
                    Text(
                        text = userProp {
                            it.nickname
                        } ?: "加载中",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = userProp {
                            it.openId
                        } ?: ""
                    )
                }
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            if (loginOnline is DataState.Success) {
                NavigationDrawerItem(
                    label = {
                        Text(text = "公告")
                    },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            navController.navigate(Destinations.OlAnnouncement)
                        }
                    },
                    icon = {
                        Icon(Icons.Outlined.Campaign, null)
                    }
                )
                NavigationDrawerItem(
                    label = {
                        Text(text = "通知")
                    },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            navController.navigate(Destinations.OlNotification)
                        }
                    },
                    icon = {
                        Icon(Icons.Outlined.Notifications, null)
                    },
                    badge = {
                        viewModel.notifyCount.takeIf { it != 0 }?.let {
                            Text(text = viewModel.notifyCount.toString())
                        }
                        profileCount.onSuccess { resp ->
                            resp.data.unReadNotifyCount.let {
                                viewModel.notifyCount = it
                            }
                        }
                    }
                )
                NavigationDrawerItem(
                    label = {
                        Text(text = "历史")
                    },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            navController.navigate(Destinations.OlRecord)
                        }
                    },
                    icon = {
                        Icon(Icons.Outlined.History, null)
                    },
                )
                NavigationDrawerItem(
                    label = {
                        Text(text = "收藏")
                    },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            navController.navigate(Destinations.OlCollection)
                        }
                    },
                    icon = {
                        Icon(Icons.Outlined.Favorite, null)
                    },
                )
                NavigationDrawerItem(
                    label = {
                        Text(text = "留言")
                    },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            navController.navigate(Destinations.OlComment)
                        }
                    },
                    icon = {
                        Icon(Icons.Outlined.Message, null)
                    },
                )
            }
            NavigationDrawerItem(
                label = {
                    Text(text = "缓存")
                },
                selected = false,
                onClick = {
                    scope.launch {
                        drawerState.close()
                        navController.navigate(Destinations.OlDownload)
                    }
                },
                icon = {
                    Icon(Icons.Outlined.Download, null)
                },
            )
        }
    }
}