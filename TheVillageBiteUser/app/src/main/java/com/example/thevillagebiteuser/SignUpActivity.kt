package com.example.thevillagebiteuser

import android.os.Bundle
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
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.VisualTransformation
import com.google.firebase.Firebase
import com.google.firebase.auth.auth


class SignUpActivity : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

        }
    }
}

@Composable
fun SignUpScreen(navController: androidx.navigation.NavController) {

    val context = LocalContext.current
    val auth = Firebase.auth
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
//    var password by remember { mutableStateOf("") }

    // this var is created for traivilijng eye icon
//    var passwordVisible by remember{ mutableStateOf(false)}
//    var passwordError by remember { mutableStateOf("") }



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

            // ── Password Field ───────────────────────────────────
//            OutlinedTextField(
//                value = password,
//                onValueChange = {
//                    password = it
//                    // ✅ Real-time validation — type karte waqt error clear hoga
//                    var passwordError = when {
//                        it.isEmpty() -> "Password cannot be empty"
//                        it.length < 6 -> "Minimum 6 characters required"
//                        !it.any { c -> c.isUpperCase() } -> "At least 1 uppercase letter required"
//                        !it.any { c -> c.isDigit() } -> "At least 1 number required"
//                        else -> ""   // sab sahi hai — error clear karo
//                    }
//                },
//                label = { Text("Enter Password") },
//
//                // ── Leading Icon — Lock ──────────────────────────
//                leadingIcon = {
//                    Icon(
//                        imageVector = Icons.Outlined.Lock,
//                        contentDescription = "Password Icon"
//                    )
//                },
//
//                // ── Trailing Icon — Eye Toggle ───────────────────
//                trailingIcon = {
//                    val icon = if (passwordVisible) {
//                        Icons.Filled.Visibility
//                    } else {
//                        Icons.Filled.VisibilityOff
//                    }
//                    IconButton(onClick = {
//                        passwordVisible = !passwordVisible
//                    }) {
//                        Icon(
//                            imageVector = icon,
//                            contentDescription = if (passwordVisible) "Hide Password" else "Show Password"
//                        )
//                    }
//                },
//
//                // ── Show/Hide Password ───────────────────────────
//                visualTransformation = if (passwordVisible) {
//                    VisualTransformation.None
//                } else {
//                    PasswordVisualTransformation()
//                },
//
//                // ── Error State ──────────────────────────────────
//                isError = passwordError.isNotEmpty(),
//                supportingText = {
//                    if (passwordError.isNotEmpty()) {
//                        Text(
//                            text = passwordError,
//                            color = Color.Red,
//                            fontSize = 12.sp
//                        )
//                    }
//                },
//
//                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(57.dp),
//                shape = cardShape,
//                singleLine = true,
//                colors = OutlinedTextFieldDefaults.colors(
//                    unfocusedContainerColor = Color.White,
//                    focusedContainerColor = Color.White,
//                    focusedBorderColor = greenColor,
//                    unfocusedBorderColor = Color(0xFFDDDDDD),
//                    errorBorderColor = Color.Red,           // ✅ error mein border red hoga
//                    errorContainerColor = Color.White
//                )
//            )

                // State Variables
            var password by remember { mutableStateOf("") }
            var passwordVisible by remember { mutableStateOf(false) }
            var passwordError by remember { mutableStateOf("") }

            OutlinedTextField(
                value = password,

                onValueChange = {
                    password = it

                    passwordError = when {
                        it.isEmpty() -> "Password cannot be empty"
                        it.length < 6 -> "Minimum 6 characters required"
                        !it.any { c -> c.isUpperCase() } ->
                            "At least 1 uppercase letter required"
                        !it.any { c -> c.isDigit() } ->
                            "At least 1 number required"
                        else -> ""
                    }
                },

                label = {
                    Text("Enter Password")
                },

                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Lock,
                        contentDescription = "Password Icon"
                    )
                },

                trailingIcon = {
                    IconButton(
                        onClick = {
                            passwordVisible = !passwordVisible
                        }
                    ) {
                        Icon(
                            imageVector = if (passwordVisible)
                                Icons.Filled.Visibility
                            else
                                Icons.Filled.VisibilityOff,
                            contentDescription = if (passwordVisible)
                                "Hide Password"
                            else
                                "Show Password"
                        )
                    }
                },

                // Password hide/show logic
                visualTransformation =
                    if (passwordVisible)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),

                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                ),

                isError = passwordError.isNotEmpty(),

                supportingText = {
                    if (passwordError.isNotEmpty()) {
                        Text(
                            text = passwordError,
                            color = Color.Red,
                            fontSize = 12.sp
                        )
                    }
                },

                // IMPORTANT: height remove kar diya
                modifier = Modifier
                    .fillMaxWidth(),

                singleLine = true,

                shape = RoundedCornerShape(12.dp),

                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    cursorColor = Color.Black,

                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,

                    focusedBorderColor = Color.Green,
                    unfocusedBorderColor = Color.LightGray,

                    errorBorderColor = Color.Green,
                    errorContainerColor = Color.White
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 5.dp),
                shape = RoundedCornerShape(7.dp),
                onClick = {

                    // ── Step 1: Name Validation ──────────────────
                    if (name.isEmpty()) {
                        Toast.makeText(context, "Enter Name", Toast.LENGTH_SHORT).show()
                    }
                    // ── Step 2: Email Validation ─────────────────
                    else if (email.isEmpty()) {
                        Toast.makeText(context, "Enter Email", Toast.LENGTH_SHORT).show()
                    }
                    else if (!email.endsWith("@gmail.com")) {
                        Toast.makeText(context, "Enter with @gmail.com", Toast.LENGTH_SHORT).show()
                    }
                    // ── Step 3: Password Validations ─────────────
                    else if (password.isEmpty()) {
                        passwordError = "Password cannot be empty"
                    }
                    else if (password.length < 6) {
                        passwordError = "Minimum 6 characters required"
                    }
                    else if (!password.any { it.isUpperCase() }) {
                        passwordError = "At least 1 uppercase letter required"
                    }
                    else if (!password.any { it.isDigit() }) {
                        passwordError = "At least 1 number required"
                    }
                    // ── Step 4: Sab Sahi — Firebase SignUp ───────
                    else {
                        passwordError = ""   // error clear karo
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    Toast.makeText(
                                        context,
                                        "SignUp Successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    // ✅ yahan navigate karo signup ke baad
                                } else {
                                    Toast.makeText(
                                        context,
                                        it.exception?.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        Log.i("Credential", "Email : $email Password : $password")
                    }
                },
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = colorResource(R.color.primaryGreen)
                )
            ) {
                Icon(
                    imageVector = Icons.Outlined.Login,
                    contentDescription = "Logout",
                    tint = Color.Red
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Signup",
                    fontSize = 18.sp,
                    color = Color.White
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
