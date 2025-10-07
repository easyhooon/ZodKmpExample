package com.example.zodkmpexample

import kotlinx.serialization.Serializable

@Serializable
data class FormData(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val mobileNumber: String = "",
    val title: Title? = null,
    val developer: Boolean? = null
)

enum class Title {
    Mr,
    Mrs,
    Miss,
    Dr,
}