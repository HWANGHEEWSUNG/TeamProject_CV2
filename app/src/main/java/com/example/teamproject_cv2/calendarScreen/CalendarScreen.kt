package com.example.teamproject_cv2.calendarScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.TextStyle.FULL
import java.util.Locale

@Composable
fun CalendarScreen(
    selectedDate: LocalDate = LocalDate.now(), // 기본값으로 현재 날짜를 설정할 수 있음
    onDateSelected: (LocalDate) -> Unit
) {
    val currentMonth = remember { mutableStateOf(LocalDate.now().withDayOfMonth(1)) }

    Column(modifier = Modifier.padding(16.dp)) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = { currentMonth.value = currentMonth.value.minusMonths(1) }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Previous Month")
            }
            Text(
                text = "${currentMonth.value.month.getDisplayName(FULL, Locale.getDefault())} ${currentMonth.value.year}",
                style = MaterialTheme.typography.titleMedium
            )
            IconButton(onClick = { currentMonth.value = currentMonth.value.plusMonths(1) }) {
                Icon(Icons.Default.ArrowForward, contentDescription = "Next Month")
            }
        }


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
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(firstDayOfWeek) {
                Spacer(modifier = Modifier.size(40.dp))
            }
            items(dates.size) { index ->
                val date = dates[index]
                Text(
                    text = date.dayOfMonth.toString(),
                    modifier = Modifier
                        .size(40.dp)
                        .padding(4.dp)
                        .clickable { onDateSelected(date) }
                        .background(
                            if (date == selectedDate) MaterialTheme.colorScheme.primary
                            else Color.Transparent,
                            shape = androidx.compose.foundation.shape.CircleShape
                        )
                        .wrapContentSize(align = Alignment.Center),
                    textAlign = TextAlign.Center,
                    color = if (date == selectedDate) Color.White else Color.Black,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}