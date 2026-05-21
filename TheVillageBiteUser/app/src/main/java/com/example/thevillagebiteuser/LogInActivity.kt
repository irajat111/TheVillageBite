package com.example.thevillagebiteuser


// LoginScreen.kt
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility      // ✅ add
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Login
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
//import androidx.lint.kotlin.metadata.Visibility
//import androidx.navigation.NavHostController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth



class LogInActivity : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LoginScreen()
        }
    }
}

@Composable
fun LoginScreen() {

    val auth  = Firebase.auth
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // this var is created for traivilijng eye icon
    var passwordVisible by remember{ mutableStateOf(false)}

    val greenColor = Color(0xFF4CAF50)
    val textGreen = Color(0xFF2E7D32)
//    val grayText = Color(0xFF888888)


    // Custom fonts — make sure these are in res/font/


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF8F2)),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 140.dp)
        ) {

//            Spacer(Modifier.height(100.dp))
            // ── Logo ──────────────────────────────────────────
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "App Logo",
                modifier = Modifier.size(100.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // ── App Name ──────────────────────────────────────
            Text(
                text = "The Village Bite",
                fontSize = 30.sp,
                fontFamily = FontObj.cause,
                fontWeight = FontWeight.Bold,  // here i apply font style
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            // ── Tagline ───────────────────────────────────────
            Text(
                text = "Delivery Food at Home",
                fontSize = 18.sp,
//                color = grayText,
                fontFamily =  FontObj.cause,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(11.dp))

            // ── Subtitle ──────────────────────────────────────
            Text(
                text = "Login to Your Account",
                fontSize = 16.sp,
//                color = grayText,
                fontFamily =  FontObj.cause,
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ── Email Field ───────────────────────────────────
            val cardShape = RoundedCornerShape(14.dp)
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = {
                    Text("Email", fontFamily =  FontObj.cause,)
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Email,
                        contentDescription = "Email Icon",
//                        tint = grayText
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(57.dp),
                shape = cardShape,
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    focusedBorderColor = greenColor,
                    unfocusedBorderColor = Color(0xFFDDDDDD)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ── Password Field ────────────────────────────────
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Enter Password", fontFamily = FontObj.cause) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Lock,
                        contentDescription = "Password Icon"
                    )
                },

                // ✅ CHANGE 1 — Eye icon trailingIcon mein add kiya
                trailingIcon = {
                    val icon = if (passwordVisible) {
                        Icons.Filled.Visibility        // Eye open — password dikh raha hai
                    } else {
                        Icons.Filled.VisibilityOff     // Eye closed — password chupa hua hai
                    }
                    IconButton(onClick = {
                        passwordVisible = !passwordVisible   // toggle karo
                    }) {
                        Icon(
                            imageVector = icon,
                            contentDescription = if (passwordVisible) "Hide Password" else "Show Password"
                        )
                    }
                },

                // ✅ CHANGE 2 — visualTransformation state se control hogi
                visualTransformation = if (passwordVisible) {
                    VisualTransformation.None            // password dikhao
                } else {
                    PasswordVisualTransformation()       // password chupao ••••
                },

                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(57.dp),
                shape = cardShape,
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    focusedBorderColor = greenColor,
                    unfocusedBorderColor = Color(0xFFDDDDDD)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ── Or Divider ────────────────────────────────────
            Text(text = "Or",
//                color = grayText,
                fontSize = 13.sp)

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Continue With",
                fontSize = 22.sp,
//                color = textGreen,
                fontFamily =  FontObj.cause,
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ── Social Buttons Row ────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Google
                OutlinedButton(
                    onClick = {},
                    modifier = Modifier
                        .weight(1f)
                        .height(57.dp),
                    shape = cardShape,
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White
                    )
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.google),
                        contentDescription = "Google",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Google",
                        color = Color.DarkGray,
                        fontFamily =  FontObj.cause,
                    )
                }

                // Facebook
                OutlinedButton(
                    onClick = {},
                    modifier = Modifier
                        .weight(1f)
                        .height(57.dp),
                    shape = cardShape,
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White
                    )
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.facebook),
                        contentDescription = "Facebook",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Facebook",
                        color = Color.DarkGray,
                        fontFamily =  FontObj.cause,
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Login Button ──────────────────────────────────
            ElevatedButton(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp),
                shape = RoundedCornerShape(7.dp),
                onClick = {
                    if (email.isEmpty()) {
                        Toast.makeText(context, "Enter Email", Toast.LENGTH_SHORT).show()
                    }
                    else if (!email.endsWith("@gmail.com")) {
                        Toast.makeText(context, "Enter Gmail with @gmail.com", Toast.LENGTH_SHORT).show()
                    }
                    else if (password.isEmpty()) {
                        Toast.makeText(context, "Enter Password", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    Toast.makeText(context, "Login Successfully", Toast.LENGTH_SHORT).show()
                                     // ✅ NavController navigate karega
                                    context.startActivity(Intent(context, DashBoardActivity::class.java))
                                    (context as Activity).finish()

                                } else {
                                    Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
                                }
                            }
                        Log.i("Credential", "Email : $email Password : $password")
                    }
//                        navController.navigate("Home")
                },
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = colorResource(R.color.primaryGreen)
                )

            ) {
                Icon(
                    imageVector = Icons.Outlined.Login,
                    contentDescription = "Logout",
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("login", fontSize = 18.sp ,color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Don't Have Account ────────────────────────────
            TextButton(onClick = {
              context.startActivity(Intent(context, SignUpActivity::class.java))
                (context as Activity).finish()
            }) {
                Text(
                    text = "Don't Have Account?",
//                    color = grayText,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // ── Footer ────────────────────────────────────────
            Text(
                text = "Design By\nRajat Singh",
                fontSize = 16.sp,
//                color = grayText,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp,
                modifier = Modifier.padding(bottom = 32.dp)
            )
        }
    }
}