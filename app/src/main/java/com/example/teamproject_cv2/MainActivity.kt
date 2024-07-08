package com.example.teamproject_cv2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.teamproject_cv2.calendarScreen.CalendarScreen
import com.example.teamproject_cv2.diaryScreen.DiaryScreen
import com.example.teamproject_cv2.entryScreen.EntryScreen
import com.example.teamproject_cv2.graphScreen.GraphScreen
import com.example.teamproject_cv2.loginScreen.ForgotPasswordScreen
import com.example.teamproject_cv2.loginScreen.LoginScreen
import com.example.teamproject_cv2.loginScreen.RegisterScreen
import com.example.teamproject_cv2.mainScreen.HistoryScreen
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
                composable("calendarScreen/{selectedDate}", arguments = listOf(
                    navArgument("selectedDate") { type = NavType.StringType }
                )) { backStackEntry ->
                    val selectedDate = backStackEntry.arguments?.getString("selectedDate")?.let {
                        LocalDate.parse(it)
                    } ?: LocalDate.now()
                    CalendarScreen(
                        selectedDate = selectedDate,
                        onDateSelected = { date ->
                            // 네비게이션 요청 시에 uniqueId를 추가하여 충돌을 방지할 수 있음
                            navController.navigate("diaryScreen/${date}?from=calendar")
                        }
                    )
                }
                composable("diaryScreen/{date}", arguments = listOf(
                    navArgument("date") { type = NavType.StringType }
                )) { backStackEntry ->
                    val date = backStackEntry.arguments?.getString("date") ?: ""
                    DiaryScreen(
                        navController = navController,
                        storageReference = storageReference,
                        firestore = firestore,
                        date = date
                    )
                }
                composable("historyScreen") { HistoryScreen(firestore, navController) }
            }
        }

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        if (currentRoute in listOf(
                "mainScreen",
                "profileScreen",
                "graphScreen",
                "calendarScreen/{selectedDate}",
                "historyScreen"
            )
        ) {
            BottomNavigationBar(navController)
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem("메인", "mainScreen"),
        BottomNavItem("캘린더", "calendarScreen/${LocalDate.now()}"),
        BottomNavItem("그래프", "graphScreen"),
        BottomNavItem("프로필", "profileScreen"),
    )

    NavigationBar(
        modifier = Modifier.background(Color(0xFF211C40)) // 배경색 설정
    ) {
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
                            inclusive = item.route == "mainScreen" // 메인 스크린일 경우 현재 스택을 모두 제거
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    val icon = when (item.route) {
                        "mainScreen" -> Icons.Filled.Home
                        "calendarScreen/${LocalDate.now()}" -> Icons.Filled.Event
                        "graphScreen" -> Icons.Filled.ShowChart // GMobiledata 아이콘 대신 ShowChart 사용
                        "profileScreen" -> Icons.Filled.Person
                        else -> null
                    }
                    if (icon != null) {
                        Icon(icon, contentDescription = item.title)
                    }
                },
                label = { Text(item.title) },
                alwaysShowLabel = false, // 라벨을 항상 표시하지 않도록 설정
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF5927EB), // 선택된 아이콘 색상
                    unselectedIconColor = Color.Gray, // 선택되지 않은 아이콘 색상
                    selectedTextColor = Color(0xFF5927EB), // 선택된 텍스트 색상
                    unselectedTextColor = Color.Gray // 선택되지 않은 텍스트 색상
                )
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