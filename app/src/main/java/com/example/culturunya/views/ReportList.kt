package com.example.culturunya.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.culturunya.viewmodels.ReportViewModel
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme

@Composable
fun ReportsListScreen(
    navController: NavController,
    viewModel: ReportViewModel = viewModel(),
){
    val reports = viewModel.reports.collectAsState()
    val error by viewModel.errorMessage.collectAsState()
    val reportResolved by viewModel.reportResolved.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getReports()
    }
    LaunchedEffect(reportResolved) {
        if (reportResolved) {
            viewModel.getReports()
        }
    }
    //Spacer(modifier = Modifier.padding(5.dp))
    if (reports.value.isEmpty() && error == null) {
        Text("No ratings yet", color = MaterialTheme.colorScheme.secondary, modifier = Modifier.padding(8.dp))
    } else {
        LazyColumn (modifier = Modifier.padding(8.dp).background(Color.White)){
            items(reports.value) { report ->
                if(!report.is_resolved) {
                    ReportBox(
                        report = report,
                        reportViewModel = viewModel
                    )
                }
            }
        }
    }
}

