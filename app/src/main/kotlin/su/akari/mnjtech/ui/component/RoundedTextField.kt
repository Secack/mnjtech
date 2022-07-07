package su.akari.mnjtech.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExposedDropdownMenuDefaults.textFieldColors
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.TextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun RoundedTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean = true,
    shape: RoundedCornerShape = RoundedCornerShape(20.dp),
    textStyle: TextStyle= LocalTextStyle.current,
    shadow: Dp = 0.dp,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    leadingIcon: @Composable () -> Unit,
    trailingIcon: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
) {
    TextField(
        modifier = modifier
            .fillMaxWidth()
            .shadow(shadow, shape = shape, clip = true)
            .clip(shape),
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        colors = defaultTextFieldColors(),
        singleLine = true,
        shape = shape,
        textStyle = textStyle,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        placeholder = placeholder,
    )
}

@Composable
fun defaultTextFieldColors() = textFieldColors(
    focusedIndicatorColor = MaterialTheme.colorScheme.secondaryContainer,
    unfocusedIndicatorColor = MaterialTheme.colorScheme.secondaryContainer,
    backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
    textColor = MaterialTheme.colorScheme.onSurfaceVariant,
    cursorColor = MaterialTheme.colorScheme.onSurfaceVariant,
)