package com.example.thevillagebiteuser

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.android.volley.toolbox.ImageRequest
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.firestore


import coil3.compose.AsyncImage
import coil3.request.crossfade
import coil3.util.CoilUtils.result
import com.google.firebase.firestore.FirebaseFirestore

class DashBoardActivity : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DashBoardScreen()
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashBoardScreen() {

    var selectedId by remember { mutableStateOf(0) }
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoot = navBackStackEntry?.destination?.route?:""

    val screenTitles = mapOf(
        "category" to "Category",
        "profile" to "Profile",
        "cart" to "Cart"
    )

    val appBarTitle = screenTitles[currentRoot]



    Scaffold(

        // ── TOP BAR ──────────────────────────────────────
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = appBarTitle.toString(),
                        fontSize = 20.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = Color(0xFF6650A4),
                    containerColor = colorResource(R.color.primaryGreen),
                    titleContentColor = Color.White
                )
            )
        },


        // ── BOTTOM BAR ───────────────────────────────────
        bottomBar = {
            NavigationBar(
//                containerColor = Color(0xFF6650A4)
                containerColor = colorResource(R.color.white),
            ) {

                NavigationBarItem(
                    selected = selectedId == 0,
                    onClick = { selectedId = 0
                        navController.navigate("category"){
                            popUpTo(navController.graph.startDestinationId){
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true

                        }},  // ✅ = fixed
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "Home"
                        )
                    },
                    label = { Text("Home") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = colorResource(R.color.primaryGreen),
                        selectedTextColor = Color.Black,
//                        unselectedIconColor = Color.White,
                        unselectedTextColor = Color.Black,
                        indicatorColor = Color.White
                    )
                )

                NavigationBarItem(
                    selected = selectedId == 1,
                    onClick = { selectedId = 1
                        navController.navigate("cart"){
                            popUpTo(navController.graph.startDestinationId){
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true

                        }

                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.ShoppingCart,
                            contentDescription = "Cart"
                        )
                    },
                    label = { Text("Cart") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = colorResource(R.color.primaryGreen),
                        selectedTextColor = Color.Black,
//                        unselectedIconColor = Color.White,
                        unselectedTextColor = Color.Black,
                        indicatorColor = Color.White
                    )
                )

                NavigationBarItem(
                    selected = selectedId == 2,
                    onClick = { selectedId = 2
                        navController.navigate("profile"){
                            popUpTo(navController.graph.startDestinationId){
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true

                        }},  // ✅ = fixed
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "Profile"
                        )
                    },
                    label = { Text("Profile") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = colorResource(R.color.primaryGreen),
                        selectedTextColor = Color.Black,
//                        unselectedIconColor = Color.White,
                        unselectedTextColor = Color.Black,
                        indicatorColor = Color.White
                    )
                )
            }
        }

    ) { innerPadding ->
        NavHost(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            startDestination = "category",
            navController = navController,
        ){
            composable("category") {
                CategoryScreen(navController)
            }

            composable("cart") {
                CartScreen()
            }
            composable("profile") {
                ProfileScreen(navController)
            }

            composable("productScreen") {
                ProductScreen(navController)
            }

            composable("productDetail/{productId}") { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                ProductDetailScreen(
                    navController = navController,
                    productId = productId
                )
            }

        }

    }
}




@Composable
fun ProductDetailScreen(navController: NavHostController,
                        productId: String) {

    // Yahan ProductDetailsScreen ka poora code copy karo
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

    // ✅ STEP 2 — Firestore se data fetch karo
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


    // ✅ UI ADD KAR — yeh missing tha
    // ✅ STEP 3 — Data dikhao
    Box(Modifier.fillMaxSize()) {

        when {

            // Loader
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = colorResource(R.color.primaryGreen))
                }
            }

            // Product na mile
            product == null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Product not found", color = Color.Gray, fontSize = 16.sp)
                }
            }

            // ✅ Data show karo
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

                        // ===== EK CARD — Image + Details =====
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

                                // IMAGE
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

                                // DETAILS
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

                        Spacer(Modifier.height(24.dp))

                        // ===== Sweet Tweet Card =====
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
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

                        // ===== Scroll Indicator =====
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
                            modifier = Modifier.fillMaxWidth().height(60.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            ),
                            elevation = CardDefaults.cardElevation(8.dp),
                            onClick = {

                            }
                        ) {
                            // ✅ Box add karo — text center ho jaayega
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
                    }
                }
            }
        }

    }
}




// Data class for fetch data from Firebase db -> collection -> CategoryClass
data class ProductClass(
    var id: String? = "",
    val foodname: String? = "",
    val foodprice: Double? = 0.0,
    val foodDescriprion: String? = "",
    val productImage: String? = null
)

