package com.example.pharmacyapp.ui.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pharmacyapp.data.repository.ProductRepository
import com.example.pharmacyapp.ui.AppScreens
import com.example.pharmacyapp.ui.screens.catalog.CatalogUiState
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.IOException

@OptIn(FlowPreview::class)
class CatalogViewModel(
    private val repository: ProductRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        CatalogUiState(isLoading = true)
    )
    val uiState: StateFlow<CatalogUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow(
        savedStateHandle.get<String>(AppScreens.Catalog.ARG_SEARCH_QUERY)?.trim() ?: ""
    )

    private val _toastMessage = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val toastMessage: SharedFlow<String> = _toastMessage.asSharedFlow()

    init {
        Log.d("CatalogViewModel", "Initializing...")

        val initialQueryFromNav = savedStateHandle.get<String>(AppScreens.Catalog.ARG_SEARCH_QUERY)
        if (!initialQueryFromNav.isNullOrBlank()) {
            _uiState.update { it.copy(searchQuery = initialQueryFromNav) }
            _searchQuery.value = initialQueryFromNav
            Log.d("CatalogViewModel", "Initial search query from navigation: '$initialQueryFromNav'")
        }

        refreshDataInBackground()

        observeProducts()
    }

    fun forceRefreshData() {
        refreshDataInBackground()
    }

    private fun refreshDataInBackground() {
        viewModelScope.launch {
            Log.d("CatalogViewModel", "Refreshing products from network...")
            try {
                repository.refreshProducts()
                Log.d("CatalogViewModel", "refreshProducts() OK")
            } catch (e: IOException) {
                Log.e("CatalogViewModel", "Network/IO error during refresh", e)
                _toastMessage.tryEmit("Ошибка сети при обновлении.")
            } catch (e: Exception) {
                Log.e("CatalogViewModel", "Generic error during refresh", e)
                _toastMessage.tryEmit("Произошла ошибка обновления.")
            }
        }
    }

    private fun observeProducts() {
        viewModelScope.launch {
            _searchQuery
                .debounce(300L)
                .map { it.trim() }
                .distinctUntilChanged()
                .flatMapLatest { query ->
                    Log.d("CatalogViewModel", "Observing products for query: '$query'")

                    _uiState.update {
                        it.copy(
                            isLoading = true,
                            error = null,
                            noResultsFound = false,
                            searchQuery = query
                        )
                    }

                    if (query.isBlank()) repository.getAllProducts()
                    else repository.searchProducts(query)
                }
                .catch { exception ->
                    Log.e("CatalogViewModel", "Error observing database", exception)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Ошибка чтения из базы данных",
                            products = emptyList(),
                            noResultsFound = false
                        )
                    }
                    _toastMessage.tryEmit("Ошибка при чтении локальных данных.")
                }
                .collect { products ->
                    val q = _searchQuery.value.trim()
                    Log.d("CatalogViewModel", "Collected ${products.size} products. Query: '$q'")

                    _uiState.update { current ->
                        current.copy(
                            products = products,
                            isLoading = false,
                            noResultsFound = products.isEmpty() && q.isNotBlank(),
                            error = null
                        )
                    }
                }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun toggleFavoriteStatus(productId: String, isFavorite: Boolean) {
        viewModelScope.launch {
            try {
                repository.updateFavoriteStatus(productId, !isFavorite)
            } catch (e: Exception) {
                Log.e("CatalogViewModel", "Error toggling favorite status", e)
                _toastMessage.tryEmit("Не удалось изменить статус избранного.")
            }
        }
    }
}
