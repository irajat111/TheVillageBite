package com.example.thevillagebiteuser

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.razorpay.Checkout
import org.json.JSONObject

@Composable
fun CartScreen() {

    val db       = Firebase.firestore
    val auth     = Firebase.auth
    val context  = LocalContext.current
    val activity = context as Activity

    var cartItems  by remember { mutableStateOf<List<CartModel>>(emptyList()) }
    var isLoading  by remember { mutableStateOf(true) }
    var quantities by remember { mutableStateOf<Map<String, Int>>(emptyMap()) }

    var showAddressDialog by remember { mutableStateOf(false) }
    var userName          by remember { mutableStateOf("") }
    var userEmail         by remember { mutableStateOf("") }
    var userAddress       by remember { mutableStateOf("") }
    var addressError      by remember { mutableStateOf("") }

    val totalPrice = cartItems.sumOf { item ->
        val qty   = quantities[item.documentId] ?: 1
        val price = item.foodprice ?: 0.0
        qty * price
    }

    LaunchedEffect(Unit) {
        val uid = auth.currentUser?.uid ?: return@LaunchedEffect

        db.collection("cart")
            .whereEqualTo("userId", uid)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) { isLoading = false; return@addSnapshotListener }
                if (snapshot != null) {
                    cartItems = snapshot.documents.mapNotNull { doc ->
                        val item = doc.toObject(CartModel::class.java)
                        item?.documentId = doc.id
                        item
                    }
                    isLoading = false
                }
            }

        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                userName    = doc.getString("name")    ?: ""
                userEmail   = doc.getString("email")   ?: auth.currentUser?.email ?: ""
                userAddress = doc.getString("address") ?: ""
            }

        if (userEmail.isEmpty()) userEmail = auth.currentUser?.email ?: ""
    }

    fun saveOrderToFirestore(paymentId: String) {
        val userId = auth.currentUser?.uid ?: return

        val orderItems = cartItems.map { item ->
            val qty = quantities[item.documentId] ?: 1
            mapOf(
                "foodname"     to (item.foodname    ?: ""),
                "foodprice"    to (item.foodprice    ?: 0.0),
                "productImage" to (item.productImage ?: ""),
                "quantity"     to qty
            )
        }

        val currentTotal = cartItems.sumOf { item ->
            val qty   = quantities[item.documentId] ?: 1
            val price = item.foodprice ?: 0.0
            qty * price
        }

        val order = hashMapOf(
            "userId"      to userId,
            "userName"    to userName,
            "userEmail"   to userEmail,
            "userAddress" to userAddress,
            "items"       to orderItems,
            "totalPrice"  to currentTotal,
            "status"      to "Pending",
            "paymentId"   to paymentId,
            "timestamp"   to System.currentTimeMillis()
        )

        db.collection("order").add(order)
            .addOnSuccessListener { docRef ->
                docRef.update("orderId", docRef.id)

                val walletEntry = hashMapOf(
                    "userId"      to userId,
                    "userName"    to userName,
                    "userEmail"   to userEmail,
                    "userAddress" to userAddress,
                    "orderId"     to docRef.id,
                    "paymentId"   to paymentId,
                    "amount"      to currentTotal,
                    "items"       to orderItems,
                    "description" to "Order #${docRef.id.takeLast(6).uppercase()}",
                    "timestamp"   to System.currentTimeMillis()
                )

                db.collection("wallet").add(walletEntry)
                    .addOnSuccessListener {
                        Log.d("WALLET", "Wallet entry saved successfully")
                    }
                    .addOnFailureListener { e ->
                        Log.e("WALLET", "Wallet save failed: ${e.message}")
                    }

                Toast.makeText(context, "Order Placed Successfully! 🎉", Toast.LENGTH_SHORT).show()

                cartItems.forEach { cartItem ->
                    db.collection("cart").document(cartItem.documentId ?: "").delete()
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Order Save Failed: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }

    fun startRazorpayPayment() {
        //val activity = context as? Activity
        val checkout = Checkout()
        checkout.setKeyID("rzp_test_SdiScR9yQT87HR")
            //Checkout.preload(context)



        try {
            val options = JSONObject().apply {
                put("name",        "The Village Bite")
                put("description", "Food Order Payment")
                put("currency",    "INR")
                put("amount",      (totalPrice * 100).toInt())
                put("prefill", JSONObject().apply {
                    put("name",    userName)
                    put("email",   userEmail)
                    put("contact", "")
                })
                put("theme", JSONObject().apply {
                    put("color", "#4CAF50")
                })
            }
            checkout.open(activity, options)
        } catch (e: Exception) {
            Toast.makeText(context, "Payment Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // ✅ rememberUpdatedState — hamesha latest cartItems/quantities capture karega
    val currentSaveOrder by rememberUpdatedState { paymentId: String ->
        saveOrderToFirestore(paymentId)
    }

    DisposableEffect(Unit) {
        CartPaymentHelper.onSuccess = { paymentId ->
            currentSaveOrder(paymentId)
        }
        CartPaymentHelper.onError = { code, desc ->
            Toast.makeText(context, "Payment Failed: $desc", Toast.LENGTH_SHORT).show()
        }
        onDispose {
            CartPaymentHelper.onSuccess = null
            CartPaymentHelper.onError   = null
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))

            cartItems.isEmpty() -> Text(
                text     = "Cart is Empty!",
                modifier = Modifier.align(Alignment.Center),
                fontSize = 16.sp,
                color    = Color.Gray
            )

            else -> {
                Box(Modifier.fillMaxSize()) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                            .padding(bottom = 120.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(cartItems) { cartItem ->
                            CartItemCard(
                                cartItem         = cartItem,
                                onQuantityChange = { newQty ->
                                    quantities = quantities.toMutableMap().also {
                                        it[cartItem.documentId!!] = newQty
                                    }
                                }
                            )
                        }
                    }

                    Card(
                        modifier  = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .padding(5.dp),
                        shape     = RoundedCornerShape(20.dp),
                        colors    = CardDefaults.cardColors(containerColor = colorResource(R.color.white)),
                        elevation = CardDefaults.cardElevation(12.dp),
                        onClick   = {}
                    ) {
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .background(colorResource(R.color.white))
                                .padding(10.dp)
                        ) {
                            Row(
                                Modifier.fillMaxWidth().padding(vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Total Price:", fontWeight = FontWeight.Bold,
                                    color = colorResource(R.color.primaryGreen))
                                Spacer(Modifier.weight(1f))
                                Text("₹${"%.2f".format(totalPrice)}", fontWeight = FontWeight.Bold)
                            }

                            ElevatedButton(
                                onClick  = { showAddressDialog = true },
                                modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                                shape    = RoundedCornerShape(7.dp),
                                colors   = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(R.color.cardGreen))
                            ) {
                                Text("Buy 🛒", color = Color.Black)
                            }
                        }
                    }
                }
            }
        }

        if (showAddressDialog) {
            Dialog(onDismissRequest = { showAddressDialog = false }) {
                Card(
                    shape     = RoundedCornerShape(16.dp),
                    colors    = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {

                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment     = Alignment.CenterVertically
                        ) {
                            Text("📦 Delivery Details", fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = colorResource(R.color.primaryGreen))
                            IconButton(onClick = { showAddressDialog = false }) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Red)
                            }
                        }

                        HorizontalDivider()
                        Spacer(Modifier.height(12.dp))

                        OutlinedTextField(
                            value         = userName,
                            onValueChange = { userName = it },
                            label         = { Text("Full Name") },
                            leadingIcon   = { Icon(Icons.Default.Person, contentDescription = null) },
                            modifier      = Modifier.fillMaxWidth(),
                            singleLine    = true,
                            shape         = RoundedCornerShape(10.dp)
                        )

                        Spacer(Modifier.height(8.dp))

                        OutlinedTextField(
                            value         = userEmail,
                            onValueChange = { userEmail = it },
                            label         = { Text("Email") },
                            leadingIcon   = { Icon(Icons.Default.Email, contentDescription = null) },
                            modifier      = Modifier.fillMaxWidth(),
                            singleLine    = true,
                            shape         = RoundedCornerShape(10.dp)
                        )

                        Spacer(Modifier.height(8.dp))

                        OutlinedTextField(
                            value         = userAddress,
                            onValueChange = { userAddress = it; addressError = "" },
                            label         = { Text("Delivery Address") },
                            leadingIcon   = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                            modifier      = Modifier.fillMaxWidth(),
                            minLines      = 2,
                            isError       = addressError.isNotEmpty(),
                            supportingText = {
                                if (addressError.isNotEmpty())
                                    Text(addressError, color = Color.Red, fontSize = 12.sp)
                            },
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(Modifier.height(8.dp))

                        Card(
                            Modifier.fillMaxWidth(),
                            shape  = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
                        ) {
                            Row(
                                Modifier.fillMaxWidth().padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Total Amount:", fontWeight = FontWeight.Bold)
                                Text("₹${"%.2f".format(totalPrice)}", fontWeight = FontWeight.Bold,
                                    color = colorResource(R.color.primaryGreen))
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        ElevatedButton(
                            onClick = {
                                if (userAddress.trim().isEmpty()) {
                                    addressError = "Please enter delivery address"
                                    return@ElevatedButton
                                }
                                auth.currentUser?.uid?.let { uid ->
                                    db.collection("users").document(uid)
                                        .update(mapOf(
                                            "name"    to userName,
                                            "email"   to userEmail,
                                            "address" to userAddress
                                        ))
                                }
                                showAddressDialog = false
                                startRazorpayPayment()
                            },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape    = RoundedCornerShape(10.dp),
                            colors   = ButtonDefaults.buttonColors(
                                containerColor = colorResource(R.color.cardGreen))
                        ) {
                            Icon(Icons.Default.Payment, contentDescription = null, tint = Color.Black)
                            Spacer(Modifier.width(8.dp))
                            Text("Proceed to Pay 💳", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

object CartPaymentHelper {
    var onSuccess: ((String) -> Unit)? = null
    var onError:   ((Int, String) -> Unit)? = null
}

@Composable
fun CartItemCard(
    cartItem: CartModel,
    onQuantityChange: (Int) -> Unit
) {
    val db      = Firebase.firestore
    val context = LocalContext.current
    var quantity         by remember { mutableStateOf(1) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var deleteDocumentId by remember { mutableStateOf("") }

    Card(
        modifier  = Modifier.fillMaxWidth().wrapContentHeight(),
        shape     = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        onClick   = {},
        colors    = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier            = Modifier.fillMaxWidth().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(Modifier.fillMaxWidth()) {
                AsyncImage(
                    model              = cartItem.productImage,
                    contentDescription = cartItem.foodname,
                    modifier           = Modifier.size(70.dp),
                    contentScale       = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(6.dp))
                Column(Modifier.fillMaxWidth()) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text(text = cartItem.foodname ?: "N/A", fontWeight = FontWeight.Bold,
                            fontSize = 16.sp, color = Color.Black)
                        Spacer(Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "₹${cartItem.foodprice}", fontSize = 14.sp, color = Color(0xFF388E3C))
                        Spacer(Modifier.weight(1f))
                        Icon(
                            Icons.Default.Delete, contentDescription = "",
                            tint     = Color.Red,
                            modifier = Modifier.size(28.dp).align(Alignment.CenterVertically)
                                .clickable {
                                    deleteDocumentId = cartItem.documentId ?: ""
                                    showDeleteDialog = true
                                }
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center) {
                        Box(
                            Modifier.size(20.dp)
                                .background(colorResource(R.color.primaryGreen), RoundedCornerShape(3.dp))
                                .clickable { if (quantity > 1) { quantity--; onQuantityChange(quantity) } },
                            contentAlignment = Alignment.Center
                        ) { Icon(Icons.Default.HorizontalRule, contentDescription = "", tint = Color.White) }
                        Spacer(Modifier.width(5.dp))
                        Text(text = quantity.toString(), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.width(5.dp))
                        Box(
                            Modifier.size(20.dp)
                                .background(colorResource(R.color.primaryGreen), RoundedCornerShape(3.dp))
                                .clickable { quantity++; onQuantityChange(quantity) },
                            contentAlignment = Alignment.Center
                        ) { Icon(Icons.Default.Add, contentDescription = "", tint = Color.White) }
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        Dialog(onDismissRequest = { showDeleteDialog = false }) {
            Card(shape = RoundedCornerShape(16.dp),
                colors    = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, contentDescription = "",
                                tint = Color.Red, modifier = Modifier.size(22.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Remove Item? 🗑️", fontSize = 15.sp,
                                fontWeight = FontWeight.Bold, color = Color.Red)
                        }
                        IconButton(onClick = { showDeleteDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Red)
                        }
                    }
                    HorizontalDivider()
                    Spacer(Modifier.height(12.dp))
                    Text("Are you sure you want to remove this item?", fontSize = 13.sp,
                        color = Color.Black, textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(16.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Card(
                            Modifier.weight(1f).clickable { showDeleteDialog = false },
                            shape  = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                        ) {
                            Box(Modifier.fillMaxWidth().height(50.dp),
                                contentAlignment = Alignment.Center) {
                                Text("Cancel ✖️", fontSize = 13.sp)
                            }
                        }
                        Card(
                            Modifier.weight(1f).clickable {
                                db.collection("cart").document(deleteDocumentId).delete()
                                    .addOnSuccessListener {
                                        Toast.makeText(context, "Item Removed", Toast.LENGTH_SHORT).show()
                                    }
                                showDeleteDialog = false
                            },
                            shape  = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                        ) {
                            Box(Modifier.fillMaxWidth().height(50.dp),
                                contentAlignment = Alignment.Center) {
                                Text("Remove 🗑️", fontSize = 13.sp, color = Color.Red)
                            }
                        }
                    }
                }
            }
        }
    }
}