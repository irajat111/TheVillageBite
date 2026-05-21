package com.example.thevillagebite

import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception


data class ProductClass(
    var id: String? = "",
    val foodname: String? = "",
    val foodprice: Double? = 0.0,
    val foodDescriprion: String? = "",
    val productImage: String? = null
)


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun ProductScreen(navController: NavHostController) {
    val db = Firebase.firestore
    var showDialog by remember { mutableStateOf(false) }
    var selectedId by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var deleteId by remember { mutableStateOf("") }
    var ProductList = remember { mutableStateListOf<ProductClass>() }

    LaunchedEffect(Unit) {
        db.collection("Productclass").addSnapshotListener { result, error ->
            if (error != null) return@addSnapshotListener
            for (doc in result!!.documentChanges) {
                when (doc.type) {
                    DocumentChange.Type.ADDED -> {
                        val model = doc.document.toObject(ProductClass::class.java)
                        model.id = doc.document.id
                        ProductList.add(model)
                    }
                    DocumentChange.Type.MODIFIED -> {
                        val model = doc.document.toObject(ProductClass::class.java)
                        model.id = doc.document.id
                        val index = ProductList.indexOfFirst { it.id == model.id }
                        if (index != -1) ProductList[index] = model
                    }
                    DocumentChange.Type.REMOVED -> {
                        val model = doc.document.toObject(ProductClass::class.java)
                        model.id = doc.document.id
                        val index = ProductList.indexOfFirst { it.id == model.id }
                        if (index != -1) ProductList.removeAt(index)
                    }
                }
            }
        }
    }

    Box(Modifier.fillMaxSize()) {

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(ProductList.size) { index ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val productId = ProductList[index].id ?: ""
                            navController.navigate("productDetails/$productId")
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = colorResource(R.color.white)
                    ),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(ProductList[index].productImage)
                                .crossfade(true)
                                .build(),
                            placeholder = painterResource(R.drawable.ic_launcher_background),
                            contentDescription = stringResource(R.string.app_name),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .padding(8.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )

                        Spacer(Modifier.height(1.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                ProductList[index].foodname ?: "",
                                textAlign = TextAlign.Center
                            )
                            Spacer(Modifier.weight(1f))
                            IconButton(onClick = {
                                deleteId = ProductList[index].id ?: ""
                                showDeleteDialog = true
                            }) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "DELETE",
                                    tint = Color.Red
                                )
                            }
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { showDialog = true },
            containerColor = colorResource(R.color.primaryGreen),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Add, tint = Color.White, contentDescription = "")
        }
    }

    if (showDialog) {
        OpenDialogboxProduct(
            showDialog = showDialog,
            dissmis = { showDialog = false },
            selectedId = selectedId
        )
    }

    // Delete Dialog
    if (showDeleteDialog) {
        Dialog(onDismissRequest = { showDeleteDialog = false }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "",
                            tint = Color.Red,
                            modifier = Modifier.size(28.dp)
                        )
                        Text(
                            text = "Warning!",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Red
                        )
                        IconButton(onClick = { showDeleteDialog = false }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.Red
                            )
                        }
                    }

                    Divider(color = Color.LightGray, thickness = 0.5.dp)
                    Spacer(Modifier.height(12.dp))

                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .padding(3.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Are you sure you want to delete this item? This action cannot be undone!",
                                fontSize = 14.sp,
                                color = Color.Black,
                                lineHeight = 22.sp
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { showDeleteDialog = false },
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Cancel",
                                    fontSize = 13.sp,
                                    color = Color.Black,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    deleteProduct(deleteId)
                                    showDeleteDialog = false
                                },
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Yes, Delete",
                                    fontSize = 13.sp,
                                    color = Color.Red,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun OpenDialogboxProduct(showDialog: Boolean, dissmis: () -> Unit, selectedId: String) {

    val supabase = createSupabaseClient(
        supabaseUrl = SupabaseObject.supaBaseUrl,
        supabaseKey = SupabaseObject.supaBasekey
    ) { install(Storage) }

    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var isImageUploading by remember { mutableStateOf(false) }
    var imageUrl by remember { mutableStateOf("") }

    // ✅ Validation States
    var imageError by remember { mutableStateOf("") }
    var foodnameError by remember { mutableStateOf("") }
    var foodpriceError by remember { mutableStateOf("") }
    var foodDescriptionError by remember { mutableStateOf("") }

    var foodname by remember { mutableStateOf("") }
    var foodprice by remember { mutableStateOf("") }
    var foodDescription by remember { mutableStateOf("") }

    val imagePermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
        else Toast.makeText(context, "Permission Not Granted", Toast.LENGTH_SHORT).show()
    }

    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        imageUri = it
        imageError = ""
        isImageUploading = true

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val fileName = "${System.currentTimeMillis()}.jpg"
                val inputStream = context.contentResolver.openInputStream(imageUri!!)
                val bytes = inputStream?.readBytes()
                val bucket = supabase.storage.from("village_bite")
                bucket.upload(path = fileName, data = bytes!!)
                imageUrl = bucket.publicUrl(fileName)
            } catch (e: Exception) {
                println("Image Upload Error: ${e.message}")
            } finally {
                isImageUploading = false
            }
        }
    }

    Dialog(onDismissRequest = { dissmis() }) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = colorResource(R.color.white),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(5.dp)
            ) {

                // ✅ Title Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = colorResource(R.color.cardGreen)
                    ),
                    onClick = {},
                ) {
                    Text(
                        "Add Food",
                        color = Color.Black,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(15.dp)
                    )
                }

                // ✅ Image Preview Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(10.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (imageUri == null) {
                        Image(
                            painterResource(R.drawable.ic_launcher_background),
                            contentDescription = "",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        AsyncImage(
                            model = imageUri,
                            placeholder = painterResource(R.drawable.ic_launcher_background),
                            contentDescription = "",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            onSuccess = {
                                Toast.makeText(context, "Image Loaded!", Toast.LENGTH_SHORT).show()
                            }
                        )
                        if (isImageUploading) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.4f)),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    color = colorResource(R.color.primaryGreen)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // ✅ Pick Image Card — red tint on error
                Card(
                    onClick = {
                        imageError = ""
                        if (checkPermisison(context)) {
                            imagePicker.launch("image/*")
                        } else {
                            imagePermission.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .padding(10.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (imageError.isNotEmpty())
                            Color.Red.copy(alpha = 0.1f)
                        else
                            colorResource(R.color.cardGreen)
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Pick Image",
                                modifier = Modifier.size(50.dp),
                                tint = if (imageError.isNotEmpty()) Color.Red else Color.Black
                            )
                            Spacer(modifier = Modifier.height(1.dp))
                            Text(
                                text = if (imageUrl.isNotEmpty()) "✅ Image Selected"
                                else "Tap to Pick Image",
                                color = if (imageError.isNotEmpty()) Color.Red
                                else if (imageUrl.isNotEmpty()) Color(0xFF4CAF50)
                                else Color.Gray
                            )
                        }
                    }
                }

                // Image Error Text
                if (imageError.isNotEmpty()) {
                    Text(
                        text = imageError,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 16.dp, bottom = 4.dp)
                    )
                }

                // ✅ Food Name Field
                OutlinedTextField(
                    value = foodname,
                    onValueChange = {
                        foodname = it
                        foodnameError = ""
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 5.dp),
                    label = { Text("Enter Food Name") },
                    isError = foodnameError.isNotEmpty(),
                    supportingText = {
                        if (foodnameError.isNotEmpty())
                            Text(foodnameError, color = Color.Red, fontSize = 12.sp)
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF4CAF50),
                        unfocusedBorderColor = Color(0xFFDDDDDD),
                        errorBorderColor = Color.Red
                    )
                )

                Spacer(modifier = Modifier.height(5.dp))

                // ✅ Food Price Field
                OutlinedTextField(
                    value = foodprice,
                    onValueChange = {
                        foodprice = it
                        foodpriceError = ""
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 5.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    label = { Text("Enter Price") },
                    isError = foodpriceError.isNotEmpty(),
                    supportingText = {
                        if (foodpriceError.isNotEmpty())
                            Text(foodpriceError, color = Color.Red, fontSize = 12.sp)
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF4CAF50),
                        unfocusedBorderColor = Color(0xFFDDDDDD),
                        errorBorderColor = Color.Red
                    )
                )

                Spacer(modifier = Modifier.height(5.dp))

                // ✅ Food Description Field
                OutlinedTextField(
                    value = foodDescription,
                    onValueChange = {
                        foodDescription = it
                        foodDescriptionError = ""
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 5.dp),
                    label = { Text("Enter Description") },
                    isError = foodDescriptionError.isNotEmpty(),
                    supportingText = {
                        if (foodDescriptionError.isNotEmpty())
                            Text(foodDescriptionError, color = Color.Red, fontSize = 12.sp)
                    },
                    maxLines = 3,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF4CAF50),
                        unfocusedBorderColor = Color(0xFFDDDDDD),
                        errorBorderColor = Color.Red
                    )
                )

                Spacer(modifier = Modifier.height(5.dp))

                // ✅ ADD Button with full validations
                ElevatedButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 5.dp),
                    onClick = {
                        var isValid = true

                        // Image
                        if (imageUrl.isEmpty()) {
                            imageError = "Please select an image"
                            isValid = false
                        }

                        // Food Name
                        if (foodname.isEmpty()) {
                            foodnameError = "Food name cannot be empty"
                            isValid = false
                        } else if (foodname.length < 3) {
                            foodnameError = "Minimum 3 characters required"
                            isValid = false
                        } else if (foodname.length > 50) {
                            foodnameError = "Maximum 50 characters allowed"
                            isValid = false
                        }

                        // Price
                        if (foodprice.isEmpty()) {
                            foodpriceError = "Price cannot be empty"
                            isValid = false
                        } else if (foodprice.toDoubleOrNull() == null) {
                            foodpriceError = "Enter a valid number"
                            isValid = false
                        } else if (foodprice.toDouble() <= 0) {
                            foodpriceError = "Price must be greater than 0"
                            isValid = false
                        } else if (foodprice.toDouble() > 10000) {
                            foodpriceError = "Price cannot exceed 10,000"
                            isValid = false
                        }

                        // Description
                        if (foodDescription.isEmpty()) {
                            foodDescriptionError = "Description cannot be empty"
                            isValid = false
                        } else if (foodDescription.length < 10) {
                            foodDescriptionError = "Minimum 10 characters required"
                            isValid = false
                        } else if (foodDescription.length > 200) {
                            foodDescriptionError = "Maximum 200 characters allowed"
                            isValid = false
                        }

                        // ✅ Sab valid — Firebase call
                        if (isValid) {
                            val productItemobj = ProductClass(
                                foodname = foodname.trim(),
                                foodprice = foodprice.toDouble(),
                                productImage = imageUrl,
                                foodDescriprion = foodDescription.trim()
                            )

                            val firebaseobj = FirebaseFirestore.getInstance()
                            firebaseobj.collection("Productclass")
                                .add(productItemobj)
                                .addOnSuccessListener { documentReference ->
                                    firebaseobj.collection("Productclass")
                                        .document(documentReference.id)
                                        .update("id", documentReference.id)
                                        .addOnSuccessListener {
                                            dissmis()
                                        }
                                        .addOnFailureListener { e ->
                                            Log.e("Firestore", "Error: ${e.message}")
                                        }
                                }
                                .addOnFailureListener { exception ->
                                    Log.e("Firestore", "Add failed: ${exception.message}")
                                }
                        }
                    },
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = colorResource(R.color.primaryGreen)
                    ),
                    shape = RoundedCornerShape(5.dp)
                ) {
                    Text("ADD", color = Color.White)
                }
            }
        }
    }
}


fun deleteProduct(docId: String? = "") {
    val db = FirebaseFirestore.getInstance()
    db.collection("Productclass").document(docId ?: "").delete()
        .addOnSuccessListener { Log.d("firestore", "Document Successfully Deleted!") }
        .addOnFailureListener { Log.d("firestore", "Delete Failed", it) }
}