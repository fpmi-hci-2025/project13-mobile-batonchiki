package com.example.pharmacyapp.ui.screens.details // Ваш package name

import com.example.pharmacyapp.data.local.ProductEntity

// Состояние экрана деталей продукта
data class ProductDetailsUiState(
    val product: ProductEntity? = null, // Продукт для отображения (может быть null во время загрузки или ошибки)
    val isLoading: Boolean = true, // Флаг загрузки
    val error: String? = null      // Сообщение об ошибке (если продукт не найден)
)