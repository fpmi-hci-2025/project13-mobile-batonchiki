package com.example.pharmacyapp.repository

import com.example.pharmacyapp.data.local.ProductDao
import com.example.pharmacyapp.data.local.ProductEntity
import com.example.pharmacyapp.data.remote.ApiService
import com.example.pharmacyapp.data.repository.ProductRepositoryImpl
import com.example.pharmacyapp.logger.Logger
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class ProductRepositoryTest {

    private lateinit var productDao: ProductDao
    private lateinit var apiService: ApiService
    private lateinit var logger: Logger
    private lateinit var repository: ProductRepositoryImpl

    @Before
    fun setup() {
        productDao = mock(ProductDao::class.java)
        apiService = mock(ApiService::class.java)
        logger = mock(Logger::class.java)
        repository = ProductRepositoryImpl(productDao, apiService, logger)
    }

    @Test
    fun `getAllProducts returns data from DAO`() = runTest {
        val expected = listOf(ProductEntity(1, "Test", "Desc", "Cat", 10.0, "img.jpg", false))
        `when`(productDao.getAllProducts()).thenReturn(flowOf(expected))

        val actual = repository.getAllProducts().first()

        assertEquals(expected, actual)
    }

    @Test
    fun `updateFavoriteStatus calls DAO method`() = runTest {
        repository.updateFavoriteStatus(1L, true)

        verify(productDao).updateFavoriteStatus(1L, true)
    }

    @Test
    fun `getFavoriteProducts returns only favorites`() = runTest {
        val favorites = listOf(ProductEntity(2, "Fav", "Desc", "Cat", 20.0, "fav.jpg", true))
        `when`(productDao.getFavoriteProducts()).thenReturn(flowOf(favorites))

        val actual = repository.getFavoriteProducts().first()

        assertEquals(favorites, actual)
    }

    @Test
    fun `getProductById returns correct product`() = runTest {
        val product = ProductEntity(3, "One", "Desc", "Cat", 30.0, "one.jpg", false)
        `when`(productDao.getProductById(3L)).thenReturn(flowOf(product))

        val result = repository.getProductById(3L).first()

        assertEquals(product, result)
    }

    @Test
    fun `searchProducts returns matching results`() = runTest {
        val query = "aspirin"
        val results = listOf(ProductEntity(4, "Aspirin", "Painkiller", "Health", 5.0, "a.jpg", false))
        `when`(productDao.searchProducts(query)).thenReturn(flowOf(results))

        val actual = repository.searchProducts(query).first()

        assertEquals(results, actual)
    }

    @Test
    fun `refreshProducts inserts data when response is successful`() = runTest {
        val remoteProducts = listOf(ProductEntity(10, "API Product", "Fetched", "API", 99.9, "api.jpg", false))
        `when`(apiService.getProductsFromUrl(anyString())).thenReturn(Response.success(remoteProducts))

        repository.refreshProducts()

        verify(productDao).insertAll(remoteProducts)
    }

    @Test
    fun `refreshProducts skips insert when response is empty`() = runTest {
        `when`(apiService.getProductsFromUrl(anyString())).thenReturn(Response.success(emptyList()))

        repository.refreshProducts()

        verify(productDao, never()).insertAll(anyList())
    }

    @Test(expected = IOException::class)
    fun `refreshProducts throws IOException when response body is null`() = runTest {
        `when`(apiService.getProductsFromUrl(anyString())).thenReturn(Response.success(null))

        repository.refreshProducts()
    }

    @Test(expected = HttpException::class)
    fun `refreshProducts throws HttpException when response is not successful`() = runTest {
        val errorBody = ResponseBody.create("application/json".toMediaTypeOrNull(), "Not Found")
        val errorResponse = Response.error<List<ProductEntity>>(404, errorBody)

        `when`(apiService.getProductsFromUrl(anyString())).thenReturn(errorResponse)

        repository.refreshProducts()
    }
}
