package com.example.teamproject_cv2

import android.app.Activity
import android.net.Uri
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import java.time.LocalDate
import java.util.UUID

fun uploadDiaryWithImageToFirebase(
    uri: Uri?,
    diaryText: String,
    storageReference: StorageReference,
    firestore: FirebaseFirestore,
    activity: Activity,
    selectedEmojiIndex: Int,
    selectedDate: LocalDate // 선택한 날짜 매개변수 추가
) {
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
                    selectedDate // 선택한 날짜 전달
                )
            }
        }.addOnFailureListener {
            Toast.makeText(activity, "이미지 업로드 실패: ${it.message}", Toast.LENGTH_LONG).show()
        }
    } else {
        saveDiaryToFirestore(
            diaryText, null, firestore, activity, selectedEmojiIndex, selectedDate // 선택한 날짜 전달
        )
    }
}

fun saveDiaryToFirestore(
    diaryText: String,
    imageUrl: String?,
    firestore: FirebaseFirestore,
    activity: Activity,
    selectedEmojiIndex: Int,
    selectedDate: LocalDate  // 추가: 선택한 날짜를 매개변수로 받음

) {
    val diaryEntry = hashMapOf(
        "text" to diaryText,
        "imageUrl" to imageUrl,
        "timestamp" to System.currentTimeMillis(),
        "selectedEmojiIndex" to selectedEmojiIndex,
        "selectedDate" to selectedDate.toString()  // 선택한 날짜를 문자열로 변환하여 저장
    )

    firestore.collection("diaries").add(diaryEntry).addOnSuccessListener {
        Toast.makeText(activity, "일기 업로드 성공", Toast.LENGTH_LONG).show()
    }.addOnFailureListener {
        Toast.makeText(activity, "일기 업로드 실패: ${it.message}", Toast.LENGTH_LONG).show()
    }
}
