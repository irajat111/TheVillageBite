package com.example.thevillagebiteuser

// ✅ CartModel — cart items ke liye
data class CartModel(
    var documentId: String? = null,
    var foodname: String? = "",
    var foodprice: Double? = 0.0,
    var foodDescription: String? = "",
    var productImage: String? = null,
    var userId: String? = null,
    var productId: String? = null
)

// ✅ ProductClass — product details ke liye
data class ProductClass(
    var id: String? = "",
    val foodname: String? = "",
    val foodprice: Double? = 0.0,
    val foodDescriprion: String? = "",
    val productImage: String? = null
)

// ✅ CategoryClass — categories ke liye
data class CategoryClass(
    var id: String? = "",
    val categoryName: String? = "",
    var image: String? = null
)

// ✅ OrderModel — orders ke liye
data class OrderModel(
    var orderId: String? = null,
    var userId: String? = null,
    var items: List<Map<String, Any>> = emptyList(),
    var totalPrice: Double? = 0.0,
    var status: String? = "Pending",
    var userAddress: String? = null,
    var timestamp: Long? = null
)