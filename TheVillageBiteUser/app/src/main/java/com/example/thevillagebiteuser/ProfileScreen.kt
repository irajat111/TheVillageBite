package com.example.thevillagebiteuser

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.google.firebase.Firebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ProfileScreen(navController: NavHostController) {

    val context     = LocalContext.current
    val auth        = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val db          = Firebase.firestore
    val uid         = currentUser?.uid

    // ── Supabase client — Admin app jaisa same ────────────────────────────────
    val supabase = createSupabaseClient(
        supabaseUrl = SupabaseObject.supaBaseUrl,
        supabaseKey = SupabaseObject.supaBasekey
    ) { install(Storage) }

    var totalOrders by remember { mutableStateOf(0) }
    var imageUrl    by remember { mutableStateOf("") }
    var isUploading by remember { mutableStateOf(false) }

    // ── 1. Firestore se data load karo ────────────────────────────────────────
    LaunchedEffect(Unit) {
        if (uid == null) return@LaunchedEffect

        // Total orders
        db.collection("order")
            .whereEqualTo("userId", uid)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) totalOrders = snapshot.documents.size
            }

        // Saved profile image
        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                val savedUrl = doc.getString("avatarUrl") ?: ""
                imageUrl = if (savedUrl.isNotEmpty()) savedUrl
                else currentUser?.photoUrl?.toString() ?: ""
            }
    }

    // ── 2. Image picker — Supabase village_bite bucket (Admin jaisa) ──────────
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        if (uid == null) {
            Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
            return@rememberLauncherForActivityResult
        }

        isUploading = true

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val fileName    = "user_${uid}_${System.currentTimeMillis()}.jpg"
                val inputStream = context.contentResolver.openInputStream(uri)
                val bytes       = inputStream?.readBytes() ?: return@launch
                inputStream.close()

                // Admin app jaisa — same bucket "village_bite"
                val bucket     = supabase.storage.from("village_bite")
                bucket.upload(path = fileName, data = bytes)
                val uploadedUrl = bucket.publicUrl(fileName)

                withContext(Dispatchers.Main) {
                    // Firestore mein save karo
                    val userDoc = db.collection("users").document(uid)
                    userDoc.get().addOnSuccessListener { doc ->
                        if (doc.exists()) {
                            userDoc.update("avatarUrl", uploadedUrl)
                        } else {
                            userDoc.set(mapOf("avatarUrl" to uploadedUrl))
                        }
                    }

                    imageUrl    = uploadedUrl
                    isUploading = false
                    Toast.makeText(context, "Photo update ho gayi!", Toast.LENGTH_SHORT).show()
                    NotificationHelper.sendProfileUpdateNotification(context)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    isUploading = false
                    Toast.makeText(context, "Upload failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // ── 3. Password fields ────────────────────────────────────────────────────
    val userEmail        = currentUser?.email ?: "No Email Found"
    var currentPassword  by remember { mutableStateOf("") }
    var newPassword      by remember { mutableStateOf("") }
    var confirmPassword  by remember { mutableStateOf("") }
    var currentPassError by remember { mutableStateOf("") }
    var newPassError     by remember { mutableStateOf("") }
    var confirmPassError by remember { mutableStateOf("") }
    var passwordVisible  by remember { mutableStateOf(false) }

    val greenColor = Color(0xFF4CAF50)
    val cardShape  = RoundedCornerShape(14.dp)

    // ── 4. UI ─────────────────────────────────────────────────────────────────
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.white))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
            .padding(top = 60.dp, bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Avatar
        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .background(greenColor, CircleShape)
                .clickable { if (!isUploading) imagePicker.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            if (imageUrl.isNotEmpty()) {
                AsyncImage(
                    model              = imageUrl,
                    contentDescription = "Avatar",
                    contentScale       = ContentScale.Crop,
                    modifier           = Modifier.fillMaxSize().clip(CircleShape)
                )
            } else {
                Text(
                    text       = userEmail.first().uppercaseChar().toString(),
                    fontSize   = 36.sp,
                    color      = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            if (isUploading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.4f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color    = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }

        Text(
            text     = if (isUploading) "Uploading..." else "Change Photo",
            fontSize = 12.sp,
            color    = greenColor,
            modifier = Modifier.clickable { if (!isUploading) imagePicker.launch("image/*") }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Email card
        Card(
            modifier  = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(4.dp),
            colors    = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9)),
            onClick   = {},
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Email", fontSize = 13.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text(userEmail, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(Modifier.height(12.dp))

        // Total orders card
        Card(
            modifier  = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(4.dp),
            colors    = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
            onClick   = { navController.navigate("orderDetails") },
        ) {            Row(
                modifier          = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text("Total Orders", fontSize = 13.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text       = "$totalOrders Orders Placed",
                        fontSize   = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = greenColor
                    )
                }
                Text(text = "🍟", fontSize = 30.sp)
            }
        }

        Spacer(Modifier.height(24.dp))
        HorizontalDivider()
        Spacer(Modifier.height(20.dp))

        // Change password card
        Card(
            modifier  = Modifier.fillMaxWidth(),
            shape     = cardShape,
            colors    = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp),
            onClick   = {},
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                Text("Change Password", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value         = currentPassword,
                    onValueChange = { currentPassword = it; currentPassError = "" },
                    label         = { Text("Current Password") },
                    trailingIcon  = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector        = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = ""
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    isError        = currentPassError.isNotEmpty(),
                    supportingText = {
                        if (currentPassError.isNotEmpty())
                            Text(currentPassError, color = Color.Red, fontSize = 12.sp)
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

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value         = newPassword,
                    onValueChange = { newPassword = it; newPassError = "" },
                    label         = { Text("New Password") },
                    trailingIcon  = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector        = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = ""
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    isError        = newPassError.isNotEmpty(),
                    supportingText = {
                        if (newPassError.isNotEmpty())
                            Text(newPassError, color = Color.Red, fontSize = 12.sp)
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

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value         = confirmPassword,
                    onValueChange = { confirmPassword = it; confirmPassError = "" },
                    label         = { Text("Confirm New Password") },
                    trailingIcon  = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector        = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = ""
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    isError        = confirmPassError.isNotEmpty(),
                    supportingText = {
                        if (confirmPassError.isNotEmpty())
                            Text(confirmPassError, color = Color.Red, fontSize = 12.sp)
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

                Spacer(modifier = Modifier.height(12.dp))

                ElevatedButton(
                    onClick = {
                        var isValid = true
                        if (currentPassword.isEmpty()) { currentPassError = "Enter your current password"; isValid = false }
                        if (newPassword.isEmpty()) { newPassError = "Enter new password"; isValid = false }
                        else if (newPassword.length < 6) { newPassError = "Password must be at least 6 characters"; isValid = false }
                        else if (newPassword == currentPassword) { newPassError = "New password cannot be same as current"; isValid = false }
                        if (confirmPassword.isEmpty()) { confirmPassError = "Please confirm your new password"; isValid = false }
                        else if (confirmPassword != newPassword) { confirmPassError = "Passwords do not match"; isValid = false }

                        if (isValid && currentUser != null) {
                            val credential = EmailAuthProvider.getCredential(userEmail, currentPassword)
                            currentUser.reauthenticate(credential)
                                .addOnCompleteListener { reAuthTask ->
                                    if (reAuthTask.isSuccessful) {
                                        currentUser.updatePassword(newPassword)
                                            .addOnCompleteListener { updateTask ->
                                                if (updateTask.isSuccessful) {
                                                    Toast.makeText(context, "Password Changed Successfully!", Toast.LENGTH_SHORT).show()
                                                    NotificationHelper.sendLocalNotification(
                                                        context = context,
                                                        title   = "Password Changed",
                                                        body    = "Aapka password successfully change ho gaya."
                                                    )
                                                    currentPassword = ""
                                                    newPassword     = ""
                                                    confirmPassword = ""
                                                } else {
                                                    Toast.makeText(context, updateTask.exception?.message ?: "Failed", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                    } else {
                                        currentPassError = "Current password is incorrect"
                                    }
                                }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape    = RoundedCornerShape(7.dp),
                    colors   = ButtonDefaults.elevatedButtonColors(
                        containerColor = colorResource(R.color.cardGreen)
                    )
                ) {
                    Text("Change Password", color = Color.White, fontSize = 16.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedButton(
            onClick = {
                auth.signOut()
                context.startActivity(Intent(context, LogInActivity::class.java))
                (context as Activity).finish()
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape    = RoundedCornerShape(8.dp),
            colors   = ButtonDefaults.outlinedButtonColors(
                contentColor   = Color.White,
                containerColor = colorResource(R.color.primaryGreen)
            )
        ) {
            Icon(imageVector = Icons.Outlined.ExitToApp, contentDescription = "Logout", tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Logout", color = Color.White, fontSize = 16.sp)
        }
    }
}