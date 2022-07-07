package su.akari.mnjtech.ui.util

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import su.akari.mnjtech.ui.local.LocalActivity

@Composable
fun adaptiveGridCell(): GridCells {
    val windowSizeClass = calculateWindowSizeClass(LocalActivity.current)
    return when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> GridCells.Fixed(3)
        else -> GridCells.Adaptive(200.dp)
    }
}