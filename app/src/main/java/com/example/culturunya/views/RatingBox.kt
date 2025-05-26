package com.example.culturunya.views

import android.widget.Toast
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.example.culturunya.R
import com.example.culturunya.dataclasses.ratings.Rating
import com.example.culturunya.session.CurrentSession
import com.example.culturunya.dataclasses.reports.ReportRequest
import com.example.culturunya.ui.theme.*
import com.example.culturunya.viewmodels.ReportViewModel
import com.google.android.gms.maps.model.Circle


@kotlin.OptIn(ExperimentalMaterial3Api::class)
@OptIn(UnstableApi::class)
@Composable
fun RatingBox(
    rating: Rating,
    onRatingClick: (Rating) -> Unit,
    reportViewModel: ReportViewModel = viewModel()
){
    val context = LocalContext.current
    CurrentSession.getInstance()
    val currentLocale by remember { mutableStateOf(CurrentSession.language) }
    var reportStatus by remember { mutableStateOf(false)}
    var report_new by remember { mutableStateOf("") }

    Log.d("RatingBox", "RatingBox recomposing. Initializing.")

    val isLoading by reportViewModel.isLoading.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(GrisMoltFluix, RoundedCornerShape(8.dp))
    ){
        Column (modifier = Modifier.padding(8.dp)){
            Row(
                modifier = Modifier.fillMaxWidth(),
            ){
                Text(text = rating.user.username + " " + getString(context, R.string.atDate, currentLocale) +" "+ rating.date.toString() + " " + getString(context, R.string.saysComment, currentLocale),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 12.sp,
                        color = Purple40
                    ), modifier = Modifier.padding(top = 8.dp, bottom = 4.dp))
                Spacer(Modifier.weight(1f))
                if(rating.user.username != CurrentSession.username){
                    IconButton(
                        modifier = Modifier
                            .size(35.dp)
                            .background(Color.Red, CircleShape)
                            .clip(CircleShape)
                        ,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color.White
                        ),
                        onClick = {
                            Log.d("RatingBox", "ROW INSIDE RatingBox CLICKED!")
                            reportStatus = !reportStatus
                        },
                        enabled = !isLoading
                    ){
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_report),
                                contentDescription = stringResource(id = R.string.Report),
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
            //Spacer(modifier = Modifier.padding(8.dp))
            Text(text = getString(context, R.string.Rating, currentLocale) + ": " + rating.rating.toString(), color = Purple40)
            Spacer(modifier = Modifier.padding(8.dp))
            rating.comment?.let { Text(text= it, color = Purple40, fontSize = 16.sp)  }
            Spacer(modifier = Modifier.padding(8.dp))
            if(reportStatus){
                OutlinedTextField(
                    value = report_new,
                    onValueChange = { report_new = it },
                    label = { Text(getString(context, R.string.Report, currentLocale)) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                    ,
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Person, contentDescription = "Persona")
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        textColor = Color.Black,
                        cursorColor = Color.Black,
                        focusedBorderColor = Color.Gray,
                        unfocusedBorderColor = Color.LightGray
                    )
                )
                //Spacer(modifier = Modifier.padding(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center
                ){
                    Button(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red,
                            contentColor = Color.White
                        ),
                        onClick = {
                            val reportRequest = ReportRequest(message = report_new, rating_id = rating.id.toInt())
                            reportViewModel.reportRating(reportRequest)
                            reportStatus = false
                        },
                        modifier = Modifier.width(200.dp),
                    ) {
                        Text(getString(context, R.string.Report, currentLocale))
                    }
                }
            }
        }

    }
}