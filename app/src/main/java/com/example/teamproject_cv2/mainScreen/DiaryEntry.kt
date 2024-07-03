package com.example.teamproject_cv2.mainScreen

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

data class DiaryEntry(
    val text: String = "",
    val imageUrl: String? = null,
    val timestamp: Long = 0L,
    val selectedEmojiIndex: Int = 0,
    val selectedDate: String = "",
    val emotion: String = "",
    val emotionScore: Double = 0.0,
    val isPlaceholder: Boolean = false,
    val onClick: (() -> Unit)? = null  // Add this line
)

suspend fun getDiaryEntries(firestore: FirebaseFirestore, onPlaceholderClick: (String) -> Unit): List<DiaryEntry> {
    val today = LocalDate.now()
    val tenDaysAgo = today.minusDays(9)  // 오늘 포함 10일
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val displayFormatter = DateTimeFormatter.ofPattern("M월 d일", Locale.KOREAN)

    val existingEntries = try {
        val snapshot = firestore.collection("diaries")
            .whereGreaterThanOrEqualTo("timestamp", tenDaysAgo.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli())
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .await()

        snapshot.documents.mapNotNull { it.toObject(DiaryEntry::class.java) }
    } catch (e: Exception) {
        emptyList()
    }

    val allEntries = mutableListOf<DiaryEntry>()
    var currentDate = today

    while (currentDate >= tenDaysAgo) {
        val formattedDate = currentDate.format(dateFormatter)
        val displayDate = currentDate.format(displayFormatter)
        val existingEntry = existingEntries.find { it.selectedDate == formattedDate }

        if (existingEntry != null) {
            allEntries.add(existingEntry)
        } else {
            allEntries.add(DiaryEntry(
                text = "$displayDate 일기를 작성해주세요!",
                selectedDate = formattedDate,
                isPlaceholder = true,
                onClick = { onPlaceholderClick(formattedDate) }  // 클릭 핸들러 설정
            ))
        }

        currentDate = currentDate.minusDays(1)
    }

    return allEntries
}