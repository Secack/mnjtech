package su.akari.mnjtech.ui.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private enum class SlotsEnum { Main, Dependent }

@Composable
fun MeasureTextWidth(text: String, content: @Composable (width: Dp) -> Unit) {
    SubcomposeLayout { constraints ->
        val textWidth = subcompose(SlotsEnum.Main) {
            Text(text = text)
        }[0].measure(Constraints()).width.toDp() + 10.dp //reserved
        val contentPlaceable = subcompose(SlotsEnum.Dependent) {
            content(textWidth)
        }[0].measure(constraints)
        layout(contentPlaceable.width, contentPlaceable.height) {
            contentPlaceable.place(0, 0)
        }
    }
}