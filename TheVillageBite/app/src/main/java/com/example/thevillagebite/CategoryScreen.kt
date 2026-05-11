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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.thevillagebite.DataClasses.FoodItem
import com.example.thevillagebite.ListRepo.FoodRepo.foodItemList
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

//class CategoryActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContent {
//            CategoryScreen()
//        }
//    }
//}



@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Preview(showSystemUi = true)
@Composable
fun CategoryScreen() {

    val supabase = createSupabaseClient(
            supabaseUrl = "https://bpiqwtvcpwqcmqiyynuu.supabase.co",
          supabaseKey = "sb_publishable_D6OsW6Fs88RTSzAFgIQh0A_CBY_2uv1"
    ) {
        install(Storage)
        }
    val db = Firebase.firestore
    val context = LocalContext.current

    var showDialog by remember { mutableStateOf(false) }
    var selectId by remember { mutableStateOf("") }
    var categoryList = remember { mutableStateListOf<FoodItem>() }


//    Fetch data from Firestore (same as CategoryScreen)
    LaunchedEffect(Unit) {
        db.collection("category").addSnapshotListener { result, error->
            if(error!=null){
                return@addSnapshotListener
            }
            for(doc in result!!.documentChanges){
                when(doc.type){
                    DocumentChange.Type.ADDED -> {
                        val model = doc.document.toObject(FoodItem::class.java)
                        model.id = doc.document.id
                        categoryList.add(model)
                    }
                    DocumentChange.Type.MODIFIED -> {}
                    DocumentChange.Type.REMOVED -> {}
                }
            }

        }
    }



    println("Check Category Call or not")

    Box(Modifier.fillMaxSize()){

//        LazyColumn(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(10.dp)
//        ) {
//            items(categoryList) { item ->       // ✅ Now works with the correct import
//                Card(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(vertical = 5.dp)
//                ) {
//
//
//                    Box(
//                        modifier = Modifier.fillMaxSize()
//
//                    ) {
//
//                        Row(  // column to Row
//                            modifier = Modifier.padding(16.dp)
//
//                        ) {
//                            Column {
//
//                            Text(text  = "Category: ${item.categoryName}")  // ✅ correct field name
//                            Spacer(modifier = Modifier.height(5.dp))
//
//                        }
//
//                            Spacer(modifier = Modifier.weight(1f))
//
//                            Row(
//                                modifier = Modifier.fillMaxHeight().padding(top = 7.dp)
//                            )
//                            {
//                                IconButton(
//                                    onClick = {
//
//                                    },
//                                    modifier = Modifier.size(35.dp)
//                                ) {
//                                    Icon(
//                                        Icons.Default.Edit,
//                                        contentDescription = "Edit",
//                                        tint = Color.Red
//                                    )
//                                }
//
//                                Spacer(modifier = Modifier.padding(5.dp))
//
//                                IconButton(
//                                    // Deleted Item in List or Firebase firestore
//                                    onClick = {
//                                        deleteItem(item.id)
//                                    },
//                                    modifier = Modifier.size(35.dp)
//                                ) {
//                                    Icon(
//                                        imageVector = Icons.Default.Delete,
//                                        contentDescription = "Delete",
//                                        tint = Color.Red
//                                    )
//                                }
//
//                            }
//
//                        }
//
//
//                    }
//
//                }
//            }
//        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            verticalArrangement = Arrangement.SpaceAround,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(categoryList.size){index->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    println("Checck image form Firebase: ${ categoryList[index].image}")
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(categoryList[index].image)
                                .crossfade(true)
                                .build(),
                            placeholder = painterResource(R.drawable.ic_launcher_background),
                            contentDescription = stringResource(R.string.app_name),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxWidth().height(150.dp),

                        )
                        Spacer(Modifier.height(10.dp))
                        Text(categoryList[index].categoryName.toString())
                        Spacer(Modifier.height(10.dp))
                    }

                }

            }
        }


        FloatingActionButton(
            onClick = {
                showDialog = true
            },
            containerColor = colorResource(R.color.primaryGreen),
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        ) {
           Icon(Icons.Default.Add,
               tint = Color.White,
               contentDescription = "")
        }


    }


    if(showDialog){

        OpenDialog(
            supabase =supabase,
            showDialog = showDialog,
            dismiss = {showDialog = false},
            selectId = selectId,
        )
    }

}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun OpenDialog(showDialog: Boolean, dismiss: () -> Unit, selectId: String, supabase: SupabaseClient) {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val db  = Firebase.firestore
//
    var imageUrl by remember { mutableStateOf("") }
    val imagePermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Permission Not Granted", Toast.LENGTH_SHORT).show()

        }
    }

    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        imageUri = it
        CoroutineScope(Dispatchers.IO).launch {
            val fileName = "${System.currentTimeMillis()}.jpg"
            val inputSream = context.contentResolver.openInputStream(imageUri!!)
            val bytes = inputSream?.readBytes()

            val bucket = supabase.storage.from("village_bite")
            bucket.upload(
                path = fileName,
                data = bytes!!,
            )

            imageUrl = bucket.publicUrl(fileName)
            println("Check Image Url : $imageUrl")
        }
    }

    var categoryName by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = {
            dismiss()
        },
        content = {
            Box(
                modifier = Modifier.fillMaxWidth()
                    .background(
                        color = colorResource(R.color.white),
                        shape = RoundedCornerShape(16.dp)   // rounded corner shap
                    ).padding(16.dp)
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
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

                        if (imageUri == null) {
                            Image(
                                painterResource(R.drawable.ic_launcher_background),
                                contentDescription = "",
                                modifier = Modifier.size(150.dp)
                            )

                        } else {
                            AsyncImage(
                                model = imageUri,
                                placeholder = painterResource(R.drawable.ic_launcher_background),
                                contentDescription = "",
                                modifier = Modifier.size(150.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                if (checkPermisison(context)) {
                                    imagePicker.launch("image/*")
                                } else {
                                    imagePermission.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Pick Image")
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = categoryName,
                            onValueChange = {
                                categoryName = it
                            },

                            label = {
                                Text("Enter Food Category")
                            },
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)

                        )
                        Spacer(Modifier.height(5.dp))
//
//                        OutlinedTextField(
//                            value = foodName,
//                            onValueChange = {
//                                foodName = it
//                            },
//                            label = {
//                                Text("Enter Food Name")
//                            },
//                            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
//
//                        )

//                        Spacer(Modifier.height(5.dp))
//
//                        OutlinedTextField(
//                            value = foodPrice,
//                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//                            onValueChange = {
//                                foodPrice = it
//                            },
//                            label = {
//                                Text("Enter Food Price")
//                            },
//                            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
//
//                        )
//
//                        Spacer(modifier = Modifier.height(10.dp))


                        ElevatedButton(
                            modifier = Modifier.fillMaxWidth()
                                .padding(horizontal = 8.dp),

                            onClick = {    // save data to firestore

                                if (categoryName.isNotEmpty()) {

                                    // ADD data in firebase firestore
                                    val foodItemobj = FoodItem(
                                        id = "",
                                        image = imageUrl,
                                        categoryName = categoryName,
                                    )
                                    println("CHeck Food Item: $foodItemobj")
                                    db.collection("category").add(foodItemobj).addOnCompleteListener {
                                        if(it.isSuccessful){
                                            dismiss()
                                            Toast.makeText(context,"Category Added", Toast.LENGTH_SHORT).show()
                                        }else{
                                            Toast.makeText(context,it.exception?.message, Toast.LENGTH_SHORT).show()

                                        }
                                    }

                                }

                            },
                            colors = ButtonDefaults.elevatedButtonColors(
                                containerColor = colorResource(R.color.primaryGreen)
                            ),
                            shape = RoundedCornerShape(5.dp),
                        ) {
                            Text("ADD", color = Color.White)
                        }

                    }
                }


            }
        }
    )
}

    fun deleteItem(docId: String? = "") {
        val db = FirebaseFirestore.getInstance()
        db.collection("FoodItem").document(docId ?: "").delete()
            .addOnSuccessListener { delete ->
                Log.d("firestore", "Documment Succesfully Deleted!")
            }
            .addOnFailureListener { fail ->
                Log.d("firestore", "Error deleting document", fail)
            }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun checkPermisison(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.READ_MEDIA_IMAGES
        ) == PackageManager.PERMISSION_GRANTED

    }




