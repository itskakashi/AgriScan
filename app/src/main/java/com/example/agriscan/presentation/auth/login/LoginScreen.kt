package com.example.agriscan.presentation.auth.login

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.agriscan.R
import com.example.agriscan.ui.theme.AgriScanTheme
import com.example.agriscan.domain.util.ObserveAsEvents
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginRoot(
    onLoginSuccess: () -> Unit,
    onGoToSignUp: () -> Unit,
    onGoToForgotPassword: () -> Unit,
    viewModel: LoginViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    ObserveAsEvents(viewModel.event) {
        when (it) {
            is LoginEvent.Success -> onLoginSuccess()
            is LoginEvent.Failure -> Toast.makeText(context, context.getString(R.string.login_failed), Toast.LENGTH_SHORT).show()
            is LoginEvent.GoToSignUp -> onGoToSignUp()
            is LoginEvent.GoToForgotPassword -> onGoToForgotPassword()
        }
    }

    LoginScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun LoginScreen(
    state: LoginState,
    onAction: (LoginAction) -> Unit,
) {
    val limeColor = Color(0xFFCBF535)

    val backgroundGradient = Brush.verticalGradient(
        colorStops = arrayOf(
            0.0f to Color(0xFF4CA35F),
            0.36f to Color(0xFF397947),
            0.61f to Color(0xFF2A5A35),
            0.94f to Color(0xFF1C3D24)
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = backgroundGradient)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(id = R.string.welcome_to_agri_scan),
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 48.dp)
        )

        OutlinedTextField(
            value = state.email,
            onValueChange = { onAction(LoginAction.OnEmailChanged(it)) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(stringResource(id = R.string.email), color = Color.Gray) },
            leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = "Email Icon") },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                cursorColor = Color.Black,
                focusedLeadingIconColor = Color.Black,
                unfocusedLeadingIconColor = Color.Gray
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = state.password,
            onValueChange = { onAction(LoginAction.OnPasswordChanged(it)) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(stringResource(id = R.string.password), color = Color.Gray) },
            leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = "Password Icon") },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                cursorColor = Color.Black,
                focusedLeadingIconColor = Color.Black,
                unfocusedLeadingIconColor = Color.Gray
            ),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { onAction(LoginAction.OnSignInClicked) },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = limeColor)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(color = Color.Black)
            } else {
                Text(stringResource(id = R.string.sign_in), color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(id = R.string.forgot_password),
            color = Color.White,
            fontSize = 14.sp,
            modifier = Modifier.clickable { onAction(LoginAction.OnForgotPasswordClicked) }
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = stringResource(id = R.string.dont_have_an_account), color = Color.White, fontSize = 14.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = stringResource(id = R.string.sign_up),
                color = limeColor,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.clickable { onAction(LoginAction.OnSignUpClicked) }
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Divider(color = Color.White.copy(alpha = 0.5f), modifier = Modifier.weight(1f))
            Text(stringResource(id = R.string.or), color = Color.White.copy(alpha = 0.8f), modifier = Modifier.padding(horizontal = 16.dp))
            Divider(color = Color.White.copy(alpha = 0.5f), modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = stringResource(id = R.string.sign_in_with_social_networks), color = Color.White, fontSize = 14.sp)

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            SocialIcon(R.drawable.ic_facebook, "Facebook")
            Spacer(modifier = Modifier.width(24.dp))
            SocialIcon(R.drawable.ic_google, "Google")
            Spacer(modifier = Modifier.width(24.dp))
            SocialIcon(R.drawable.ic_twitter, "Twitter")
        }
    }
}

@Composable
fun SocialIcon(iconRes: Int, contentDesc: String) {
    Box(
        modifier = Modifier.size(48.dp).clip(CircleShape).background(Color.White).clickable { /* TODO: */ },
        contentAlignment = Alignment.Center
    ) {
        Image(painter = painterResource(id = iconRes), contentDescription = contentDesc, modifier = Modifier.size(24.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewLoginScreen() {
    AgriScanTheme {
        LoginScreen(
            state = LoginState(),
            onAction = {}
        )
    }
}
