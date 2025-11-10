package com.example.agriscan.presentation.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.agriscan.presentation.AccentLime
import com.example.agriscan.presentation.BrandGreenDark
import com.example.agriscan.ui.theme.AgriScanTheme
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalInformationScreen(
    viewModel: PersonalInformationViewModel = koinViewModel(),
    navController: NavController
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showDatePicker by remember { mutableStateOf(false) }
    var isEditMode by remember { mutableStateOf(false) }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                Button(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val formatter = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
                        viewModel.onAction(PersonalInformationAction.OnDobChanged(formatter.format(Date(it))))
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

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Personal Information",
                        fontWeight = FontWeight.Bold,
                        color = BrandGreenDark
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = BrandGreenDark
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { isEditMode = !isEditMode }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = BrandGreenDark
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = Color(0xFFF0F2EF)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        InfoTextField(
                            value = state.name,
                            onValueChange = { viewModel.onAction(PersonalInformationAction.OnNameChanged(it)) },
                            label = "Full Name",
                            icon = Icons.Default.Person,
                            enabled = isEditMode
                        )
                        InfoTextField(
                            value = state.email,
                            onValueChange = { },
                            label = "Email Address",
                            icon = Icons.Default.Email,
                            enabled = false
                        )
                        InfoTextField(
                            value = state.phone,
                            onValueChange = { viewModel.onAction(PersonalInformationAction.OnPhoneChanged(it)) },
                            label = "Phone Number",
                            icon = Icons.Default.Phone,
                            enabled = isEditMode
                        )
                        Box(modifier = Modifier.clickable(enabled = isEditMode) { showDatePicker = true }) {
                            InfoTextField(
                                value = state.dob,
                                onValueChange = { },
                                label = "Date of Birth",
                                icon = Icons.Default.Cake,
                                enabled = false
                            )
                        }
                    }
                }
            }

            item {
                if (isEditMode) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { 
                            viewModel.onAction(PersonalInformationAction.SaveChanges)
                            isEditMode = false
                         },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandGreenDark)
                    ) {
                        Text("Save Changes", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    enabled: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = BrandGreenDark.copy(alpha = 0.7f)
            )
        },
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BrandGreenDark,
            unfocusedBorderColor = Color.LightGray,
            focusedLabelColor = BrandGreenDark,
            cursorColor = BrandGreenDark,
            disabledTextColor = Color.Black,
            disabledLabelColor = Color.Gray,
            disabledLeadingIconColor = BrandGreenDark.copy(alpha = 0.7f)
        ),
        enabled = enabled
    )
}

@Preview
@Composable
private fun PersonalInformationScreenPreview() {
    AgriScanTheme {
        PersonalInformationScreen(navController = rememberNavController())
    }
}
