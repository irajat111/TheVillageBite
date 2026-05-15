package com.example.thevillagebiteuser

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import com.google.firebase.Firebase
import com.google.firebase.auth.auth



@Composable
fun SignUpScreen(navController: androidx.navigation.NavController) {

    val context = LocalContext.current
    val auth = Firebase.auth
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }



    val greenColor = Color(0xFF4CAF50)
    val textGreen = Color(0xFF2E7D32)
    val cardShape = RoundedCornerShape(14.dp)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF8F2)) // warm background like your drawable
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.TopCenter

//            .fillMaxSize()
//            .background(Color.White),
//        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 120.dp)
        )
        {

            // ── Logo ──────────────────────────────────────────
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "App Logo",
                modifier = Modifier.size(100.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // ── App Name ──────────────────────────────────────
            Text(
                text = "The Village Bite",
                fontSize = 30.sp,
                color = textGreen,
                fontFamily = FontFamily.Serif // replace with your yeon_sung font
            )

            Text(
                text = "Delivery Favourite Food",
                fontSize = 14.sp,
                color = Color.Black,
                fontFamily = FontFamily.Serif
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Sign Up Here",
                fontSize = 20.sp,
                color = textGreen,
                fontFamily = FontFamily.Serif
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ── Name Field ────────────────────────────────────
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                leadingIcon = {
                    Icon(Icons.Outlined.Person, contentDescription = "Name Icon")
                },
                label = {Text("Enter Name")},
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

            Spacer(modifier = Modifier.height(12.dp))

            // ── Email Field ───────────────────────────────────
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                leadingIcon = {
                    Icon(Icons.Outlined.Email, contentDescription = "Email Icon")
                },
                label = {Text("Enter Email")},
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

            Spacer(modifier = Modifier.height(12.dp))

            // ── Password Field ────────────────────────────────
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                leadingIcon = {
                    Icon(Icons.Outlined.Lock, contentDescription = "Password Icon")
                },
                label = {Text("Enter password")},
                visualTransformation = PasswordVisualTransformation(),
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

            // ── Divider ───────────────────────────────────────
            Text(text = "Or", color = Color.Black, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Sign Up With",
                fontSize = 18.sp,
                color = textGreen,
                fontFamily = FontFamily.Serif
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ── Social Buttons Row ────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Google Button
                OutlinedButton(
                    onClick = { /* Google Sign In */ },
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
                    Text("Google", color = Color.DarkGray)
                }

                // Facebook Button
                OutlinedButton(
                    onClick = { /* Facebook Sign In */ },
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
                    Text("Facebook", color = Color.DarkGray)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Create Account Button (Gradient) ──────────────
//            Button(
//                onClick = { navController.navigate("login")  },
//                modifier = Modifier.fillMaxWidth(),
//                shape = cardShape,
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = greenColor
//                )
//            ) {
//                Text(
//                    text = "Create Account",
//                    fontSize = 18.sp,
//                    color = Color.White,
//                    fontFamily = FontFamily.Serif,
//                    textAlign = TextAlign.Center
//                )


            ElevatedButton(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp),
                shape = RoundedCornerShape(7.dp),
                onClick = {
                    if (name.isEmpty()){
                        Toast.makeText(context,"Enter Name",Toast.LENGTH_SHORT).show()
                    } else if (email.isEmpty()) {
                        Toast.makeText(context, "Enter Email", Toast.LENGTH_SHORT).show()
                    } else if (password.isEmpty()) {
                        Toast.makeText(context, "Enter Password", Toast.LENGTH_SHORT).show()
                    }else {
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    Toast.makeText(context, "Login Successfully", Toast.LENGTH_SHORT).show()
                                    // ✅ NavController navigate karega
                                } else {
                                    Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
                                }
                            }
                        Log.i("Credential", "Email : $email Password : $password")
                    }
                },
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = colorResource(R.color.primaryGreen)
                )
            ){
//                text = "Create Account",
//                    fontSize = 18.sp,
//                    color = Color.White,
//                    fontFamily = FontFamily.Serif,
//                    textAlign = TextAlign.Center
                Text(
                    text ="SignUp"
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ── Already Have Account ──────────────────────────
            Text(
                text = "Already Have an Account?",
                color = Color.Black,
                fontSize = 13.sp,
                modifier = Modifier
                    .clickable {
                        navController.navigate("login")  // ✅ login route pe navigate
                    }
                    .padding(4.dp)
            )
        }
    }
}
