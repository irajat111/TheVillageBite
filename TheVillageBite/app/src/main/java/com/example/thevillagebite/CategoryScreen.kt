package com.example.thevillagebite

import android.content.Context
import android.content.pm.PackageManager
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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
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

// ===================== DATA CLASS =====================
val Croductlist = mutableStateListOf<CategoryClass>()

data class CategoryClass(
    var id: String? = "",
    val categoryName: String? = "",
    var image: String? = null
)

// ===================== CATEGORY SCREEN =====================
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun CategoryScreen(navController: NavController) {

    val db = Firebase.firestore
    val context = LocalContext.current

    var showDialog by remember { mutableStateOf(false) }
    var selectId by remember { mutableStateOf("") }
    var categoryList = remember { mutableStateListOf<CategoryClass>() }


    // var created for Deleted icon popUp
    var showDeleteDialog by remember { mutableStateOf(false) }
    var deleteId by remember { mutableStateOf("") }

    // Firestore se data fetch
    LaunchedEffect(Unit) {
        db.collection("CategoryClass").addSnapshotListener { result, error ->
            if (error != null) return@addSnapshotListener

            for (doc in result!!.documentChanges) {
                when (doc.type) {
                    DocumentChange.Type.ADDED -> {
                        val model = doc.document.toObject(CategoryClass::class.java)
                        model.id = doc.document.id
                        categoryList.add(model)
                    }
                    DocumentChange.Type.MODIFIED -> {
                        val model = doc.document.toObject(CategoryClass::class.java)
                        model.id = doc.document.id
                        val index = categoryList.indexOfFirst { it.id == model.id }
                        if (index != -1) categoryList[index] = model
                    }
                    DocumentChange.Type.REMOVED -> {
                        val model = doc.document.toObject(CategoryClass::class.java)
                        model.id = doc.document.id
                        val index = categoryList.indexOfFirst { it.id == model.id }
                        if (index != -1) categoryList.removeAt(index)
                    }
                }
            }
        }
    }

    Box(Modifier.fillMaxSize()) {

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(categoryList.size) { index ->
                Card(
                    modifier = Modifier.fillMaxWidth().clickable {
                        navController.navigate("product")
                    },
                    colors = CardDefaults.cardColors(
                        containerColor = colorResource(R.color.white)
                    ),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {

                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(categoryList[index].image)
                                .crossfade(true)
                                .build(),
                            placeholder = painterResource(R.drawable.ic_launcher_background),
                            contentDescription = stringResource(R.string.app_name),
                            contentScale = ContentScale.Crop,
//
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .padding(8.dp)                        // 👈 Padding
                                .clip(RoundedCornerShape(12.dp))   // 👈 Rounded corners
                        )

                        Spacer(Modifier.height(1.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                categoryList[index].categoryName.toString(),
                                textAlign = TextAlign.Center
                            )
                            Spacer(Modifier.weight(1f))

//                            IconButton(onClick = { deleteItem(categoryList[index].id) }) {
//                                Icon(
//                                    Icons.Default.Delete,
//                                    contentDescription = "DELETE",
//                                    tint = Color.Red
//                                )
//                            }

                            IconButton(onClick = {
                                deleteId = categoryList[index].id ?: ""
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
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        ) {
            Icon(Icons.Default.Add, tint = Color.White, contentDescription = "")
        }
    }

    if (showDialog) {
        OpenDialogCategory(
            showDialog = showDialog,
            dismiss = { showDialog = false },
            selectId = selectId
        )
    }

    // Dialogbox for Delete Item
    if (showDeleteDialog) {
        Dialog(onDismissRequest = { showDeleteDialog = false }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    // ✅ Warning + Red Cross Icon
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

                        // 3. Spacing control kar sakte ho
//                        Spacer(Modifier.width(6.dp)) // 👈 Icon aur Text ke beech thodi jagah

                        // 4. Title alag se add kar sakte ho
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
                                tint = Color.Red  // ✅ Red cross
                            )
                        }
                    }

                    // ✅ Divider line
                    Divider(color = Color.LightGray, thickness = 0.5.dp)

                    Spacer(Modifier.height(12.dp))

                    Card(
                        modifier = Modifier
//                            .weight(1f)
                            .clickable {  },
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = colorResource(R.color.white)
                        ),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth().height(80.dp)
                                .padding(3.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            // ✅ Message
                            Text(
                                text = "Are you sure you want to delete this item? This action cannot be undone!",
                                fontSize = 14.sp,
                                color = Color.Black,
                                lineHeight = 22.sp
                            )
                        }
                    }
                    Spacer(Modifier.height(16.dp))

                    // ✅ 2 Cards — Cancel aur Yes Delete
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Cancel Card
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { showDeleteDialog = false },
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFF5F5F5)
                            ),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth().height(50.dp)
                                    .padding(3.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
//                                Icon(
//                                    imageVector = Icons.Default.Close,
//                                    contentDescription = "Cancel",
//                                    tint = Color.Gray,
//                                    modifier = Modifier.size(32.dp)
//                                )
//                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "Cancel",
//                                    text = "",
                                    fontSize = 13.sp,
                                    color = Color.Black,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        // Yes Delete Card
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    deleteItem(deleteId)  // ✅ Delete action
                                    showDeleteDialog = false
                                },
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFFFEBEE)
                            ),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth().height(50.dp)
                                    .padding(3.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
//                                Icon(
//                                    imageVector = Icons.Default.Delete,
//                                    contentDescription = "Delete",
//                                    tint = Color.Red,
//                                    modifier = Modifier.size(22.dp)
//                                )
//                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "Yes, Delete",
//                                    text = "",
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

// ===================== ADD CATEGORY DIALOG =====================
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun OpenDialogCategory(showDialog: Boolean, dismiss: () -> Unit, selectId: String) {

    val supabase = createSupabaseClient(
        supabaseUrl = SupabaseObject.supaBaseUrl,
        supabaseKey = SupabaseObject.supaBasekey
    ) {
        install(Storage)
    }

    val context = LocalContext.current
    val db = Firebase.firestore

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imageUrl by remember { mutableStateOf("") }
    var categoryName by remember { mutableStateOf("") }

    // ✅ CHANGE 1 — Naya variable add kiya loader ke liye
    var isImageUploading by remember { mutableStateOf(false) }

    val imagePermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
        else Toast.makeText(context, "Permission Not Granted", Toast.LENGTH_SHORT).show()
    }

    // ✅ CHANGE 2 — imagePicker mein loader ON/OFF kiya
    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        imageUri = it

        isImageUploading = true  // 👈 Loader START

        CoroutineScope(Dispatchers.IO).launch {
            try {

                val fileName = "${System.currentTimeMillis()}.jpg"
                val inputStream = context.contentResolver.openInputStream(imageUri!!)
                val bytes = inputStream?.readBytes()
                val bucket = supabase.storage.from("village_bite")
                bucket.upload(path = fileName, data = bytes!!)
                imageUrl = bucket.publicUrl(fileName)
                println("Check Image Url: $imageUrl")

            } catch (e: Exception) {
                println("Check Exception of Image: ${e.message}")
            } finally {
                isImageUploading = false  // 👈 Loader STOP
            }
        }
    }

    Dialog(onDismissRequest = { dismiss() }) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.White, shape = RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(7.dp)
            ) {

                // Title Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = colorResource(R.color.cardGreen)
                    )
                ) {
                    Text(
                        "Add Category",
                        color = Color.Black,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(15.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // ✅ CHANGE 3 — Image preview box mein loader add kiya
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(10.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (imageUri == null) {
                        // Koi image nahi chuni — placeholder dikhao
                        Image(
                            painterResource(R.drawable.ic_launcher_background),
                            contentDescription = "",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        // Image chun li — dikhao
                        AsyncImage(
                            model = imageUri,
                            placeholder = painterResource(R.drawable.ic_launcher_background),
                            contentDescription = "",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            // ✅ CHANGE 4 — Image load hone par Toast dikhao
                            onSuccess = {
                                Toast.makeText(context, "Image Loaded!", Toast.LENGTH_SHORT).show()
                            }
                        )

                        // ✅ CHANGE 5 — Upload ke time loader dikhao
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

                // Pick Image Card
                Card(
                    onClick = {
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
                        containerColor = colorResource(R.color.cardGreen)
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
                                tint = Color.Black
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = "Tap to Pick Image", color = Color.Gray)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Category Name Field
                OutlinedTextField(
                    value = categoryName,
                    onValueChange = { categoryName = it },
                    label = { Text("Enter Food Category") },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
                )

                Spacer(Modifier.height(5.dp))

                // ADD Button
                ElevatedButton(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                    onClick = {
                        if (categoryName.isNotEmpty()) {
                            val foodItemobj = CategoryClass(
                                id = "",
                                image = imageUrl,
                                categoryName = categoryName
                            )
                            db.collection("CategoryClass").add(foodItemobj)
                                .addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        dismiss()
                                        Toast.makeText(context, "Category Added", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
                                    }
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

// ===================== HELPER FUNCTIONS =====================
fun deleteItem(docId: String? = "") {
    val db = FirebaseFirestore.getInstance()
    db.collection("CategoryClass").document(docId ?: "").delete()
        .addOnSuccessListener { Log.d("firestore", "Document Successfully Deleted!") }
        .addOnFailureListener { Log.d("firestore", "Delete Failed", it) }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
fun checkPermisison(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        android.Manifest.permission.READ_MEDIA_IMAGES
    ) == PackageManager.PERMISSION_GRANTED
}
