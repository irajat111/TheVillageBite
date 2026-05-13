package com.example.thevillagebite

import android.R.attr.maxLines
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil3.compose.AsyncImage
import com.google.firebase.firestore.FirebaseFirestore
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.Storage


val Productlist = mutableStateListOf<ProductClass>()

    data class ProductClass(
        var id: String? = "",
        val foodname: String? = "",
        val foodprice: Double? = 0.0,
        val foodimg: String? = "",
        val foodDescriprion: String? = ""
    )


@Preview(showSystemUi = true)
@Composable()
fun ProductScreen() {

    var showDialog by remember { mutableStateOf(false) }
    var selectedId by remember { mutableStateOf("") }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        FloatingActionButton(
            onClick = { showDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = colorResource(R.color.primaryGreen)
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "",
                tint = Color.White
            )
        }
    }


    if (showDialog){
        OpenDialogboxProduct(
            showDialog = showDialog,
            dissmis = { showDialog  = false },
            selectedId = selectedId
        )
    }

}

fun Modifier.Companion.align(bottomEnd: Alignment) {}

@Composable
fun OpenDialogboxProduct(showDialog: Boolean, dissmis: () -> Unit, selectedId: String){

    val supabase = createSupabaseClient(
        supabaseUrl = SupabaseObject.supaBaseUrl,
        supabaseKey = SupabaseObject.supaBasekey
    ) {
        install(Storage)
    }

    val context = LocalContext.current
    var foodname by remember { mutableStateOf("") }
    var foodprice by remember { mutableStateOf("") }
    var foodimg by remember { mutableStateOf("") }
    var foodDescription by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = {
            dissmis()  // ✅ closes when user taps outside
        },
        content = {
            Box(
                modifier = Modifier.fillMaxWidth()
                    .background(color = colorResource(R.color.white),
                shape = RoundedCornerShape(16.dp)).padding(10.dp),
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(5.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = colorResource(R.color.cardGreen)
                        )
                    ) {
                        Text(
                            "Add Food",
                            color = Color.Black,
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(15.dp)
                        )
                    }

                    var imageUri by remember { mutableStateOf<Uri?>(null) }

                    if (imageUri == null) {
                        Image(
                            painterResource(R.drawable.ic_launcher_background),
                            contentDescription = "",
                            modifier = Modifier.fillMaxWidth().padding(10.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )

                    } else {
                        AsyncImage(
                            model = imageUri,
                            placeholder = painterResource(R.drawable.ic_launcher_background),
                            contentDescription = "",
                            modifier = Modifier.fillMaxWidth().padding(10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(5.dp))


                    Card(
                        onClick = {
//                            if (checkPermisison(context)) {
//                                imagePicker.launch("image/*")
//                            } else {
//                                imagePermission.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
//                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp).padding(10.dp),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = colorResource(R.color.cardGreen)
                        )
//
                    ) {

                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            if (imageUri != null) {
                                // ✅ Show selected image
                                AsyncImage(
                                    model = imageUri,
                                    contentDescription = "Selected Image",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                // ✅ Show placeholder when no image selected
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Pick Image",
                                        modifier = Modifier.size(50.dp),
                                        tint = Color.Black
                                    )
                                    Spacer(modifier = Modifier.height(1.dp))
                                    Text(
                                        text = "Tap to Pick Image",
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }


                    OutlinedTextField(
                        value = foodname,
                        onValueChange = {
                            foodname = it
                        },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp),
                        label = { Text("Enter Food ") }
                    )
                    Spacer(modifier = Modifier.height(5.dp))

                    OutlinedTextField(
                        value = foodprice,
                        onValueChange = { foodprice  = it},
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        label = { Text(text = "Enter Price")}
                    )
                    Spacer(modifier = Modifier.height(5.dp))

                    OutlinedTextField(
                        value = foodDescription,
                        onValueChange = { foodDescription = it},
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp),
//                        maxLines:
                        label = { Text(text = "Enter Description ")},
                    )
                    Spacer(modifier = Modifier.height(5.dp))


                    ElevatedButton(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp),
                        onClick = {

                            if (foodname.isNotEmpty() && foodprice.isNotEmpty() && foodDescription.isNotEmpty() && foodimg.isEmpty()) {
                                dissmis()  // ✅ dialog band karo
                            }

                            // Add data in firebase

                            val productItemobj = ProductClass(  // dataclass name
                                foodname = foodname,
                                foodprice = foodprice.toDouble(),
                                foodimg = foodimg,
                                foodDescriprion = foodDescription
                            )

                            // store data in firebase firestore
                            val firebaseobj = FirebaseFirestore.getInstance()
                            firebaseobj.collection("Productclass")
                                .add(productItemobj)
                                .addOnSuccessListener { documentReference ->
                                    firebaseobj.collection("Productclass")
                                        .document(documentReference.id)
                                        .update("id",documentReference.id)
                                        .addOnSuccessListener {
                                            dissmis()
                                        }
                                        .addOnFailureListener { e ->
                                            Log.e("Firestore","Error : ${e.message}")
                                        }
                                }
                                .addOnFailureListener { exception ->
                                    Log.e("Firestore","Add failed : ${exception.message}")
                                }
                        },
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = colorResource(R.color.primaryGreen)
                        ),
                        shape = RoundedCornerShape(5.dp)
                    ) { Text("ADD",color = Color.White)}
                }
            }
        }
    )

}

