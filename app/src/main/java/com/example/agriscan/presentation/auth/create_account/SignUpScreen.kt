package com.example.agriscan.presentation.auth.create_account

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.agriscan.R
import com.example.agriscan.ui.theme.AgriScanTheme
import com.uk.ac.tees.mad.agriscan.domain.util.ObserveAsEvents
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SignUpRoot(
    onSignUpSuccess: () -> Unit,
    onGoToSignIn: () -> Unit,
    viewModel: SignUpViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    ObserveAsEvents(viewModel.events) {
        when (it) {
            is SignUpEvent.Success -> onSignUpSuccess()
            is SignUpEvent.Failure -> Toast.makeText(context, "Sign up failed", Toast.LENGTH_SHORT).show()
            is SignUpEvent.GoToLogin -> onGoToSignIn()
        }
    }

    SignUpScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    state: SignUpState,
    onAction: (SignUpAction) -> Unit
) {
    val limeColor = Color(0xFFCBF535)
    var showDatePicker by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Using the same gradient as the login screen
    val backgroundGradient = Brush.verticalGradient(
        colorStops = arrayOf(
            0.0f to Color(0xFF4CA35F),
            0.36f to Color(0xFF397947),
            0.61f to Color(0xFF2A5A35),
            0.94f to Color(0xFF1C3D24)
        )
    )

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                Button(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val formatter = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
                        onAction(SignUpAction.OnDobChange(formatter.format(Date(it))))
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                Button(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {DatePicker(state = datePickerState)}
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = backgroundGradient)
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()), // Make the column scrollable
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(64.dp))

        Text(
            text = stringResource(id = R.string.create_account),
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Input Fields
        SignUpTextField(
            value = state.fullName,
            onValueChange = { onAction(SignUpAction.OnFullNameChanged(it)) },
            placeholder = stringResource(id = R.string.enter_your_full_name)
        )
        SignUpTextField(
            value = state.email,
            onValueChange = { onAction(SignUpAction.OnEmailChange(it)) },
            placeholder = stringResource(id = R.string.enter_your_email_address),
            keyboardType = KeyboardType.Email
        )
        SignUpTextField(
            value = state.phone,
            onValueChange = { onAction(SignUpAction.OnPhoneChange(it)) },
            placeholder = stringResource(id = R.string.enter_your_phone_number),
            keyboardType = KeyboardType.Phone
        )
        Box(modifier = Modifier.clickable { showDatePicker = true }) {
            SignUpTextField(
                value = state.dob,
                onValueChange = {},
                placeholder = stringResource(id = R.string.dd_mm_yy),
                enabled = false
            )
        }
        SignUpTextField(
            value = state.password,
            onValueChange = { onAction(SignUpAction.OnPasswordChange(it)) },
            placeholder = stringResource(id = R.string.password),
            isPassword = true
        )
        SignUpTextField(
            value = state.confirmPassword,
            onValueChange = { onAction(SignUpAction.OnConfirmPasswordChange(it)) },
            placeholder = stringResource(id = R.string.confirm_password),
            isPassword = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Terms and Policy Text
        Text(
            text = stringResource(id = R.string.terms_and_policy),
            color = Color.White,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Sign Up Button
        Button(
            onClick = { onAction(SignUpAction.OnCreateAccountClick) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = limeColor)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(color = Color.Black)
            } else {
                Text(stringResource(id = R.string.sign_up), color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // "Already have an account?" Text
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = stringResource(id = R.string.already_have_an_account), color = Color.White, fontSize = 14.sp)
            Text(
                text = stringResource(id = R.string.log_in),
                color = limeColor,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.clickable { onAction(SignUpAction.OnSignInClick) }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

// A reusable composable for the text fields to reduce repetition
@Composable
private fun SignUpTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    enabled: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        placeholder = { Text(placeholder, color = Color.Gray) },
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            disabledContainerColor = Color.White,
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            cursorColor = Color.Black,
        ),
        singleLine = true,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = if (isPassword) KeyboardType.Password else keyboardType),
        enabled = enabled
    )
}


@Preview(showBackground = true)
@Composable
private fun PreviewSignUpScreen() {
    AgriScanTheme {
        SignUpScreen(
            state = SignUpState(),
            onAction = {}
        )
    }
}
