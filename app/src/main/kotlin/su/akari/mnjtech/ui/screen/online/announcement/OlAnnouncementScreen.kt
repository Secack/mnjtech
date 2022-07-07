package su.akari.mnjtech.ui.screen.online.announcement

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.viewModel
import su.akari.mnjtech.data.model.online.Announcement
import su.akari.mnjtech.ui.component.Centered
import su.akari.mnjtech.ui.component.HtmlText
import su.akari.mnjtech.ui.component.LazyListScaffold
import su.akari.mnjtech.util.format
import su.akari.mnjtech.util.handlerWithLoadingAnim

@Composable
fun OlAnnouncementScreen() {
    val viewModel by viewModel<OlAnnouncementViewModel>()
    val announcementList by viewModel.announcementListFlow.collectAsState()

    LazyListScaffold(
        title = {
            Text(text = "公告")
        }
    ) { padding, state ->
        announcementList.handlerWithLoadingAnim(
            errorAction = {
                viewModel.getAnnouncementList()
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
                    items(list) {
                        AnnouncementCard(it)
                    }
                }
            } else {
                Centered(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(text = "暂无公告")
                }
            }
        }
    }
}

@Composable
private fun AnnouncementCard(announcement: Announcement) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (announcement.isTop == 1) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "置顶",
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontSize = 22.sp
                        )
                    }
                }
                Text(
                    text = announcement.title,
                    style = MaterialTheme.typography.titleLarge
                )
            }
            HtmlText(
                text = announcement.content,
                imageFillWidth = true
            )
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(announcement.createdBy)
                    }
                    append(" 发布于")
                    append(announcement.createdOn.format(detail = true))
                },
                fontSize = 14.sp
            )
        }
    }
}

