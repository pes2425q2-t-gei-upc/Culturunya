package com.example.culturunya.views

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.culturunya.ui.theme.Purple40
import com.example.culturunya.viewmodels.ReportViewModel

@Composable
fun ReportsListScreen(
    navController: NavController,
    viewModel: ReportViewModel,
){
    val reports = viewModel.reports.collectAsState()
    val error by viewModel.errorMessage.collectAsState()

    error?.let { errorMessage ->
        Text(text = errorMessage, Modifier.padding(4.dp), color = Color.Red)
    }
    //Spacer(modifier = Modifier.padding(5.dp))
    if (reports.value.isEmpty() && error == null) {
        Text("No ratings yet", color = Purple40, modifier = Modifier.padding(8.dp))
    } else {
        reports.value.forEach { report ->
            ReportBox(report = report)
        }
    }
}