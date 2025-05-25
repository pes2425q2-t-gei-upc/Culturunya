package com.example.culturunya.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.culturunya.CurrentSession
import com.example.culturunya.R
import com.example.culturunya.dataclasses.ratings.Report
import com.example.culturunya.views.PantallaXat
import okhttp3.internal.format
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@Composable
fun ReportBox(
    report: Report
){

    val context = LocalContext.current
    CurrentSession.getInstance()
    val currentLocale by remember { mutableStateOf(CurrentSession.language) }

    Box (
        modifier = Modifier.fillMaxWidth().padding(4.dp),
    ){
        Column(
           modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = report.reported_user,
                modifier = Modifier.padding(4.dp),
            )
            Spacer(modifier = Modifier.padding(5.dp))
            Text(
                text = getString(context, R.string.Reporter, currentLocale) + report.reporter,
                modifier = Modifier.padding(4.dp),
            )
            Spacer(modifier = Modifier.padding(5.dp))
            report.message?.let {
                Text(
                    text = getString(context, R.string.ReportReason, currentLocale) + report.message,
                    modifier = Modifier.padding(4.dp),
                )
            }
            Spacer(modifier = Modifier.padding(5.dp))
            Text(

                text = getString(context, R.string.atDate, currentLocale) + formatDate(report.date),
                modifier = Modifier.padding(4.dp),
            )
        }
    }
}