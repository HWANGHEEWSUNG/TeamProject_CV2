package com.example.teamproject_cv2.profileScreen

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

@Composable
fun ProfileScreen(navController: NavController) {
    val firestore = FirebaseFirestore.getInstance()
    val storage = FirebaseStorage.getInstance().reference
    val context = LocalContext.current

    // 사용자 입력과 이미지 URI를 위한 상태 변수
    var username by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isUploading by remember { mutableStateOf(false) }

    // 이미지 선택기를 위한 런처
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            selectedImageUri = uri
        }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1B0A40))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        // 배경
        Box(
            modifier = Modifier
                .size(643.28.dp, 973.34.dp)
                .offset((-85).dp, (-69).dp)
                .background(Color(0xFF1B0A40))
        )

        // Ellipse 5
        Box(
            modifier = Modifier
                .size(407.dp, 390.dp)
                .offset((-85).dp, (-69).dp)
                .background(Color(0x30FF43A8), shape = CircleShape)
                .blur(130.dp)
                .alpha(0.8f)
        )

        // Ellipse 6
        Box(
            modifier = Modifier
                .size(388.47.dp, 528.dp)
                .offset((-571.5).dp, (-231.53).dp)
                .background(Color(0x30D943FF), shape = CircleShape)
                .blur(130.dp)
                .rotate(89.96f)
                .alpha(0.8f)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // 선택된 이미지를 표시하거나 이미지를 선택할 수 있는 옵션을 제공
            if (selectedImageUri != null) {
                Image(
                    painter = rememberImagePainter(selectedImageUri),
                    contentDescription = null,
                    modifier = Modifier
                        .size(128.dp)
                        .clip(shape = MaterialTheme.shapes.medium)
                        .clickable { imagePickerLauncher.launch("image/*") },
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(128.dp)
                        .clip(shape = MaterialTheme.shapes.medium)
                        .background(Color.Gray)
                        .clickable { imagePickerLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "사진 추가", fontSize = 16.sp, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 사용자 입력을 위한 텍스트 필드
            TextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("username") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            Box(
                modifier = Modifier
                    .width(171.dp)
                    .height(60.dp)
                    .clip(shape = CircleShape.copy(all = CornerSize(100)))
                    .background(Color(0xFFB063ED))
                    .clickable {
                        isUploading = true
                        selectedImageUri?.let { uri ->
                            // 이미지 파일 이름 생성
                            val imageName = "${UUID.randomUUID()}.jpg"
                            val imageRef = storage.child("profiles/$imageName")

                            // 이미지 Firebase Storage에 업로드
                            val uploadTask = imageRef.putFile(uri)
                            uploadTask.continueWithTask { task ->
                                if (!task.isSuccessful) {
                                    task.exception?.let { throw it }
                                }
                                imageRef.downloadUrl
                            }.addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    // 업로드된 이미지의 다운로드 URL 얻기
                                    val downloadUri = task.result

                                    // 사용자 객체 생성
                                    val user = hashMapOf(
                                        "username" to username,
                                        "photoUrl" to downloadUri.toString()
                                    )

                                    // Firestore에 사용자 객체 저장
                                    firestore.collection("users")
                                        .add(user)
                                        .addOnSuccessListener {
                                            Toast.makeText(context, "프로필 저장 성공", Toast.LENGTH_SHORT).show()
                                            navController.popBackStack() // 성공 후 뒤로 가기
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(context, "프로필 저장 실패: ${it.message}", Toast.LENGTH_SHORT).show()
                                            isUploading = false
                                        }
                                } else {
                                    Toast.makeText(context, "사진 업로드 실패", Toast.LENGTH_SHORT).show()
                                    isUploading = false
                                }
                            }
                        } ?: run {
                            Toast.makeText(context, "사진을 선택해주세요", Toast.LENGTH_SHORT).show()
                            isUploading = false
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                if (isUploading) {
                    CircularProgressIndicator(color = Color.White)
                } else {
                    Text(text = "저장", color = Color.White)
                }
            }
        }
    }
}