package com.example.teamproject_cv2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.teamproject_cv2.diaryScreen.DiaryScreen
import com.example.teamproject_cv2.loginScreen.LoginScreen
import com.example.teamproject_cv2.mainScreen.MainScreen
import com.example.teamproject_cv2.ui.theme.TeamProject_CV2Theme
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {
    private lateinit var analytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Firebase 초기화
        FirebaseApp.initializeApp(this)

        analytics = Firebase.analytics

        setContent {
            TeamProject_CV2Theme {
                AppContent()
            }
        }
    }
}
@Composable
fun AppContent() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "loginScreen") {
        composable("loginScreen") { LoginScreen(navController) }
        composable("mainScreen") { MainScreen(navController) }
        composable("diaryScreen") { DiaryScreen(navController) }
    }
}