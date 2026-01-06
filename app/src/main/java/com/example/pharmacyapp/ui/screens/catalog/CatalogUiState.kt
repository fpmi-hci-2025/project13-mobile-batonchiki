package com.example.pharmacyapp.ui.screens.catalog

import com.example.pharmacyapp.data.local.ProductEntity

data class CatalogUiState(
    val products: List<ProductEntity> = emptyList(),
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val error: String? = null,
    val noResultsFound: Boolean = false
)
