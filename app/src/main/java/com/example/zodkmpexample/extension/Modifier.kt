package com.example.zodkmpexample.extension

import androidx.compose.ui.Modifier

@PublishedApi
internal inline fun Modifier.ifTrue(
    value: Boolean, builder: Modifier.() -> Modifier
): Modifier {
    return if (value) then(builder()) else this
}