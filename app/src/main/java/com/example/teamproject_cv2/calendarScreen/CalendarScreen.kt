package com.example.teamproject_cv2.calendarScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.EmojiEmotions
import androidx.compose.material.icons.rounded.SentimentDissatisfied
import androidx.compose.material.icons.rounded.SentimentSatisfied
import androidx.compose.material.icons.rounded.SentimentVeryDissatisfied
import androidx.compose.material.icons.rounded.SentimentVerySatisfied
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*

@Composable
fun CalendarScreen(
    selectedDate: LocalDate = LocalDate.now(),
    onDateSelected: (LocalDate) -> Unit
) {
    val currentMonth = remember { mutableStateOf(LocalDate.now().withDayOfMonth(1)) }
    val emotions = remember { mutableStateOf(mutableMapOf<LocalDate, ImageVector>()) }
    val openDialog = remember { mutableStateOf(false) }
    val selectedEmotionDate = remember { mutableStateOf<LocalDate?>(null) }
    val onDateClick: (LocalDate) -> Unit = { date ->
        onDateSelected(date)
    }

    val emotionIcons = listOf(
        Icons.Rounded.EmojiEmotions to "Happy",
        Icons.Rounded.SentimentDissatisfied to "Sad",
        Icons.Rounded.SentimentSatisfied to "Neutral",
        Icons.Rounded.SentimentVeryDissatisfied to "Angry",
        Icons.Rounded.SentimentVerySatisfied to "Excited"
    )

    if (openDialog.value && selectedEmotionDate.value != null) {
        AlertDialog(
            onDismissRequest = { openDialog.value = false },
            title = { Text("Select Emotion") },
            text = {
                Column {
                    emotionIcons.forEach { (icon, label) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    emotions.value[selectedEmotionDate.value!!] = icon
                                    openDialog.value = false
                                }
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(icon, contentDescription = label)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(label)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { openDialog.value = false }) {
                    Text("Confirm")
                }
            }
        )
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = { currentMonth.value = currentMonth.value.minusMonths(1) }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Previous Month")
            }
            Text(
                text = "${currentMonth.value.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${currentMonth.value.year}",
                style = MaterialTheme.typography.titleMedium
            )
            IconButton(onClick = { currentMonth.value = currentMonth.value.plusMonths(1) }) {
                Icon(Icons.Default.ArrowForward, contentDescription = "Next Month")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            val daysOfWeek = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
            for (day in daysOfWeek) {
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }

        val firstDayOfWeek = currentMonth.value.dayOfWeek.value % 7
        val daysInMonth = currentMonth.value.lengthOfMonth()
        val dates = (1..daysInMonth).map { currentMonth.value.withDayOfMonth(it) }

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            contentPadding = PaddingValues(vertical = 16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(firstDayOfWeek) {
                Spacer(modifier = Modifier.size(50.dp))
            }
            items(dates.size) { index ->
                val date = dates[index]
                Column(
                    modifier = Modifier
                        .size(50.dp)
                        .padding(8.dp)
                        .clickable {
                            selectedEmotionDate.value = date
                            openDialog.value = true
                        }
                        .background(
                            if (date == selectedDate) MaterialTheme.colorScheme.primary
                            else Color.Transparent,
                            shape = androidx.compose.foundation.shape.CircleShape
                        )
                        .wrapContentSize(align = Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = date.dayOfMonth.toString(),
                        textAlign = TextAlign.Center,
                        color = if (date == selectedDate) Color.White else Color.Black,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    val emotionIcon = emotions.value[date]
                    if (emotionIcon != null) {
                        Icon(
                            emotionIcon,
                            contentDescription = null,
                            tint = if (date == selectedDate) Color.White else Color.Black
                        )
                    }
                }
            }
        }
    }
}