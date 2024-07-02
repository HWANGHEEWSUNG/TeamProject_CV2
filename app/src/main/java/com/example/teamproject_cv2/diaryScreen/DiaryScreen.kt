package com.example.teamproject_cv2.diaryScreen


import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.net.Uri
import android.widget.ImageView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.teamproject_cv2.R
import com.example.teamproject_cv2.uploadDiaryWithImageToFirebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryScreen(
    navController: NavController,
    storageReference: StorageReference,
    firestore: FirebaseFirestore
) {
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var diaryText by remember { mutableStateOf("") }
    val context = LocalContext.current // Get context safely
    val activity = LocalContext.current as Activity
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            selectedImageUri = uri
        }
    var selectedEmojiIndex by remember { mutableStateOf(-1) }
    val emojis = listOf(R.drawable.emoji_happy, R.drawable.emoji_neutral, R.drawable.emoji_sad)

    val selectedDate = remember { mutableStateOf(LocalDate.now()) }
    val dateFormatter = remember {
        DateTimeFormatter.ofPattern("M월 d일", Locale.getDefault())
    }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(topBar = {
        TopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        dateFormatter.format(selectedDate.value),
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    IconButton(onClick = {
                        // Show date picker dialog
                        showDatePickerDialog(context, selectedDate)
                    }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Select Date")
                    }
                }
            },
            navigationIcon = {
                IconButton(onClick = {
                    navController.popBackStack()
                }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        )
    }) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {

                // 1번 박스: 이모지 선택
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        emojis.forEachIndexed { index, emoji ->
                            val alpha = if (selectedEmojiIndex == index) 1f else 0.5f
                            Icon(
                                painter = painterResource(id = emoji),
                                contentDescription = null,
                                tint = Color.Unspecified,
                                modifier = Modifier
                                    .size(64.dp)
                                    .alpha(alpha)
                                    .clickable { selectedEmojiIndex = index }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 2번 박스: 이미지 선택 및 미리보기
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                        .padding(16.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Button(onClick = { launcher.launch("image/*") }) {
                            Text("이미지 선택")
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (selectedImageUri != null) {
                            AndroidView(
                                factory = { context ->
                                    ImageView(context).apply {
                                        setImageURI(selectedImageUri)
                                    }
                                },
                                modifier = Modifier.size(200.dp)
                            )
                        } else {
                            Text("오늘 하루의 대표 이미지를 선택해주세요", color = Color.Gray)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // 3번 박스: 일기 입력 필드
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                        .padding(16.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        OutlinedTextField(
                            value = diaryText,
                            onValueChange = { diaryText = it },
                            label = { Text("일기 작성") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(onClick = {
                            coroutineScope.launch {
                                uploadDiaryWithImageToFirebase(
                                    selectedImageUri,
                                    diaryText,
                                    storageReference,
                                    firestore,
                                    activity,
                                    selectedEmojiIndex,
                                    selectedDate.value
                                )
                            }
                        }) {
                            Text("일기 업로드")
                        }

                    }
                }
            }
        }
    }
}
private fun showDatePickerDialog(context: Context, selectedDate: MutableState<LocalDate>) {
    val calendar = Calendar.getInstance()
    calendar.set(selectedDate.value.year, selectedDate.value.monthValue - 1, selectedDate.value.dayOfMonth)
    DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            selectedDate.value = LocalDate.of(year, month + 1, dayOfMonth)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).show()
}