package com.example.ui.screens.profile

import android.util.Patterns
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.core.designsystem.LumiButton
import com.example.core.designsystem.LumiDemoNotice
import com.example.core.designsystem.LumiHeader
import com.example.core.designsystem.LumiOutlinedButton
import com.example.data.repository.LumiRepository
import com.example.ui.theme.LumiBorderDivider
import com.example.ui.theme.LumiCardShape
import com.example.ui.theme.LumiCharcoal
import com.example.ui.theme.LumiError
import com.example.ui.theme.LumiInputFieldShape
import com.example.ui.theme.LumiMutedGray
import com.example.ui.theme.LumiPlum
import com.example.ui.theme.LumiSurfaceWhite
import com.example.ui.theme.LumiWarmGray
import com.example.ui.theme.LumiWarmIvory
import kotlinx.coroutines.launch

@Composable
fun EditProfileScreen(
    repository: LumiRepository,
    onBack: () -> Unit
) {
    val userProfile by repository.userProfile.collectAsState(initial = null)
    val scope = rememberCoroutineScope()

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    var firstNameError by remember { mutableStateOf<String?>(null) }
    var lastNameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var isSaving by remember { mutableStateOf(false) }

    LaunchedEffect(userProfile) {
        if (userProfile != null) {
            firstName = userProfile!!.firstName
            lastName = userProfile!!.lastName
            email = userProfile!!.email
            phone = userProfile!!.phoneNumber
        }
    }

    fun validate(): Boolean {
        var isValid = true

        if (firstName.trim().isEmpty()) {
            firstNameError = "First name is required"
            isValid = false
        } else if (firstName.length > 50) {
            firstNameError = "Name cannot exceed 50 characters"
            isValid = false
        } else {
            firstNameError = null
        }

        if (lastName.trim().isEmpty()) {
            lastNameError = "Last name is required"
            isValid = false
        } else if (lastName.length > 50) {
            lastNameError = "Last name cannot exceed 50 characters"
            isValid = false
        } else {
            lastNameError = null
        }

        val trimmedEmail = email.trim()
        if (trimmedEmail.isEmpty()) {
            emailError = "Email address is required"
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
            emailError = "Please enter a valid email address"
            isValid = false
        } else {
            emailError = null
        }

        return isValid
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LumiWarmIvory)
            .testTag("edit_profile_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            LumiHeader(
                title = "Edit Profile",
                subtitle = "Manage your contact information",
                onBack = onBack
            )

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
            ) {
                item {
                    Card(
                        shape = LumiCardShape,
                        colors = CardDefaults.cardColors(containerColor = LumiSurfaceWhite),
                        border = BorderStroke(1.dp, LumiBorderDivider),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            // First Name
                            Text(
                                text = "First Name",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = LumiCharcoal
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = firstName,
                                onValueChange = {
                                    firstName = it
                                    if (firstNameError != null) firstNameError = null
                                },
                                singleLine = true,
                                isError = firstNameError != null,
                                supportingText = {
                                    if (firstNameError != null) {
                                        Text(text = firstNameError!!, color = LumiError)
                                    }
                                },
                                shape = LumiInputFieldShape,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = LumiPlum,
                                    unfocusedBorderColor = LumiBorderDivider
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_first_name")
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Last Name
                            Text(
                                text = "Last Name",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = LumiCharcoal
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = lastName,
                                onValueChange = {
                                    lastName = it
                                    if (lastNameError != null) lastNameError = null
                                },
                                singleLine = true,
                                isError = lastNameError != null,
                                supportingText = {
                                    if (lastNameError != null) {
                                        Text(text = lastNameError!!, color = LumiError)
                                    }
                                },
                                shape = LumiInputFieldShape,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = LumiPlum,
                                    unfocusedBorderColor = LumiBorderDivider
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_last_name")
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Email
                            Text(
                                text = "Email Address",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = LumiCharcoal
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = email,
                                onValueChange = {
                                    email = it
                                    if (emailError != null) emailError = null
                                },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                isError = emailError != null,
                                supportingText = {
                                    if (emailError != null) {
                                        Text(text = emailError!!, color = LumiError)
                                    }
                                },
                                shape = LumiInputFieldShape,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = LumiPlum,
                                    unfocusedBorderColor = LumiBorderDivider
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_email")
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Phone
                            Text(
                                text = "Phone Number (Optional)",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = LumiCharcoal
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = phone,
                                onValueChange = { phone = it },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                shape = LumiInputFieldShape,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = LumiPlum,
                                    unfocusedBorderColor = LumiBorderDivider
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_phone")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    LumiDemoNotice(text = "Profile edits are stored locally in on-device Room database")
                }
            }

            // Bottom Buttons
            Surface(
                color = LumiSurfaceWhite,
                shadowElevation = 8.dp,
                border = BorderStroke(1.dp, LumiBorderDivider),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    LumiOutlinedButton(
                        text = "Cancel",
                        onClick = onBack,
                        modifier = Modifier.weight(1f),
                        testTag = "edit_profile_cancel_btn"
                    )

                    LumiButton(
                        text = if (isSaving) "Saving..." else "Save Changes",
                        enabled = !isSaving,
                        onClick = {
                            if (validate()) {
                                isSaving = true
                                scope.launch {
                                    repository.updateUserProfile(firstName, lastName, email, phone)
                                    isSaving = false
                                    onBack()
                                }
                            }
                        },
                        modifier = Modifier.weight(1.2f),
                        testTag = "edit_profile_save_btn"
                    )
                }
            }
        }
    }
}
