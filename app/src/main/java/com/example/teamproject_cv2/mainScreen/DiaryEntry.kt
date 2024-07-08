package com.example.teamproject_cv2.mainScreen

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale


data class DiaryEntry(
    val text: String = "",
    val imageUrl: String? = null,
    val timestamp: Long = 0L,
    val selectedEmojiIndex: Int = 0,
    val selectedDate: String = "",
    val emotions: Map<String, Double> = emptyMap(),
    val isPlaceholder: Boolean = false,
    val emotion: String = "Unknown",  // 기본값 추가
    val emotionScore: Double = 0.0  // 기본값 추가
) {
    val dominantEmotion: Pair<String, Double>
        get() = emotions.maxByOrNull { it.value }?.toPair() ?: ("Unknown" to 0.0)
}

data class DiaryEntryFirestore(
    val text: String = "",
    val imageUrl: String? = null,
    val timestamp: Long = 0L,
    val selectedEmojiIndex: Int = 0,
    val selectedDate: String = "",  // LocalDate 대신 String
    val emotion: String = "",
    val emotionScore: Double = 0.0,
    val emotions: Map<String, Double> = emptyMap() // emotions 필드 추가
)

suspend fun getDiaryEntries(firestore: FirebaseFirestore): List<DiaryEntry> {
    val today = LocalDate.now()
    val tenDaysAgo = today.minusDays(9)
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val displayFormatter = DateTimeFormatter.ofPattern("M월 d일", Locale.KOREAN)

    val existingEntries = try {
        val snapshot = firestore.collection("diaries")
            .whereGreaterThanOrEqualTo("timestamp", tenDaysAgo.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli())
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .await()

        snapshot.documents.mapNotNull { it.toObject(DiaryEntryFirestore::class.java)?.toDiaryEntry() }
    } catch (e: Exception) {
        Log.e("Firestore", "Error getting diary entries", e)
        emptyList()
    }

    val allEntries = mutableListOf<DiaryEntry>()
    var currentDate = today

    while (currentDate.isAfter(tenDaysAgo) || currentDate.isEqual(tenDaysAgo)) {
        val formattedDate = currentDate.format(dateFormatter)
        val displayDate = displayFormatter.format(currentDate)
        val existingEntry = existingEntries.find { it.selectedDate == formattedDate }

        if (existingEntry != null) {
            allEntries.add(existingEntry)
        } else {
            allEntries.add(DiaryEntry(
                text = "$displayDate 일기를 작성해주세요!",
                selectedDate = formattedDate,
                isPlaceholder = true
            ))
        }

        currentDate = currentDate.minusDays(1)
    }

    return allEntries
}

fun DiaryEntryFirestore.toDiaryEntry(): DiaryEntry {
    val emotionsMap =   emotions ?: emptyMap()
    val dominantEmotion = emotionsMap.maxByOrNull { it.value }?.toPair() ?: ("Unknown" to 0.0)

    return DiaryEntry(
        text = text,
        imageUrl = imageUrl,
        timestamp = timestamp,
        selectedEmojiIndex = selectedEmojiIndex,
        selectedDate = selectedDate,
        emotions = emotionsMap,
        emotion = dominantEmotion.first,
        emotionScore = dominantEmotion.second
    )
}
