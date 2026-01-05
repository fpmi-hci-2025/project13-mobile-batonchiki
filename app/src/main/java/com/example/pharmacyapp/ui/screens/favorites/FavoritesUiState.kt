package com.example.pharmacyapp.ui.screens.favorites

import com.example.pharmacyapp.data.local.ProductEntity

data class FavoritesUiState(
    val favoriteProducts: List<ProductEntity> = emptyList(),
    val isLoading: Boolean = true
)