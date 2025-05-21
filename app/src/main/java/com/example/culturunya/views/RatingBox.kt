package com.example.culturunya.views

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.culturunya.R
import com.example.culturunya.dataclasses.ratings.Rating
import com.example.culturunya.CurrentSession
import com.example.culturunya.ui.theme.*
import com.google.android.gms.maps.model.Circle

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
                        // logica reportar
                    }
                ){
                    Icon(
                            painter = painterResource(id = R.drawable.ic_report),
                            contentDescription = stringResource(id = R.string.Report),
                            tint = Color.White,
                            modifier = Modifier.size(25.dp)
                    )
                }
            }
            //Spacer(modifier = Modifier.padding(8.dp))
            Text(text = getString(context, R.string.Rating, currentLocale) + ": " + rating.rating.toString(), color = Purple40)
            Spacer(modifier = Modifier.padding(8.dp))
            rating.comment?.let { Text(text= it, color = Purple40, fontSize = 16.sp)  }



        }

    }
}