package com.example.thevillagebiteuser

import android.widget.Toast
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.HorizontalRule
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil3.compose.AsyncImage
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

@Composable
fun CartScreen() {

    val db = Firebase.firestore
    val auth = Firebase.auth


    val context = LocalContext.current

    var cartItems by remember { mutableStateOf<List<CartModel>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var quantities by remember { mutableStateOf<Map<String, Int>>(emptyMap()) }

    val totalPrice = cartItems.sumOf { item ->
        val qty = quantities[item.documentId] ?: 1
//        val price = item.foodprice?.toDoubleOrNull() ?: 0.0
        val price = item.foodprice ?: 0.0
        qty * price
    }

    LaunchedEffect(Unit) {
        db.collection("cart")
            .whereEqualTo("userId", auth.currentUser?.uid.toString())
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    isLoading = false
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    cartItems = snapshot.documents.mapNotNull { doc ->
                        val item = doc.toObject(CartModel::class.java)
                        item?.documentId = doc.id
                        item
                    }
                    isLoading = false
                }
            }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            cartItems.isEmpty() -> {
                Text(
                    text = "Cart is Empty!",
                    modifier = Modifier.align(Alignment.Center),
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }
            else -> {
                Box(Modifier.fillMaxSize()) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                            .padding(bottom = 120.dp), // ✅ Bottom column se overlap na ho
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(cartItems) { cartItem ->
                            CartItemCard(
                                cartItem = cartItem,
                                onQuantityChange = { newQty -> // ✅ Ye add kiya
                                    quantities = quantities.toMutableMap().also {
                                        it[cartItem.documentId!!] = newQty
                                    }
                                }
                            )
                        }
                    }


                    Card(
                        onClick = { },
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .padding(5.dp),
                        shape = RoundedCornerShape( 20.dp
//                            topStart = 20.dp,
//                            topEnd = 20.dp
                        ), // ✅ Sirf upar rounded
                        colors = CardDefaults.cardColors(containerColor = colorResource(R.color.white)),
                        elevation = CardDefaults.cardElevation(12.dp)  // ✅ Shadow upar se aayega
                    ) {
                        Column(
                            Modifier
                                .fillMaxWidth()
//                                .background(color = Color.White)
                                .background(colorResource(R.color.white))
//                                .align(Alignment.BottomCenter)
                                .padding(10.dp)
                        ) {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Total Price:",
                                    fontWeight = FontWeight.Bold,
                                    color = colorResource(R.color.primaryGreen))
                                Spacer(Modifier.weight(1f))
                                Text("₹$totalPrice")
                            }

                            // data is not order in click Buy button
//                        ElevatedButton(
//                            onClick = { },
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(vertical = 10.dp),
//                            shape = RoundedCornerShape(7.dp),
//                            colors = ButtonDefaults.buttonColors(
//                                containerColor = colorResource(R.color.primaryGreen)
//                            )
//                        )
//                        {
//                            Text("Buy 🛒", color = Color.White)
//                        }

                            ElevatedButton(
                                onClick = {
                                    val userId = auth.currentUser?.uid ?: return@ElevatedButton

                                    // ✅ Cart items ko order format mein convert karo
                                    val orderItems = cartItems.map { item ->
                                        val qty = quantities[item.documentId] ?: 1
                                        mapOf(
                                            "foodname" to (item.foodname ?: ""),
                                            "foodprice" to (item.foodprice ?: 0.0),
                                            "productImage" to (item.productImage ?: ""),
                                            "quantity" to qty
                                        )
                                    }

                                    // ✅ OrderModel ke hisaab se data banao
                                    val order = OrderModel(
                                        userId = userId,
                                        items = orderItems,
                                        totalPrice = totalPrice,
                                        status = "Pending",
                                        timestamp = System.currentTimeMillis()
                                    )

                                    // ✅ Step 1 — Firestore mein order save karo
                                    db.collection("order").add(order)
                                        .addOnSuccessListener {
                                            Toast.makeText(
                                                context,
                                                "Order Placed Successfully! 🎉",
                                                Toast.LENGTH_SHORT
                                            ).show()

                                            // ✅ Step 2 — Cart ke saare items delete karo
                                            cartItems.forEach { cartItem ->
                                                db.collection("cart")
                                                    .document(cartItem.documentId ?: "")
                                                    .delete()
                                            }
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(
                                                context,
                                                "Order Failed: ${it.message}",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp),
                                shape = RoundedCornerShape(7.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(R.color.cardGreen)
                                )
                            ) {
                                Text("Buy 🛒", color = Color.Black)
                            }

                        }
                    }
                }
            }
        }
    }

} // ✅ CartScreen yahan band hua


