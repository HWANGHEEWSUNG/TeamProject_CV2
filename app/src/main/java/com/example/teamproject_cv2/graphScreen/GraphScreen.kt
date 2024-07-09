package com.example.teamproject_cv2.graphScreen

import android.graphics.Color
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.teamproject_cv2.mainScreen.DiaryEntry
import com.example.teamproject_cv2.mainScreen.getDiaryEntries
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.RadarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.RadarData
import com.github.mikephil.charting.data.RadarDataSet
import com.github.mikephil.charting.data.RadarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.Utils
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.ui.graphics.Color as ComposeColor

val emotionColors = mapOf(
    "anger" to Color.RED,
    "disgust" to Color.GREEN,
    "fear" to Color.BLUE,
    "joy" to Color.YELLOW,
    "neutral" to Color.CYAN,
    "sadness" to Color.MAGENTA,
    "surprise" to Color.LTGRAY
)

@Composable
fun GraphScreen(navController: NavController, firestore: FirebaseFirestore) {
    val coroutineScope = rememberCoroutineScope()
    var diaryEntries by remember { mutableStateOf<List<DiaryEntry>>(emptyList()) }
    var barChartData by remember { mutableStateOf<BarData?>(null) }
    var labels by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedEntry by remember { mutableStateOf<DiaryEntry?>(null) }
    var selectedColor by remember { mutableStateOf(Color.LTGRAY) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        Utils.init(context)  // MPAndroidChart Utils 초기화
        coroutineScope.launch {
            diaryEntries = getDiaryEntries(firestore).reversed()  // Reverse the order of entries
            val entries = diaryEntries.mapIndexed { index, entry ->
                val dominantEmotion = entry.dominantEmotion
                BarEntry(index.toFloat(), dominantEmotion.second.toFloat())
            }
            val colors = diaryEntries.map { entry ->
                emotionColors[entry.dominantEmotion.first] ?: Color.LTGRAY
            }
            val dataSet = BarDataSet(entries, "Dominant Emotion Scores").apply {
                this.colors = colors
                valueTextColor = Color.BLACK
                valueTextSize = 16f
            }
            barChartData = BarData(dataSet)
            labels = diaryEntries.map { entry ->
                LocalDate.parse(entry.selectedDate).format(DateTimeFormatter.ofPattern("M/d"))
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Emotion Scores Over Time",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            if (barChartData != null && labels.isNotEmpty()) {
                BarChartCompose(barChartData!!, labels) { index, color ->
                    selectedEntry = diaryEntries[index]
                    selectedColor = color
                    Log.d("GraphScreen", "Selected Entry Updated: $selectedEntry, Color: $color")
                }
            } else {
                Text(text = "Loading chart data...", style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(modifier = Modifier.height(16.dp))
            selectedEntry?.let { selectedEntry ->
                key(selectedEntry) {
                    Log.d("GraphScreen", "Rendering RadarChart with selectedEntry: $selectedEntry")
                    RadarChartCompose(selectedEntry, selectedColor)
                }
            } ?: Text(
                text = "Select a bar to view radar chart",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        ExtendedFloatingActionButton(
            onClick = { navController.navigate("historyScreen") },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = ComposeColor.White,
            icon = { Icon(Icons.Default.Book, contentDescription = "History", tint = ComposeColor.White) },
            text = { Text(text = "일기 확인", color = ComposeColor.White) }
        )
    }
}

@Composable
fun BarChartCompose(barData: BarData, labels: List<String>, onBarClick: (Int, Int) -> Unit) {
    AndroidView(
        factory = { context ->
            BarChart(context).apply {
                this.data = barData
                description.isEnabled = false
                xAxis.valueFormatter = IndexAxisValueFormatter(labels)
                xAxis.granularity = 1f
                xAxis.setLabelRotationAngle(270f)  // Rotate x-axis labels
                axisLeft.axisMinimum = 0f
                axisRight.axisMinimum = 0f
                xAxis.isGranularityEnabled = true
                xAxis.isGranularityEnabled = true
                xAxis.granularity = 1f
                xAxis.labelCount = labels.size
                xAxis.setDrawGridLines(false)
                axisLeft.setDrawGridLines(false)
                axisRight.setDrawGridLines(false)
                xAxis.position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM_INSIDE
                setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                    override fun onValueSelected(e: Entry?, h: Highlight?) {
                        e?.let {
                            val color = (data.getDataSetByIndex(0) as BarDataSet).getColor(it.x.toInt())
                            Log.d("BarChartCompose", "Bar selected at index: ${it.x.toInt()}, Color: $color")
                            onBarClick(it.x.toInt(), color)
                        }
                    }

                    override fun onNothingSelected() {
                        Log.d("BarChartCompose", "No bar selected")
                    }
                })
                invalidate() // refresh

                // Add legend for emotions
                val legend = this.legend
                legend.isEnabled = true
                legend.form = Legend.LegendForm.CIRCLE
                legend.textSize = 12f
                legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
                legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                legend.orientation = Legend.LegendOrientation.HORIZONTAL
                legend.setDrawInside(false)

                val legendEntries = emotionColors.map { (emotion, color) ->
                    com.github.mikephil.charting.components.LegendEntry().apply {
                        formColor = color
                        label = emotion
                    }
                }
                legend.setCustom(legendEntries)
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    )
}

@Composable
fun RadarChartCompose(selectedEntry: DiaryEntry, selectedColor: Int) {
    val emotions = selectedEntry.emotions.toList()
    val dominantEmotionIndex = emotions.indexOfFirst { it.first == selectedEntry.dominantEmotion.first }
    val rotatedEmotions = if (dominantEmotionIndex != -1) {
        emotions.subList(dominantEmotionIndex, emotions.size) + emotions.subList(0, dominantEmotionIndex)
    } else {
        emotions
    }

    AndroidView(
        factory = { context ->
            Utils.init(context)  // MPAndroidChart Utils 초기화
            RadarChart(context).apply {
                if (rotatedEmotions.isNotEmpty()) {
                    val radarEntries = rotatedEmotions.map { (_, score) ->
                        RadarEntry(score.toFloat())
                    }
                    val radarDataSet = RadarDataSet(radarEntries, "Emotion Scores").apply {
                        color = selectedColor
                        setDrawValues(false) // 데이터 값 텍스트 비활성화
                        lineWidth = 2f // 데이터 라인 두께 설정
                        setDrawHighlightCircleEnabled(true) // 하이라이트 원 활성화
                        setDrawHighlightIndicators(true) // 하이라이트 인디케이터 활성화
                        setDrawFilled(true) // 데이터 점 채우기 활성화
                        fillColor = selectedColor // 채우기 색상 설정
                        fillAlpha = 100 // 채우기 투명도 설정
                    }

                    val radarData = RadarData(radarDataSet)
                    this.data = radarData
                    description.isEnabled = false
                    xAxis.apply {
                        valueFormatter = IndexAxisValueFormatter(rotatedEmotions.map { it.first })
                        textSize = 14f
                        textColor = Color.BLACK
                    }
                    yAxis.apply {
                        axisMinimum = 0f
                        axisMaximum = 1f
                        setDrawLabels(false) // y축 레이블 비활성화
                        granularity = 0.1f // y축 간격 설정
                    }
                    legend.isEnabled = false // 범례 비활성화
                    setPadding(16, 16, 16, 16) // 차트 패딩 설정
                    setExtraOffsets(16f, 16f, 16f, 16f) // 차트 오프셋 설정
                    invalidate() // refresh
                    Log.d("RadarChartCompose", "Chart invalidated with data: $radarData")
                } else {
                    clear() // 데이터가 없을 경우 차트 초기화
                    invalidate()
                    Log.d("RadarChartCompose", "Chart cleared due to empty data")
                }
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp) // 차트 높이를 크게 설정
    )
}
