package com.example.culturunya.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Pk2() {
    var currentDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Calendar Header with Navigation
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${currentDate.month.getDisplayName(TextStyle.FULL, Locale("es"))
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale("es")) else it.toString() }} ${currentDate.year}",
                style = MaterialTheme.typography.titleLarge
            )
            Row {
                IconButton(onClick = {
                    currentDate = currentDate.minusMonths(1)
                }) {
                    Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Previous Month")
                }
                IconButton(onClick = {
                    currentDate = currentDate.plusMonths(1)
                }) {
                    Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Next Month")
                }
            }
        }

        // Calendar Grid
        CalendarGrid(
            currentDate = currentDate,
            selectedDate = selectedDate,
            onDateSelected = { selectedDate = it }
        )

        // Selected Date Details
        Spacer(modifier = Modifier.height(16.dp))
        SelectedDateDisplay(selectedDate)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarGrid(
    currentDate: LocalDate,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    val firstDayOfMonth = currentDate.withDayOfMonth(1)
    val daysInMonth = currentDate.lengthOfMonth()

    // Adjust startingWeekday to make Monday the first day (0-based index)
    val startingWeekday = (firstDayOfMonth.dayOfWeek.value - 1 + 7) % 7

    val dayNames = listOf("Dl", "Dt", "Dc", "Dj", "Dv", "Ds", "Dg")

    // Day names header
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        dayNames.forEach { day ->
            Text(
                text = day,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
        }
    }

    // Calendar days grid
    Column {
        var dayCounter = 1
        var dateToRender: LocalDate

        for (week in 0 until 6) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (day in 0 until 7) {
                    val gridDay = week * 7 + day
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(4.dp)
                            .background(
                                color = when {
                                    gridDay < startingWeekday -> Color.Transparent
                                    dayCounter > daysInMonth -> Color.Transparent
                                    else -> {
                                        dateToRender = currentDate.withDayOfMonth(dayCounter)
                                        when {
                                            dateToRender == LocalDate.now() -> MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                            dateToRender == selectedDate -> MaterialTheme.colorScheme.primary
                                            else -> Color.Transparent
                                        }
                                    }
                                },
                                shape = RoundedCornerShape(4.dp)
                            )
                            .clickable(
                                enabled = gridDay >= startingWeekday && dayCounter <= daysInMonth
                            ) {
                                if (gridDay >= startingWeekday && dayCounter <= daysInMonth) {
                                    onDateSelected(currentDate.withDayOfMonth(dayCounter))
                                }
                            }
                    ) {
                        if (gridDay >= startingWeekday && dayCounter <= daysInMonth) {
                            Text(
                                text = dayCounter.toString(),
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(4.dp),
                                color = when {
                                    currentDate.withDayOfMonth(dayCounter) == LocalDate.now() ->
                                        MaterialTheme.colorScheme.primary
                                    currentDate.withDayOfMonth(dayCounter) == selectedDate ->
                                        MaterialTheme.colorScheme.onPrimary
                                    else -> MaterialTheme.colorScheme.onSurface
                                }
                            )
                            dayCounter++
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SelectedDateDisplay(selectedDate: LocalDate) {
    val dayOfWeekName = selectedDate.dayOfWeek.getDisplayName(TextStyle.FULL, Locale("es"))
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale("es")) else it.toString() }

    Text(
        text = "$dayOfWeekName, ${selectedDate.dayOfMonth} de ${selectedDate.month.getDisplayName(TextStyle.FULL, Locale("es"))} de ${selectedDate.year}",
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}