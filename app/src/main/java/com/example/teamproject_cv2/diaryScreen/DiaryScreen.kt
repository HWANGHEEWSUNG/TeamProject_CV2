package com.example.teamproject_cv2.diaryScreen

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
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
import com.google.firebase.storage.StorageReference
import java.util.UUID

@Composable
fun DiaryScreen(navController: NavController, storageReference: StorageReference) {
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val activity = LocalContext.current as Activity
    val context = LocalContext.current
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

            Button(onClick = { uploadImageToFirebaseStorage(uri, storageReference, context) }) {
                Text("이미지 업로드")
            }
        }
    }
}

fun uploadImageToFirebaseStorage(uri: Uri, storageReference: StorageReference, context: Context) {
    val fileName = UUID.randomUUID().toString()
    val ref = storageReference.child("images/$fileName")

    ref.putFile(uri)
        .addOnSuccessListener { taskSnapshot ->
            // 업로드 성공 처리
            val downloadUrl = taskSnapshot.metadata?.reference?.downloadUrl
            downloadUrl?.addOnSuccessListener {
                // 이미지 URL을 사용할 수 있음
                Toast.makeText(context, "업로드 성공: $it", Toast.LENGTH_LONG).show()
            }
        }
        .addOnFailureListener {
            // 업로드 실패 처리
            Toast.makeText(context, "업로드 실패: ${it.message}", Toast.LENGTH_LONG).show()
        }
}