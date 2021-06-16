package dev.sebastiano.bundel.composables

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.sebastiano.bundel.BundelTheme

@Composable
@Preview
fun Preview() {
    BundelTheme {
        var checked by remember { mutableStateOf(false) }
        MaterialChip(
            modifier = Modifier.padding(12.dp, 6.dp),
            checked = checked,
            onCheckedChanged = {
                checked = !checked
                println("Click! $checked")
            }
        ) {
            Text("Ciao Ivan")
        }
    }
}

@Composable
internal fun MaterialChip(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    checked: Boolean,
    checkedBackgroundColor: Color = MaterialTheme.colors.surface,
    checkedContentColor: Color = contentColorFor(checkedBackgroundColor),
    checkedBorder: BorderStroke? = null,
    uncheckedBackgroundColor: Color = checkedBackgroundColor.copy(alpha = .54f),
    uncheckedContentColor: Color = checkedContentColor.copy(alpha = .54f),
    uncheckedBorder: BorderStroke? = null,
    elevation: Dp = 0.dp,
    onCheckedChanged: (checked: Boolean) -> Unit,
    content: @Composable () -> Unit
) {
    val bgColor by animateColorAsState(
        targetValue = if (checked) checkedBackgroundColor else uncheckedBackgroundColor
    )

    MaterialPill(
        modifier = Modifier
            .toggleable(
                value = checked,
                enabled = enabled,
                role = Role.Checkbox,
                onValueChange = { onCheckedChanged(!checked) }
            )
            .then(modifier),
        bgColor = bgColor,
        contentColor = if (checked) checkedContentColor else uncheckedContentColor,
        borderStroke = if (checked) checkedBorder else uncheckedBorder,
        elevation = elevation,
        content = content
    )
}

@Composable
private fun MaterialPill(
    modifier: Modifier = Modifier,
    bgColor: Color,
    contentColor: Color,
    borderStroke: BorderStroke? = null,
    elevation: Dp = 0.dp,
    content: @Composable () -> Unit
) {
    Surface(
        shape = CircleShape,
        color = bgColor,
        contentColor = contentColor,
        border = borderStroke,
        elevation = elevation
    ) {
        // This is a hack to work around a regression in beta08 where some modifiers don't work on Surface,
        // TODO we should be able to remove this when moving to beta09
        Box(
            modifier = modifier
        ) {
            content()
        }
    }
}
