package com.afi.record.presentation

sealed class Screen (val route: String){
    data object SignIn : Screen("signin")
    data object SignUp : Screen("signup")
    object Dashboard : Screen("dashboard")
}