//package com.example.thevillagebite
//
//import android.os.Build
//import androidx.annotation.RequiresApi
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.AccountBalanceWallet
//import androidx.compose.material.icons.filled.AccountCircle
//import androidx.compose.material.icons.filled.List
//import androidx.compose.material.icons.filled.ShoppingCart
//import androidx.compose.material3.*
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.res.colorResource
//import androidx.navigation.NavHostController
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.currentBackStackEntryAsState
//import androidx.navigation.compose.rememberNavController
//
//@RequiresApi(Build.VERSION_CODES.TIRAMISU)
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun HomeScreenUI(parentNavController: NavHostController) {
//    val navController = rememberNavController()
//    val navBackStackEntry by navController.currentBackStackEntryAsState()
//    val currentRoute = navBackStackEntry?.destination?.route
//
//    val topBarTitle = when (currentRoute) {
//        "orders"   -> "Orders"
//        "category" -> "Category"
//        "wallet"   -> "Revenue & Payments"
//        "profile"  -> "Profile"
//        else       -> "The Village Bite"
//    }
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text(topBarTitle, color = Color.White) },
//                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = colorResource(id = R.color.primaryGreen)
//                )
//            )
//        },
//        bottomBar = {
//            BottomAppBar(containerColor = colorResource(R.color.white)) {
//                NavigationBar(containerColor = colorResource(R.color.white)) {
//
//                    // ── Orders ────────────────────────────────
//                    NavigationBarItem(
//                        selected = currentRoute == "orders",
//                        onClick  = {
//                            navController.navigate("orders") {
//                                popUpTo("orders") { saveState = true }
//                                launchSingleTop = true
//                                restoreState    = true
//                            }
//                        },
//                        icon   = { Icon(Icons.Filled.ShoppingCart, contentDescription = "Orders") },
//                        label  = { Text("Orders") },
//                        colors = adminNavColors()
//                    )
//
//                    // ── Category ──────────────────────────────
//                    NavigationBarItem(
//                        selected = currentRoute == "category",
//                        onClick  = {
//                            navController.navigate("category") {
//                                popUpTo("orders") { saveState = true }
//                                launchSingleTop = true
//                                restoreState    = true
//                            }
//                        },
//                        icon   = { Icon(Icons.Filled.List, contentDescription = "Category") },
//                        label  = { Text("Category") },
//                        colors = adminNavColors()
//                    )
//
//                    // ── Wallet / Revenue ──────────────────────
//                    NavigationBarItem(
//                        selected = currentRoute == "wallet",
//                        onClick  = {
//                            navController.navigate("wallet") {
//                                popUpTo("orders") { saveState = true }
//                                launchSingleTop = true
//                                restoreState    = true
//                            }
//                        },
//                        icon   = { Icon(Icons.Filled.AccountBalanceWallet, contentDescription = "Wallet") },
//                        label  = { Text("Revenue") },
//                        colors = adminNavColors()
//                    )
//
//                    // ── Profile ───────────────────────────────
//                    NavigationBarItem(
//                        selected = currentRoute == "profile",
//                        onClick  = {
//                            navController.navigate("profile") {
//                                popUpTo("orders") { saveState = true }
//                                launchSingleTop = true
//                                restoreState    = true
//                            }
//                        },
//                        icon   = { Icon(Icons.Filled.AccountCircle, contentDescription = "Profile") },
//                        label  = { Text("Profile") },
//                        colors = adminNavColors()
//                    )
//                }
//            }
//        }
//    ) { padding ->
//        NavHost(
//            navController    = navController,
//            modifier         = Modifier.padding(padding),
//            startDestination = "orders"
//        ) {
//            composable("orders") {
//                AdminOrdersScreen()
//            }
//
//            composable("category") {
//                CategoryScreen(navController)
//            }
//
//            composable("product") {
//                ProductScreen(navController)
//            }
//
//            composable("productDetails/{productId}") { backStackEntry ->
//                val productId = backStackEntry.arguments?.getString("productId") ?: ""
//                ProductDetailsScreen(navController = navController, productId = productId)
//            }
//
//            composable("wallet") {
//                AdminWalletScreen()
//            }
//
//            composable("profile") {
//                // ✅ Fix: navController directly pass kiya, onLogout/onWalletClick hata diya
//                ProfileScreen(navController = navController)
//            }
//        }
//    }
//}
//
//@Composable
//fun adminNavColors() = NavigationBarItemDefaults.colors(
//    selectedIconColor   = colorResource(R.color.primaryGreen),
//    selectedTextColor   = Color.Black,
//    unselectedTextColor = Color.Black,
//    indicatorColor      = Color.White
//)


