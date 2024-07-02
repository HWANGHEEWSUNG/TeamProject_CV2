package com.example.teamproject_cv2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.GMobiledata
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.teamproject_cv2.calendarScreen.CalendarScreen
import com.example.teamproject_cv2.diaryScreen.DiaryScreen
import com.example.teamproject_cv2.entryScreen.EntryScreen
import com.example.teamproject_cv2.graphScreen.GraphScreen
import com.example.teamproject_cv2.loginScreen.ForgotPasswordScreen
import com.example.teamproject_cv2.loginScreen.LoginScreen
import com.example.teamproject_cv2.loginScreen.RegisterScreen
import com.example.teamproject_cv2.mainScreen.MainScreen
import com.example.teamproject_cv2.profileScreen.ProfileScreen
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

data class BottomNavItem(val title: String, val route: String)

@Composable
fun AppContent(storageReference: StorageReference, firestore: FirebaseFirestore) {
    val navController = rememberNavController()

    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(1f)) {
            NavHost(navController, startDestination = "entryScreen") {
                composable("entryScreen") { EntryScreen(navController) }
                composable("loginScreen") { LoginScreen(navController) }
                composable("mainScreen") { MainScreen(navController) }
                composable("registerScreen") { RegisterScreen(navController) }
                composable("forgotPasswordScreen") { ForgotPasswordScreen(navController) }
                composable("profileScreen") { ProfileScreen(navController) }
                composable("graphScreen") { GraphScreen(navController) }
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
            }
        }

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        if (currentRoute in listOf("mainScreen", "profileScreen", "graphScreen", "calendarScreen/{selectedDate}")) {
            BottomNavigationBar(navController)
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem("Main", "mainScreen"),
        BottomNavItem("Calendar", "calendarScreen/${LocalDate.now()}"),
        BottomNavItem("Graph", "graphScreen"),
        BottomNavItem("Profile", "profileScreen")
    )

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        // 백 스택 관리 설정
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    when (item.route) {
                        "mainScreen" -> Icons.Filled.Home
                        "calendarScreen/${LocalDate.now()}" -> Icons.Filled.Event
                        "graphScreen" -> Icons.Filled.GMobiledata
                        "profileScreen" -> Icons.Filled.Person
                        else -> null
                    }?.let { Icon(it, contentDescription = null) }
                },
                label = { Text(item.title) }
            )
        }
    }
}

@Composable
fun TeamProject_CV2Theme(content: @Composable () -> Unit) {
    MaterialTheme {
        Surface {
            Box(modifier = Modifier.fillMaxSize()) {
                content()
            }
        }
    }
}