package com.example.fitnesspaw.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitnesspaw.R
import com.example.fitnesspaw.auth.AuthManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginClick: () -> Unit,
    onSignupClick: () -> Unit
) {
    // RESPONSIVE
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp

    val titleSize = if (screenWidth < 360) 28.sp else 34.sp
    val cardPadding = if (screenWidth < 360) 18.dp else 24.dp

    // INPUTS
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // AUTH
    val authManager = remember { AuthManager() }

    // ERROR
    var errorMessage by remember { mutableStateOf("") }

    // UI
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF030712),
                        Color(0xFF081120),
                        Color(0xFF0F172A)
                    )
                )
            )
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(30.dp))

            // LOGO
            Image(
                painter = painterResource(id = R.drawable.fitnesspaw_logo),
                contentDescription = "FitnessPaw Logo",
                modifier = Modifier
                    .size(100.dp)
                    .padding(bottom = 12.dp)
            )

            // TITLE
            Text(
                text = "FitnessPaw",
                color = Color.White,
                fontSize = titleSize,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Track habits with your virtual pet",
                color = Color(0xFF94A3B8),
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(30.dp))

            // LOGIN CARD
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.08f)
                ),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(cardPadding)
                ) {
                    // EMAIL
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        textStyle = LocalTextStyle.current.copy(color = Color.White),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedLabelColor = Color(0xFF8EA2FF),
                            unfocusedLabelColor = Color.LightGray,
                            focusedBorderColor = Color(0xFF8EA2FF),
                            unfocusedBorderColor = Color.Gray,
                            cursorColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // PASSWORD
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = image,
                                    contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                    tint = Color.LightGray
                                )
                            }
                        },
                        textStyle = LocalTextStyle.current.copy(color = Color.White),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedLabelColor = Color(0xFF8EA2FF),
                            unfocusedLabelColor = Color.LightGray,
                            focusedBorderColor = Color(0xFF8EA2FF),
                            unfocusedBorderColor = Color.Gray,
                            cursorColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(26.dp))

                    // LOGIN BUTTON
                    Button(
                        onClick = {
                            if (email.isNotBlank() && password.isNotBlank()) {
                                authManager.login(
                                    email = email,
                                    password = password,
                                    onSuccess = {
                                        onLoginClick()
                                    },
                                    onError = {
                                        errorMessage = it
                                    }
                                )
                            } else {
                                errorMessage = "Please fill in all fields"
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF8EA2FF)
                        )
                    ) {
                        Text(
                            text = "Sign In",
                            fontSize = 18.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // ERROR CONTAINER
                    if (errorMessage.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.15f)
                            ),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ErrorOutline,
                                    contentDescription = "Error",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = errorMessage,
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // SIGNUP TEXT
            Text(
                text = "Create new account",
                color = Color(0xFF94A3B8),
                modifier = Modifier.clickable {
                    onSignupClick()
                }
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
