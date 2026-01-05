package com.example.pharmacyapp.ui.screens.catalog // Или ваш пакет для UiState

import com.example.pharmacyapp.data.local.ProductEntity

data class CatalogUiState(
    val products: List<ProductEntity> = emptyList(),
    val isLoading: Boolean = false, // Изначально может быть false, загрузка начнется при инициализации ViewModel
    val searchQuery: String = "",
    val error: String? = null,
    val noResultsFound: Boolean = false // Флаг, если поиск не дал результатов
)