package com.example.pharmacyapp.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pharmacyapp.data.repository.ProductRepository
import com.example.pharmacyapp.ui.screens.details.ProductDetailsUiState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProductDetailsViewModel(
    private val repository: ProductRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {


    private val productId: Long = checkNotNull(savedStateHandle["productId"])

    private val _uiState = MutableStateFlow(ProductDetailsUiState())
    val uiState: StateFlow<ProductDetailsUiState> = _uiState.asStateFlow()

    init {
        loadProductDetails()
    }

    private fun loadProductDetails() {
        viewModelScope.launch {
            repository.getProductById(productId)
                .catch { exception ->
                    _uiState.update {
                        it.copy(isLoading = false, error = "Ошибка загрузки: ${exception.message}")
                    }
                }
                .collect { product ->
                    if (product != null) {
                        _uiState.update {
                            it.copy(isLoading = false, product = product, error = null)
                        }
                    } else {
                        _uiState.update {
                            it.copy(isLoading = false, error = "Продукт с ID $productId не найден.")
                        }
                    }
                }
        }
    }

    fun toggleFavoriteStatus() {
        val currentProduct = _uiState.value.product ?: return
        viewModelScope.launch {
            repository.updateFavoriteStatus(currentProduct.id, !currentProduct.isFavorite)
        }
    }
}