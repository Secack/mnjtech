package su.akari.mnjtech.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.dokar.sheets.*

@Composable
fun SimpleBottomSheet(
    state: BottomSheetState,
    modifier: Modifier = Modifier,
    skipPeek: Boolean = false,
    title: @Composable BoxScope.() -> Unit = {},
    action: @Composable BoxScope.() -> Unit = {},
    onClose: () -> Unit = {},
    peekHeight: PeekHeight = PeekHeight.fraction(1f),
    shape: Shape = MaterialTheme.shapes.medium.copy(
        bottomStart = CornerSize(0.dp),
        bottomEnd = CornerSize(0.dp)
    ),
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    dimColor: Color = Color.Black,
    maxDimAmount: Float = BottomSheetDefaults.MaxDimAmount,
    behaviors: DialogSheetBehaviors = BottomSheetDefaults.dialogSheetBehaviors(),
    dragHandle: @Composable () -> Unit = { DragHandle() },
    content: @Composable () -> Unit
) {
    BottomSheet(
        state = state,
        modifier = modifier,
        skipPeek = skipPeek,
        peekHeight = peekHeight,
        shape = shape,
        backgroundColor = backgroundColor,
        dimColor = dimColor,
        maxDimAmount = maxDimAmount,
        behaviors = behaviors,
        dragHandle = dragHandle
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                IconButton(
                    onClick = {
                        onClose()
                    }
                ) {
                    Icon(Icons.Outlined.Close, null)
                }
                title()
                action()
            }
            content()
        }
    }
}