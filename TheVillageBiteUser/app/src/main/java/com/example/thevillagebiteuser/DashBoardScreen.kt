package com.example.thevillagebiteuser

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {

    var selectedId by remember { mutableStateOf(0) }



    Scaffold(

        // ── TOP BAR ──────────────────────────────────────
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Home",
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

        // ── FAB ──────────────────────────────────────────
        floatingActionButton = {
            FloatingActionButton(
                onClick = { },
//                containerColor = Color(0xFF6650A4),
                containerColor = colorResource(R.color.primaryGreen),
                contentColor = Color.White
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add"
                )
            }
        },

        // ── BOTTOM BAR ───────────────────────────────────
        bottomBar = {
            NavigationBar(
//                containerColor = Color(0xFF6650A4)
                containerColor = colorResource(R.color.primaryGreen),
            ) {

                NavigationBarItem(
                    selected = selectedId == 0,
                    onClick = { selectedId = 0
                        navController.navigate("Home")},  // ✅ = fixed
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "Home"
                        )
                    },
                    label = { Text("Home") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = colorResource(R.color.primaryGreen),
//                        selectedIconColor = Color(0xFF6650A4),
                        selectedTextColor = Color.White,
                        unselectedIconColor = Color.White,
                        unselectedTextColor = Color.White,
                        indicatorColor = Color.White
                    )
                )

                NavigationBarItem(
                    selected = selectedId == 1,
                    onClick = { selectedId = 1
                        navController.navigate("cart")},  // ✅ = fixed
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.ShoppingCart,
                            contentDescription = "Cart"
                        )
                    },
                    label = { Text("Cart") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = colorResource(R.color.primaryGreen),
//                        selectedIconColor = Color(0xFF6650A4),
                        selectedTextColor = Color.White,
                        unselectedIconColor = Color.White,
                        unselectedTextColor = Color.White,
                        indicatorColor = Color.White
                    )
                )

                NavigationBarItem(
                    selected = selectedId == 2,
                    onClick = { selectedId = 2
                        navController.navigate("Profile")},  // ✅ = fixed
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "Profile"
                        )
                    },
                    label = { Text("Profile") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = colorResource(R.color.primaryGreen),
//                        selectedIconColor = Color( 0xFF6650A4),
                        selectedTextColor = Color.White,
                        unselectedIconColor = Color.White,
                        unselectedTextColor = Color.White,
                        indicatorColor = Color.White
                    )
                )
            }
        }

    ) { innerPadding ->

        // ── CONTENT ──────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Welcome to DashBoard!",
                fontSize = 22.sp
            )
        }
    }
}