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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitnesspaw.R
import com.example.fitnesspaw.auth.AuthManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupScreen(
    onSignupSuccess: (String, Int) -> Unit,
    onLoginClick: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp

    val titleSize = if (screenWidth < 360) 28.sp else 34.sp
    val cardPadding = if (screenWidth < 360) 18.dp else 24.dp

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var selectedPetIndex by remember { mutableIntStateOf(0) } // 0 = Cat, 1 = Dog

    var errorMessage by remember { mutableStateOf("") }
    val authManager = remember { AuthManager() }

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
                    .size(90.dp)
                    .padding(bottom = 12.dp)
            )

            // TITLE
            Text(
                text = "Create Account",
                color = Color.White,
                fontSize = titleSize,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Join FitnessPaw today 🚀",
                color = Color(0xFF94A3B8),
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(30.dp))

            // CARD
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
                    // USERNAME
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Username") },
                        textStyle = TextStyle(color = Color.White),
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

                    // PET SELECTOR WIDGET
                    Text(
                        text = "Choose your Starting Pet:",
                        color = Color.LightGray,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // CAT CARD
                        Card(
                            onClick = { selectedPetIndex = 0 },
                            modifier = Modifier
                                .weight(1f)
                                .height(72.dp),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (selectedPetIndex == 0) Color(0xFF8EA2FF).copy(alpha = 0.15f) else Color.Transparent
                            ),
                            border = BorderStroke(
                                width = 2.dp,
                                color = if (selectedPetIndex == 0) Color(0xFF8EA2FF) else Color.Gray.copy(alpha = 0.4f)
                            )
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("🐱", fontSize = 24.sp)
                                    Text("Cat", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // DOG CARD
                        Card(
                            onClick = { selectedPetIndex = 1 },
                            modifier = Modifier
                                .weight(1f)
                                .height(72.dp),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (selectedPetIndex == 1) Color(0xFF8EA2FF).copy(alpha = 0.15f) else Color.Transparent
                            ),
                            border = BorderStroke(
                                width = 2.dp,
                                color = if (selectedPetIndex == 1) Color(0xFF8EA2FF) else Color.Gray.copy(alpha = 0.4f)
                            )
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("🐶", fontSize = 24.sp)
                                    Text("Dog", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // EMAIL
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        textStyle = TextStyle(color = Color.White),
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
                        textStyle = TextStyle(color = Color.White),
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

                    Spacer(modifier = Modifier.height(30.dp))

                    // SIGNUP BUTTON
                    Button(
                        onClick = {
                            if (email.isNotBlank() && password.isNotBlank() && username.isNotBlank()) {
                                authManager.signup(
                                    email = email,
                                    password = password,
                                    onSuccess = {
                                        onSignupSuccess(username, selectedPetIndex)
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
                            text = "Create Account",
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

            // LOGIN NAVIGATION
            Text(
                text = "Already have an account? Login",
                color = Color(0xFF94A3B8),
                modifier = Modifier.clickable {
                    onLoginClick()
                }
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}