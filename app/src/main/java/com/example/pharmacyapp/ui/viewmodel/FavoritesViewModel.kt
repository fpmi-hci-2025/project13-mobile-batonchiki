package com.example.pharmacyapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pharmacyapp.data.repository.ProductRepository
import com.example.pharmacyapp.ui.screens.favorites.FavoritesUiState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val repository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    init { observeFavoriteProducts() }

    private fun observeFavoriteProducts() {
        viewModelScope.launch {
            repository.getFavoriteProducts()
                .catch { _uiState.update { it.copy(isLoading = false) } }
                .collect { favorites ->
                    _uiState.update { it.copy(isLoading = false, favoriteProducts = favorites) }
                }
        }
    }

    fun toggleFavoriteStatus(productId: String, isFavorite: Boolean) {
        viewModelScope.launch {
            repository.updateFavoriteStatus(productId, !isFavorite) // было всегда false
        }
    }
}
