package su.akari.mnjtech.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import su.akari.mnjtech.data.api.URL_ONLINE
import su.akari.mnjtech.util.format

@Composable
fun MessagePreviewCard(
    modifier: Modifier = Modifier,
    avatarUrl: String,
    nickname: String,
    modifiedOn: Long,
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable () -> Unit
) {
    ElevatedCard(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(40.dp),
                    model = ImageRequest.Builder(LocalContext.current).run {
                        avatarUrl.replace("@njtech.edu.cn", "").let {
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
                    },
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
                Column {
                    Text(
                        text = nickname,
                        fontSize = 16.sp
                    )
                    Text(
                        text = modifiedOn.format(detail = true),
                        fontSize = 12.sp
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                actions()
            }
            content()
        }
    }
}