@Composable
fun CartItemCard(
    cartItem: CartModel,
    onQuantityChange: (Int) -> Unit  // ✅ Parameter add kiya
) {

    val db = Firebase.firestore
    val context = LocalContext.current
    var quantity by remember { mutableStateOf(1) }

    // var for dialogbox
    var showDeleteDialog by remember { mutableStateOf(false) }
    var deleteDocumentId by remember { mutableStateOf("") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        onClick = { },
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(Modifier.fillMaxWidth()) {
                AsyncImage(
                    model = cartItem.productImage,
                    contentDescription = cartItem.foodname,
                    modifier = Modifier.size(70.dp),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(6.dp))

                Column(Modifier.fillMaxWidth()) {

                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = cartItem.foodname ?: "N/A",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.Black
                        )
                        Spacer(Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "₹${cartItem.foodprice}", // ✅ totalItemPrice use kiya
                            fontSize = 14.sp,
                            color = Color(0xFF388E3C)
                        )

                        Spacer(Modifier.weight(1f))

                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "",
                            tint = Color.Red,
                            modifier = Modifier
                                .size(28.dp)
                                .align(Alignment.CenterVertically)
                                .clickable {

                                    //if you want to delete item without dialogbox
//                                    db.collection("cart")
//                                        .document(cartItem.documentId!!)
//                                        .delete()
//                                        .addOnSuccessListener {
//                                            Toast.makeText(
//                                                context,
//                                                "Item deleted Successfully",
//                                                Toast.LENGTH_SHORT
//                                            ).show()
//                                        }
//                                        .addOnFailureListener {
//                                            Toast.makeText(
//                                                context,
//                                                it.message,
//                                                Toast.LENGTH_SHORT
//                                            ).show()
//                                        }

                                    // ✅ Dialog open karo
                                    deleteDocumentId = cartItem.documentId ?: ""
                                    showDeleteDialog = true

                                }
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            Modifier
                                .size(20.dp)
                                .background(
                                    color = colorResource(R.color.primaryGreen),
                                    shape = RoundedCornerShape(3.dp)
                                )
                                .clickable {
                                    if (quantity > 1) { // ✅ 1 se kam nahi jayega
                                        quantity--
                                        onQuantityChange(quantity)
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.HorizontalRule,
                                contentDescription = "",
                                tint = colorResource(R.color.white)
                            )
                        }

                        Spacer(Modifier.width(5.dp))

                        Text(
                            text = quantity.toString(),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(Modifier.width(5.dp))

                        Box(
                            Modifier
                                .size(20.dp)
                                .background(
                                    color = colorResource(R.color.primaryGreen),
                                    shape = RoundedCornerShape(3.dp)
                                )
                                .clickable {
                                    quantity++
                                    onQuantityChange(quantity)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "",
                                tint = colorResource(R.color.white)
                            )
                        }
                    }
                }
            }
        }

    }


    // ✅ Dialog — Composable ke end mein
    if (showDeleteDialog) {
        Dialog(onDismissRequest = { showDeleteDialog = false }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    // ✅ Title Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "",
                                tint = Color.Red,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "Remove Item? 🗑️",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Red
                            )
                        }
                        IconButton(onClick = { showDeleteDialog = false }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.Red
                            )
                        }
                    }

                    // ✅ Divider
                    Divider(color = Color.LightGray, thickness = 0.5.dp)

                    Spacer(Modifier.height(12.dp))

                    // ✅ Message Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
//                            containerColor = Color(0xFFFFEBEE)
                            containerColor = Color.White
                        ),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Are you sure you want to remove this item from your cart? This action cannot be undone!",
                                fontSize = 13.sp,
                                color = Color.Black,
                                lineHeight = 20.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // ✅ 2 Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Cancel
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
                                    .fillMaxWidth()
                                    .height(50.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Cancel ✖️",
                                    fontSize = 13.sp,
                                    color = Color.Black,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        // Yes Delete
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    // ✅ Firebase se delete
                                    db.collection("cart")
                                        .document(deleteDocumentId)
                                        .delete()
                                        .addOnSuccessListener {
                                            Toast.makeText(
                                                context,
                                                "Item Removed Successfully",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(
                                                context,
                                                it.message,
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
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
                                    .fillMaxWidth()
                                    .height(50.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Remove 🗑️",
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




