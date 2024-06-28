package com.example.teamproject_cv2.loginScreen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


@Composable
fun LoginScreen(navController: NavController) {
    val email = "1whrhddydrPwjd@gmail.com"
    val password = "qwer123$"
    val auth: FirebaseAuth = Firebase.auth
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color.White),
        verticalArrangement = Arrangement.SpaceBetween, // Update this line
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))

        // Inner column to center the main components
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f), // Adjust this value to change vertical centering
            verticalArrangement = Arrangement.Center // Centers the inner content vertically
        ) {
            Text(
                text = "Welcome back! Glad to see you, Again!",
                fontSize = 28.sp,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            TextField(
                value = email,
                onValueChange = {},
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                enabled = false, // 값을 변경하지 못하도록 비활성화
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = password,
                onValueChange = {},
                label = { Text("Enter your password") },
                modifier = Modifier.fillMaxWidth(),
                enabled = false, // 값을 변경하지 못하도록 비활성화
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Forgot Password?",
                color = Color.Gray,
                fontSize = 14.sp,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(bottom = 16.dp)
            )

            Button(
                onClick = {
                    coroutineScope.launch {
                        try {
                            auth.signInWithEmailAndPassword(email, password).await()
                            navController.navigate("mainScreen")
                        } catch (e: Exception) {
                            Toast.makeText(
                                context,
                                "Authentication failed: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                ),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
            ) {
                Text("Login", color = Color.White)
            }
        }

        // Spacer to push "Register Now" to the bottom
        Spacer(modifier = Modifier.weight(1f))

        // This text will now be at the bottom
        Text(
            text = "Don't have an account? Register Now",
            color = Color.Gray,
            fontSize = 16.sp,
            modifier = Modifier
                .clickable {
                    navController.navigate("registerScreen")
                }
        )
    }
}

