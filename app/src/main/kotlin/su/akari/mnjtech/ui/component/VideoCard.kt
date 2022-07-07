package su.akari.mnjtech.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import su.akari.mnjtech.ui.local.LocalNavController
import su.akari.mnjtech.ui.navigation.Destinations

data class VideoPosition(
    val index: Int,
    val time: Long
)

@Composable
fun VideoCard(
    videoId: Int,
    cover: String,
    extraRoute: String = "",
    onDelete: (videoId: Int) -> Unit,
    content: @Composable () -> Unit
) {
    val navController = LocalNavController.current
    val scope = rememberCoroutineScope()
    val swipeState = rememberSwipeableState(0)
    SwipeableItem(
        swipeState = swipeState,
        childContent = {
            Surface(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .padding(end = 16.dp)
                    .fillMaxHeight(),
                color = Color.Red,
                shape = RoundedCornerShape(12.0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .clickable {
                            scope.launch {
                                swipeState.animateTo(0)
                            }
                            onDelete(videoId)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "删除",
                        color = Color.White
                    )
                }
            }
        }
    ) {
        ElevatedCard(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth(),
            onClick = {
                navController.navigate("${Destinations.OlDetail}/$videoId$extraRoute")
            }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    modifier = Modifier
                        .fillMaxWidth(0.25f)
                        .aspectRatio(1f),
                    model = cover,
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxHeight()
                ) {
                    content()
                }
            }
        }
    }
}
