@file:Suppress("DEPRECATION")

package com.example.teamproject_cv2.diaryScreen

import android.app.Activity
import android.net.Uri
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import java.util.UUID

@Composable
fun DiaryScreen(navController: NavController, storageReference: StorageReference, firestore: FirebaseFirestore) {
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var diaryText by remember { mutableStateOf("") }
    val activity = LocalContext.current as Activity
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        selectedImageUri = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(onClick = { launcher.launch("image/*") }) {
            Text("이미지 선택")
        }

        Spacer(modifier = Modifier.height(16.dp))

        selectedImageUri?.let { uri ->
            // 선택된 이미지 미리보기
            AndroidView(
                factory = { context ->
                    ImageView(context).apply {
                        setImageURI(uri)
                    }
                },
                modifier = Modifier.size(200.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        // 일기 입력 필드
        OutlinedTextField(
            value = diaryText,
            onValueChange = { diaryText = it },
            label = { Text("일기 작성") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { uploadDiaryWithImageToFirebase(selectedImageUri, diaryText, storageReference, firestore, activity) }) {
            Text("일기 업로드")
        }
    }
}

fun uploadDiaryWithImageToFirebase(uri: Uri?, diaryText: String, storageReference: StorageReference, firestore: FirebaseFirestore, activity: Activity) {
    if (uri != null) {
        val fileName = UUID.randomUUID().toString()
        val ref = storageReference.child("images/$fileName")

        ref.putFile(uri)
            .addOnSuccessListener { taskSnapshot ->
                val downloadUrl = taskSnapshot.metadata?.reference?.downloadUrl
                downloadUrl?.addOnSuccessListener { url ->
                    saveDiaryToFirestore(diaryText, url.toString(), firestore, activity)
                }
            }
            .addOnFailureListener {
                Toast.makeText(activity, "이미지 업로드 실패: ${it.message}", Toast.LENGTH_LONG).show()
            }
    } else {
        // 이미지 없이 일기만 업로드
        saveDiaryToFirestore(diaryText, null, firestore, activity)
    }
}

fun saveDiaryToFirestore(diaryText: String, imageUrl: String?, firestore: FirebaseFirestore, activity: Activity) {
    val diaryEntry = hashMapOf(
        "text" to diaryText,
        "imageUrl" to imageUrl,
        "timestamp" to System.currentTimeMillis()
    )

    firestore.collection("diaries")
        .add(diaryEntry)
        .addOnSuccessListener {
            Toast.makeText(activity, "일기 업로드 성공", Toast.LENGTH_LONG).show()
        }
        .addOnFailureListener {
            Toast.makeText(activity, "일기 업로드 실패: ${it.message}", Toast.LENGTH_LONG).show()
        }
}