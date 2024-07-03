package com.example.teamproject_cv2.mainScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.teamproject_cv2.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@Composable
fun HistoryScreen(navController: NavController, firestore: FirebaseFirestore) {
    val coroutineScope = rememberCoroutineScope()
    var diaryEntries by remember { mutableStateOf<List<DiaryEntry>>(emptyList()) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            diaryEntries = getDiaryEntries(firestore) { date ->
                navController.navigate("diaryScreen/$date")
            }
        }
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(diaryEntries) { entry ->
            DiaryCard(entry)
        }
    }
}

@Composable
fun DiaryCard(entry: DiaryEntry) {
    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { entry.onClick?.invoke() }  // 클릭 이벤트 처리
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            if (entry.isPlaceholder) {
                // 플레이스홀더 카드 UI
                Text(
                    text = entry.text,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
            } else {
                // 실제 일기 데이터 UI
                Image(
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(LocalContext.current)
                            .data(data = entry.imageUrl.takeIf { !it.isNullOrBlank() } ?: R.drawable.ic_launcher_foreground)
                            .apply {
                                crossfade(true)
                                placeholder(R.drawable.ic_launcher_foreground)
                                error(R.drawable.ic_launcher_foreground)
                            }.build()
                    ),
                    contentDescription = null,
                    modifier = Modifier
                        .size(64.dp)
                        .aspectRatio(1f)
                        .padding(end = 16.dp),
                    contentScale = ContentScale.Crop
                )

                Column {
                    Text(text = "Date: ${entry.selectedDate}", style = MaterialTheme.typography.titleMedium)
                    Text(text = "Emotion: ${entry.emotion}", style = MaterialTheme.typography.bodySmall)
                    Text(text = "Score: ${entry.emotionScore}", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = entry.text, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}