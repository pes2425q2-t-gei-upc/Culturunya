package com.example.culturunya.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.culturunya.endpoints.events.Rating
import com.example.culturunya.models.currentSession.CurrentSession
import com.example.culturunya.ui.theme.*
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment

@Composable
fun RatingBox(
    rating: Rating,
    onRatingClick: (Rating) -> Unit
){
    val context = LocalContext.current
    CurrentSession.getInstance()
    val currentLocale by remember { mutableStateOf(CurrentSession.language) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(PurpleGrey80, RoundedCornerShape(8.dp))
    ){
        Column (modifier = Modifier.padding(8.dp)){
            Text(text = rating.user.username + " " + rating.date.toString() ,style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 18.sp,
                color = Purple40
            ),
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp))
            Spacer(modifier = Modifier.padding(8.dp))
            rating.comment?.let { Text(text= it, color = Purple40) }
            Spacer(modifier = Modifier.padding(8.dp))
            Text(text = rating.rating.toString(), color = Purple40)
            Spacer(modifier = Modifier.padding(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ){
                Button(
                    modifier = Modifier.width(200.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red,
                        contentColor = Color.White
                    ),
                    onClick = {
                        // logica reportar
                    }
                ){
                    Text(text = "Reportar", modifier = Modifier.padding(2.dp).background(Color.Red), color= Color.White)
                }
            }


        }

    }
}