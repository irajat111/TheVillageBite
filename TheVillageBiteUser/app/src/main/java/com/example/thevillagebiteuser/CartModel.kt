package com.example.thevillagebiteuser

import com.google.firebase.firestore.DocumentId

data class CartModel(
    var id: String? = "",
    var foodname: String? = "",
    var foodprice: Double? = 0.0,
    var foodDescriprion: String? = "",
    var productImage: String? = null,
    var userId: String? = null,
    var productId: String? = null,
    var documentId: String? = null
)
// Data class for fetch data from Firebase db -> collection -> CategoryClass
data class ProductClass(
    var id: String? = "",
    val foodname: String? = "",
    val foodprice: Double? = 0.0,
    val foodDescriprion: String? = "",
    val productImage: String? = null
)

data class CategoryClass(
    var id: String? = "",
    val categoryName: String? = "",
    var image: String? = null
)


data class OrderModel(
    var id: String? = "",
    val userId: String? = "",
    val items: List<Map<String, Any>> = emptyList(),
    val totalPrice: Double? = 0.0,
    val status: String? = "Pending",
    val timestamp: Long? = System.currentTimeMillis()
)