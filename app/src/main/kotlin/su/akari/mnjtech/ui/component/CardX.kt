package su.akari.mnjtech.ui.component

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun CardX(
    style: CardStyle,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    Crossfade(style) { cardStyle ->
        when (cardStyle) {
            CardStyle.Filled -> Card(
                onClick = onClick,
                modifier = modifier,
                enabled = enabled,
                content = content
            )
            CardStyle.Outlined -> OutlinedCard(
                onClick = onClick,
                modifier = modifier,
                enabled = enabled,
                content = content
            )
            CardStyle.Elevated -> ElevatedCard(
                onClick = onClick,
                modifier = modifier,
                enabled = enabled,
                content = content
            )
        }
    }
}

enum class CardStyle {
    Filled,
    Outlined,
    Elevated
}