package com.example.thevillagebite

sealed class BottomNavRoute(val route: String){

    object Orders : BottomNavRoute("orders")

    object Category : BottomNavRoute("category")

    object Profile : BottomNavRoute("profile")
}