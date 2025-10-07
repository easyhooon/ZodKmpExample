package com.example.zodkmpexample.component

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.center
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.zodkmpexample.extension.ifTrue
import com.example.zodkmpexample.ui.theme.AppTheme
import soil.form.FieldValidator
import soil.form.compose.FormField
import soil.form.compose.tooling.PreviewField
import soil.form.rule.notNull

@Composable
fun <T : Any> FormField<T?>.RadioField(
    modifier: Modifier = Modifier,
    content: @Composable RadioFieldScope<T>.() -> Unit
) {
    Box(
        modifier = modifier
            .selectableGroup()
            .onFocusChanged { handleFocus(it.isFocused || it.hasFocus) },
    ) {
        val scope = remember { RadioFieldScope(this@RadioField) }
        scope.content()
    }
}

@Stable
class RadioFieldScope<T : Any>(
    @PublishedApi internal val field: FormField<T?>
) {

    @Composable
    inline fun Option(
        value: T,
        modifier: Modifier = Modifier,
        enabled: Boolean = true,
        content: @Composable () -> Unit
    ) {
        key(value) {
            val interactionSource = remember { MutableInteractionSource() }
            val isFocused by interactionSource.collectIsFocusedAsState()
            val isSelected = value == field.value
            val focusedColor = AppTheme.colorScheme.onBackground.copy(alpha = 0.1f)
            Row(
                modifier = modifier.selectable(
                    selected = isSelected,
                    role = Role.RadioButton,
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = { field.onValueChange(value) },
                    enabled = field.isEnabled && enabled
                ),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                content()
                RadioButton(
                    selected = isSelected,
                    onClick = null,
                    modifier = Modifier.ifTrue(isFocused) {
                        Modifier.drawBehind {
                            drawCircle(
                                focusedColor,
                                radius = size.maxDimension * 0.75f,
                                center = size.center
                            )
                        }
                    },
                    interactionSource = interactionSource,
                    enabled = field.isEnabled && enabled
                )
            }
        }
    }
}

@Preview
@Composable
private fun RadioFieldPreview() {
    AppTheme {
        PreviewField<Boolean?>(
            initialValue = null,
            validator = FieldValidator {
                notNull { "This field is required" }
            },
            render = { field ->
                field.WithLayout {
                    RadioField {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Option(true) {
                                Text(text = "Yes")
                            }
                            Option(false) {
                                Text(text = "No")
                            }
                        }
                    }
                }
            }
        )
    }
}