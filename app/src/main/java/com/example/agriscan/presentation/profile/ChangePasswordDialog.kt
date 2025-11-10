package com.example.agriscan.presentation.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.agriscan.R
import com.example.agriscan.presentation.BrandGreenDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit,
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var currentPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        title = { Text(text = stringResource(id = R.string.change_password), color = BrandGreenDark) },
        text = {
            Column {
                val textFieldColors = TextFieldDefaults.colors(
                    focusedIndicatorColor = BrandGreenDark,
                    unfocusedIndicatorColor = BrandGreenDark.copy(alpha = 0.5f),
                    cursorColor = BrandGreenDark,
                    focusedLabelColor = BrandGreenDark,
                )

                TextField(
                    value = currentPassword,
                    onValueChange = { currentPassword = it },
                    label = { Text(text = stringResource(id = R.string.current_password)) },
                    visualTransformation = if (currentPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (currentPasswordVisible)
                            Icons.Filled.Visibility
                        else Icons.Filled.VisibilityOff

                        IconButton(onClick = { currentPasswordVisible = !currentPasswordVisible }) {
                            Icon(imageVector = image, contentDescription = null, tint = BrandGreenDark)
                        }
                    },
                    colors = textFieldColors
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text(text = stringResource(id = R.string.new_password)) },
                    visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (newPasswordVisible)
                            Icons.Filled.Visibility
                        else Icons.Filled.VisibilityOff

                        IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                            Icon(imageVector = image, contentDescription = null, tint = BrandGreenDark)
                        }
                    },
                    colors = textFieldColors
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text(text = stringResource(id = R.string.confirm_password)) },
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (confirmPasswordVisible)
                            Icons.Filled.Visibility
                        else Icons.Filled.VisibilityOff

                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(imageVector = image, contentDescription = null, tint = BrandGreenDark)
                        }
                    },
                    colors = textFieldColors
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(currentPassword, newPassword) },
                enabled = newPassword.isNotEmpty() && newPassword == confirmPassword,
                colors = ButtonDefaults.buttonColors(containerColor = BrandGreenDark)
            ) {
                Text(text = stringResource(id = R.string.change_password), color = Color.White)
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = BrandGreenDark)
            ) {
                Text(text = stringResource(id = R.string.cancel))
            }
        }
    )
}
