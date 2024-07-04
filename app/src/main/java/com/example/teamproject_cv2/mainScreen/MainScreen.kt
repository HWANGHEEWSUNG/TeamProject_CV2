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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController) {

    val newsArticles = remember { mutableStateOf<List<NewsItem>>(emptyList()) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val response = RetrofitInstance.api.getNews(
                    clientId = "vo5x1zPAjvdidvQPIKMQ",
                    clientSecret = "_mFqgHd_EO",
                    query = "정신건강",
                    display = 9
                )
                newsArticles.value = response.items
            } catch (e: Exception) {
                // Handle error
            }
        }
    }


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
            MainContent(navController, paddingValues, newsArticles.value)
        }
    )
}

@Composable
fun MainContent(navController: NavController, paddingValues: PaddingValues, newsItems: List<NewsItem>) {
    val items = listOf("히스토리 페이지로 이동") + newsItems.map { it.title }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(items) { item ->
            val isHistory = item == "히스토리 페이지로 이동"
            val newsItem = newsItems.firstOrNull { it.title == item }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(vertical = 8.dp)
                    .clickable {
                        if (isHistory) {
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
                    if (isHistory) {
                        Text(
                            text = "여기를 클릭하여 지난 10일간의 일기 히스토리를 확인하세요.",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    } else {
                        Text(
                            text = newsItem?.description ?: "뉴스 설명을 불러올 수 없습니다.",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}
