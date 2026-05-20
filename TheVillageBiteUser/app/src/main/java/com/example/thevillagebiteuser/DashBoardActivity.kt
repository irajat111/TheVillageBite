package com.example.thevillagebiteuser

import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.window.Dialog
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
import com.google.firebase.auth.auth
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
        "productScreen" to "Product",
        "cart" to "Cart",
        "productDetail/{productId}" to "Product Details"
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
         //   if(!appBarTitle.equals("Cart")) {
                NavigationBar(
//                containerColor = Color(0xFF6650A4)
                    containerColor = colorResource(R.color.white),
                ) {

                    NavigationBarItem(
                        selected = selectedId == 0,
                        onClick = {
                            selectedId = 0
                            navController.navigate("category") {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true

                            }
                        },  // ✅ = fixed
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
                        onClick = {
                            selectedId = 1
                            navController.navigate("cart") {
                                popUpTo(navController.graph.startDestinationId) {
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
                        onClick = {
                            selectedId = 2
                            navController.navigate("profile") {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true

                            }
                        },  // ✅ = fixed
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
           // }
        }

    ) { innerPadding ->
        NavHost(
            modifier = Modifier.fillMaxSize()
                .background(color = Color.White)
                .padding(innerPadding),
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





