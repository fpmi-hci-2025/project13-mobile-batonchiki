package com.example.pharmacyapp.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

import com.example.pharmacyapp.ui.screens.catalog.CatalogScreen
import com.example.pharmacyapp.ui.screens.details.ProductDetailsScreen
import com.example.pharmacyapp.ui.screens.favorites.FavoritesScreen
import com.example.pharmacyapp.ui.screens.home.HomeScreen


import com.example.pharmacyapp.ui.theme.PharmacyAppTheme


sealed class AppScreens(val route: String) {
    data object Home : AppScreens("home")

    data object Catalog : AppScreens("catalog?searchQuery={searchQuery}") {

        fun createRoute(searchQuery: String? = null): String {
            return if (searchQuery != null) {
                "catalog?searchQuery=$searchQuery"
            } else {
                "catalog"
            }
        }
        const val ARG_SEARCH_QUERY = "searchQuery"
    }

    data object ProductDetails : AppScreens("product_details/{productId}") {
        const val ARG_PRODUCT_ID = "productId"
        fun createRoute(productId: Long) = "product_details/$productId"
    }
    data object Favorites : AppScreens("favorites")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PharmacyAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController: NavHostController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppScreens.Home.route
    ) {
        composable(route = AppScreens.Home.route) {
            HomeScreen(navController = navController)
        }

        composable(
            route = AppScreens.Catalog.route,
            arguments = listOf(navArgument(AppScreens.Catalog.ARG_SEARCH_QUERY) {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) {
            CatalogScreen(
                navController = navController,
                onProductClick = { productId ->
                    navController.navigate(AppScreens.ProductDetails.createRoute(productId))
                }
            )
        }

        composable(
            route = AppScreens.ProductDetails.route,
            arguments = listOf(navArgument(AppScreens.ProductDetails.ARG_PRODUCT_ID) {
                type = NavType.LongType
            })
        ) {
            ProductDetailsScreen(navController = navController)
        }

        composable(route = AppScreens.Favorites.route) {
            FavoritesScreen(navController = navController)
        }
    }
}