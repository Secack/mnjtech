package su.akari.mnjtech.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import su.akari.mnjtech.data.model.online.Video
import su.akari.mnjtech.ui.local.LocalNavController
import su.akari.mnjtech.ui.navigation.Destinations

@Composable
fun VideoPreviewCard(video: Video) {
    val navController = LocalNavController.current
    ElevatedCard(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 16.dp),
        onClick = {
            navController.navigate("${Destinations.OlDetail}/${video.id}")
        }
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopEnd
        ) {
            Column {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current).run {
                        data(video.cover)
                        crossfade(true)
                        build()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = video.name,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = video.year ?: "未知年份",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            maxLines = 1
                        )
                        Text(
                            text = video.playCount.toString(),
                            fontSize = 12.sp,
                            color = Color.Gray,
                            maxLines = 1
                        )
                    }
                }
            }
            video.score?.average?.let {
                Surface(
                    color = Color.Black.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(bottomStart = 4.dp)
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 6.dp),
                        text = it,
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }
            }
        }

    }
}