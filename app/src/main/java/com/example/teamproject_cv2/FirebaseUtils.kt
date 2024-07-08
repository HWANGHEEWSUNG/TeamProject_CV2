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
import java.math.RoundingMode
import java.text.DecimalFormat

suspend fun uploadDiaryWithImageToFirebase(
    uri: Uri?,
    diaryText: String,
    storageReference: StorageReference,
    firestore: FirebaseFirestore,
    activity: Activity,
    selectedEmojiIndex: Int,
    selectedDate: LocalDate // 선택한 날짜 매개변수 추가
) {
    val emotions = analyzeEmotion(diaryText)

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
                    emotions
                )
            }
        }.addOnFailureListener {
            Toast.makeText(activity, "이미지 업로드 실패: ${it.message}", Toast.LENGTH_LONG).show()
        }
    } else {
        saveDiaryToFirestore(
            diaryText, null, firestore, activity, selectedEmojiIndex, selectedDate, emotions
        )
    }
}

fun saveDiaryToFirestore(
    diaryText: String,
    imageUrl: String?,
    firestore: FirebaseFirestore,
    activity: Activity,
    selectedEmojiIndex: Int,
    selectedDate: LocalDate,
    emotions: List<Pair<String, Double>>
) {
    val emotionMap = emotions.associate { it.first to it.second }

    val diaryEntry = hashMapOf(
        "text" to diaryText,
        "imageUrl" to imageUrl,
        "timestamp" to System.currentTimeMillis(),
        "selectedEmojiIndex" to selectedEmojiIndex,
        "selectedDate" to selectedDate.toString(),
        "emotions" to emotionMap // 감정과 그 점수들을 저장
    )

    firestore.collection("diaries").add(diaryEntry).addOnSuccessListener {
        Toast.makeText(activity, "일기 업로드 성공", Toast.LENGTH_LONG).show()
    }.addOnFailureListener {
        Toast.makeText(activity, "일기 업로드 실패: ${it.message}", Toast.LENGTH_LONG).show()
    }
}


suspend fun analyzeEmotion(text: String): List<Pair<String, Double>> {
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
                val jsonResponse = JSONObject(responseBody)
                val resultsArray = jsonResponse.getJSONArray("results")

                val emotions = mutableListOf<Pair<String, Double>>()
                for (i in 0 until resultsArray.length()) {
                    val result = resultsArray.getJSONObject(i)
                    val label = result.getString("label")
                    val score = result.getDouble("score")

                    // 소수점 둘째자리로 반올림
                    val df = DecimalFormat("#.##")
                    df.roundingMode = RoundingMode.CEILING
                    val roundedScore = df.format(score).toDouble()

                    emotions.add(Pair(label, roundedScore))
                }

                return@withContext emotions
            } else {
                Log.e("AnalyzeEmotion", "Error response: ${response.code} ${responseBody}")
                return@withContext emptyList<Pair<String, Double>>()
            }
        } catch (e: Exception) {
            Log.e("AnalyzeEmotion", "Error analyzing emotion", e)
            return@withContext emptyList<Pair<String, Double>>()
        }
    }
}
