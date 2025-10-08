package com.example.zodkmpexample

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusRequester.Companion.FocusRequesterFactory.component1
import androidx.compose.ui.focus.FocusRequester.Companion.FocusRequesterFactory.component2
import androidx.compose.ui.focus.FocusRequester.Companion.FocusRequesterFactory.component3
import androidx.compose.ui.focus.FocusRequester.Companion.FocusRequesterFactory.component4
import androidx.compose.ui.focus.FocusRequester.Companion.FocusRequesterFactory.component5
import androidx.compose.ui.focus.FocusRequester.Companion.FocusRequesterFactory.component6
import androidx.compose.ui.focus.FocusRequester.Companion.FocusRequesterFactory.component7
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.zodkmpexample.component.InputField
import com.example.zodkmpexample.component.RadioField
import com.example.zodkmpexample.component.SelectField
import com.example.zodkmpexample.component.Submit
import com.example.zodkmpexample.component.WithLayout
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import soil.form.FieldValidator
import soil.form.compose.Field
import soil.form.compose.Form
import soil.form.compose.FormField
import soil.form.compose.rememberForm
import soil.form.compose.rememberFormState
import soil.form.compose.serializationSaver
import soil.form.compose.text.Field
import soil.form.rule.StringRule
import soil.form.rule.StringRuleBuilder
import soil.form.rule.notBlank
import soil.form.rule.notNull

@OptIn(ExperimentalSerializationApi::class)
@Composable
fun HelloFormScreen(innerPadding: PaddingValues) {
    val feedback = LocalFeedbackHost.current
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val formState = rememberFormState(initialValue = FormData(), saver = serializationSaver())
    val form = rememberForm(state = formState) {
        coroutineScope.launch {
            feedback.showAlert("Form submitted successfully")
            focusManager.clearFocus()
            formState.reset(FormData())
        }
    }
    HelloFormContent(
        form = form,
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    )
}

// The form input fields are based on the Live Demo used in React Hook Form.
// You can reference it here: https://react-hook-form.com/
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HelloFormContent(
    form: Form<FormData>,
    modifier: Modifier = Modifier
) = MaterialTheme {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        val (f1, f2, f3, f4, f5, f6, f7) = FocusRequester.createRefs()
        form.FirstName { field ->
            field.WithLayout {
                InputField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(f1),
                    label = { Text("First name") }
                )
            }
        }
        form.LastName { field ->
            field.WithLayout {
                InputField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(f2),
                    label = { Text("Last name") }
                )
            }
        }
        form.Email { field ->
            field.WithLayout {
                InputField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(f3),
                    label = { Text("Email") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )
            }
        }
        form.MobileNumber { field ->
            field.WithLayout {
                InputField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(f4),
                    label = { Text("Mobile number") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        }
        form.Title { field ->
            field.WithLayout {
                SelectField(
                    transform = { it?.name ?: "" },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(f5),
                    label = { Text("Title") },
                ) {
                    Title.entries.forEach { value ->
                        Option(value) {
                            Text(text = value.name)
                        }
                    }
                }
            }
        }
        form.Developer { field ->
            field.WithLayout {
                RadioField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(f6)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        listOf(true, false).forEach { value ->
                            Option(value) {
                                Text(if (value) "Yes" else "No")
                            }
                        }
                    }
                }
            }
        }
        form.Submit {
            Text(
                text = "Submit",
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(f7),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun Form<FormData>.FirstName(
    content: @Composable (FormField<String>) -> Unit
) {
    Field(
        selector = { it.firstName },
        updater = { copy(firstName = it) },
        validator = FieldValidator {
            notBlank { "must be not blank" }
        },
        render = content
    )
}

@Composable
private fun Form<FormData>.LastName(
    content: @Composable (FormField<String>) -> Unit
) {
    Field(
        selector = { it.lastName },
        updater = { copy(lastName = it) },
        validator = FieldValidator {
            notBlank { "must be not blank" }
        },
        render = content
    )
}

@Composable
private fun Form<FormData>.Email(
    content: @Composable (FormField<String>) -> Unit
) {
    Field(
        selector = { it.email },
        updater = { copy(email = it) },
        validator = FieldValidator {
            notBlank { "must be not blank" }
            email { "must be valid email address" }
        },
        render = content
    )
}

@Composable
private fun Form<FormData>.MobileNumber(
    content: @Composable (FormField<String>) -> Unit
) {
    Field(
        selector = { it.mobileNumber },
        updater = { copy(mobileNumber = it) },
        validator = FieldValidator {
            notBlank { "must be not blank" }
            phoneNumber { "must be valid phone number" }
        },
        render = content
    )
}

@Composable
private fun Form<FormData>.Title(
    content: @Composable (FormField<Title?>) -> Unit
) {
    Field(
        selector = { it.title },
        updater = { copy(title = it) },
        validator = FieldValidator {
            notNull { "must be selected" }
        },
        render = content
    )
}

@Composable
private fun Form<FormData>.Developer(
    content: @Composable (FormField<Boolean?>) -> Unit
) {
    Field(
        selector = { it.developer },
        updater = { copy(developer = it) },
        validator = FieldValidator {
            notNull { "must be selected" }
        },
        render = content
    )
}

// Basic custom validation rule for email addresses
private fun StringRuleBuilder.email(message: () -> String) {
    val pattern = Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}\$")
    extend(StringRule({ pattern.matches(this) }, message))
}

// Basic custom validation rule for phone numbers
private fun StringRuleBuilder.phoneNumber(message: () -> String) {
    val pattern = Regex("^01[0-9]-?[0-9]{3,4}-?[0-9]{4}$")
    extend(StringRule({ pattern.matches(this) }, message))
}