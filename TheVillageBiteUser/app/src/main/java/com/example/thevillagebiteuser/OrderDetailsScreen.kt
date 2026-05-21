package com.example.thevillagebiteuser

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

// ✅ OrderModel yahan se hata diya — OrderModel.kt mein define hai

@Composable
fun OrderDetailsScreen(navController: NavHostController) {

    val db = Firebase.firestore
    val auth = Firebase.auth
    val currentUser = auth.currentUser

    var orders by remember { mutableStateOf<List<OrderModel>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    val greenColor = Color(0xFF4CAF50)

    LaunchedEffect(Unit) {
        currentUser?.uid?.let { uid ->
            db.collection("order")
                .whereEqualTo("userId", uid)
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
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(6.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            onClick = { }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "My Orders",
                        color = Color.Black,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Track your orders here",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                }

                Text(text = "🍔🍟🍕", fontSize = 28.sp)
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
                        Text(text = "🛒", fontSize = 50.sp)
                        Spacer(Modifier.height(8.dp))
                        Text(text = "No Orders Yet!", fontSize = 16.sp, color = Color.Gray)
                        Spacer(Modifier.height(4.dp))
                        Text(text = "Place your first order now", fontSize = 13.sp, color = Color.LightGray)
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
                            CustomerOrderCard(order = order)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CustomerOrderCard(order: OrderModel) {

    val greenColor = Color(0xFF4CAF50)

    val date = order.timestamp?.let {
        java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a", java.util.Locale.getDefault())
            .format(java.util.Date(it))
    } ?: "N/A"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        onClick = { }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "📅 $date", fontSize = 12.sp, color = Color.Gray)

                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = when (order.status) {
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
                        text = order.status ?: "Pending",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (order.status) {
                            "Pending"          -> Color(0xFFF57F17)
                            "Preparing"        -> Color(0xFF1565C0)
                            "Out for Delivery" -> Color(0xFF283593)
                            "Delivered"        -> Color(0xFF2E7D32)
                            "Cancelled"        -> Color.Red
                            else               -> Color.Gray
                        },
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
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

            order.userAddress?.let { address ->
                if (address.isNotEmpty()) {
                    Spacer(Modifier.height(6.dp))
                    HorizontalDivider(color = Color(0xFFEEEEEE))
                    Spacer(Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.Top) {
                        Text(text = "📍 ", fontSize = 12.sp)
                        Text(text = address, fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }

            Spacer(Modifier.height(6.dp))
            Text(
                text = "Order ID: #${order.orderId?.takeLast(6)?.uppercase() ?: "N/A"}",
                fontSize = 11.sp,
                color = Color.LightGray
            )
        }
    }
}