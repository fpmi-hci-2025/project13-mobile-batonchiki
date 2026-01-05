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

    init {
        observeFavoriteProducts()
    }

    private fun observeFavoriteProducts() {
        viewModelScope.launch {
            repository.getFavoriteProducts()
                .catch { exception ->
                    _uiState.update { it.copy(isLoading = false /*, error = ... */) }
                    println("Error collecting favorites: ${exception.message}")
                }
                .collect { favorites ->
                    _uiState.update {
                        it.copy(isLoading = false, favoriteProducts = favorites)
                    }
                }
        }
    }


    fun toggleFavoriteStatus(productId: Long, isFavorite: Boolean) {
        viewModelScope.launch {
            repository.updateFavoriteStatus(productId, false)
        }
    }
}