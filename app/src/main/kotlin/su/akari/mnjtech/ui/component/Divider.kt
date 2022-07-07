package su.akari.mnjtech.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.unit.dp

@Composable
fun ThinDivider(modifier: Modifier = Modifier, color: Color = Color(0x99999999)) {
    Divider(modifier = modifier, color = color, thickness = 1.dp)
}

@Composable
fun DashedDivider(modifier: Modifier = Modifier, color: Color = Color(0x99999999)) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
    ) {
        drawLine(
            color = color,
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 5f), 0f)
        )
    }
}