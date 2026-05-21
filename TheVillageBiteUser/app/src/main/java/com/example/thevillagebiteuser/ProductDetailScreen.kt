package com.example.thevillagebiteuser

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import coil3.request.crossfade
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore


@Composable
fun ProductDetailScreen(navController: NavHostController,
                        productId: String) {

    val db = FirebaseFirestore.getInstance()
    var product by remember { mutableStateOf<ProductClass?>(null) }

    var isLoading by remember { mutableStateOf(true) }

    var showCartDialog by remember { mutableStateOf(false) }

    val sweetTweets = listOf(
        "🍽️ Good food is the ingredient that binds us together.",
        "❤️ Every bite tells a delicious story.",
        "🌟 Happiness is homemade and served fresh.",
        "😋 Taste the love in every single bite.",
        "🍕 Life is short — eat what you love.",
        "🥗 Fresh ingredients, unforgettable flavors.",
        "🔥 Made with passion, served with love.",
        "🍔 Great food creates great memories.",
        "🌿 Savor the flavor, enjoy the moment.",
        "🍰 Sweet moments begin with tasty bites."
    )

    val randomTweet = remember {
        sweetTweets.random()
    }

    LaunchedEffect(productId) {
        db.collection("Productclass")
            .document(productId)
            .addSnapshotListener { document, error ->

                if (error != null) {
                    isLoading = false
                    return@addSnapshotListener
                }
                if (document != null && document.exists()) {
                    val model = document.toObject(ProductClass::class.java)
                    model?.id = document.id
                    product = model
                }
                isLoading = false
            }
    }

    Box(Modifier.fillMaxSize()) {

        when {

            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = colorResource(R.color.primaryGreen))
                }
            }

            product == null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Product not found", color = Color.Gray, fontSize = 16.sp)
                }
            }

            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp)
                        .background(Color(0xFFF0F0F0)),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Spacer(Modifier.height(8.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(8.dp),
                            onClick = {},
                        ) {
                            Column {

                                AsyncImage(
                                    model = coil3.request.ImageRequest.Builder(LocalContext.current)
                                        .data(product!!.productImage)
                                        .crossfade(true)
                                        .build(),
                                    placeholder = painterResource(R.drawable.ic_launcher_background),
                                    contentDescription = product!!.foodname,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(280.dp)
                                        .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                                )

                                Column(modifier = Modifier.padding(20.dp)) {

                                    Text(
                                        text = product!!.foodname ?: "",
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )

                                    Spacer(Modifier.height(12.dp))

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(
                                                colorResource(R.color.primaryGreen).copy(alpha = 0.12f)
                                            )
                                            .padding(horizontal = 16.dp, vertical = 12.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Price",
                                                fontSize = 16.sp,
                                                color = Color.Gray
                                            )
                                            Spacer(Modifier.weight(1f))
                                            Text(
                                                text = "₹ ${product!!.foodprice}",
                                                fontSize = 22.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = colorResource(R.color.primaryGreen)
                                            )
                                        }
                                    }

                                    Spacer(Modifier.height(16.dp))

                                    Divider(color = Color(0xFFEEEEEE), thickness = 1.dp)

                                    Spacer(Modifier.height(16.dp))

                                    Text(
                                        text = "Description",
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.Black
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    Text(
                                        text = product!!.foodDescriprion ?: "",
                                        fontSize = 14.sp,
                                        color = Color.Gray,
                                        lineHeight = 24.sp
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(24.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            onClick = {},
                            elevation = CardDefaults.cardElevation(8.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "✨ Daily Food Quote",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colorResource(R.color.primaryGreen)
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = randomTweet,
                                    fontSize = 15.sp,
                                    lineHeight = 24.sp,
                                    color = Color.DarkGray,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Spacer(Modifier.height(24.dp))

                        Box(
                            modifier = Modifier
                                .width(80.dp)
                                .height(6.dp)
                                .clip(RoundedCornerShape(50))
                                .background(Color.LightGray.copy(alpha = 0.4f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .width(40.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(colorResource(R.color.primaryGreen))
                            )
                        }

                        Spacer(Modifier.height(16.dp))

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(8.dp),
                            onClick = {
                                showCartDialog = true
                            }
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "🛒 Add to Cart",
                                    textAlign = TextAlign.Center,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colorResource(R.color.primaryGreen)
                                )
                            }
                        }

                        val auth = Firebase.auth
                        var cartList = remember { mutableStateListOf<CartModel?>(null) }

                        db.collection("cart").whereEqualTo("productId", product?.id)
                            .whereEqualTo("userId", auth.currentUser?.uid.toString())
                            .addSnapshotListener { snapshot, error ->
                                if (error != null) {
                                    return@addSnapshotListener
                                }
                                for (doc in snapshot!!.documentChanges) {
                                    when (doc.type) {
                                        DocumentChange.Type.ADDED -> {
                                            val model = doc.document.toObject(CartModel::class.java)
                                            model.documentId = doc.document.id  // ✅ FIXED: id -> documentId
                                            cartList.add(model)
                                        }
                                        DocumentChange.Type.MODIFIED -> {}
                                        DocumentChange.Type.REMOVED -> {}
                                    }
                                }
                            }

                        if (showCartDialog) {
                            Dialog(onDismissRequest = { showCartDialog = false }) {
                                val context = LocalContext.current
                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    elevation = CardDefaults.cardElevation(8.dp)
                                ) {
                                    Column(modifier = Modifier.padding(20.dp)) {

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(text = "😋", fontSize = 22.sp)
                                                Spacer(Modifier.width(6.dp))
                                                Text(
                                                    text = "Keep Ordering",
                                                    fontSize = 15.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = colorResource(R.color.primaryGreen)
                                                )
                                            }
                                            IconButton(onClick = { showCartDialog = false }) {
                                                Icon(
                                                    imageVector = Icons.Default.Close,
                                                    contentDescription = "Close",
                                                    tint = Color.Red
                                                )
                                            }
                                        }

                                        Divider(color = Color.LightGray, thickness = 0.5.dp)

                                        Spacer(Modifier.height(12.dp))

                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(10.dp),
                                            colors = CardDefaults.cardColors(containerColor = colorResource(R.color.white)),
                                            onClick = {},
                                            elevation = CardDefaults.cardElevation(2.dp)
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(12.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Text(
                                                    text = "🛒 Item will be added to the cart!",
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = colorResource(R.color.primaryGreen)
                                                )
                                                Spacer(Modifier.height(6.dp))
                                                Text(
                                                    text = "You can add this item to the cart or continue Ordering...",
                                                    fontSize = 13.sp,
                                                    color = Color.Black,
                                                    lineHeight = 20.sp,
                                                    textAlign = TextAlign.Center
                                                )
                                            }
                                        }

                                        Spacer(Modifier.height(16.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            // Continue Ordering Card
                                            Card(
                                                modifier = Modifier.weight(1f),
                                                shape = RoundedCornerShape(10.dp),
                                                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                                                elevation = CardDefaults.cardElevation(2.dp),
                                                onClick = {
                                                    val model = CartModel()
                                                    model.productId = product?.id
                                                    model.foodname = product?.foodname
                                                    model.productImage = product?.productImage
                                                    model.foodDescription = product?.foodDescriprion
                                                    model.foodprice = product?.foodprice
                                                    model.userId = auth.currentUser?.uid.toString()

                                                    db.collection("cart").add(model)
                                                        .addOnCompleteListener {
                                                            if (it.isSuccessful) {
                                                                Toast.makeText(
                                                                    context,
                                                                    "Added in Cart Successfully",
                                                                    Toast.LENGTH_SHORT
                                                                ).show()
                                                                showCartDialog = false
                                                            } else {
                                                                Toast.makeText(
                                                                    context,
                                                                    it.exception?.message,
                                                                    Toast.LENGTH_SHORT
                                                                ).show()
                                                            }
                                                        }
                                                }
                                            ) {
                                                Column(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .height(50.dp),
                                                    horizontalAlignment = Alignment.CenterHorizontally,
                                                    verticalArrangement = Arrangement.Center
                                                ) {
                                                    Text(
                                                        text = "Continue Ordering",
                                                        fontSize = 13.sp,
                                                        color = Color.Black,
                                                        fontWeight = FontWeight.Medium
                                                    )
                                                }
                                            }

                                            // Go to Cart Card
                                            Card(
                                                modifier = Modifier.weight(1f),
                                                shape = RoundedCornerShape(10.dp),
                                                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                                                elevation = CardDefaults.cardElevation(2.dp),
                                                onClick = {
                                                    val model = CartModel()
                                                    model.productId = product?.id
                                                    model.foodname = product?.foodname
                                                    model.productImage = product?.productImage
                                                    model.foodDescription = product?.foodDescriprion
                                                    model.foodprice = product?.foodprice
                                                    model.userId = auth.currentUser?.uid.toString()

                                                    db.collection("cart").add(model)
                                                        .addOnCompleteListener {
                                                            if (it.isSuccessful) {
                                                                Toast.makeText(
                                                                    context,
                                                                    "Added in Cart Successfully",
                                                                    Toast.LENGTH_SHORT
                                                                ).show()
                                                                navController.navigate("cart")
                                                            } else {
                                                                Toast.makeText(
                                                                    context,
                                                                    it.exception?.message,
                                                                    Toast.LENGTH_SHORT
                                                                ).show()
                                                            }
                                                        }
                                                }
                                            ) {
                                                Column(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .height(50.dp),
                                                    horizontalAlignment = Alignment.CenterHorizontally,
                                                    verticalArrangement = Arrangement.Center
                                                ) {
                                                    Text(
                                                        text = "🛒 Cart",
                                                        fontSize = 13.sp,
                                                        color = colorResource(R.color.primaryGreen),
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
                }
            }
        }
    }
}