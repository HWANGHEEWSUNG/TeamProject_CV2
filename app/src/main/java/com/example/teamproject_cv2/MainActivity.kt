package com.example.teamproject_cv2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.teamproject_cv2.calendarScreen.CalendarScreen
import com.example.teamproject_cv2.diaryScreen.DiaryScreen
import com.example.teamproject_cv2.entryScreen.EntryScreen
import com.example.teamproject_cv2.loginScreen.ForgotPasswordScreen
import com.example.teamproject_cv2.loginScreen.LoginScreen
import com.example.teamproject_cv2.loginScreen.RegisterScreen
import com.example.teamproject_cv2.mainScreen.MainScreen
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.time.LocalDate

class MainActivity : ComponentActivity() {
    private lateinit var analytics: FirebaseAnalytics
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Firebase 초기화
        FirebaseApp.initializeApp(this)

        analytics = Firebase.analytics
        storage = Firebase.storage
        storageReference = storage.reference
        firestore = FirebaseFirestore.getInstance()

        setContent {
            TeamProject_CV2Theme {
                AppContent(storageReference, firestore)
            }
        }
    }
}

@Composable
fun AppContent(storageReference: StorageReference, firestore: FirebaseFirestore) {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "entryScreen") {
        composable("entryScreen") { EntryScreen(navController) }
        composable("loginScreen") { LoginScreen(navController) }
        composable("mainScreen") { MainScreen(navController) }
        composable("registerScreen") { RegisterScreen(navController) }
        composable("forgotPasswordScreen") { ForgotPasswordScreen(navController) }
        composable("calendarScreen/{selectedDate}") { backStackEntry ->
            val selectedDate = backStackEntry.arguments?.getString("selectedDate")?.let {
                LocalDate.parse(it)
            } ?: LocalDate.now()
            CalendarScreen(
                selectedDate = selectedDate,
                onDateSelected = { date ->
                    navController.navigate("someOtherScreen/${date}")
                }
            )
        }
        composable("diaryScreen") { DiaryScreen(navController, storageReference, firestore) }
        composable("someOtherScreen/{selectedDate}") { backStackEntry ->
            // Handle someOtherScreen content with selectedDate
        }
    }
}

@Composable
fun TeamProject_CV2Theme(content: @Composable () -> Unit) {
    MaterialTheme {
        Surface {
            content()
        }
    }
}