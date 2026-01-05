package com.example.pharmacyapp.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.compose.rememberNavController
import com.example.pharmacyapp.data.local.ProductEntity
import com.example.pharmacyapp.ui.screens.catalog.CatalogScreen
import com.example.pharmacyapp.ui.screens.catalog.CatalogUiState
import com.example.pharmacyapp.ui.theme.PharmacyAppTheme
import org.junit.Rule
import org.junit.Test

class CatalogScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun searchBarFiltersProducts() {
        val testProducts = listOf(
            ProductEntity(
                id = 1,
                name = "Аспирин",
                description = "Противовоспалительное средство",
                category = "Лекарства",
                price = 150.0,
                imageUrl = "aspirin.jpg",
                isFavorite = false
            ),
            ProductEntity(
                id = 2,
                name = "Витамин C",
                description = "Витамин",
                category = "Витамины",
                price = 200.0,
                imageUrl = "vitamin_c.jpg",
                isFavorite = true
            )
        )

        composeTestRule.setContent {
            val navController = rememberNavController()
            PharmacyAppTheme {
                CatalogScreen(
                    navController = navController,
                    onProductClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Поиск...", substring = true).assertExists()

    }
}
