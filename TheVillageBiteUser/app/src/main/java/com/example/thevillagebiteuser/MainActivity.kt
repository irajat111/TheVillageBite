package com.example.thevillagebiteuser

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppNavigation()
        }
    }
}



@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            SplashScreenuser(
                onFinish = {
                    navController.navigate("SignUp") {
                        popUpTo("splash") { inclusive = true }
                    }
                },

                navController
            )
        }

        // ✅ ADD SignUpScreen
        composable("signup") {
            SignUpScreen(navController = navController)
        }

        // ✅ ADD LoginScreen
        composable("login") {
            LoginScreen(navController = navController,
                onFinish = {
                    navController.navigate("splash") {
                        popUpTo("splash") { inclusive = true }
                    }
                },)
        }

        // ADD HomeScreen
//        composable("Home") {
//            HomeScreen(navController = navController)
//        }
//
//        composable("cart") {
//            CartScreen(navController = navController)
//        }
//
//        composable("Profile") {
//            ProfileScreen(navController = navController)
//        }
//
//        composable() {
//
//        }

    }
}