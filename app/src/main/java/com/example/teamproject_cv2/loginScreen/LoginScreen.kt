package com.example.teamproject_cv2.loginScreen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White, RoundedCornerShape(12.dp))
            .border(1.dp, Color(0xFFE8ECF4), RoundedCornerShape(12.dp))
    ) {
        // BackButton 컴포저블을 사용하여 뒤로 가기 버튼을 만듭니다.
        BackButton {
            navController.popBackStack()
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // Inner column to center the main components
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Welcome back! Glad to see you, Again!",
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 30.sp,
                    color = Color(0xFF1E232C),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = {},
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFE8ECF4),
                        unfocusedBorderColor = Color(0xFFE8ECF4),
                        disabledBorderColor = Color(0xFFE8ECF4),
                        disabledLabelColor = Color(0xFF8391A1),
                        disabledTextColor = Color(0xFF8391A1)
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = {},
                    label = { Text("Enter your password") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFE8ECF4),
                        unfocusedBorderColor = Color(0xFFE8ECF4),
                        disabledBorderColor = Color(0xFFE8ECF4),
                        disabledLabelColor = Color(0xFF8391A1),
                        disabledTextColor = Color(0xFF8391A1)
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { navController.navigate("forgotPasswordScreen") }) {
                        Text(
                            text = "Forgot Password?",
                            fontFamily = FontFamily.Default,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            color = Color(0xFF1E232C),
                            modifier = Modifier
                                .padding(bottom = 16.dp)
                        )
                    }
                }

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

            Spacer(modifier = Modifier.weight(1f))

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
}