package com.example.thevillagebiteuser

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.delay


class SplashActivity: ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SplashScreenuser()
        }
    }
}

@Composable
fun SplashScreenuser() {
    val auth  = Firebase.auth

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        delay(3000)
        if(auth.currentUser?.uid!=null){
            context.startActivity(Intent(context, DashBoardActivity::class.java))
            (context as Activity).finish()
        }else{
            context.startActivity(Intent(context, LogInActivity::class.java))
            (context as Activity).finish()
        }
   }

    Box(
        modifier = Modifier
            .fillMaxSize()
//            .background(Color.White),
            .background(Color(0xFFFFF8F2)),
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
//                color = colorResource(androidx.compose.runtime.R.color.primaryGreen),
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