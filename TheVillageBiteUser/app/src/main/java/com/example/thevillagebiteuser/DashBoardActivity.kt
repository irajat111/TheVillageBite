package com.example.thevillagebiteuser

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener

class DashBoardActivity : ComponentActivity(), PaymentResultWithDataListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        //Checkout.preload(this)
        setContent {
            DashBoardScreen()
        }
    }

    // ✅ Razorpay — Payment successful
    override fun onPaymentSuccess(razorpayPaymentId: String?, paymentData: PaymentData?) {
        CartPaymentHelper.onSuccess?.invoke(razorpayPaymentId ?: "")
    }

    // ✅ Razorpay — Payment failed
    override fun onPaymentError(errorCode: Int, errorDescription: String?, paymentData: PaymentData?) {
        CartPaymentHelper.onError?.invoke(errorCode, errorDescription ?: "Unknown error")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashBoardScreen() {

    var selectedId by remember { mutableStateOf(0) }
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoot = navBackStackEntry?.destination?.route ?: ""

    val screenTitles = mapOf(
        "category"                  to "Home",
        "cart"                      to "My Cart",
        "orderDetails"              to "My Orders",
        "profile"                   to "Profile",
        "productScreen"             to "Products",
        "productDetail/{productId}" to "Product Details"
    )

    val appBarTitle = screenTitles[currentRoot] ?: "The Village Bite"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = appBarTitle, fontSize = 20.sp) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor    = colorResource(R.color.primaryGreen),
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            NavigationBar(containerColor = colorResource(R.color.white)) {

                // ── Home ──────────────────────────────────────
                NavigationBarItem(
                    selected = selectedId == 0,
                    onClick  = {
                        selectedId = 0
                        navController.navigate("category") {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState    = true
                        }
                    },
                    icon   = { Icon(Icons.Filled.Menu, contentDescription = "Home") },
                    label  = { Text("Home") },
                    colors = navItemColors()
                )

                // ── Cart ──────────────────────────────────────
                NavigationBarItem(
                    selected = selectedId == 1,
                    onClick  = {
                        selectedId = 1
                        navController.navigate("cart") {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState    = true
                        }
                    },
                    icon   = { Icon(Icons.Filled.ShoppingCart, contentDescription = "Cart") },
                    label  = { Text("Cart") },
                    colors = navItemColors()
                )

                // ── Orders ────────────────────────────────────
                NavigationBarItem(
                    selected = selectedId == 2,
                    onClick  = {
                        selectedId = 2
                        navController.navigate("orderDetails") {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState    = true
                        }
                    },
                    icon   = { Icon(Icons.Filled.Receipt, contentDescription = "Orders") },
                    label  = { Text("Orders") },
                    colors = navItemColors()
                )

                // ── Profile ───────────────────────────────────
                NavigationBarItem(
                    selected = selectedId == 3,
                    onClick  = {
                        selectedId = 3
                        navController.navigate("profile") {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState    = true
                        }
                    },
                    icon   = { Icon(Icons.Filled.Person, contentDescription = "Profile") },
                    label  = { Text("Profile") },
                    colors = navItemColors()
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            modifier         = Modifier
                .fillMaxSize()
                .background(color = Color.White)
                .padding(innerPadding),
            startDestination = "category",
            navController    = navController,
        ) {
            composable("category") {
                CategoryScreen(navController)
            }
            composable("cart") {
                CartScreen()
            }
            composable("orderDetails") {
                OrderDetailsScreen(navController = navController)
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
                    productId     = productId
                )
            }
        }
    }
}

@Composable
fun navItemColors() = NavigationBarItemDefaults.colors(
    selectedIconColor   = colorResource(R.color.primaryGreen),
    selectedTextColor   = Color.Black,
    unselectedTextColor = Color.Black,
    indicatorColor      = Color.White
)