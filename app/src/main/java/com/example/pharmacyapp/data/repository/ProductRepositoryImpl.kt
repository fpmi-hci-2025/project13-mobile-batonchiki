package com.example.pharmacyapp.data.repository

import com.example.pharmacyapp.data.local.ProductDao
import com.example.pharmacyapp.data.local.ProductEntity
import com.example.pharmacyapp.data.remote.ApiService
import com.example.pharmacyapp.logger.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class ProductRepositoryImpl(
    private val productDao: ProductDao,
    private val apiService: ApiService,
    private val logger: Logger
) : ProductRepository {

    private val productsJsonUrl = "https://gist.githubusercontent.com/RustambekTalipov/423e2d0ac9ade2f20c345eebe2862e00/raw/78fe5405a707c6531e59c2e37fe7230782f2ecce/pharmacy_products.json"

    override fun getAllProducts(): Flow<List<ProductEntity>> {
        logger.debug("ProductRepositoryImpl1234", "Getting all products flow from DAO")
        return productDao.getAllProducts()
    }

    override fun getFavoriteProducts(): Flow<List<ProductEntity>> {
        logger.debug("ProductRepositoryImpl", "Getting favorite products flow from DAO")
        return productDao.getFavoriteProducts()
    }

    override fun getProductById(productId: Long): Flow<ProductEntity?> {
        logger.debug("ProductRepositoryImpl", "Getting product by ID $productId flow from DAO")
        return productDao.getProductById(productId)
    }

    override fun searchProducts(query: String): Flow<List<ProductEntity>> {
        logger.debug("ProductRepositoryImpl", "Searching products with query '$query' flow from DAO")
        return productDao.searchProducts(query)
    }

    override suspend fun updateFavoriteStatus(productId: Long, isFavorite: Boolean) {
        logger.debug("ProductRepositoryImpl", "Updating favorite status for $productId to $isFavorite")
        withContext(Dispatchers.IO) {
            productDao.updateFavoriteStatus(productId, isFavorite)
        }
    }

    override suspend fun refreshProducts() {
        withContext(Dispatchers.IO) {
            try {
                logger.info("ProductRepositoryImpl", "Attempting to fetch products from: $productsJsonUrl")

                val response = apiService.getProductsFromUrl(productsJsonUrl)

                if (response.isSuccessful) {
                    val productList = response.body()
                    if (productList != null) {
                        if (productList.isNotEmpty()) {
                            productDao.insertAll(productList)
                            logger.info("ProductRepositoryImpl", "Successfully fetched and saved ${productList.size} products to DB.")
                        } else {
                            logger.warn("ProductRepositoryImpl", "Fetched product list is empty. No changes made to DB.")
                        }
                    } else {
                        logger.warn("ProductRepositoryImpl", "Network request successful, but response body was null.")
                        throw IOException("Received null response body")
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Unknown HTTP error"
                    logger.error("ProductRepositoryImpl", "Network request failed with code ${response.code()}: $errorBody")
                    throw HttpException(response)
                }
            } catch (e: IOException) {
                logger.error("ProductRepositoryImpl", "Network IO error during product refresh", e)
                throw e
            } catch (e: HttpException) {
                logger.error("ProductRepositoryImpl", "HTTP error during product refresh", e)
                throw e
            } catch (e: Exception) {
                logger.error("ProductRepositoryImpl", "Generic error during product refresh", e)
                throw e
            }
        }
    }
}
