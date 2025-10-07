package com.example.zodkmpexample.component

import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import com.example.zodkmpexample.ui.theme.withAppTheme

@Composable
fun FieldValidationError(
    text: String,
    modifier: Modifier = Modifier
) = withAppTheme {
    Text(
        text = text,
        modifier = modifier.semantics { error(text) },
        style = typography.labelSmall,
        color = colorScheme.error
    )
}