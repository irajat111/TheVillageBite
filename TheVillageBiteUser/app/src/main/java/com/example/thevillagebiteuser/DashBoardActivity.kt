package com.example.thevillagebiteuser

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener

class DashBoardActivity : ComponentActivity(), PaymentResultWithDataListener {

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
        else    Toast.makeText(this, "Permission Denied",  Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        getNotification()
        setContent { DashBoardScreen() }
    }

    private fun getNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    override fun onPaymentSuccess(razorpayPaymentId: String?, paymentData: PaymentData?) {
        CartPaymentHelper.onSuccess?.invoke(razorpayPaymentId ?: "")
    }

    override fun onPaymentError(errorCode: Int, errorDescription: String?, paymentData: PaymentData?) {
        CartPaymentHelper.onError?.invoke(errorCode, errorDescription ?: "Unknown error")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashBoardScreen() {

    var selectedId by remember { mutableStateOf(0) }
    val navController    = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoot      = navBackStackEntry?.destination?.route ?: ""

    // ── Route ke hisaab se title, subtitle aur emoji ──────
    val topBarTitle = when (currentRoot) {
        "category"                  -> "The Village Bite"
        "cart"                      -> "My Cart"
        "orderDetails"              -> "My Orders"
        "profile"                   -> "Profile"
        "productScreen"             -> "Products"
        "productDetail/{productId}" -> "Product Details"
        else                        -> "The Village Bite"
    }

    val topBarSubtitle = when (currentRoot) {
        "category"                  -> "What are you craving today?"
        "cart"                      -> "Review your selected items"
        "orderDetails"              -> "Track your order history"
        "profile"                   -> "Your account & settings"
        "productScreen"             -> "Browse our menu"
        "productDetail/{productId}" -> "Item details"
        else                        -> "Fresh from the village"
    }

    val topBarEmoji = when (currentRoot) {
        "category"                  -> "🌿"
        "cart"                      -> "🛒"
        "orderDetails"              -> "📦"
        "profile"                   -> "👤"
        "productScreen"             -> "🍽️"
        "productDetail/{productId}" -> "✨"
        else                        -> "🌿"
    }

    // ── Gradients — same as Admin app ─────────────────────
    val gradientGreen = Brush.horizontalGradient(
        colors = listOf(Color(0xFF2E7D32), Color(0xFF43A047))
    )

    val gradientGreenBadge = Brush.horizontalGradient(
        colors = listOf(Color(0xFF66BB6A), Color(0xFF2E7D32))
    )

    Scaffold(
        topBar = {
            // ── Green gradient background ──────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(brush = gradientGreen)
                    .statusBarsPadding()
                    .padding(horizontal = 14.dp)
                    .padding(top = 10.dp, bottom = 14.dp)
            ) {
                // ── White card inside green TopAppBar ─────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation    = 12.dp,
                            shape        = RoundedCornerShape(18.dp),
                            ambientColor = Color(0xFF1B5E20).copy(alpha = 0.3f),
                            spotColor    = Color(0xFF1B5E20).copy(alpha = 0.4f)
                        )
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color.White)
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier          = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Gradient emoji badge
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(RoundedCornerShape(13.dp))
                                .background(brush = gradientGreenBadge),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = topBarEmoji, fontSize = 22.sp)
                        }

                        Spacer(Modifier.width(12.dp))

                        // Title + Subtitle
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text          = topBarTitle,
                                fontSize      = 17.sp,
                                fontWeight    = FontWeight.Bold,
                                color         = Color(0xFF1B5E20),
                                letterSpacing = 0.2.sp
                            )
                            Spacer(Modifier.height(1.dp))
                            Text(
                                text       = topBarSubtitle,
                                fontSize   = 11.sp,
                                color      = Color(0xFF66BB6A),
                                fontWeight = FontWeight.Normal
                            )
                        }

                        // User pill badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(brush = gradientGreen)
                                .padding(horizontal = 12.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text       = "User",
                                fontSize   = 11.sp,
                                color      = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
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
            composable("category")     { CategoryScreen(navController) }
            composable("cart")         { CartScreen() }
            composable("orderDetails") { OrderDetailsScreen(navController = navController) }
            composable("profile")      { ProfileScreen(navController) }
            composable("productScreen"){ ProductScreen(navController) }
            composable("productDetail/{productId}") { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                ProductDetailScreen(navController = navController, productId = productId)
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