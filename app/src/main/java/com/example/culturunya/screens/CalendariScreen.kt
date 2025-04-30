package com.example.culturunya.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import java.time.LocalDate
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.culturunya.R

import com.example.culturunya.endpoints.events.Event
import com.example.culturunya.endpoints.events.EventViewModel
import com.example.culturunya.models.currentSession.CurrentSession
import com.example.culturunya.screens.events.EventBox
import com.example.culturunya.screens.events.EventInfo
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarScreen(viewModel: EventViewModel) {
    var currentDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedEvent by remember { mutableStateOf<Event?>(null) }
    val context = LocalContext.current
    CurrentSession.getInstance()
    val currentLocale = CurrentSession.language

    // Usar filteredEvents para el calendario
    val filteredEvents by viewModel.filteredEvents.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(selectedDate) {
        val formattedDate = selectedDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
        viewModel.filterEventsByDate(
            dateStart = formattedDate,
            dateEnd = formattedDate
        )
    }

    val monthResources = listOf(
        R.string.january, R.string.february, R.string.march, R.string.april,
        R.string.may, R.string.june, R.string.july, R.string.august,
        R.string.september, R.string.october, R.string.november, R.string.december
    )

    // Mostrar EventInfo si hay un evento seleccionado
    if (selectedEvent != null) {
        EventInfo(
            event = selectedEvent!!,
            onBack = { selectedEvent = null }
        )
        return
    }

    // Pantalla principal del calendario
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Calendar Header with Navigation
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${getString(context, monthResources[currentDate.month.ordinal], currentLocale)} ${currentDate.year}",
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
        }

        item {
            // Calendar Grid
            CalendarGrid(
                currentDate = currentDate,
                selectedDate = selectedDate,
                onDateSelected = { selectedDate = it }
            )
        }

        item {
            // Selected Date Details
            SelectedDateDisplay(selectedDate)
        }

        if (isLoading) {
            item {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .wrapContentWidth(Alignment.CenterHorizontally)
                )
            }
        }

        error?.let { errorMessage ->
            item {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
            }
        }

        // Event List usando EventBox
        if (filteredEvents.isEmpty() && !isLoading && error == null) {
            item {
                Text(
                    text = "No hay eventos para este día",
                    modifier = Modifier
                        .fillMaxWidth()
                        //.padding(16.dp)
                    ,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            items(filteredEvents) { event ->
                EventBox(
                    event = event,
                    onEventClick = { selectedEvent = it }
                )
            }
        }
    }
}

// Elimina el composable EventItem original ya que ahora usamos EventBox


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarGrid(
    currentDate: LocalDate,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    val context = LocalContext.current
    CurrentSession.getInstance()
    val currentLocale = CurrentSession.language
    val firstDayOfMonth = currentDate.withDayOfMonth(1)
    val daysInMonth = currentDate.lengthOfMonth()

    // Adjust startingWeekday to make Monday the first day (0-based index)
    val startingWeekday = (firstDayOfMonth.dayOfWeek.value - 1 + 7) % 7

    val dayShortResources = listOf(
        R.string.mondayShort, R.string.tuesdayShort, R.string.wednesdayShort, R.string.thursdayShort,
        R.string.fridayShort, R.string.saturdayShort, R.string.sundayShort
    )

    // Day names header
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        dayShortResources.forEach { resId ->
            Text(
                text = getString(context, resId, currentLocale),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }

    // Calendar days grid
    Column {
        for (week in 0 until 6) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (day in 0 until 7) {
                    val gridDay = week * 7 + day
                    val dayNumber = gridDay - startingWeekday + 1

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(4.dp)
                            .background(
                                color = when {
                                    dayNumber < 1 || dayNumber > daysInMonth -> Color.Transparent
                                    currentDate.withDayOfMonth(dayNumber) == LocalDate.now() ->
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                    currentDate.withDayOfMonth(dayNumber) == selectedDate ->
                                        MaterialTheme.colorScheme.primary
                                    else -> Color.Transparent
                                },
                                shape = RoundedCornerShape(4.dp)
                            )
                            .clickable(
                                enabled = dayNumber in 1..daysInMonth
                            ) {
                                onDateSelected(currentDate.withDayOfMonth(dayNumber))
                            }
                    ) {
                        if (dayNumber in 1..daysInMonth) {
                            Text(
                                text = dayNumber.toString(),
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(4.dp),
                                color = when {
                                    currentDate.withDayOfMonth(dayNumber) == LocalDate.now() ->
                                        MaterialTheme.colorScheme.primary
                                    currentDate.withDayOfMonth(dayNumber) == selectedDate ->
                                        MaterialTheme.colorScheme.onPrimary
                                    else -> MaterialTheme.colorScheme.onSurface
                                }
                            )
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
    val context = LocalContext.current
    CurrentSession.getInstance()
    val currentLocale = CurrentSession.language

    val dayResources = listOf(
        R.string.monday, R.string.tuesday, R.string.wednesday,
        R.string.thursday, R.string.friday, R.string.saturday, R.string.sunday
    )

    val monthResources = listOf(
        R.string.january, R.string.february, R.string.march, R.string.april,
        R.string.may, R.string.june, R.string.july, R.string.august,
        R.string.september, R.string.october, R.string.november, R.string.december
    )

    Text(
        text = "${getString(context, dayResources[selectedDate.dayOfWeek.ordinal], currentLocale)}, " +
                "${selectedDate.dayOfMonth} ${getString(context, R.string.ofconnector, currentLocale)} " +
                "${getString(context, monthResources[selectedDate.month.ordinal], currentLocale)} " +
                "${selectedDate.year}",
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )

}
