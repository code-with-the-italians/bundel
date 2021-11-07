package dev.sebastiano.bundel.composables

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.sebastiano.bundel.ui.BundelYouTheme

@Composable
@Preview
internal fun ChipPreview() {
    BundelYouTheme {
        Surface {
            var checked by remember { mutableStateOf(false) }
            MaterialChip(
                checked = checked,
                onCheckedChanged = {
                    checked = it
                    println("Click! $checked")
                },
                uncheckedAppearance = uncheckedMaterialPillAppearance(
                    borderColor = Color.Green,
                    borderWidth = 4.dp
                ),
                checkedAppearance = checkedMaterialPillAppearance(
                    contentColor = Color.Yellow,
                    borderColor = Color.Red,
                    borderWidth = 1.dp
                )
            ) {
                Text("Ciao Ivan")
            }
        }
    }
}

@Composable
internal fun MaterialChip(
    enabled: Boolean = true,
    checked: Boolean,
    checkedAppearance: MaterialPillAppearance = checkedMaterialPillAppearance(),
    uncheckedAppearance: MaterialPillAppearance = uncheckedMaterialPillAppearance(),
    elevation: Dp = 0.dp,
    contentPadding: PaddingValues = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
    onCheckedChanged: (checked: Boolean) -> Unit,
    content: @Composable () -> Unit
) {
    MaterialPill(
        modifier = Modifier
            .clip(CircleShape)
            .toggleable(
                value = checked,
                enabled = enabled,
                role = Role.Checkbox,
                onValueChange = { onCheckedChanged(!checked) }
            ),
        appearance = if (checked) checkedAppearance else uncheckedAppearance,
        elevation = elevation,
        contentPadding = contentPadding,
        content = content
    )
}

@Composable
internal fun checkedMaterialPillAppearance(
    backgroundColor: Color = MaterialTheme.colorScheme.onSurface,
    contentColor: Color = contentColorFor(backgroundColor),
    borderColor: Color = Color.Transparent,
    borderWidth: Dp = 0.dp
) = MaterialPillAppearance(backgroundColor, contentColor, borderColor, borderWidth)

@Composable
internal fun uncheckedMaterialPillAppearance(
    backgroundColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = .54f),
    contentColor: Color = contentColorFor(backgroundColor),
    borderColor: Color = Color.Transparent,
    borderWidth: Dp = 0.dp
) = MaterialPillAppearance(backgroundColor, contentColor, borderColor, borderWidth)

internal data class MaterialPillAppearance(
    val backgroundColor: Color,
    val contentColor: Color,
    val borderColor: Color,
    val borderWidth: Dp
)

@Composable
@Preview
private fun PillPreview() {
    BundelYouTheme {
        Surface {
            MaterialPill {
                Text("I am a pill hello")
            }
        }
    }
}

@Composable
internal fun MaterialPill(
    modifier: Modifier = Modifier,
    appearance: MaterialPillAppearance = uncheckedMaterialPillAppearance(),
    elevation: Dp = 0.dp,
    contentPadding: PaddingValues = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
    content: @Composable () -> Unit
) {
    val backgroundColor by animateColorAsState(appearance.backgroundColor)
    val contentColor by animateColorAsState(appearance.contentColor)
    val borderColor by animateColorAsState(appearance.borderColor)
    val borderWidth by animateDpAsState(appearance.borderWidth)

    Surface(
        shape = CircleShape,
        color = backgroundColor,
        contentColor = contentColor,
        border = BorderStroke(borderWidth, borderColor),
        tonalElevation = elevation,
        modifier = modifier
    ) {
        Box(modifier = Modifier.padding(contentPadding)) {
            content()
        }
    }
}
