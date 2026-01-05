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
import retrofit2.HttpException
import java.io.IOException

@OptIn(FlowPreview::class)
class CatalogViewModel(
    private val repository: ProductRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(CatalogUiState(isLoading = true))
    val uiState: StateFlow<CatalogUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow(
        savedStateHandle.get<String>(AppScreens.Catalog.ARG_SEARCH_QUERY) ?: ""
    )

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage: SharedFlow<String> = _toastMessage.asSharedFlow()

    init {
        Log.d("CatalogViewModel", "Initializing...")

        val initialQueryFromNav = savedStateHandle.get<String>(AppScreens.Catalog.ARG_SEARCH_QUERY)
        if (!initialQueryFromNav.isNullOrBlank()) {
            _uiState.update { it.copy(searchQuery = initialQueryFromNav) }
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
            Log.d("CatalogViewModel", "Attempting to refresh products from network...")
            try {
                repository.refreshProducts()
                Log.d("CatalogViewModel", "Network refreshProducts successful")
            } catch (e: IOException) {
                Log.e("CatalogViewModel", "Network error during refreshData", e)
                _toastMessage.emit("Ошибка сети при обновлении.")
            } catch (e: HttpException) {
                Log.e("CatalogViewModel", "HTTP error during refreshData: ${e.code()}", e)
                _toastMessage.emit("Ошибка загрузки данных (${e.code()}).")
            } catch (e: Exception) {
                Log.e("CatalogViewModel", "Generic error during refreshData", e)
                _toastMessage.emit("Произошла ошибка обновления.")
            }

        }
    }

    private fun observeProducts() {
        viewModelScope.launch {
            _searchQuery
                .debounce(300L)
                .distinctUntilChanged()
                .flatMapLatest { query ->
                    Log.d("CatalogViewModel", "Observing products for query: '$query'")
                    _uiState.update { it.copy(isLoading = true, error = null, noResultsFound = false) } // Показываем загрузку перед новым запросом
                    if (query.isBlank()) {
                        repository.getAllProducts()
                    } else {
                        repository.searchProducts(query)
                    }
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
                    _toastMessage.emit("Ошибка при чтении локальных данных.")
                }
                .collect { products ->
                    Log.d("CatalogViewModel", "Collected ${products.size} products. Query: '${_searchQuery.value}'")
                    _uiState.update { currentState ->
                        currentState.copy(
                            products = products,
                            isLoading = false,
                            noResultsFound = products.isEmpty() && _searchQuery.value.isNotBlank(),
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

    fun toggleFavoriteStatus(productId: Long, isFavorite: Boolean) {
        viewModelScope.launch {
            try {
                repository.updateFavoriteStatus(productId, !isFavorite)
            } catch (e: Exception) {
                Log.e("CatalogViewModel", "Error toggling favorite status", e)
                _toastMessage.emit("Не удалось изменить статус избранного.")
            }
        }
    }
}