package com.example.pharmacyapp.data.repository

import com.example.pharmacyapp.data.local.ProductDao
import com.example.pharmacyapp.data.local.ProductEntity
import com.example.pharmacyapp.data.remote.ApiService
import com.example.pharmacyapp.logger.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.IOException

class ProductRepositoryImpl(
    private val productDao: ProductDao,
    private val apiService: ApiService,
    private val logger: Logger
) : ProductRepository {

    private val apiBaseUrl = "https://pharmacy-api-k0ad.onrender.com/"

    override fun getAllProducts(): Flow<List<ProductEntity>> = productDao.getAllProducts()

    override fun getFavoriteProducts(): Flow<List<ProductEntity>> = productDao.getFavoriteProducts()

    override fun getProductById(productId: String): Flow<ProductEntity?> =
        productDao.getProductById(productId)

    override fun searchProducts(query: String): Flow<List<ProductEntity>> =
        productDao.searchProducts(query)

    override suspend fun updateFavoriteStatus(productId: String, isFavorite: Boolean) {
        withContext(Dispatchers.IO) {
            productDao.updateFavoriteStatus(productId, isFavorite)
        }
    }

    override suspend fun refreshProducts() {
        withContext(Dispatchers.IO) {
            try {
                logger.info("ProductRepositoryImpl", "Fetching items from API: ${apiBaseUrl}api/items")

                val current = productDao.getAllProducts().first()
                val favoritesMap = current.associateBy({ it.id }, { it.isFavorite })

                val items = apiService.getItems()

                val mapped = items.map { dto ->
                    ProductEntity(
                        id = dto.itemId,
                        name = dto.name,
                        description = dto.description ?: "",
                        category = "Без категории",
                        price = dto.price,
                        imageUrl = if (dto.hasImage) {
                            "${apiBaseUrl}api/items/${dto.itemId}/image"
                        } else {
                            ""
                        },
                        isFavorite = favoritesMap[dto.itemId] ?: false
                    )
                }

                productDao.insertAll(mapped)
                logger.info("ProductRepositoryImpl", "Saved ${mapped.size} items to DB")
            } catch (e: Exception) {
                logger.error("ProductRepositoryImpl", "Failed to refresh products", e)
                throw IOException("Failed to refresh products: ${e.message}", e)
            }
        }
    }
}
