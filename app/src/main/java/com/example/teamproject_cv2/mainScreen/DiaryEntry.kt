package com.example.teamproject_cv2.mainScreen

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

data class DiaryEntry(
    val text: String = "",
    val imageUrl: String? = null,
    val timestamp: Long = 0L,
    val selectedEmojiIndex: Int = 0,
    val selectedDate: String = "",
    val emotion: String = "",
    val emotionScore: Double = 0.0
)

suspend fun getDiaryEntries(firestore: FirebaseFirestore): List<DiaryEntry> {
    val tenDaysAgo = Date.from(LocalDate.now().minusDays(10).atStartOfDay(ZoneId.systemDefault()).toInstant())

    return try {
        val snapshot = firestore.collection("diaries")
            .whereGreaterThan("timestamp", tenDaysAgo.time)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .await()

        snapshot.documents.mapNotNull { it.toObject(DiaryEntry::class.java) }
    } catch (e: Exception) {
        emptyList()
    }
}
