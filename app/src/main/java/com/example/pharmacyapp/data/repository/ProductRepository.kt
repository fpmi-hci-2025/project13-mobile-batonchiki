package com.example.pharmacyapp.data.repository

import com.example.pharmacyapp.data.local.ProductEntity
import kotlinx.coroutines.flow.Flow

interface ProductRepository {

    fun getAllProducts(): Flow<List<ProductEntity>>

    fun getFavoriteProducts(): Flow<List<ProductEntity>>

    fun getProductById(productId: Long): Flow<ProductEntity?>

    fun searchProducts(query: String): Flow<List<ProductEntity>>

    suspend fun updateFavoriteStatus(productId: Long, isFavorite: Boolean)

    suspend fun refreshProducts()
}