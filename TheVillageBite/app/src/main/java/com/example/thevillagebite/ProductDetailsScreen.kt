
package com.example.thevillagebite

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsScreen(
    navController: NavHostController,
    productId: String
) {
    val db = FirebaseFirestore.getInstance()
    var product by remember { mutableStateOf<ProductClass?>(null) }
    var isLoading by remember { mutableStateOf(true) }



    // ===================== SWEET TWEET LIST =====================
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

    // Random Tweet
    val randomTweet = remember {
        sweetTweets.random()
    }




    // Fetch data from firebase
    LaunchedEffect(productId) {
        db.collection("Productclass").document(productId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val model = document.toObject(ProductClass::class.java)
                    model?.id = document.id
                    product = model
                }
                isLoading = false
            }
            .addOnFailureListener { isLoading = false }
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

                // ✅ Grey background — card Scaffold se alag dikhe
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

                        // ✅ EK CARD — Image + Details dono iske andar
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(8.dp),
                            onClick = {
                                // for animaition  adding onClick
                            },
                        ) {
                            Column {

                                // ===== IMAGE — Card ke andar =====
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(product!!.productImage)
                                        .crossfade(true)
                                        .build(),
                                    placeholder = painterResource(R.drawable.ic_launcher_background),
                                    contentDescription = product!!.foodname,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(280.dp)
                                        // ✅ Sirf upar ke corners round — neeche straight
                                        .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                                )

                                // ===== DETAILS — Image ke neeche same card mein =====
                                Column(modifier = Modifier.padding(20.dp)) {

                                    // Food Name
                                    Text(
                                        text = product!!.foodname ?: "",
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )

                                    Spacer(Modifier.height(12.dp))

                                    // Price Box
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(
                                                colorResource(R.color.primaryGreen).copy(
                                                    alpha = 0.12f
                                                )
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

                                    // Description
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




                        // Card ke neeche yeh section add kar do

                        Spacer(Modifier.height(24.dp))

                        // ===== Sweet Tweet Card =====
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = colorResource(R.color.white)
//                                    .copy(alpha = 0.08f)
                            ),
                            onClick = {
                                // for animaition  adding onClick
                            },
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

                        // ===== Attractive Horizontal Scroll Indicator =====
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

//                        Spacer(Modifier.height(32.dp))


                        Spacer(Modifier.height(24.dp))
                    }
                }
            }
        }

        }
    }
