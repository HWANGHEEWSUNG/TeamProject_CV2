package com.example.teamproject_cv2.mainScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                title = { Text("메인 화면", color = Color.White) },
                actions = {
                    IconButton(onClick = { navController.navigate("diaryScreen") }) {
                        Icon(Icons.Filled.Add, contentDescription = "추가", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = Color(0xFF1B0A40)
                )
            )
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF1B0A40)) // 배경색 설정
                    .padding(paddingValues)
            ) {
                MainContent(navController, newsArticles.value)
            }
        }
    )
}

@Composable
fun MainContent(navController: NavController, newsItems: List<NewsItem>) {
    val items = listOf("히스토리 페이지로 이동") + newsItems.map { it.title }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(items) { item ->
            val isHistory = item == "히스토리 페이지로 이동"
            val newsItem = newsItems.firstOrNull { it.title == item }

            CustomCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(vertical = 8.dp)
                    .clickable {
                        if (isHistory) {
                            navController.navigate("historyScreen")
                        }
                    },
                title = item,
                description = if (isHistory) {
                    "여기를 클릭하여 지난 10일간의 일기 히스토리를 확인하세요."
                } else {
                    newsItem?.description ?: "뉴스 설명을 불러올 수 없습니다."
                },
                isHistory = isHistory
            )
        }
    }
}

@Composable
fun CustomCard(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    isHistory: Boolean
) {
    val backgroundColor = if (isHistory) {
        Color(0xFF6200EA)
    } else {
        Color(0xFFFF9800) // 주황색
    }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        )
    ) {
        Column(
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(
                        colors = if (isHistory) listOf(Color(0xFF5927EB), Color(0xFFC33CE8)) else listOf(Color(0xFFFFA726), Color(0xFFFF9800)),
                        start = Offset(0f, 0f),
                        end = Offset(1000f, 1000f)
                    )
                )
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = TextStyle(
                    color = Color(0xFFECF3FF),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    lineHeight = 22.sp
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                style = TextStyle(
                    color = Color(0xFFECF3FF),
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            )
        }
    }
}