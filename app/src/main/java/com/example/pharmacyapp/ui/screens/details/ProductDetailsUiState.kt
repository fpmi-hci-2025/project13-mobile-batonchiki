package com.example.pharmacyapp.ui.screens.details

import com.example.pharmacyapp.data.local.ProductEntity

data class ProductDetailsUiState(
    val product: ProductEntity? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)
