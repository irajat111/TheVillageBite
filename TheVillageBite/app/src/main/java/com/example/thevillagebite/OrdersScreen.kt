package com.example.thevillagebite

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext          // ✅ ADD THIS
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

// ✅ OrderModel — Firestore se data map hoga
data class OrderModel(
    val orderId: String? = null,
    val userId: String? = null,
    val userName: String? = null,
    val userPhone: String? = null,
    val userAddress: String? = null,
    val items: List<Map<String, Any>> = emptyList(),
    val totalPrice: Double? = 0.0,
    val status: String? = "Pending",
    val timestamp: Long? = null
)

class AdminOrdersActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AdminOrdersScreen()
        }
    }
}

@Composable
fun AdminOrdersScreen() {

    val db = Firebase.firestore
    val greenColor = Color(0xFF4CAF50)

    var orders by remember { mutableStateOf<List<OrderModel>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        db.collection("order")
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    orders = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(OrderModel::class.java)
                            ?.copy(orderId = doc.id)
                    }.sortedByDescending { it.timestamp }
                    isLoading = false
                }
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(6.dp),
            colors = CardDefaults
                .cardColors(containerColor = colorResource(R.color.white)),
            onClick = { }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "All Customer Orders",
                        color = Color.Black,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (orders.isNotEmpty()) {
                        Text(
                            text = "${orders.size} orders total",
                            color = Color.Black.copy(alpha = 0.6f),
                            fontSize = 12.sp
                        )
                    }
                }
                Text(text = "🍟", fontSize = 28.sp)
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = greenColor
                    )
                }

                orders.isEmpty() -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "📦", fontSize = 50.sp)
                        Spacer(Modifier.height(8.dp))
                        Text(text = "No Orders Yet!", fontSize = 16.sp, color = Color.Gray)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Customer orders will appear here",
                            fontSize = 13.sp,
                            color = Color.LightGray
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(orders) { order ->
                            AdminOrderCard(order = order, db = db)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminOrderCard(order: OrderModel, db: FirebaseFirestore) {

    val context = LocalContext.current                   // ✅ GET CONTEXT HERE
    val greenColor = Color(0xFF4CAF50)
    val statusOptions = listOf("Pending", "Preparing", "Out for Delivery", "Delivered", "Cancelled")

    var expanded by remember { mutableStateOf(false) }
    var currentStatus by remember { mutableStateOf(order.status ?: "Pending") }

    val date = order.timestamp?.let {
        java.text.SimpleDateFormat(
            "dd MMM yyyy, hh:mm a",
            java.util.Locale.getDefault()
        ).format(java.util.Date(it))
    } ?: "N/A"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        onClick = {}
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "📅 $date", fontSize = 12.sp, color = Color.Gray)
                Text(
                    text = "#${order.orderId?.takeLast(6)?.uppercase() ?: "N/A"}",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(Modifier.height(10.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "👤 Customer Details",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF388E3C)
                    )
                    Spacer(Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Name:  ", fontSize = 12.sp, color = Color.Gray)
                        Text(
                            text = order.userName ?: "N/A",
                            fontSize = 12.sp,
                            color = Color.DarkGray,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Phone:  ", fontSize = 12.sp, color = Color.Gray)
                        Text(
                            text = order.userPhone ?: "N/A",
                            fontSize = 12.sp,
                            color = Color.DarkGray,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.Top) {
                        Text(text = "Address:  ", fontSize = 12.sp, color = Color.Gray)
                        Text(
                            text = order.userAddress ?: "N/A",
                            fontSize = 12.sp,
                            color = Color.DarkGray,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(Modifier.height(10.dp))
            HorizontalDivider(color = Color(0xFFEEEEEE))
            Spacer(Modifier.height(10.dp))

            Text(
                text = "Items Ordered:",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(Modifier.height(6.dp))

            order.items.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "• ", color = greenColor, fontSize = 14.sp)
                        Text(text = "${item["foodname"] ?: "N/A"}", fontSize = 13.sp, color = Color.Black)
                        Text(text = "  x${item["quantity"] ?: 1}", fontSize = 12.sp, color = Color.Gray)
                    }
                    Text(
                        text = "₹${item["foodprice"] ?: 0.0}",
                        fontSize = 13.sp,
                        color = greenColor,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(Modifier.height(10.dp))
            HorizontalDivider(color = Color(0xFFEEEEEE))
            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Total Amount:", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Text(text = "₹${order.totalPrice}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = greenColor)
            }

            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFEEEEEE))
            Spacer(Modifier.height(10.dp))

            Text(
                text = "Update Order Status:",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(Modifier.height(6.dp))

            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = { expanded = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = when (currentStatus) {
                            "Pending"          -> Color(0xFFFFF9C4)
                            "Preparing"        -> Color(0xFFE3F2FD)
                            "Out for Delivery" -> Color(0xFFE8EAF6)
                            "Delivered"        -> Color(0xFFE8F5E9)
                            "Cancelled"        -> Color(0xFFFFEBEE)
                            else               -> Color(0xFFF5F5F5)
                        }
                    )
                ) {
                    Text(
                        text = currentStatus,
                        fontWeight = FontWeight.Bold,
                        color = when (currentStatus) {
                            "Pending"          -> Color(0xFFF57F17)
                            "Preparing"        -> Color(0xFF1565C0)
                            "Out for Delivery" -> Color(0xFF283593)
                            "Delivered"        -> Color(0xFF2E7D32)
                            "Cancelled"        -> Color.Red
                            else               -> Color.Gray
                        }
                    )
                    Spacer(Modifier.weight(1f))
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = Color.Gray
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    statusOptions.forEach { status ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = status,
                                    fontWeight = FontWeight.Medium,
                                    color = when (status) {
                                        "Pending"          -> Color(0xFFF57F17)
                                        "Preparing"        -> Color(0xFF1565C0)
                                        "Out for Delivery" -> Color(0xFF283593)
                                        "Delivered"        -> Color(0xFF2E7D32)
                                        "Cancelled"        -> Color.Red
                                        else               -> Color.Gray
                                    }
                                )
                            },
                            onClick = {
                                currentStatus = status
                                expanded = false

                                order.orderId?.let { id ->
                                    // ✅ STEP 1: Firestore mein status update karo
                                    db.collection("order")
                                        .document(id)
                                        .update("status", status)

                                    // ✅ STEP 2: Admin ko local notification do
                                    AdminNotificationHelper.sendOrderStatusChangedNotification(
                                        context = context,
                                        status  = status,
                                        orderId = id
                                    )

                                    // ✅ STEP 3: User ke liye Firestore mein notification entry save karo
                                    //    (sendOrderStatusChangedNotification ke andar ye already hota hai)
                                    //    Par agar user ka userName chahiye notification mein, yahan se pass karo:
                                    db.collection("userNotifications").add(
                                        mapOf(
                                            "userId"    to (order.userId ?: ""),
                                            "title"     to "Order Update 🔔",
                                            "body"      to "Order #${id.takeLast(6).uppercase()} — Status: $status",
                                            "orderId"   to id,
                                            "timestamp" to System.currentTimeMillis(),
                                            "read"      to false
                                        )
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}