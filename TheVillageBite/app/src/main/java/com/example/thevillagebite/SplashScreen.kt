package com.example.thevillagebite

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.delay

@Composable
fun SplashScreenUI(onFinish: () -> Unit = {}) {
     val auth  = Firebase.auth

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        delay(3000)
        onFinish()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 45.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Text(
                text = "Design By \nRajat Singh",
                color = colorResource(R.color.primaryGreen),
                fontSize = 16.sp,
//                color = Color.Green,
                textAlign = TextAlign.Center,
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 50.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "The Village Bite Logo",
                modifier = Modifier.wrapContentSize()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "The Village Bite",
                fontSize = 40.sp,
                color = Color.Black,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Delivery Favourite Food",
                fontSize = 20.sp,
                color = Color.Green,
            )
        }
    }
}