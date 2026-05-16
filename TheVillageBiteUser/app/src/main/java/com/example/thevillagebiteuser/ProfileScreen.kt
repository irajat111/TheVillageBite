package com.example.thevillagebiteuser

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest

@Composable
fun ProfileScreen(navController: NavHostController) {

    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    // ── States ────────────────────────────────────────────
    val userEmail = currentUser?.email ?: "No Email Found"
    var displayName by remember { mutableStateOf(currentUser?.displayName ?: "") }

    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // Error states
    var nameError by remember { mutableStateOf("") }
    var currentPassError by remember { mutableStateOf("") }
    var newPassError by remember { mutableStateOf("") }
    var confirmPassError by remember { mutableStateOf("") }



    var passwordVisible by remember { mutableStateOf(false) }


    val greenColor = Color(0xFF4CAF50)
    val cardShape = RoundedCornerShape(14.dp)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF8F2))
            .verticalScroll(rememberScrollState())  // scroll support
            .padding(horizontal = 24.dp)
            .padding(top = 60.dp, bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // ── Avatar Circle ─────────────────────────────────
        Box(
            modifier = Modifier
                .size(90.dp)
                .background(greenColor,CircleShape),
//                .background(colorResource(R.color.cardGreen), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                // Email ka pehla letter capital mein dikhayenge
                text = userEmail.first().uppercaseChar().toString(),
                fontSize = 36.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ── Email Display ─────────────────────────────────
        Text(
            text = userEmail,
            fontSize = 16.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(32.dp))

        // ════════════════════════════════════════════════
        //   SECTION 1 — Update Display Name
        // ════════════════════════════════════════════════
//        Card(
//            modifier = Modifier.fillMaxWidth(),
//            shape = cardShape,
//            colors = CardDefaults.cardColors(containerColor = Color.White),
//            elevation = CardDefaults.cardElevation(2.dp)
//        ) {
//            Column(modifier = Modifier.padding(16.dp)) {
//
//                Text(
//                    text = "Update Name",
//                    fontSize = 18.sp,
//                    fontWeight = FontWeight.Bold
//                )
//
//                Spacer(modifier = Modifier.height(12.dp))
//
//                // Name Field
//                OutlinedTextField(
//                    value = displayName,
//                    onValueChange = {
//                        displayName = it
//                        nameError = ""   // error clear karo jab type kare
//                    },
//                    label = { Text("Display Name") },
//                    leadingIcon = {
//                        Icon(
//                            imageVector = Icons.Outlined.Person,
//                            contentDescription = "Name"
//                        )
//                    },
//                    isError = nameError.isNotEmpty(),
//                    supportingText = {
//                        if (nameError.isNotEmpty()) {
//                            Text(text = nameError, color = Color.Red, fontSize = 12.sp)
//                        }
//                    },
//                    modifier = Modifier.fillMaxWidth(),
//                    shape = cardShape,
//                    singleLine = true,
//                    colors = OutlinedTextFieldDefaults.colors(
//                        focusedBorderColor = greenColor,
//                        unfocusedBorderColor = Color(0xFFDDDDDD),
//                        focusedContainerColor = Color.White,
//                        unfocusedContainerColor = Color.White
//                    )
//                )
//
//                Spacer(modifier = Modifier.height(12.dp))
//
//                // Update Name Button
//                ElevatedButton(
//                    onClick = {
//                        // ── Validation ──────────────────
//                        if (displayName.trim().isEmpty()) {
//                            nameError = "Name cannot be empty"
//                        } else if (displayName.trim().length < 3) {
//                            nameError = "Name must be at least 3 characters"
//                        } else {
//                            // Firebase mein name update karo
//                            val profileUpdate = userProfileChangeRequest {
//                                displayName = displayName.trim()
//                            }
//                            currentUser?.updateProfile(profileUpdate)
//                                ?.addOnCompleteListener { task ->
//                                    if (task.isSuccessful) {
//                                        Toast.makeText(
//                                            context,
//                                            "Name Updated Successfully!",
//                                            Toast.LENGTH_SHORT
//                                        ).show()
//                                    } else {
//                                        Toast.makeText(
//                                            context,
//                                            task.exception?.message ?: "Update Failed",
//                                            Toast.LENGTH_SHORT
//                                        ).show()
//                                    }
//                                }
//                        }
//                    },
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(50.dp),
//                    shape = RoundedCornerShape(7.dp),
//                    colors = ButtonDefaults.elevatedButtonColors(
//                        containerColor = greenColor
//                    )
//                ) {
//                    Text("Update Name", color = Color.White, fontSize = 16.sp)
//                }
//            }
//        }



        Spacer(modifier = Modifier.height(20.dp))

        // ════════════════════════════════════════════════
        //   SECTION 2 — Change Password
        // ════════════════════════════════════════════════
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = cardShape,
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                Text(
                    text = "Change Password",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Current Password
                OutlinedTextField(
                    value = currentPassword,
                    onValueChange = {
                        currentPassword = it
                        currentPassError = ""
                    },
                    label = { Text("Current Password") },

//                    leadingIcon = {
//                        Icon(
//                            imageVector = Icons.Outlined.Lock,
//                            contentDescription = "Current Password"
//                        )
//                    },
//
//                    visualTransformation = PasswordVisualTransformation(),


                    // ✅ NAYA - yeh dono changes kar
                    trailingIcon = {
                        IconButton(
                            onClick = { passwordVisible = !passwordVisible }
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
                    visualTransformation = if (passwordVisible)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),


                    isError = currentPassError.isNotEmpty(),
                    supportingText = {
                        if (currentPassError.isNotEmpty()) {
                            Text(text = currentPassError, color = Color.Red, fontSize = 12.sp)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = cardShape,
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = greenColor,
                        unfocusedBorderColor = Color(0xFFDDDDDD),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )

                )



                Spacer(modifier = Modifier.height(10.dp))

                // New Password
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = {
                        newPassword = it
                        newPassError = ""
                    },
                    label = { Text("New Password") },

//                    leadingIcon = {
//                        Icon(
//                            imageVector = Icons.Outlined.Lock,
//                            contentDescription = "New Password"
//                        )
//                    },
//                    visualTransformation = PasswordVisualTransformation(),

                    // ✅ NAYA - yeh dono changes kar
                    trailingIcon = {
                        IconButton(
                            onClick = { passwordVisible = !passwordVisible }
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
                    visualTransformation = if (passwordVisible)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),

                    isError = newPassError.isNotEmpty(),
                    supportingText = {
                        if (newPassError.isNotEmpty()) {
                            Text(text = newPassError, color = Color.Red, fontSize = 12.sp)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = cardShape,
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = greenColor,
                        unfocusedBorderColor = Color(0xFFDDDDDD),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Confirm New Password
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        confirmPassError = ""
                    },
                    label = { Text("Confirm New Password") },

//                    leadingIcon = {
//                        Icon(
//                            imageVector = Icons.Outlined.Lock,
//                            contentDescription = "Confirm Password"
//                        )
//                    },
//                    visualTransformation = PasswordVisualTransformation(),

                    // ✅ NAYA - yeh dono changes kar
                    trailingIcon = {
                        IconButton(
                            onClick = { passwordVisible = !passwordVisible }
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
                    visualTransformation = if (passwordVisible)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),

                    isError = confirmPassError.isNotEmpty(),
                    supportingText = {
                        if (confirmPassError.isNotEmpty()) {
                            Text(text = confirmPassError, color = Color.Red, fontSize = 12.sp)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = cardShape,
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = greenColor,
                        unfocusedBorderColor = Color(0xFFDDDDDD),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Change Password Button
                ElevatedButton(
                    onClick = {
                        // ── Validations ─────────────────
                        var isValid = true

                        if (currentPassword.isEmpty()) {
                            currentPassError = "Enter your current password"
                            isValid = false
                        }
                        if (newPassword.isEmpty()) {
                            newPassError = "Enter new password"
                            isValid = false
                        } else if (newPassword.length < 6) {
                            newPassError = "Password must be at least 6 characters"
                            isValid = false
                        } else if (newPassword == currentPassword) {
                            newPassError = "New password cannot be same as current"
                            isValid = false
                        }
                        if (confirmPassword.isEmpty()) {
                            confirmPassError = "Please confirm your new password"
                            isValid = false
                        } else if (confirmPassword != newPassword) {
                            confirmPassError = "Passwords do not match"
                            isValid = false
                        }

                        // ── Firebase Re-authenticate & Update ──
                        if (isValid && currentUser != null) {
                            val credential = EmailAuthProvider.getCredential(
                                userEmail,
                                currentPassword
                            )
                            // Pehle re-authenticate karo — Firebase security require karta hai
                            currentUser.reauthenticate(credential)
                                .addOnCompleteListener { reAuthTask ->
                                    if (reAuthTask.isSuccessful) {
                                        currentUser.updatePassword(newPassword)
                                            .addOnCompleteListener { updateTask ->
                                                if (updateTask.isSuccessful) {
                                                    Toast.makeText(
                                                        context,
                                                        "Password Changed Successfully!",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    // Fields clear karo
                                                    currentPassword = ""
                                                    newPassword = ""
                                                    confirmPassword = ""
                                                } else {
                                                    Toast.makeText(
                                                        context,
                                                        updateTask.exception?.message ?: "Failed",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            }
                                    } else {
                                        currentPassError = "Current password is incorrect"
                                    }
                                }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(7.dp),
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = greenColor
                    )
                ) {
                    Text("Change Password", color = Color.White, fontSize = 16.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ── Logout Button ─────────────────────────────────
        OutlinedButton(
            onClick = {
                auth.signOut()
                context.startActivity(Intent(context, LogInActivity::class.java))
                (context as Activity).finish()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color.Red,
                containerColor = colorResource(R.color.cardGreen)
            )
        ) {
            Icon(
                imageVector = Icons.Outlined.ExitToApp,
                contentDescription = "Logout",
                tint = Color.Red
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Logout", color = Color.Red, fontSize = 16.sp)
        }
    }
}