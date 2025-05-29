package com.example.culturunya.views

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.culturunya.viewmodels.ReportViewModel
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.example.culturunya.session.CurrentSession


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaReport(navController: NavController) {
    val context = LocalContext.current
    CurrentSession.getInstance()
    val currentLocale by remember { mutableStateOf(CurrentSession.language) }
    var imAdmin = CurrentSession.is_admin

    val reportsViewModel = if (imAdmin) {
        viewModel<ReportViewModel>()
    } else {
        null
    }
    reportsViewModel?.let {
        ReportsListScreen(navController, it)
    }
}