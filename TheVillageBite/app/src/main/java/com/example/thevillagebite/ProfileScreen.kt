package com.example.thevillagebite

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.auth

class ProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProfileScreen(onLogout = {})
        }
    }
}

@Composable
@Preview(showSystemUi = true)
fun ProfileScreen(
    modifier: Modifier = Modifier,
    onLogout: () -> Unit = {}
) {
    val context     = LocalContext.current
    val auth        = Firebase.auth
    val currentUser = auth.currentUser
    val adminEmail  = currentUser?.email ?: "Not Logged In"

    var currentPassword by remember { mutableStateOf("") }
    var newPassword     by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    LazyColumn(
        modifier = modifier.fillMaxSize().padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(Modifier.height(20.dp))

            Icon(
                imageVector = Icons.Filled.AccountCircle,
                contentDescription = "Admin",
                modifier = Modifier.size(100.dp),
                tint = Color(0xFF388E3C)
            )
            Spacer(Modifier.height(12.dp))
            Text("Admin Profile", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))

            // Email Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Email", fontSize = 13.sp, color = Color.Gray)
                    Spacer(Modifier.height(4.dp))
                    Text(adminEmail, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(Modifier.height(16.dp))

            Text(
                "Change Password", fontSize = 18.sp, fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = currentPassword, onValueChange = { currentPassword = it },
                label = { Text("Current Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = newPassword, onValueChange = { newPassword = it },
                label = { Text("New Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = confirmPassword, onValueChange = { confirmPassword = it },
                label = { Text("Confirm New Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))

            ElevatedButton(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                onClick = {
                    when {
                        currentPassword.isEmpty() -> {
                            Toast.makeText(context, "Enter current password", Toast.LENGTH_SHORT).show()
                        }
                        newPassword.isEmpty() -> {
                            Toast.makeText(context, "Enter new password", Toast.LENGTH_SHORT).show()
                        }
                        newPassword != confirmPassword -> {
                            Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                        }
                        newPassword.length < 6 -> {
                            Toast.makeText(context, "Min 6 characters required", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            val credential = EmailAuthProvider.getCredential(adminEmail, currentPassword)
                            currentUser?.reauthenticate(credential)?.addOnCompleteListener { reAuth ->
                                if (reAuth.isSuccessful) {
                                    currentUser.updatePassword(newPassword).addOnCompleteListener { update ->
                                        if (update.isSuccessful) {
                                            Toast.makeText(context, "Password updated!", Toast.LENGTH_SHORT).show()
                                            currentPassword = ""; newPassword = ""; confirmPassword = ""
                                        } else {
                                            Toast.makeText(context, update.exception?.message, Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                } else {
                                    Toast.makeText(context, "Current password incorrect", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                }
            ) { Text("Update Password") }

            Spacer(Modifier.height(20.dp))
            HorizontalDivider()
            Spacer(Modifier.height(16.dp))

            OutlinedButton(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                onClick = {
                    auth.signOut()
                    Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show()
                    onLogout() // ✅ NavController login pe le jaayega
                },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
            ) { Text("Logout", color = Color.Red) }

            Spacer(Modifier.height(20.dp))
        }
    }
}







