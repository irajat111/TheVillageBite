package com.example.thevillagebite

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.platform.LocalLocale

// ── Data model ────────────────────────────────────────────────
data class AdminWalletEntry(
    val id          : String = "",
    val orderId     : String = "",
    val paymentId   : String = "",
    val amount      : Double = 0.0,
    val userName    : String = "",
    val userEmail   : String = "",
    val userAddress : String = "",
    val description : String = "",
    val timestamp   : Long   = 0L,
    val items       : List<Map<String, Any>> = emptyList()
)

@Composable
fun AdminWalletScreen() {

    val db         = Firebase.firestore
    val greenColor = Color(0xFF4CAF50)

    var entries   by remember { mutableStateOf<List<AdminWalletEntry>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    val totalRevenue = entries.sumOf { it.amount }

    // ✅ FIX: Wallet document mein hi sab data hai, order fetch karne ki zaroorat nahi
    LaunchedEffect(Unit) {
        db.collection("wallet")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("WALLET", "Error: ${error.message}")
                    isLoading = false
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    Log.d("WALLET", "Docs found: ${snapshot.documents.size}")

                    val list = snapshot.documents.mapNotNull { doc ->
                        try {
                            @Suppress("UNCHECKED_CAST")
                            val items = (doc.get("items") as? List<Map<String, Any>>) ?: emptyList()

                            AdminWalletEntry(
                                id          = doc.id,
                                orderId     = doc.getString("orderId")     ?: "",
                                paymentId   = doc.getString("paymentId")   ?: "",
                                amount      = doc.getDouble("amount")      ?: 0.0,
                                userName    = doc.getString("userName")    ?: "Unknown",
                                userEmail   = doc.getString("userEmail")   ?: "",
                                userAddress = doc.getString("userAddress") ?: "",
                                description = doc.getString("description") ?: "",
                                timestamp   = doc.getLong("timestamp")     ?: 0L,
                                items       = items
                            )
                        } catch (e: Exception) {
                            Log.e("WALLET", "Parse error: ${e.message}")
                            null
                        }
                    }.sortedByDescending { it.timestamp }

                    entries   = list
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
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(6.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            onClick = {}
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .background(Color(0xFF4CAF50).copy(alpha = 0.12f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.AccountBalanceWallet,
                            contentDescription = null,
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "Revenue & Payments",
                            color = Color.Black,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "View all transactions",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                }

                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = greenColor)
            }
            return@Column
        }

        LazyColumn(
            modifier            = Modifier.fillMaxSize(),
            contentPadding      = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            item {
                Card(
                    modifier  = Modifier.fillMaxWidth(),
                    shape     = RoundedCornerShape(20.dp),
                    colors    = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(8.dp),
                    onClick = { }
                ) {
                    Column(
                        modifier            = Modifier.fillMaxWidth().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier         = Modifier
                                .size(64.dp)
                                .background(greenColor.copy(alpha = 0.12f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.TrendingUp, contentDescription = null,
                                tint = greenColor, modifier = Modifier.size(34.dp))
                        }
                        Spacer(Modifier.height(12.dp))
                        Text("Total Revenue", fontSize = 14.sp, color = Color.Gray)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text       = "₹${"%.2f".format(totalRevenue)}",
                            fontSize   = 34.sp,
                            fontWeight = FontWeight.Bold,
                            color      = greenColor
                        )
                        Spacer(Modifier.height(10.dp))
                        HorizontalDivider()
                        Spacer(Modifier.height(10.dp))

                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StatChip(
                                label = "Payments",
                                value = entries.size.toString(),
                                icon  = Icons.Default.Payment,
                                color = greenColor
                            )
                            StatChip(
                                label = "Avg Order",
                                value = "₹${"%.0f".format(if (entries.isEmpty()) 0.0 else totalRevenue / entries.size)}",
                                icon  = Icons.Default.Analytics,
                                color = Color(0xFF1565C0)
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    text       = "All Transactions",
                    fontSize   = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color      = Color.Black
                )
            }

            if (entries.isEmpty()) {
                item {
                    Box(
                        Modifier.fillMaxWidth().padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("💳", fontSize = 48.sp)
                            Spacer(Modifier.height(8.dp))
                            Text("No Payments Yet", fontSize = 16.sp, color = Color.Gray)
                        }
                    }
                }
            }

            items(entries) { entry ->
                AdminPaymentCard(entry = entry, greenColor = greenColor)
            }
        }
    }
}

