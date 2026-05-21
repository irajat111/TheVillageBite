package com.example.thevillagebite

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
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ChevronRight
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

@Composable
fun ProfileScreen(
    onLogout:       () -> Unit = {},
    onWalletClick:  () -> Unit = {}          // ✅ Wallet navigate callback
) {
    val context     = LocalContext.current
    val auth        = Firebase.auth
    val currentUser = auth.currentUser
    val adminEmail  = currentUser?.email ?: "Not Logged In"

    val supabase = createSupabaseClient(
        supabaseUrl = SupabaseObject.supaBaseUrl,
        supabaseKey = SupabaseObject.supaBasekey
    ) { install(Storage) }

    val db = Firebase.firestore

    var isUploading by remember { mutableStateOf(false) }
    var imageUri    by remember { mutableStateOf<Uri?>(null) }
    var imageUrl    by remember { mutableStateOf("") }

    // ── Wallet stats ──────────────────────────────────────
    var totalRevenue   by remember { mutableStateOf(0.0) }
    var paymentCount   by remember { mutableStateOf(0) }

    // ✅ Wallet summary fetch karo
    LaunchedEffect(Unit) {
        db.collection("wallet").get().addOnSuccessListener { snapshot ->
            paymentCount = snapshot.size()
            totalRevenue = snapshot.documents.sumOf { it.getDouble("amount") ?: 0.0 }
        }
        db.collection("Admin").document(auth.currentUser?.uid.toString()).get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    imageUrl = it.result.get("adminProfileImage").toString()
                }
            }
    }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            imageUri = uri
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val fileName    = "${System.currentTimeMillis()}.jpg"
                    val inputStream = context.contentResolver.openInputStream(uri)
                    val bytes       = inputStream?.readBytes()
                    val bucket      = supabase.storage.from("village_bite")
                    bucket.upload(path = fileName, data = bytes!!)
                    val uploadedUrl = bucket.publicUrl(fileName)
                    val hashMap = mapOf(
                        "name"               to "Admin",
                        "email"              to adminEmail,
                        "adminProfileImage"  to uploadedUrl
                    )
                    db.collection("Admin").document(auth.currentUser?.uid.toString()).set(hashMap)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful)
                                Toast.makeText(context, "Image Uploaded Successfully", Toast.LENGTH_SHORT).show()
                            else
                                Toast.makeText(context, task.exception?.message, Toast.LENGTH_SHORT).show()
                        }
                } catch (e: Exception) {
                    println("Image upload error: ${e.message}")
                }
            }
        }
    }

    var currentPassword    by remember { mutableStateOf("") }
    var newPassword        by remember { mutableStateOf("") }
    var confirmPassword    by remember { mutableStateOf("") }
    var currentPassVisible by remember { mutableStateOf(false) }
    var newPassVisible     by remember { mutableStateOf(false) }
    var confirmPassVisible by remember { mutableStateOf(false) }
    var currentPassError   by remember { mutableStateOf("") }
    var newPassError       by remember { mutableStateOf("") }
    var confirmPassError   by remember { mutableStateOf("") }

    val greenColor = Color(0xFF4CAF50)
    val cardShape  = RoundedCornerShape(14.dp)

    Box(
        Modifier
            .fillMaxSize()
            .background(colorResource(R.color.white))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(R.color.white))
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .padding(top = 60.dp, bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // ── Avatar ────────────────────────────────────
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(greenColor, CircleShape)
                        .clickable { imagePicker.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model              = imageUri ?: imageUrl,
                        contentDescription = "Avatar",
                        contentScale       = ContentScale.Crop,
                        modifier           = Modifier.fillMaxSize().clip(CircleShape)
                    )
                    if (isUploading) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.4f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(30.dp))
                        }
                    }
                }

                Text(
                    text     = "📷 Change Photo",
                    fontSize = 12.sp,
                    color    = greenColor,
                    modifier = Modifier.clickable { imagePicker.launch("image/*") }
                )

                Spacer(Modifier.height(8.dp))

                Text("Admin Profile", fontSize = 20.sp, fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B5E20))

                Spacer(Modifier.height(12.dp))

                // ── Email Card ────────────────────────────────
                Card(
                    modifier  = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors    = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9)),
                    onClick   = {}
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Email", fontSize = 13.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(4.dp))
                        Text(adminEmail, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(Modifier.height(16.dp))

                // ✅ ── Wallet Summary Card ─────────────────────
                Card(
                    modifier  = Modifier.fillMaxWidth(),
                    shape     = RoundedCornerShape(14.dp),
                    colors    = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                    elevation = CardDefaults.cardElevation(4.dp),
                    onClick   = { onWalletClick() }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier         = Modifier
                                    .size(46.dp)
                                    .background(greenColor.copy(alpha = 0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.AccountBalanceWallet, contentDescription = null,
                                    tint = greenColor, modifier = Modifier.size(26.dp))
                            }
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text("Revenue & Payments", fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold, color = Color.Black)
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    text     = "₹${"%.2f".format(totalRevenue)} • $paymentCount payments",
                                    fontSize = 12.sp,
                                    color    = greenColor
                                )
                            }
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null,
                            tint = greenColor)
                    }
                }

                Spacer(Modifier.height(24.dp))
                HorizontalDivider()
                Spacer(Modifier.height(20.dp))

                // ── Change Password Card ──────────────────────
                Card(
                    modifier  = Modifier.fillMaxWidth(),
                    shape     = cardShape,
                    colors    = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp),
                    onClick   = {}
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {

                        Text("Change Password", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(12.dp))

                        // Current Password
                        OutlinedTextField(
                            value         = currentPassword,
                            onValueChange = { currentPassword = it; currentPassError = "" },
                            label         = { Text("Current Password") },
                            leadingIcon   = { Icon(Icons.Outlined.Lock, contentDescription = null) },
                            trailingIcon  = {
                                IconButton(onClick = { currentPassVisible = !currentPassVisible }) {
                                    Icon(if (currentPassVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                        contentDescription = null)
                                }
                            },
                            visualTransformation = if (currentPassVisible) VisualTransformation.None
                            else PasswordVisualTransformation(),
                            isError        = currentPassError.isNotEmpty(),
                            supportingText = { if (currentPassError.isNotEmpty()) Text(currentPassError, color = Color.Red, fontSize = 12.sp) },
                            modifier       = Modifier.fillMaxWidth(),
                            shape          = cardShape,
                            singleLine     = true,
                            colors         = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor     = greenColor,
                                unfocusedBorderColor   = Color(0xFFDDDDDD),
                                focusedContainerColor  = Color.White,
                                unfocusedContainerColor = Color.White
                            )
                        )

                        Spacer(Modifier.height(10.dp))

                        // New Password
                        OutlinedTextField(
                            value         = newPassword,
                            onValueChange = { newPassword = it; newPassError = "" },
                            label         = { Text("New Password") },
                            leadingIcon   = { Icon(Icons.Outlined.Lock, contentDescription = null) },
                            trailingIcon  = {
                                IconButton(onClick = { newPassVisible = !newPassVisible }) {
                                    Icon(if (newPassVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                        contentDescription = null)
                                }
                            },
                            visualTransformation = if (newPassVisible) VisualTransformation.None
                            else PasswordVisualTransformation(),
                            isError        = newPassError.isNotEmpty(),
                            supportingText = { if (newPassError.isNotEmpty()) Text(newPassError, color = Color.Red, fontSize = 12.sp) },
                            modifier       = Modifier.fillMaxWidth(),
                            shape          = cardShape,
                            singleLine     = true,
                            colors         = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor     = greenColor,
                                unfocusedBorderColor   = Color(0xFFDDDDDD),
                                focusedContainerColor  = Color.White,
                                unfocusedContainerColor = Color.White
                            )
                        )

                        Spacer(Modifier.height(10.dp))

                        // Confirm Password
                        OutlinedTextField(
                            value         = confirmPassword,
                            onValueChange = { confirmPassword = it; confirmPassError = "" },
                            label         = { Text("Confirm New Password") },
                            leadingIcon   = { Icon(Icons.Outlined.Lock, contentDescription = null) },
                            trailingIcon  = {
                                IconButton(onClick = { confirmPassVisible = !confirmPassVisible }) {
                                    Icon(if (confirmPassVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                        contentDescription = null)
                                }
                            },
                            visualTransformation = if (confirmPassVisible) VisualTransformation.None
                            else PasswordVisualTransformation(),
                            isError        = confirmPassError.isNotEmpty(),
                            supportingText = { if (confirmPassError.isNotEmpty()) Text(confirmPassError, color = Color.Red, fontSize = 12.sp) },
                            modifier       = Modifier.fillMaxWidth(),
                            shape          = cardShape,
                            singleLine     = true,
                            colors         = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor     = greenColor,
                                unfocusedBorderColor   = Color(0xFFDDDDDD),
                                focusedContainerColor  = Color.White,
                                unfocusedContainerColor = Color.White
                            )
                        )

                        Spacer(Modifier.height(12.dp))

                        ElevatedButton(
                            onClick = {
                                var isValid = true
                                if (currentPassword.isEmpty()) { currentPassError = "Enter your current password"; isValid = false }
                                if (newPassword.isEmpty()) { newPassError = "Enter new password"; isValid = false }
                                else if (newPassword.length < 6) { newPassError = "Password must be at least 6 characters"; isValid = false }
                                else if (!newPassword.any { it.isUpperCase() }) { newPassError = "Must contain at least one uppercase letter"; isValid = false }
                                else if (!newPassword.any { it.isDigit() }) { newPassError = "Must contain at least one number"; isValid = false }
                                else if (newPassword == currentPassword) { newPassError = "New password cannot be same as current"; isValid = false }
                                if (confirmPassword.isEmpty()) { confirmPassError = "Please confirm your new password"; isValid = false }
                                else if (confirmPassword != newPassword) { confirmPassError = "Passwords do not match"; isValid = false }

                                if (isValid && currentUser != null) {
                                    val credential = EmailAuthProvider.getCredential(adminEmail, currentPassword)
                                    currentUser.reauthenticate(credential).addOnCompleteListener { reAuth ->
                                        if (reAuth.isSuccessful) {
                                            currentUser.updatePassword(newPassword).addOnCompleteListener { update ->
                                                if (update.isSuccessful) {
                                                    Toast.makeText(context, "Password Changed Successfully!", Toast.LENGTH_SHORT).show()
                                                    currentPassword = ""; newPassword = ""; confirmPassword = ""
                                                } else {
                                                    Toast.makeText(context, update.exception?.message ?: "Update Failed", Toast.LENGTH_SHORT).show()
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
                            Text("Change Password", color = Color.Black,
                                fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                // ── Logout Button ─────────────────────────────
                OutlinedButton(
                    onClick = {
                        auth.signOut()
                        Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
                        context.startActivity(Intent(context, LoginActivity::class.java))
                        (context as Activity).finish()
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape    = RoundedCornerShape(8.dp),
                    colors   = ButtonDefaults.outlinedButtonColors(
                        contentColor   = Color.White,
                        containerColor = colorResource(R.color.primaryGreen)
                    )
                ) {
                    Icon(Icons.Outlined.ExitToApp, contentDescription = "Logout", tint = Color.White)
                    Spacer(Modifier.width(8.dp))
                    Text("Logout", color = Color.White, fontSize = 16.sp)
                }
            }
        }
    }
}