package com.example.culturunya.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.culturunya.CurrentSession
import com.example.culturunya.R
import com.example.culturunya.dataclasses.reports.Report
import com.example.culturunya.ui.theme.GrisMoltFluix
import com.example.culturunya.ui.theme.Orange
import com.example.culturunya.ui.theme.Purple40
import com.example.culturunya.viewmodels.ReportViewModel

@Composable
fun ReportBox(
    report: Report,
    reportViewModel: ReportViewModel
){
    val error by reportViewModel.errorMessage.collectAsState()
    val context = LocalContext.current
    CurrentSession.getInstance()
    val currentLocale by remember { mutableStateOf(CurrentSession.language) }

    Box (
        modifier = Modifier.fillMaxWidth().padding(4.dp).background(GrisMoltFluix, shape = RoundedCornerShape(16.dp)),
    ){
        Column(
           modifier = Modifier.fillMaxWidth().padding(32.dp),
        ) {
            Text(
                text = getString(context, R.string.Reported, currentLocale) + ": "+ report.reported_user,
                modifier = Modifier.padding(4.dp),
                color = Purple40
            )
            Spacer(modifier = Modifier.padding(5.dp))
            report.reporter?.let{
                Text(
                    text = getString(context, R.string.Reporter, currentLocale) + " " + report.reporter,
                    modifier = Modifier.padding(4.dp),
                    color = Purple40
                )
                Spacer(modifier = Modifier.padding(5.dp))
            }

            report.message?.let {
                Text(
                    text = getString(context, R.string.ReportReason, currentLocale) + ": "+ report.message,
                    modifier = Modifier.padding(4.dp),
                    color = Purple40
                )
                Spacer(modifier = Modifier.padding(5.dp))
            }
            Text(

                text = getString(context, R.string.atDate, currentLocale) + " " + formatDate(report.date),
                modifier = Modifier.padding(4.dp),
                color = Purple40
            )
            Spacer(modifier = Modifier.padding(3.dp))
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Green,
                    contentColor = Color.White
                ),
                onClick = {
                    reportViewModel.resolveReport(report.id.toString(), "NoAction", "NoAction")
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(getString(context, R.string.NoAction, currentLocale))
            }
            Spacer(modifier = Modifier.padding(2.dp))
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Orange,
                    contentColor = Color.White
                ),
                onClick = {
                    reportViewModel.resolveReport(report.id.toString(), "Warning", "Warning")
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(getString(context, R.string.Warning, currentLocale))
            }
            Spacer(modifier = Modifier.padding(2.dp))
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red,
                    contentColor = Color.White
                ),
                onClick = {
                    reportViewModel.resolveReport(report.id.toString(), "Ban", "Banned")
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(getString(context, R.string.Ban, currentLocale))
            }
            error?.let { errorMessage ->
                Spacer(modifier = Modifier.padding(2.dp))
                Text(text = errorMessage, Modifier.padding(4.dp), color = MaterialTheme.colorScheme.error)
            }
        }
    }
}