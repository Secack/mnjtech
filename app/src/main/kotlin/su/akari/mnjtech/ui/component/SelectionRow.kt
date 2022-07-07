package su.akari.mnjtech.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.NavigateNext
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import su.akari.mnjtech.util.animateRotate

@Composable
fun <T> SelectionRow(
    label: String,
    items: List<Pair<String, T>>,
    defValue: T? = null,
    onSelected: (T) -> Unit = {}
) {
    var expanded by rememberSaveable {
        mutableStateOf(false)
    }
    var option by rememberSaveable {
        mutableStateOf((items.find { it.second == defValue } ?: items[0]).first)
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.width(80.dp),
            text = label
        )

        SimpleExposedDropdownMenu(expanded = expanded, onExpandedChange = {
            expanded = expanded.not()
        }, items = items, onSelected = {
            onSelected(it.second)
            option = it.first
        }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .clickable {}
            ) {
                Text(
                    modifier = Modifier.align(Alignment.CenterStart),
                    text = option,
                    color = MaterialTheme.colorScheme.primary
                )
                IconButton(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .animateRotate(if (expanded) 180f else 0f),
                    onClick = {}
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ArrowDropDown, contentDescription = null
                    )
                }
            }
        }
    }
    ThinDivider()
}

@Composable
fun SelectionRow(
    expanded: Boolean,
    label: String,
    text: String,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.width(80.dp),
            text = label
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .clickable(onClick = onClick),
        ) {
            Text(
                modifier = Modifier.align(Alignment.CenterStart),
                text = text,
                color = MaterialTheme.colorScheme.primary
            )
            IconButton(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .animateRotate(if (expanded) 90f else 0f),
                onClick = onClick
            ) {
                Icon(
                    imageVector = Icons.Outlined.NavigateNext, contentDescription = null
                )
            }
        }
    }
    AnimatedVisibility(visible = expanded.not()) {
        ThinDivider()
    }
}