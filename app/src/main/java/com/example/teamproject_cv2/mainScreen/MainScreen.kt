package com.example.teamproject_cv2.mainScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("메인 화면") },
                actions = {
                    IconButton(onClick = {
                        val currentDate = LocalDate.now().toString()
                        navController.navigate("diaryScreen/$currentDate")
                    }) {
                        Icon(Icons.Filled.Add, contentDescription = "추가")
                    }
                }
            )
        },
        content = { paddingValues ->
            MainContent(navController, paddingValues)
        }
    )
}

@Composable
fun MainContent(navController: NavController, paddingValues: PaddingValues) {
    val items = List(10) { index ->
        if (index == 0) {
            "히스토리 페이지로 이동"
        } else {
            "일기 ${index + 1}"
        }
    }

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
                    .height(200.dp)
                    .padding(vertical = 8.dp)
                    .clickable {
                        if (item == "히스토리 페이지로 이동") {
                            navController.navigate("historyScreen")
                        }
                    },
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = item,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    when (item) {
                        "히스토리 페이지로 이동" -> {
                            Text(
                                text = "여기를 클릭하여 지난 10일간의 일기 히스토리를 확인하세요.",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        else -> {
                            Text(
                                text = "이곳에 일기 내용 요약이 들어갑니다.",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
        }
    }
}