// ── Single Payment Card with expandable items ─────────────────
@Composable
fun AdminPaymentCard(entry: AdminWalletEntry, greenColor: Color) {

    var expanded by remember { mutableStateOf(false) }

    // ✅ FIX: LocalLocale deprecated hata diya, direct Locale.getDefault() use kiya
    val date = SimpleDateFormat("dd MMM yyyy, hh:mm a", LocalLocale.current.platformLocale)
        .format(Date(entry.timestamp))

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp),
        onClick = { }
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {

            Row(
                modifier          = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier         = Modifier
                        .size(44.dp)
                        .background(greenColor.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text       = entry.userName.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                        fontSize   = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color      = greenColor
                    )
                }

                Spacer(Modifier.width(10.dp))

                Column(Modifier.weight(1f)) {
                    Text(
                        text       = entry.userName.ifEmpty { "Unknown User" },
                        fontSize   = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color      = Color.Black
                    )
                    Text(
                        text     = entry.userEmail.ifEmpty { "No email" },
                        fontSize = 12.sp,
                        color    = Color.Gray
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text       = "₹${"%.2f".format(entry.amount)}",
                        fontSize   = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color      = greenColor
                    )
                    Card(
                        shape  = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
                    ) {
                        Text(
                            text       = "Paid ✅",
                            fontSize   = 10.sp,
                            color      = greenColor,
                            fontWeight = FontWeight.Bold,
                            modifier   = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(10.dp))
            HorizontalDivider(color = Color(0xFFEEEEEE))
            Spacer(Modifier.height(8.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("📅 $date", fontSize = 11.sp, color = Color.Gray)
            }
            Spacer(Modifier.height(4.dp))
            Text(
                "Payment ID: ${if (entry.paymentId.length > 12) entry.paymentId.takeLast(12).uppercase() else entry.paymentId.uppercase()}",
                fontSize = 10.sp, color = Color.LightGray
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Order ID: #${if (entry.orderId.length > 6) entry.orderId.takeLast(6).uppercase() else entry.orderId.uppercase()}",
                fontSize = 10.sp, color = Color.LightGray
            )

            if (entry.userAddress.isNotEmpty()) {
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.Top) {
                    Icon(Icons.Default.LocationOn, contentDescription = null,
                        tint = greenColor, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(entry.userAddress, fontSize = 11.sp, color = Color.Gray)
                }
            }

            if (entry.items.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                HorizontalDivider(color = Color(0xFFEEEEEE))

                Row(
                    modifier  = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = !expanded }
                        .padding(vertical = 8.dp),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text       = "🍽️ Order Items (${entry.items.size})",
                        fontSize   = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color      = Color.Black
                    )
                    Icon(
                        imageVector        = if (expanded) Icons.Default.KeyboardArrowUp
                        else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint               = greenColor
                    )
                }

                AnimatedVisibility(
                    visible = expanded,
                    enter   = expandVertically(),
                    exit    = shrinkVertically()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF9FBF9), RoundedCornerShape(10.dp))
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        entry.items.forEach { item ->
                            Row(
                                modifier          = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("• ", color = greenColor, fontSize = 14.sp)
                                    Text(
                                        text     = "${item["foodname"] ?: "N/A"}",
                                        fontSize = 13.sp,
                                        color    = Color.Black
                                    )
                                    Text(
                                        text     = "  ×${item["quantity"] ?: 1}",
                                        fontSize = 12.sp,
                                        color    = Color.Gray
                                    )
                                }
                                Text(
                                    text       = "₹${item["foodprice"] ?: 0.0}",
                                    fontSize   = 13.sp,
                                    color      = greenColor,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        HorizontalDivider(color = Color(0xFFDDDDDD))

                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(
                                text       = "₹${"%.2f".format(entry.amount)}",
                                fontWeight = FontWeight.Bold,
                                fontSize   = 13.sp,
                                color      = greenColor
                            )
                        }
                    }
                }
            }
        }
    }
}

// ── Small stat chip ───────────────────────────────────────────
@Composable
fun StatChip(
    label: String,
    value: String,
    icon:  androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier         = Modifier
                .size(40.dp)
                .background(color.copy(alpha = 0.12f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null,
                tint = color, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.height(4.dp))
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = color)
        Text(label, fontSize = 11.sp, color = Color.Gray)
    }
}