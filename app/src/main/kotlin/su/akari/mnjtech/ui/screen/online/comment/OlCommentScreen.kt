package su.akari.mnjtech.ui.screen.online.comment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.outlined.Comment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.dokar.sheets.rememberBottomSheetState
import kotlinx.coroutines.launch
import org.koin.androidx.compose.viewModel
import su.akari.mnjtech.data.model.online.Comment
import su.akari.mnjtech.ui.component.*
import su.akari.mnjtech.util.handlerWithLoadingAnim
import su.akari.mnjtech.util.toast

@Composable
fun OlCommentScreen() {
    val viewModel by viewModel<OlCommentViewModel>()
    val commentList by viewModel.commentListFlow.collectAsState()

    LazyListScaffold(
        title = {
            Text(text = "留言")
        }
    ) { padding, state ->
        LaunchedEffect(Unit) {
            viewModel.getCommentList()
            state.scrollToItem(0)
        }
        commentList.handlerWithLoadingAnim(
            errorAction = {
                viewModel.getCommentList()
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
                    items(list) { comment ->
                        CommentCard(comment = comment)
                    }
                }
            } else {
                Centered(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(text = "暂无留言")
                }
            }
        }
    }
}

@Composable
private fun CommentCard(comment: Comment) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val bottomSheetState = rememberBottomSheetState()
    MessagePreviewCard(
        avatarUrl = comment.fromUser.avatarUrl,
        nickname = comment.fromUser.nickname,
        modifiedOn = comment.modifiedOn,
        actions = {
            IconButton(
                onClick = {
                    scope.launch {
                        bottomSheetState.peek()
                    }
                }
            ) {
                Icon(
                    imageVector = if (comment.replies.isNotEmpty()) Icons.Filled.Comment else Icons.Outlined.Comment,
                    contentDescription = null
                )
            }
        }
    ) {
        HtmlText(text = comment.content)
        SimpleBottomSheet(
            state = bottomSheetState,
            title = {
                Text(
                    text = "评论",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.align(Alignment.Center)
                )
            },
            action = {
                TextButton(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    onClick = {
                        context.toast("评论暂时关闭")
                    }
                ) {
                    Text(text = "评论")
                }
            },
            onClose = {
                scope.launch {
                    bottomSheetState.collapse()
                }
            }
        ) {
            if (comment.replies.isNotEmpty()) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(comment.replies) { reply ->
                        MessagePreviewCard(
                            avatarUrl = reply.fromUser.avatarUrl,
                            nickname = reply.fromUser.nickname,
                            modifiedOn = reply.modifiedOn
                        ) {
                            HtmlText(text = reply.content)
                        }
                    }
                }
            } else {
                Centered(modifier = Modifier.fillMaxSize()) {
                    Text(text = "暂无评论")
                }
            }
        }
    }
}