@Composable
fun ProductScreen(navController: NavHostController) {

    val db = Firebase.firestore
    val context = LocalContext.current


    // ✅ STEP 1 — List banao
    var ProductList = remember { mutableStateListOf<ProductClass>() }

    // ✅ STEP 2 — Firestore se data fetch karo
    LaunchedEffect(Unit) {
        db.collection("Productclass").addSnapshotListener { result, error ->
            if (error != null) return@addSnapshotListener

            for (doc in result!!.documentChanges) {
                when (doc.type) {
                    DocumentChange.Type.ADDED -> {
                        val model = doc.document.toObject(ProductClass::class.java)
                        model.id = doc.document.id
                        ProductList.add(model)
                    }
                    DocumentChange.Type.MODIFIED -> {
                        val model = doc.document.toObject(ProductClass::class.java)
                        model.id = doc.document.id
                        val index = ProductList.indexOfFirst { it.id == model.id }
                        if (index != -1) ProductList[index] = model
                    }
                    DocumentChange.Type.REMOVED -> {
                        val model = doc.document.toObject(ProductClass::class.java)
                        model.id = doc.document.id
                        val index = ProductList.indexOfFirst { it.id == model.id }
                        if (index != -1) ProductList.removeAt(index)
                    }

                    else -> { }
                }
            }
        }
    }


    // ✅ STEP 3 — Data dikhao
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxWidth().padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(ProductList.size){index->
            Card(
                modifier = Modifier.fillMaxWidth().clickable(){
                    // yhan pr InsideProductItem Screen show krwaani hai
                    // ✅ CHANGE — productId pass karke navigate karo
                    val productId = ProductList[index].id ?: ""
                    navController.navigate("productDetail/$productId") // 👈 navigate
//                            navController.navigate("productDetails")
                },
                colors = CardDefaults.cardColors(   // card color = white
                    containerColor = colorResource(R.color.whitefaint)
                ),
//                onClick = {
//
//                }
            ) {

                println("Checck image form Firebase: ${ ProductList[index].productImage}")

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AsyncImage(
                        model = coil3.request.ImageRequest.Builder(LocalContext.current)
                            .data(ProductList[index].productImage)
                            .crossfade(true)
                            .build(),
                        placeholder = painterResource(R.drawable.ic_launcher_background),
                        contentDescription = stringResource(R.string.app_name),
                        contentScale = ContentScale.Crop,
//                                modifier = Modifier.fillMaxWidth().height(150.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .padding(8.dp)                        // 👈 Padding
                            .clip(RoundedCornerShape(12.dp))   // 👈 Rounded corners

                    )

                    Spacer(Modifier.height(1.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        // changes here productList[index].categoryName..toString()
                        Text(ProductList[index].foodname?:"",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }

            }

        }
    }


}






// Data class for fetch data from Firebase db -> collection -> CategoryClass
data class CategoryClass(
    var id: String? = "",
    val categoryName: String? = "",
    var image: String? = null
)
@Composable
fun CategoryScreen(navController: NavHostController) {
    val db = Firebase.firestore
    val context = LocalContext.current

    // ✅ STEP 1 — List banao
    var categoryList = remember { mutableStateListOf<CategoryClass>() }

    // ✅ STEP 2 — Firestore se data fetch karo
    LaunchedEffect(Unit) {
        db.collection("CategoryClass").addSnapshotListener { result, error ->
            if (error != null) return@addSnapshotListener
            for (doc in result!!.documentChanges) {
                when (doc.type) {
                    DocumentChange.Type.ADDED -> {
                        val model = doc.document.toObject(CategoryClass::class.java)
                        model.id = doc.document.id
                        categoryList.add(model)
                    }
                    DocumentChange.Type.MODIFIED -> {
                        val model = doc.document.toObject(CategoryClass::class.java)
                        model.id = doc.document.id
                        val index = categoryList.indexOfFirst { it.id == model.id }
                        if (index != -1) categoryList[index] = model
                    }
                    DocumentChange.Type.REMOVED -> {
                        val model = doc.document.toObject(CategoryClass::class.java)
                        model.id = doc.document.id
                        val index = categoryList.indexOfFirst { it.id == model.id }
                        if (index != -1) categoryList.removeAt(index)
                    }

                    else -> { }
                }
            }
        }
    }

    // ✅ STEP 3 — Data dikhao
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxWidth().padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(categoryList.size) { index ->
            Card(
                modifier = Modifier.fillMaxWidth()
                    .clickable {
                        navController.navigate("productScreen") // 👈 ProductScreen pe jaao
                    },
//                onClick = {
//
//                },
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AsyncImage(
                        model = coil3.request.ImageRequest.Builder(LocalContext.current)
                            .data(categoryList[index].image)
                            .crossfade(true)
                            .build(),
                        placeholder = painterResource(R.drawable.ic_launcher_background),
                        contentDescription = categoryList[index].categoryName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .padding(8.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                    Text(
                        text = categoryList[index].categoryName ?: "",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}




