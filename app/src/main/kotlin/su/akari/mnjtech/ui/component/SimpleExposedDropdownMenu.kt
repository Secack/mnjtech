package su.akari.mnjtech.ui.component

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.util.fastForEach

@Composable
fun <T> SimpleExposedDropdownMenu(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    items: List<Pair<String, T>>,
    onSelected: (Pair<String, T>) -> Unit = {},
    content: @Composable () -> Unit
) {
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = onExpandedChange
    ) {
        content()
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                onExpandedChange(false)
            }
        ) {
            items.fastForEach {
                DropdownMenuItem(
                    text = {
                        Text(text = it.first)
                    },
                    onClick = {
                        onSelected(it)
                        onExpandedChange(false)
                    }
                )
            }
        }
    }
}