package com.example.thevillagebite


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenUI(parentNavController: NavHostController) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val topBarTitle = when (currentRoute) {
        "orders"   -> "Orders"
        "category" -> "Category"
        "wallet"   -> "Revenue & Payments"
        "profile"  -> "Profile"
        else       -> "The Village Bite"
    }

    val topBarSubtitle = when (currentRoute) {
        "orders"   -> "Manage all incoming orders"
        "category" -> "Browse & manage categories"
        "wallet"   -> "Track your earnings"
        "profile"  -> "Admin account settings"
        else       -> "Admin Dashboard"
    }

    val topBarEmoji = when (currentRoute) {
        "orders"   -> "🛒"
        "category" -> "📋"
        "wallet"   -> "💰"
        "profile"  -> "👤"
        else       -> "🌿"
    }

    val gradientGreen = Brush.horizontalGradient(
        colors = listOf(Color(0xFF2E7D32), Color(0xFF43A047))
    )

    val gradientGreenBadge = Brush.horizontalGradient(
        colors = listOf(Color(0xFF66BB6A), Color(0xFF2E7D32))
    )

    Scaffold(
        topBar = {
            // ── Green gradient background — TopAppBar ─────
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
                            elevation       = 12.dp,
                            shape           = RoundedCornerShape(18.dp),
                            ambientColor    = Color(0xFF1B5E20).copy(alpha = 0.3f),
                            spotColor       = Color(0xFF1B5E20).copy(alpha = 0.4f)
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

                        // Admin pill badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(brush = gradientGreen)
                                .padding(horizontal = 12.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text       = "Admin",
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
            BottomAppBar(containerColor = colorResource(R.color.white)) {
                NavigationBar(containerColor = colorResource(R.color.white)) {

                    NavigationBarItem(
                        selected = currentRoute == "orders",
                        onClick  = {
                            navController.navigate("orders") {
                                popUpTo("orders") { saveState = true }
                                launchSingleTop = true
                                restoreState    = true
                            }
                        },
                        icon   = { Icon(Icons.Filled.ShoppingCart, contentDescription = "Orders") },
                        label  = { Text("Orders") },
                        colors = adminNavColors()
                    )

                    NavigationBarItem(
                        selected = currentRoute == "category",
                        onClick  = {
                            navController.navigate("category") {
                                popUpTo("orders") { saveState = true }
                                launchSingleTop = true
                                restoreState    = true
                            }
                        },
                        icon   = { Icon(Icons.Filled.List, contentDescription = "Category") },
                        label  = { Text("Category") },
                        colors = adminNavColors()
                    )

                    NavigationBarItem(
                        selected = currentRoute == "wallet",
                        onClick  = {
                            navController.navigate("wallet") {
                                popUpTo("orders") { saveState = true }
                                launchSingleTop = true
                                restoreState    = true
                            }
                        },
                        icon   = { Icon(Icons.Filled.AccountBalanceWallet, contentDescription = "Wallet") },
                        label  = { Text("Revenue") },
                        colors = adminNavColors()
                    )

                    NavigationBarItem(
                        selected = currentRoute == "profile",
                        onClick  = {
                            navController.navigate("profile") {
                                popUpTo("orders") { saveState = true }
                                launchSingleTop = true
                                restoreState    = true
                            }
                        },
                        icon   = { Icon(Icons.Filled.AccountCircle, contentDescription = "Profile") },
                        label  = { Text("Profile") },
                        colors = adminNavColors()
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController    = navController,
            modifier         = Modifier.padding(padding),
            startDestination = "orders"
        ) {
            composable("orders")   { AdminOrdersScreen() }
            composable("category") { CategoryScreen(navController) }
            composable("product")  { ProductScreen(navController) }
            composable("productDetails/{productId}") { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                ProductDetailsScreen(navController = navController, productId = productId)
            }
            composable("wallet")  { AdminWalletScreen() }
            composable("profile") { ProfileScreen(navController = navController) }
        }
    }
}

@Composable
fun adminNavColors() = NavigationBarItemDefaults.colors(
    selectedIconColor   = colorResource(R.color.primaryGreen),
    selectedTextColor   = Color.Black,
    unselectedTextColor = Color.Black,
    indicatorColor      = Color.White
)