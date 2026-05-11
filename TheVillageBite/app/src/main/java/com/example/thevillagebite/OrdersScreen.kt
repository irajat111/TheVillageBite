package com.example.thevillagebite

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class OrdersActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OrdersScreen()
        }
    }
}

val foodlist = mutableStateListOf<FoodItem1>()

data class FoodItem1(
    var id: String? = "",
    val category: String? = "",
    val food: String? = "",
    val price: Int? = 0
)
@Composable  // ✅ ADD THIS — it was missing!
@Preview(showSystemUi = true)
fun OrdersScreen() {

    val db = Firebase.firestore

    var showDialog by remember { mutableStateOf(false) }
    var selectedId by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        db.collection("FoodItem1").get()
            .addOnSuccessListener { res ->
                foodlist.clear()
                for (doc in res) {
                    val foodobj = doc.toObject(FoodItem1::class.java)
                        .copy(id = doc.id)
                    foodlist.add(foodobj)
                }
            }
    }

    // ✅ ONE Box contains everything
    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        // ✅ Step 1 — LazyColumn INSIDE Box
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(7.dp)
        ) {
            items(foodlist) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = "Category : ${item.category}")
                            Spacer(modifier = Modifier.height(5.dp))
                            Text(text = "Food : ${item.food}")
                            Spacer(modifier = Modifier.height(5.dp))
                            Text(text = "Price : ${item.price}")
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        Row(
                            modifier = Modifier.padding(15.dp)
                        ) {

                            IconButton(
                                onClick = {

                                },
                                modifier = Modifier.size(35.dp)
                            ) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = "Edit",
                                    tint = Color.Red
                                )
                            }

                            Spacer(modifier = Modifier.padding(5.dp))

                            IconButton(
                                // Deleted Item in List or Firebase firestore
                                onClick = {
                                    deleteItem(item.id)
                                },
                                modifier = Modifier.size(35.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = Color.Red
                                )
                            }

                        }
                    }
                }
            }
        }

        // ✅ Step 2 — FAB INSIDE Box (floats on top)
        FloatingActionButton(
            onClick = { showDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = colorResource(R.color.teal_200)
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "",
                tint = Color.White
            )
        }
    }

    // ✅ Step 3 — Dialog OUTSIDE Box
    if (showDialog) {
        openDialogbox(
            showDialog = showDialog,
            dissmis = { showDialog = false },
            selectedId = selectedId
        )
    }
}

@Composable
fun openDialogbox(showDialog: Boolean, dissmis: () -> Unit, selectedId: String) {

    val context = LocalContext.current
    var category by remember { mutableStateOf("") }
    var food by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }


    Dialog(
        onDismissRequest = {
            dissmis()  // ✅ closes when user taps outside
        },
        content = {
            Box(
                modifier = Modifier.fillMaxWidth()
                    .background(color = colorResource(R.color.white),
                shape = RoundedCornerShape(16.dp)   // rounded corner shap
                    ).padding(16.dp)
            )
            {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(10.dp)
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

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = category,
                            onValueChange = {
                                category = it
                            },
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp),
                            label = { Text("Enter Food Category") }
                        )
                    Spacer(modifier = Modifier.height(5.dp))

                    OutlinedTextField(
                        value = food,
                        onValueChange = {
                            food = it
                        },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp),
                        label = { Text("Enter Food") }
                    )
                    Spacer(modifier = Modifier.height(5.dp))

                    OutlinedTextField(
                        value = price,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        onValueChange = {
                            price = it
                        },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp),
                        label = { Text("Enter Price") }
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    ElevatedButton(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp),
                        onClick = {

                            if (category.isNotEmpty() && food.isNotEmpty() && price.isNotEmpty()) {
                                dissmis()  // ✅ dialog band karo
                            }

                            // Add data in firebase


                            val foodItemobj = FoodItem1(
                                id = "",
                                category = category,
                                food = food,
                                price = price.toInt()
                            )

                            val firebaseobj = FirebaseFirestore.getInstance()
                            firebaseobj.collection("FoodItem1")
                                .add(foodItemobj)
                                .addOnSuccessListener { documentReference ->
                                    firebaseobj.collection("FoodItem1")
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



