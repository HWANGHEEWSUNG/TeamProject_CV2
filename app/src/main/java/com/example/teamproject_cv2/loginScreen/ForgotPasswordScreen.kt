package com.example.teamproject_cv2.loginScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isSuccess by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // "Back" button
        Box(
            modifier = Modifier
                .padding(22.dp)
                .size(41.dp)
                .background(Color.White, RoundedCornerShape(12.dp))
                .border(1.dp, Color(0xFFE8ECF4), RoundedCornerShape(12.dp))
                .clickable { navController.popBackStack() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color(0xFF1E232C)
            )
        }

        Column(
            modifier = Modifier
                .padding(horizontal = 22.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(56.dp))

            // Title
            Text(
                text = "Welcome back! Glad to see you, Again!",
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                color = Color(0xFF1E232C),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 69.dp, bottom = 10.dp)
            )

            // Subtitle
            Text(
                text = "Don't worry! It occurs. Please enter the email address linked with your account.",
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                color = Color(0xFF8391A1),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 79.dp)
            )

            // Email Input
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Enter your email", color = Color(0xFF8391A1)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(Color(0xFFF7F8F9), RoundedCornerShape(8.dp)),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFFE8ECF4),
                    unfocusedBorderColor = Color(0xFFE8ECF4)
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Send Code Button
            Button(
                onClick = {
                    if (email.isNotEmpty()) {
                        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    isSuccess = true
                                    errorMessage = ""
                                } else {
                                    isSuccess = false
                                    errorMessage = task.exception?.message ?: "Error occurred"
                                }
                            }
                    } else {
                        errorMessage = "Please enter your email"
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E232C)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Send Code",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            if (isSuccess) {
                Text(
                    text = "A reset link has been sent to your email",
                    color = Color.Green,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Remember Password? Login
            TextButton(
                onClick = { navController.navigate("loginScreen") },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 47.dp)
            ) {
                Text(
                    text = "Remember Password? Login",
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp,
                    color = Color(0xFF1E232C)
                )
            }
        }
    }
}