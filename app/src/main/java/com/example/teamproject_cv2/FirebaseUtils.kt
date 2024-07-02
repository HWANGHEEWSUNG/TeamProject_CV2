package com.example.teamproject_cv2

import android.app.Activity
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import java.time.LocalDate
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONArray
import org.json.JSONObject

suspend fun uploadDiaryWithImageToFirebase(
    uri: Uri?,
    diaryText: String,
    storageReference: StorageReference,
    firestore: FirebaseFirestore,
    activity: Activity,
    selectedEmojiIndex: Int,
    selectedDate: LocalDate // 선택한 날짜 매개변수 추가
) {
    try {
        // 감정 분석을 비동기로 수행하고 결과를 기다립니다
        val emotion = analyzeEmotion(diaryText)

        if (uri != null) {
            val fileName = UUID.randomUUID().toString()
            val ref = storageReference.child("images/$fileName")

            ref.putFile(uri).addOnSuccessListener { taskSnapshot ->
                val downloadUrl = taskSnapshot.metadata?.reference?.downloadUrl
                downloadUrl?.addOnSuccessListener { url ->
                    saveDiaryToFirestore(
                        diaryText,
                        url.toString(),
                        firestore,
                        activity,
                        selectedEmojiIndex,
                        selectedDate,
                        emotion
                    )
                }
            }.addOnFailureListener {
                Toast.makeText(activity, "이미지 업로드 실패: ${it.message}", Toast.LENGTH_LONG).show()
            }
        } else {
            saveDiaryToFirestore(
                diaryText, null, firestore, activity, selectedEmojiIndex, selectedDate, emotion
            )
        }
    } catch (e: Exception) {
        Log.e("UploadDiary", "Error uploading diary", e)
        Toast.makeText(activity, "일기 업로드 실패: ${e.message}", Toast.LENGTH_LONG).show()
    }
}

fun saveDiaryToFirestore(
    diaryText: String,
    imageUrl: String?,
    firestore: FirebaseFirestore,
    activity: Activity,
    selectedEmojiIndex: Int,
    selectedDate: LocalDate,
    emotion: String
) {
    val diaryEntry = hashMapOf(
        "text" to diaryText,
        "imageUrl" to imageUrl,
        "timestamp" to System.currentTimeMillis(),
        "selectedEmojiIndex" to selectedEmojiIndex,
        "selectedDate" to selectedDate.toString(),
        "emotion" to emotion  // Add the analyzed emotion
    )

    firestore.collection("diaries").add(diaryEntry).addOnSuccessListener {
        Toast.makeText(activity, "일기 업로드 성공", Toast.LENGTH_LONG).show()
    }.addOnFailureListener {
        Toast.makeText(activity, "일기 업로드 실패: ${it.message}", Toast.LENGTH_LONG).show()
    }
}

suspend fun analyzeEmotion(text: String): String {
    return withContext(Dispatchers.IO) {
        try {
            val client = OkHttpClient()

            val json = JSONObject().put("text", text)

            Log.d("AnalyzeEmotion", "Sending data: ${json.toString()}")

            val body = json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())

            val request = Request.Builder()
                .url("http://192.168.45.66:5000/analyze_emotion")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            if (response.isSuccessful && responseBody != null) {
                val jsonResponse = JSONArray(responseBody)

                if (jsonResponse.length() > 0) {
                    val label = jsonResponse.getJSONObject(0).getString("label")
                    return@withContext label
                } else {
                    return@withContext "Unknown"
                }
            } else {
                Log.e("AnalyzeEmotion", "Error response: ${response.code} ${responseBody}")
                return@withContext "Unknown"
            }
        } catch (e: Exception) {
            Log.e("AnalyzeEmotion", "Error analyzing emotion", e)
            return@withContext "Unknown"
        }
    }
}
