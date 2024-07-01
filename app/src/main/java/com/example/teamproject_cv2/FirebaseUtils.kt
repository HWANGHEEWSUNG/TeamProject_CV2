package com.example.teamproject_cv2

import android.app.Activity
import android.net.Uri
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import java.util.UUID

fun uploadDiaryWithImageToFirebase(
    uri: Uri?,
    diaryText: String,
    storageReference: StorageReference,
    firestore: FirebaseFirestore,
    activity: Activity,
    selectedEmojiIndex: Int
) {
    if (uri != null) {
        val fileName = UUID.randomUUID().toString()
        val ref = storageReference.child("images/$fileName")

        ref.putFile(uri).addOnSuccessListener { taskSnapshot ->
            val downloadUrl = taskSnapshot.metadata?.reference?.downloadUrl
            downloadUrl?.addOnSuccessListener { url ->
                saveDiaryToFirestore(diaryText, url.toString(), firestore, activity, selectedEmojiIndex)
            }
        }.addOnFailureListener {
            Toast.makeText(activity, "이미지 업로드 실패: ${it.message}", Toast.LENGTH_LONG).show()
        }
    } else {
        saveDiaryToFirestore(diaryText, null, firestore, activity, selectedEmojiIndex)
    }
}

fun saveDiaryToFirestore(
    diaryText: String, imageUrl: String?, firestore: FirebaseFirestore, activity: Activity, selectedEmojiIndex: Int
) {
    val diaryEntry = hashMapOf(
        "text" to diaryText,
        "imageUrl" to imageUrl,
        "timestamp" to System.currentTimeMillis(),
        "selectedEmojiIndex" to selectedEmojiIndex
    )

    firestore.collection("diaries").add(diaryEntry).addOnSuccessListener {
        Toast.makeText(activity, "일기 업로드 성공", Toast.LENGTH_LONG).show()
    }.addOnFailureListener {
        Toast.makeText(activity, "일기 업로드 실패: ${it.message}", Toast.LENGTH_LONG).show()
    }
}
