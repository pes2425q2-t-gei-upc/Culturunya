package com.example.culturunya.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.culturunya.viewmodels.ReportViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaReport(navController: NavController, userId: Int?, username: String?, imageUrl: String?) {
    var imAdmin = false
    if (userId != -1 && username != null) imAdmin = true

    val reportsViewModel = if (imAdmin) {
        viewModel<ReportViewModel>()
    } else {
        null
    }
    reportsViewModel?.let {
        Column {
            ReportsListScreen(navController, it)
        }
    }
}