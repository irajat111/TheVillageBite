package com.example.thevillagebite

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

// ✅ Class hatao, sirf Composable rakho
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
        "profile"  -> "Profile"
        else       -> "The Village Bite"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(topBarTitle, color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(id = R.color.primaryGreen)
                )
            )
        },
        bottomBar = {
            BottomAppBar(containerColor = colorResource(R.color.white)) {
                NavigationBar(containerColor = colorResource(R.color.white)) {

                    NavigationBarItem(
                        selected = currentRoute == "orders",
                        onClick = {
                            navController.navigate("orders") {
                                popUpTo("orders") { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.Filled.ShoppingCart, contentDescription = "Orders") },
                        label = { Text("Orders") },

                                // ✅ SIRF YEH ADD KAR
                        colors = NavigationBarItemDefaults.colors
                            (selectedIconColor = colorResource(R.color.primaryGreen),
                        selectedTextColor = Color.Black,
                        unselectedTextColor = Color.Black,
                        indicatorColor = Color.White )

                    )

                    NavigationBarItem(
                        selected = currentRoute == "category",
                        onClick = {
                            navController.navigate("category") {
                                popUpTo("orders") { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.Filled.List, contentDescription = "Category") },
                        label = { Text("Category") },

                        colors = NavigationBarItemDefaults.colors
                            (selectedIconColor = colorResource(R.color.primaryGreen),
                            selectedTextColor = Color.Black,
                            unselectedTextColor = Color.Black,
                            indicatorColor = Color.White )

                    )

                    NavigationBarItem(
                        selected = currentRoute == "profile",
                        onClick = {
                            navController.navigate("profile") {
                                popUpTo("orders") { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.Filled.AccountCircle,
                            contentDescription = "Profile") },
                        label = { Text("Profile") },

                        colors = NavigationBarItemDefaults.colors
                            (selectedIconColor = colorResource(R.color.primaryGreen),
                            selectedTextColor = Color.Black,
                            unselectedTextColor = Color.Black,
                            indicatorColor = Color.White )

                    )

                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            modifier = Modifier.padding(padding),
            startDestination = "orders"
        ) {
            composable("orders") { OrdersScreen() }

            composable("category") { CategoryScreen(navController) }

            composable("product") { ProductScreen(navController) }

            composable("productDetails/{productId}") { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                ProductDetailsScreen(navController = navController, productId = productId)
            }


            composable("profile") {
                ProfileScreen(
                    modifier = Modifier.padding(padding),
                    onLogout = {
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}