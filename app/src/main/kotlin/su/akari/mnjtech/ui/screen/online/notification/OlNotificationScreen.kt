package su.akari.mnjtech.ui.screen.online.notification

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Markunread
import androidx.compose.material.icons.outlined.NavigateNext
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.viewModel
import su.akari.mnjtech.data.model.online.Notification
import su.akari.mnjtech.ui.component.Centered
import su.akari.mnjtech.ui.component.HtmlText
import su.akari.mnjtech.ui.component.LazyListScaffold
import su.akari.mnjtech.ui.component.MessagePreviewCard
import su.akari.mnjtech.ui.local.LocalNavController
import su.akari.mnjtech.ui.navigation.Destinations
import su.akari.mnjtech.util.handlerWithLoadingAnim
import su.akari.mnjtech.util.toast

@Composable
fun OlNotificationScreen() {
    val context = LocalContext.current
    val viewModel by viewModel<OlNotificationViewModel>()
    val notificationList by viewModel.notificationListFlow.collectAsState()

    LazyListScaffold(
        title = {
            Text(text = "通知")
        }
    ) { padding, state ->
        LaunchedEffect(Unit) {
            viewModel.getNotificationList()
            state.scrollToItem(0)
        }
        notificationList.handlerWithLoadingAnim(
            errorAction = {
                viewModel.getNotificationList()
            }
        ) { resp ->
            val list = resp.data.list
            if (list.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .padding(padding)
                        .navigationBarsPadding(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(16.dp),
                    state = state
                ) {
                    items(list) { notification ->
                        NotificationCard(notification = notification) {
                            viewModel.markNotify(it) {
                                context.toast("标记失败")
                            }
                        }
                    }
                }
            } else {
                Centered(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(text = "暂无通知")
                }
            }
        }
    }
}

@Composable
private fun NotificationCard(notification: Notification, onMark: (Int) -> Unit) {
    val navController = LocalNavController.current
    MessagePreviewCard(
        avatarUrl = notification.sender.avatarUrl,
        nickname = notification.sender.nickname,
        modifiedOn = notification.modifiedOn,
        actions = {
            AnimatedVisibility(
                visible = notification.userNotify.isRead == 0
            ) {
                IconButton(
                    onClick = {
                        onMark(notification.userNotify.id)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Markunread,
                        contentDescription = null
                    )
                }
            }
            IconButton(
                onClick = {
                    navController.navigate(Destinations.OlMessage)
                }
            ) {
                Icon(
                    imageVector = Icons.Outlined.NavigateNext,
                    contentDescription = null
                )
            }
        }
    ) {
        notification.content.takeIf { it.isNotEmpty() }?.let {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                tonalElevation = 4.dp
            ) {
                HtmlText(
                    modifier = Modifier.padding(4.dp),
                    text = notification.origin
                )
            }
            HtmlText(text = it)
        } ?: HtmlText(text = notification.origin)
    }
}