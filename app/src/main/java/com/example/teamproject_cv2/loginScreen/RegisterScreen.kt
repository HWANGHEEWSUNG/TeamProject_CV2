package com.example.teamproject_cv2.loginScreen

import android.content.Context
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController) {
    var username by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.TopStart
            ) {
                BackButton { navController.popBackStack() }
            }
            Spacer(modifier = Modifier.height(69.dp))
            Text(
                text = "Hello! Register to get started",
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                color = Color(0xFF1E232C),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(47.dp))
            InputField("Username", username) { username = it }
            Spacer(modifier = Modifier.height(16.dp))
            InputField("Email", email) { email = it }
            Spacer(modifier = Modifier.height(16.dp))
            InputField("Password", password) { password = it }
            Spacer(modifier = Modifier.height(16.dp))
            InputField("Confirm password", confirmPassword) { confirmPassword = it }
            Spacer(modifier = Modifier.height(16.dp))
            RegisterButton {
                if (password == confirmPassword) {
                    registerUser(email, password, navController, context) { error ->
                        errorMessage = error
                    }
                } else {
                    errorMessage = "Passwords do not match"
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            errorMessage?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            Spacer(modifier = Modifier.height(40.dp))
            TextButton(onClick = { navController.navigate("loginScreen") }) {
                Text(
                    text = "Already have an account? Login Now",
                    fontFamily = FontFamily.Default,
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp,
                    color = Color(0xFF1E232C)
                )
            }
        }
    }
}

fun registerUser(email: String, password: String, navController: NavController, context: Context, onError: (String) -> Unit) {
    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // 회원가입 성공 시, 로그인 화면으로 이동
                navController.navigate("loginScreen")
            } else {
                // 회원가입 실패 시, 에러 메시지 표시
                onError(task.exception?.message ?: "Registration failed")
            }
        }
}

@Composable
fun BackButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(16.dp)
            .size(41.dp)
            .background(Color.White, RoundedCornerShape(12.dp))
            .border(1.dp, Color(0xFFE8ECF4), RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "Back",
            tint = Color(0xFF1E232C)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(text = label, color = Color(0xFF8391A1))
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Color(0xFFF7F8F9), RoundedCornerShape(8.dp)),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Color(0xFFE8ECF4),
            unfocusedBorderColor = Color(0xFFE8ECF4)
        )
    )
}

@Composable
fun RegisterButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E232C)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(text = "Register", color = Color.White, fontWeight = FontWeight.Bold)
    }
}