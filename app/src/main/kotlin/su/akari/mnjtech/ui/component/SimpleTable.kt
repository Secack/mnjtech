package su.akari.mnjtech.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun SimpleTable(
    modifier: Modifier = Modifier,
    rowSize: Int,
    header: List<Pair<String, Float>>,
    headerHeight: Dp = 40.dp,
    dataRowHeight: Dp = 60.dp,
    border: Boolean = false,
    stripe: Boolean = false,
    borderWidth: Dp = Dp.Hairline,
    dataRow: @Composable (Int, Int) -> Unit
) {
    LazyColumn(
        modifier = modifier
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                header.forEach {
                    var rowModifier = Modifier
                        .fillParentMaxWidth(it.second)
                        .height(headerHeight)
                    if (border) {
                        rowModifier = rowModifier.border(
                            border = BorderStroke(
                                borderWidth,
                                color = Color.Gray
                            )
                        )
                    }
                    Row(
                        modifier = rowModifier,
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = it.first,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
            if (!border) {
                Divider(modifier = Modifier.fillMaxWidth(), color = Color.LightGray)
            }
        }
        items(rowSize) { row ->
            var wholeRowModifier = Modifier
                .fillMaxWidth()
                .height(dataRowHeight)
                .background(color = MaterialTheme.colorScheme.background)
            if (stripe && (row % 2 == 1)) {
                wholeRowModifier =
                    wholeRowModifier.background(color = Color.Gray.copy(alpha = 0.191f))
            }
            Row(
                modifier = wholeRowModifier,
                verticalAlignment = Alignment.CenterVertically
            ) {
                header.forEachIndexed { index, it ->
                    var rowModifier = Modifier
                        .fillParentMaxWidth(it.second)
                        .height(dataRowHeight)
                    if (border) {
                        rowModifier = rowModifier.border(
                            border = BorderStroke(
                                borderWidth,
                                color = Color.Gray
                            )
                        )
                    }
                    Row(
                        modifier = rowModifier,
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        dataRow.invoke(row, index)
                    }
                }
            }
            if (!border) {
                Divider(modifier = Modifier.fillMaxWidth(), color = Color.LightGray)
            }
        }
    }
}