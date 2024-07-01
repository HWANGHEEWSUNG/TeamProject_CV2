package com.example.teamproject_cv2.mainScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.time.LocalDate
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("메인 화면") },
                actions = {
                    IconButton(onClick = { navController.navigate("diaryScreen") }) {
                        Icon(Icons.Filled.Add, contentDescription = "추가")
                    }
                }
            )
        },
        content = { paddingValues ->
            MainContent(navController, paddingValues)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("calendarScreen/${LocalDate.now()}")
                }
            ) {
                Text("달력 열기")
            }
        },
        floatingActionButtonPosition = FabPosition.End
    )
}

@Composable
fun MainContent(navController: NavController, paddingValues: PaddingValues) {
    val items = listOf("Item 1", "Item 2", "Item 3")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(items) { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable {
                        navController.navigate("diaryScreen")
                    },
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Text(
                    text = item,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}