package com.example.thevillagebite

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
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LoginScreenUI(onLoginSuccess = {
                // Navigate to your main admin screen
            })
        }
    }
}

@Composable
fun LoginScreenUI(onLoginSuccess: () -> Unit) {
    val context = LocalContext.current
    val auth    = Firebase.auth

    var email    by remember { mutableStateOf("admin@gmail.com") }
    var password by remember { mutableStateOf("Admin@123") }
    var passwordVisible by remember { mutableStateOf(false) }
    var emailError      by remember { mutableStateOf("") }
    var passwordError   by remember { mutableStateOf("") }
    var isLoading       by remember { mutableStateOf(false) }

    val greenColor = Color(0xFF4CAF50)
    val cardShape  = RoundedCornerShape(14.dp)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF8F2))
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Image(
            painter            = painterResource(id = R.drawable.logo),
            contentDescription = "App Logo",
            modifier           = Modifier.size(160.dp)
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text       = "Welcome Back",
            fontSize   = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color      = Color(0xFF1B5E20)
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text       = "Login to your Admin Account",
            fontSize   = 14.sp,
            fontWeight = FontWeight.Medium,
            color      = Color.Black
        )

        Spacer(Modifier.height(32.dp))

        OutlinedTextField(
            value         = email,
            onValueChange = { email = it; emailError = "" },
            label         = { Text("Enter Email") },
            placeholder   = { Text("admin@example.com") },
            leadingIcon   = { Icon(Icons.Outlined.Email, contentDescription = "Email Icon") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError        = emailError.isNotEmpty(),
            supportingText = {
                if (emailError.isNotEmpty()) Text(emailError, color = Color.Red, fontSize = 12.sp)
            },
            modifier   = Modifier.fillMaxWidth(),
            shape      = cardShape,
            singleLine = true,
            colors     = OutlinedTextFieldDefaults.colors(
                focusedBorderColor      = greenColor,
                unfocusedBorderColor    = Color(0xFFDDDDDD),
                focusedContainerColor   = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value         = password,
            onValueChange = { password = it; passwordError = "" },
            label         = { Text("Password") },
            leadingIcon   = { Icon(Icons.Outlined.Lock, contentDescription = "Password Icon") },
            trailingIcon  = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector        = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = if (passwordVisible) "Hide" else "Show"
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError        = passwordError.isNotEmpty(),
            supportingText = {
                if (passwordError.isNotEmpty()) Text(passwordError, color = Color.Red, fontSize = 12.sp)
            },
            modifier   = Modifier.fillMaxWidth(),
            shape      = cardShape,
            singleLine = true,
            colors     = OutlinedTextFieldDefaults.colors(
                focusedBorderColor      = greenColor,
                unfocusedBorderColor    = Color(0xFFDDDDDD),
                focusedContainerColor   = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        Spacer(Modifier.height(24.dp))

        ElevatedButton(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape   = RoundedCornerShape(10.dp),
            enabled = !isLoading,
            onClick = {
                var isValid = true

                if (email.trim().isEmpty()) {
                    emailError = "Email cannot be empty"; isValid = false
                } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
                    emailError = "Enter a valid email address"; isValid = false
                }
                if (password.isEmpty()) {
                    passwordError = "Password cannot be empty"; isValid = false
                } else if (password.length < 6) {
                    passwordError = "Password must be at least 6 characters"; isValid = false
                }

                if (isValid) {
                    isLoading = true
                    auth.signInWithEmailAndPassword(email.trim(), password)
                        .addOnCompleteListener {
                            isLoading = false
                            if (it.isSuccessful) {
                                Log.i("AdminLogin", "Login success: ${email.trim()}")
                                Toast.makeText(context, "Login Successful!", Toast.LENGTH_SHORT).show()

                                // ✅ LOGIN NOTIFICATION + TOKEN SAVE
                                AdminNotificationHelper.sendAdminLoginNotification(context)
                                AdminNotificationHelper.refreshAndSaveAdminFCMToken()

                                onLoginSuccess()
                            } else {
                                val errorMsg = when {
                                    it.exception?.message?.contains("password") == true ->
                                        "Incorrect password. Please try again."
                                    it.exception?.message?.contains("no user") == true ||
                                            it.exception?.message?.contains("identifier") == true ->
                                        "No account found with this email."
                                    it.exception?.message?.contains("network") == true ->
                                        "Network error. Check your connection."
                                    it.exception?.message?.contains("blocked") == true ->
                                        "Too many attempts. Try again later."
                                    else ->
                                        it.exception?.message ?: "Login failed. Try again."
                                }
                                Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                            }
                        }
                }
            },
            colors = ButtonDefaults.elevatedButtonColors(
                containerColor         = greenColor,
                disabledContainerColor = Color(0xFFA5D6A7)
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color       = Color.White,
                    modifier    = Modifier.size(22.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text("Login", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}