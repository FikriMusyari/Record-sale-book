package com.afi.record.presentation

sealed class Screen(val route: String) {
    data object SignIn : Screen("signin")
    data object SignUp : Screen("signup")
    object Dashboard : Screen("dashboard")
    object Customer : Screen("customer")
    object AddCustomer : Screen("addcustomer")
    object AddProduct : Screen("addproduct")
    object AddQueue : Screen("addqueue")
    object Product : Screen("product")
    object Queue : Screen("queue")
    object SelectProduct : Screen("selectproduct")
    object EditProduct : Screen("editproduct")
}