package com.example.teamproject_cv2.profileScreen

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.teamproject_cv2.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*

@Composable
fun ProfileScreen(navController: NavController) {
    val firestore = FirebaseFirestore.getInstance()
    val storage = FirebaseStorage.getInstance().reference
    val context = LocalContext.current

    // State variables for user inputs and image URI
    var name by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isUploading by remember { mutableStateOf(false) }

    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            selectedImageUri = uri
        }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Display selected image or offer to pick one
            if (selectedImageUri != null) {
                Image(
                    painter = painterResource(id = R.drawable.img), // Placeholder image resource
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

            // Text fields for user inputs
            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("이름") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            )
            TextField(
                value = gender,
                onValueChange = { gender = it },
                label = { Text("성별") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            )
            TextField(
                value = age,
                onValueChange = { age = it },
                label = { Text("나이") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // Button to save profile
            Button(
                onClick = {
                    isUploading = true
                    selectedImageUri?.let { uri ->
                        // Generate a unique file name for the image
                        val imageName = "${UUID.randomUUID()}.jpg"
                        val imageRef = storage.child("profiles/$imageName")

                        // Upload image to Firebase Storage
                        val uploadTask = imageRef.putFile(uri)
                        uploadTask.continueWithTask { task ->
                            if (!task.isSuccessful) {
                                task.exception?.let { throw it }
                            }
                            imageRef.downloadUrl
                        }.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // Get download URL of uploaded image
                                val downloadUri = task.result

                                // Create a user object with profile details and photo URL
                                val user = hashMapOf(
                                    "name" to name,
                                    "gender" to gender,
                                    "age" to age,
                                    "photoUrl" to downloadUri.toString()
                                )

                                // Store user object in Firestore
                                firestore.collection("users")
                                    .add(user)
                                    .addOnSuccessListener {
                                        Toast.makeText(context, "프로필 저장 성공", Toast.LENGTH_SHORT).show()
                                        navController.popBackStack() // Navigate back after success
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
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            ) {
                if (isUploading) {
                    CircularProgressIndicator(color = Color.White)
                } else {
                    Text(text = "저장")
                }
            }
        }
    }
}