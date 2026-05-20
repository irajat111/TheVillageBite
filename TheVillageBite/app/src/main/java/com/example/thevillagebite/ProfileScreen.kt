package com.example.thevillagebite

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import coil3.compose.AsyncImage
import com.google.firebase.Firebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

@Composable
fun ProfileScreen(
    onLogout: () -> Unit = {}
) {
    val context     = LocalContext.current
    val auth = Firebase.auth
    val currentUser = auth.currentUser
    val adminEmail  = currentUser?.email ?: "Not Logged In"


    // supabas ojbect
    val supabase = createSupabaseClient(
        supabaseUrl = SupabaseObject.supaBaseUrl,
        supabaseKey = SupabaseObject.supaBasekey
    ) {
        install(Storage)
    }
    val db = Firebase.firestore
    // profile image and variables for pick images
    var isUploading by remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            imageUri = uri // ✅ Gallery se image turant dikhegi
            CoroutineScope(Dispatchers.IO).launch {
                try {

                    val fileName = "${System.currentTimeMillis()}.jpg"
                    val inputStream = context.contentResolver.openInputStream(imageUri!!)
                    val bytes = inputStream?.readBytes()
                    val bucket = supabase.storage.from("village_bite")
                    bucket.upload(path = fileName, data = bytes!!)
                   val imageUrl = bucket.publicUrl(fileName)
                    println("Check Image Url: $imageUrl")
                    val hashMap = mapOf<String, String>(
                        "name" to "Admin",
                        "email" to "admin@gmail.com",
                        "adminProfileImage" to imageUrl
                    )
                    db.collection("Admin").document(auth.currentUser?.uid.toString()).set(hashMap)
                        .addOnCompleteListener {
                            if(it.isSuccessful){
                                Toast.makeText(context,"Image Uploaded Successfully", Toast.LENGTH_SHORT).show()
                            }else{
                                Toast.makeText(context,it.exception?.message, Toast.LENGTH_SHORT).show()
                            }
                        }

                } catch (e: Exception) {
                    println("Check Exception of Image: ${e.message}")
                } finally {

                }
            }

        }
    }
    var imageUrl by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        db.collection("Admin").document(auth.currentUser?.uid.toString()).get().addOnCompleteListener {
            if(it.isSuccessful){
                imageUrl = it.result.get("adminProfileImage").toString()
            }
        }
    }


    // ── Password field states ──────────────────────────────
    var currentPassword    by remember { mutableStateOf("") }
    var newPassword        by remember { mutableStateOf("") }
    var confirmPassword    by remember { mutableStateOf("") }

    // ── Separate eye-icon visibility per field ─────────────
    var currentPassVisible by remember { mutableStateOf(false) }
    var newPassVisible     by remember { mutableStateOf(false) }
    var confirmPassVisible by remember { mutableStateOf(false) }

    // ── Error states ───────────────────────────────────────
    var currentPassError   by remember { mutableStateOf("") }
    var newPassError       by remember { mutableStateOf("") }
    var confirmPassError   by remember { mutableStateOf("") }

    // ── Design tokens ──────────────────────────────────────
    val greenColor  = Color(0xFF4CAF50)
    val cardShape   = RoundedCornerShape(14.dp)


    Box(Modifier.fillMaxSize()
          .background(colorResource(R.color.white))) {
        Column(
            modifier = Modifier.fillMaxSize()
                .background(colorResource(R.color.white))
                .verticalScroll(rememberScrollState())
        ) {

            Column(
                modifier = Modifier.fillMaxSize()

                    .background(colorResource(R.color.white))
                    .padding(horizontal = 24.dp)
                    .padding(top = 60.dp, bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // ── Avatar Circle ──────────────────────────────────
//                Box(
//                    modifier = Modifier
//                        .size(90.dp)
//                        .background(greenColor, CircleShape),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Text(
//                        text = adminEmail.first().uppercaseChar().toString(),
//                        fontSize = 36.sp,
//                        color = Color.White,
//                        fontWeight = FontWeight.Bold
//                    )
//                }


                // ✅ PURANA hatao — NAYA lagao
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(greenColor, CircleShape)
                        .clickable {
                            imagePicker.launch("image/*")   // 👈 Click pe gallery open
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (imageUri!=null) {
                        // ✅ Image hai toh dikhao
                        AsyncImage(
                            model = imageUri,
                            contentDescription = "Avatar",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                        )
                    } else {
                        // ✅ Image nahi hai toh letter dikhao
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = "Avatar",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                        )
                    }

                    // ✅ Upload ho raha hai toh loader dikhao
                    if (isUploading) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.4f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                }

                // ✅ Camera icon neeche dikhao
                Text(
                    text = "📷 Change Photo",
                    fontSize = 12.sp,
                    color = greenColor,
                    modifier = Modifier.clickable {
                        imagePicker.launch("image/*")
                    }
                )


                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Admin Profile",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B5E20)
                )

                Spacer(Modifier.height(12.dp))

                // ── Email Card ─────────────────────────────────────
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9)),
                    onClick = {
                        // for animaition  adding onClick
                    },
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Email", fontSize = 13.sp, color = Color.Black,
                            fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(4.dp))
                        Text(adminEmail, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(Modifier.height(24.dp))
                HorizontalDivider()
                Spacer(Modifier.height(20.dp))

                // ── Change Password Card ───────────────────────────
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = cardShape,
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp),
                    onClick = {
                        // for animaition  adding onClick
                    },
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {

                        Text(
                            text = "Change Password",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(Modifier.height(12.dp))

                        // ── Current Password ───────────────────────
                        OutlinedTextField(
                            value = currentPassword,
                            onValueChange = {
                                currentPassword = it
                                currentPassError = ""
                            },
                            label = { Text("Current Password") },
                            leadingIcon = {
                                Icon(Icons.Outlined.Lock, contentDescription = "Current Password")
                            },
                            trailingIcon = {
                                IconButton(onClick = { currentPassVisible = !currentPassVisible }) {
                                    Icon(
                                        imageVector = if (currentPassVisible)
                                            Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                        contentDescription = if (currentPassVisible)
                                            "Hide Password" else "Show Password"
                                    )
                                }
                            },
                            visualTransformation = if (currentPassVisible)
                                VisualTransformation.None else PasswordVisualTransformation(),
                            isError = currentPassError.isNotEmpty(),
                            supportingText = {
                                if (currentPassError.isNotEmpty())
                                    Text(currentPassError, color = Color.Red, fontSize = 12.sp)
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

                        Spacer(Modifier.height(10.dp))

                        // ── New Password ───────────────────────────
                        OutlinedTextField(
                            value = newPassword,
                            onValueChange = {
                                newPassword = it
                                newPassError = ""
                            },
                            label = { Text("New Password") },
                            leadingIcon = {
                                Icon(Icons.Outlined.Lock, contentDescription = "New Password")
                            },
                            trailingIcon = {
                                IconButton(onClick = { newPassVisible = !newPassVisible }) {
                                    Icon(
                                        imageVector = if (newPassVisible)
                                            Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                        contentDescription = if (newPassVisible)
                                            "Hide Password" else "Show Password"
                                    )
                                }
                            },
                            visualTransformation = if (newPassVisible)
                                VisualTransformation.None else PasswordVisualTransformation(),
                            isError = newPassError.isNotEmpty(),
                            supportingText = {
                                if (newPassError.isNotEmpty())
                                    Text(newPassError, color = Color.Red, fontSize = 12.sp)
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

                        Spacer(Modifier.height(10.dp))

                        // ── Confirm New Password ───────────────────
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = {
                                confirmPassword = it
                                confirmPassError = ""
                            },
                            label = { Text("Confirm New Password") },
                            leadingIcon = {
                                Icon(Icons.Outlined.Lock, contentDescription = "Confirm Password")
                            },
                            trailingIcon = {
                                IconButton(onClick = { confirmPassVisible = !confirmPassVisible }) {
                                    Icon(
                                        imageVector = if (confirmPassVisible)
                                            Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                        contentDescription = if (confirmPassVisible)
                                            "Hide Password" else "Show Password"
                                    )
                                }
                            },
                            visualTransformation = if (confirmPassVisible)
                                VisualTransformation.None else PasswordVisualTransformation(),
                            isError = confirmPassError.isNotEmpty(),
                            supportingText = {
                                if (confirmPassError.isNotEmpty())
                                    Text(confirmPassError, color = Color.Red, fontSize = 12.sp)
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

                        Spacer(Modifier.height(12.dp))

                        // ── Update Password Button ─────────────────
                        ElevatedButton(
                            onClick = {
                                var isValid = true

                                // ── Validations ────────────────────
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
                                } else if (!newPassword.any { it.isUpperCase() }) {
                                    newPassError = "Must contain at least one uppercase letter"
                                    isValid = false
                                } else if (!newPassword.any { it.isDigit() }) {
                                    newPassError = "Must contain at least one number"
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

                                // ── Firebase Re-auth & Update ──────
                                if (isValid && currentUser != null) {
                                    val credential = EmailAuthProvider.getCredential(
                                        adminEmail, currentPassword
                                    )
                                    currentUser.reauthenticate(credential)
                                        .addOnCompleteListener { reAuth ->
                                            if (reAuth.isSuccessful) {
                                                currentUser.updatePassword(newPassword)
                                                    .addOnCompleteListener { update ->
                                                        if (update.isSuccessful) {
                                                            Toast.makeText(
                                                                context,
                                                                "Password Changed Successfully!",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                            currentPassword = ""
                                                            newPassword = ""
                                                            confirmPassword = ""
                                                        } else {
                                                            Toast.makeText(
                                                                context,
                                                                update.exception?.message
                                                                    ?: "Update Failed",
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
                                containerColor = colorResource(R.color.cardGreen)
                            )
                        ) {
                            Text(
                                "Change Password",
                                color = Color.Black,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                // ── Logout Button ──────────────────────────────────
                OutlinedButton(
                    onClick = {
                        auth.signOut()
                        Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT)
                            .show()

                        // changes here
                        context.startActivity(Intent(context, LoginActivity::class.java))
                        (context as Activity).finish()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.Red,
                        containerColor = colorResource(R.color.primaryGreen)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ExitToApp,
                        contentDescription = "Logout",
                        tint = Color.Red
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Logout", color = Color.Red, fontSize = 16.sp)
                }
            }
        }
    }
}

private fun Nothing?.launch(string: String) {
    TODO("Not yet implemented")
}
