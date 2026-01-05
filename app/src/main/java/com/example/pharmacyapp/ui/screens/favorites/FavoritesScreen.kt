package com.example.pharmacyapp.ui.screens.favorites

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.pharmacyapp.PharmacyApplication
import com.example.pharmacyapp.ui.screens.catalog.ProductItem
import com.example.pharmacyapp.ui.viewmodel.FavoritesViewModel
import com.example.pharmacyapp.ui.viewmodel.FavoritesViewModelFactory
import com.example.pharmacyapp.ui.AppScreens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    navController: NavHostController,
)
{
    val application = LocalContext.current.applicationContext as PharmacyApplication
    val viewModel: FavoritesViewModel = viewModel(
        factory = FavoritesViewModelFactory(application.productRepository) // Используем нашу фабрику
    )

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Избранное") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) { // Кнопка "назад"
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { paddingValues ->

        when {
            uiState.isLoading -> {
                Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            uiState.favoriteProducts.isEmpty() -> {
                Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    Text("Нет избранных товаров", textAlign = TextAlign.Center)
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp)
                        .fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.favoriteProducts, key = { product -> product.id }) { product ->
                        ProductItem(
                            product = product,
                            onFavoriteClick = {

                                viewModel.toggleFavoriteStatus(product.id, product.isFavorite)
                            },
                            onItemClick = {
                                navController.navigate(AppScreens.ProductDetails.createRoute(product.id))
                            }
                        )
                    }
                }
            }
        }
    }
}