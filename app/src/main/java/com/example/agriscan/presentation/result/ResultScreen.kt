package com.example.agriscan.presentation.result

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.agriscan.R
import com.example.agriscan.presentation.BrandGreenDark
import com.example.agriscan.presentation.navigation.Screen
import com.example.agriscan.presentation.scan.BitmapData
import com.example.agriscan.ui.theme.AgriScanTheme
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun ResultRoot(
    viewModel: ResultViewModel = koinViewModel(
        parameters = { parametersOf(navController.previousBackStackEntry?.arguments) }
    ),
    navController: NavController,
    result: String,
    address: String,
    latitude: Double,
    longitude: Double
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ResultScreen(
        state = state,
        onAction = viewModel::onAction,
        navController = navController
    )
}

@Composable
fun ResultScreen(
    state: ResultState,
    onAction: (ResultAction) -> Unit,
    navController: NavController,
) {
    Scaffold(
        containerColor = Color(0xFFF0F2EF)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(id = R.string.prediction_result),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandGreenDark
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(Color.Gray)
                    ) {
                        BitmapData.bitmap?.let {
                            Image(
                                bitmap = it.asImageBitmap(),
                                contentDescription = stringResource(id = R.string.captured_image),
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    state.result?.let {
                        ResultRow(label = stringResource(id = R.string.breed_name), value = it)
                    }
                    if (!state.address.isNullOrBlank()) {
                        ResultRow(label = stringResource(id = R.string.address), value = state.address)
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Button(
                            onClick = { 
                                onAction(ResultAction.DisableButtons)
                                navController.popBackStack() 
                            },
                            enabled = state.buttonsEnabled,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandGreenDark)
                        ) {
                            Text(stringResource(id = R.string.scan_again), color = Color.White)
                        }
                        Button(
                            onClick = {
                                onAction(ResultAction.DisableButtons)
                                navController.navigate(Screen.HomeScreen) {
                                    popUpTo(Screen.HomeScreen) { inclusive = true }
                                }
                            },
                            enabled = state.buttonsEnabled,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandGreenDark)
                        ) {
                            Text(stringResource(id = R.string.done), color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ResultRow(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(text = label, fontWeight = FontWeight.Bold, color = BrandGreenDark)
        Text(text = value, color = BrandGreenDark)
    }
}

@Preview
@Composable
private fun Preview() {
    AgriScanTheme {
        ResultScreen(
            state = ResultState(),
            onAction = {},
            navController = rememberNavController()
        )
    }
}
