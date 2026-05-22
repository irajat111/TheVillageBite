package com.example.thevillagebiteuser

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
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Email
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
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch

class LogInActivity : ComponentActivity() {
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

    val auth    = Firebase.auth
    val db = Firebase.firestore
    val context = LocalContext.current
    val credentialManager = remember { CredentialManager.create(context) }
    var loading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var email   by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val greenColor = Color(0xFF4CAF50)
    val textGreen  = Color(0xFF2E7D32)

    var isGoogleLogin by remember { mutableStateOf(false) }

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
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "App Logo",
                modifier = Modifier.size(100.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "The Village Bite",
                fontSize = 30.sp,
                fontFamily = FontObj.cause,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Delivery Food at Home",
                fontSize = 18.sp,
                fontFamily = FontObj.cause,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(11.dp))

            Text(
                text = "Login to Your Account",
                fontSize = 16.sp,
                fontFamily = FontObj.cause,
            )

            Spacer(modifier = Modifier.height(16.dp))

            val cardShape = RoundedCornerShape(14.dp)

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("Email", fontFamily = FontObj.cause) },
                leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = "Email Icon") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth().height(57.dp),
                shape = cardShape,
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor   = Color.White,
                    focusedBorderColor      = greenColor,
                    unfocusedBorderColor    = Color(0xFFDDDDDD)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Enter Password", fontFamily = FontObj.cause) },
                leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = "Password Icon") },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = if (passwordVisible) "Hide Password" else "Show Password"
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth().height(57.dp),
                shape = cardShape,
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor   = Color.White,
                    focusedBorderColor      = greenColor,
                    unfocusedBorderColor    = Color(0xFFDDDDDD)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Or", fontSize = 13.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Continue With", fontSize = 22.sp, fontFamily = FontObj.cause)
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = {
                     //   isGoogleLogin = true
                         scope.launch {
                             loading = true
                             try {
                                 val googleIdOption = GetGoogleIdOption.Builder()
                                     .setServerClientId(context.getString(R.string.token))
                                     .setFilterByAuthorizedAccounts(false)
                                     .build()

                                 val request = GetCredentialRequest.Builder()
                                     .addCredentialOption(googleIdOption)
                                     .build()

                                 val result = credentialManager.getCredential(context, request)
                                 val credential = result.credential

                                 if (credential is CustomCredential &&
                                     credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                                 ) {
                                     val googleCredential = GoogleIdTokenCredential.createFrom(credential.data)
                                  //   if (googleCredential.idToken.isNotEmpty()) {
                                     val credential = GoogleAuthProvider.getCredential(googleCredential.idToken, null)
                                     auth.signInWithCredential(credential).addOnCompleteListener { task ->
                                             if (task.isSuccessful) {
                                                 val user = auth.currentUser
                                                 val hashMap = mapOf<String, String>(
                                                     "name" to user?.displayName.toString(),
                                                     "email" to user?.email.toString()
                                                 )
                                                 db.collection("users").document(
                                                     auth.currentUser?.uid.toString()
                                                 ).set(hashMap).addOnCompleteListener {
                                                     if (it.isSuccessful) {
                                                         Toast.makeText(
                                                             context,
                                                             "Login Successfully",
                                                             Toast.LENGTH_SHORT
                                                         )
                                                             .show()
                                                         context.startActivity(
                                                             Intent(
                                                                 context,
                                                                 DashBoardActivity::class.java
                                                             )
                                                         )
                                                         (context as Activity).finish()
                                                     } else {
                                                         Toast.makeText(
                                                             context,
                                                             it.exception?.message,
                                                             Toast.LENGTH_SHORT
                                                         )
                                                             .show()
                                                     }
                                                 }
                                             }else {

                                             }


                                         }
                                   //  } else {
                                 //    }
                                 }
                             } catch (e: NoCredentialException) {
                                 Toast.makeText(context, "No Google accounts found", Toast.LENGTH_SHORT).show()

                                 println(e.message ?: "Sign-in failed")
                             } catch (e: GetCredentialCancellationException) {
                                  println(e.message ?: "Sign-in failed")
                             } catch (e: GetCredentialException) {
                                 println(e.message ?: "Sign-in failed")
                             } finally {
                                 loading = false
                             }
                         }

                    },
                    modifier = Modifier.weight(1f).height(57.dp),
                    shape = cardShape,
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.google),
                        contentDescription = "Google",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Google", color = Color.DarkGray, fontFamily = FontObj.cause)
                }

                OutlinedButton(
                    onClick = {},
                    modifier = Modifier.weight(1f).height(57.dp),
                    shape = cardShape,
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.facebook),
                        contentDescription = "Facebook",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Facebook", color = Color.DarkGray, fontFamily = FontObj.cause)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            ElevatedButton(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp),
                shape = RoundedCornerShape(7.dp),
                onClick = {
                    if (email.isEmpty()) {
                        Toast.makeText(context, "Enter Email", Toast.LENGTH_SHORT).show()
                    } else if (!email.endsWith("@gmail.com")) {
                        Toast.makeText(context, "Enter Gmail with @gmail.com", Toast.LENGTH_SHORT).show()
                    } else if (password.isEmpty()) {
                        Toast.makeText(context, "Enter Password", Toast.LENGTH_SHORT).show()
                    } else {
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    Toast.makeText(context, "Login Successfully", Toast.LENGTH_SHORT).show()

                                    // ✅ LOGIN NOTIFICATION — yahan call karo
                                    NotificationHelper.sendLoginNotification(context)

                                    // ✅ FCM Token refresh karo login ke baad
                                    NotificationHelper.refreshAndSaveFCMToken()

                                    context.startActivity(Intent(context, DashBoardActivity::class.java))
                                    (context as Activity).finish()
                                } else {
                                    Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
                                }
                            }
                        Log.i("Credential", "Email: $email Password: $password")
                    }
                },
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = colorResource(R.color.primaryGreen)
                )
            ) {
                Icon(imageVector = Icons.Outlined.Login, contentDescription = "Login", tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("login", fontSize = 18.sp, color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = {
                context.startActivity(Intent(context, SignUpActivity::class.java))
                (context as Activity).finish()
            }) {
                Text(text = "Don't Have Account?", fontSize = 14.sp, textAlign = TextAlign.Center)
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Design By\nRajat Singh",
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp,
                modifier = Modifier.padding(bottom = 32.dp)
            )
        }

    }

}


