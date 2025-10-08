package com.example.zodkmpexample

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.piashcse.zodkmp.Zod
import com.piashcse.zodkmp.ZodResult
import kotlinx.coroutines.launch

@Composable
fun FormScreen(innerPadding: PaddingValues) {
    val feedback = LocalFeedbackHost.current
    val coroutineScope = rememberCoroutineScope()

    var formData by remember { mutableStateOf(FormData()) }
    var errors by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var touchedFields by remember { mutableStateOf<Set<String>>(emptySet()) }
    var focusedFields by remember { mutableStateOf<Set<String>>(emptySet()) }

    val firstNameSchema = remember { Zod.string().min(1, "must be not blank") }
    val lastNameSchema = remember { Zod.string().min(1, "must be not blank") }
    val emailSchema = remember { Zod.string().min(1, "must be not blank").email("must be valid email address") }
    val mobileNumberSchema = remember {
        Zod.string()
            .min(1, "must be not blank")
            .regex(Regex("^01[0-9]-?[0-9]{3,4}-?[0-9]{4}$"), "must be valid phone number")
    }

    fun validateField(fieldName: String) {
        val newErrors = errors.toMutableMap()

        when (fieldName) {
            "firstName" -> {
                val result = firstNameSchema.safeParse(formData.firstName)
                if (result is ZodResult.Failure) {
                    newErrors["firstName"] = result.error.errors.joinToString(", ")
                } else {
                    newErrors.remove("firstName")
                }
            }
            "lastName" -> {
                val result = lastNameSchema.safeParse(formData.lastName)
                if (result is ZodResult.Failure) {
                    newErrors["lastName"] = result.error.errors.joinToString(", ")
                } else {
                    newErrors.remove("lastName")
                }
            }
            "email" -> {
                val result = emailSchema.safeParse(formData.email)
                if (result is ZodResult.Failure) {
                    newErrors["email"] = result.error.errors.joinToString(", ")
                } else {
                    newErrors.remove("email")
                }
            }
            "mobileNumber" -> {
                val result = mobileNumberSchema.safeParse(formData.mobileNumber)
                if (result is ZodResult.Failure) {
                    newErrors["mobileNumber"] = result.error.errors.joinToString(", ")
                } else {
                    newErrors.remove("mobileNumber")
                }
            }
            "title" -> {
                if (formData.title == null) {
                    newErrors["title"] = "must be selected"
                } else {
                    newErrors.remove("title")
                }
            }
            "developer" -> {
                if (formData.developer == null) {
                    newErrors["developer"] = "must be selected"
                } else {
                    newErrors.remove("developer")
                }
            }
        }

        errors = newErrors
    }

    FormContent(
        formData = formData,
        errors = errors,
        touchedFields = touchedFields,
        onFormDataChange = { newFormData ->
            formData = newFormData
        },
        onFieldFocusChanged = { fieldName, isFocused ->
            if (isFocused) {
                // Track that this field has been focused
                focusedFields = focusedFields + fieldName
            } else if (focusedFields.contains(fieldName)) {
                // Only validate if field was previously focused (not initial state)
                touchedFields = touchedFields + fieldName
                validateField(fieldName)
            }
        },
        onFieldValueChanged = { fieldName ->
            // Don't validate on value change, only on focus loss
        },
        onSubmit = {
            // Mark all fields as touched
            touchedFields = setOf("firstName", "lastName", "email", "mobileNumber", "title", "developer")

            // Validate all fields
            listOf("firstName", "lastName", "email", "mobileNumber", "title", "developer").forEach {
                validateField(it)
            }

            if (errors.isEmpty()) {
                coroutineScope.launch {
                    feedback.showAlert("Form submitted successfully")
                    formData = FormData()
                    touchedFields = emptySet()
                    errors = emptyMap()
                }
            }
        },
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FormContent(
    formData: FormData,
    errors: Map<String, String>,
    touchedFields: Set<String>,
    onFormDataChange: (FormData) -> Unit,
    onFieldFocusChanged: (String, Boolean) -> Unit,
    onFieldValueChanged: (String) -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // First Name
        OutlinedTextField(
            value = formData.firstName,
            onValueChange = {
                onFormDataChange(formData.copy(firstName = it))
                onFieldValueChanged("firstName")
            },
            label = { Text("First name") },
            isError = touchedFields.contains("firstName") && errors.containsKey("firstName"),
            supportingText = if (touchedFields.contains("firstName")) errors["firstName"]?.let { { Text(it) } } else null,
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { onFieldFocusChanged("firstName", it.isFocused) }
        )

        // Last Name
        OutlinedTextField(
            value = formData.lastName,
            onValueChange = {
                onFormDataChange(formData.copy(lastName = it))
                onFieldValueChanged("lastName")
            },
            label = { Text("Last name") },
            isError = touchedFields.contains("lastName") && errors.containsKey("lastName"),
            supportingText = if (touchedFields.contains("lastName")) errors["lastName"]?.let { { Text(it) } } else null,
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { onFieldFocusChanged("lastName", it.isFocused) }
        )

        // Email
        OutlinedTextField(
            value = formData.email,
            onValueChange = {
                onFormDataChange(formData.copy(email = it))
                onFieldValueChanged("email")
            },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = touchedFields.contains("email") && errors.containsKey("email"),
            supportingText = if (touchedFields.contains("email")) errors["email"]?.let { { Text(it) } } else null,
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { onFieldFocusChanged("email", it.isFocused) }
        )

        // Mobile Number
        OutlinedTextField(
            value = formData.mobileNumber,
            onValueChange = {
                onFormDataChange(formData.copy(mobileNumber = it))
                onFieldValueChanged("mobileNumber")
            },
            label = { Text("Mobile number") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = touchedFields.contains("mobileNumber") && errors.containsKey("mobileNumber"),
            supportingText = if (touchedFields.contains("mobileNumber")) errors["mobileNumber"]?.let { { Text(it) } } else null,
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { onFieldFocusChanged("mobileNumber", it.isFocused) }
        )

        // Title Dropdown
        var titleExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = titleExpanded,
            onExpandedChange = { titleExpanded = it },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = formData.title?.name ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Title") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = titleExpanded) },
                isError = touchedFields.contains("title") && errors.containsKey("title"),
                supportingText = if (touchedFields.contains("title")) errors["title"]?.let { { Text(it) } } else null,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = titleExpanded,
                onDismissRequest = { titleExpanded = false }
            ) {
                Title.entries.forEach { title ->
                    DropdownMenuItem(
                        text = { Text(title.name) },
                        onClick = {
                            onFormDataChange(formData.copy(title = title))
                            onFieldValueChanged("title")
                            titleExpanded = false
                        }
                    )
                }
            }
        }

        // Developer Radio Group
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Are you a developer?",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.selectableGroup()
            ) {
                listOf(true to "Yes", false to "No").forEach { (value, label) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .selectable(
                                selected = formData.developer == value,
                                onClick = {
                                    onFormDataChange(formData.copy(developer = value))
                                    onFieldValueChanged("developer")
                                },
                                role = Role.RadioButton
                            )
                    ) {
                        RadioButton(
                            selected = formData.developer == value,
                            onClick = null
                        )
                        Text(
                            text = label,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
            }
            if (touchedFields.contains("developer") && errors.containsKey("developer")) {
                Text(
                    text = errors["developer"]!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }
        }

        // Submit Button
        Button(
            onClick = onSubmit,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Submit",
                textAlign = TextAlign.Center
            )
        }
    }
}