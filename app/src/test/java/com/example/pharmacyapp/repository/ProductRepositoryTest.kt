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
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

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
    fun `getAllProducts delegates to DAO`() = runTest {
        val expected = listOf(
            ProductEntity(
                id = "1",
                name = "Aspirin",
                description = "Pain reliever",
                category = "Головная боль",
                price = 5.99,
                imageUrl = "",
                isFavorite = false
            )
        )
        `when`(productDao.getAllProducts()).thenReturn(flowOf(expected))

        val result = repository.getAllProducts().first()

        assertEquals(expected, result)
        verify(productDao, times(1)).getAllProducts()
        verifyNoMoreInteractions(productDao)
    }

    @Test
    fun `getFavoriteProducts delegates to DAO`() = runTest {
        val favorites = listOf(
            ProductEntity(
                id = "2",
                name = "Vitamin C",
                description = "Vitamins",
                category = "Витамины",
                price = 2.5,
                imageUrl = "",
                isFavorite = true
            )
        )
        `when`(productDao.getFavoriteProducts()).thenReturn(flowOf(favorites))

        val result = repository.getFavoriteProducts().first()

        assertEquals(favorites, result)
        verify(productDao, times(1)).getFavoriteProducts()
        verifyNoMoreInteractions(productDao)
    }

    @Test
    fun `getProductById delegates to DAO`() = runTest {
        val product = ProductEntity(
            id = "3",
            name = "Ibuprofen",
            description = "Anti-inflammatory",
            category = "Головная боль",
            price = 7.0,
            imageUrl = "",
            isFavorite = false
        )
        `when`(productDao.getProductById("3")).thenReturn(flowOf(product))

        val result = repository.getProductById("3").first()

        assertEquals(product, result)
        verify(productDao, times(1)).getProductById("3")
        verifyNoMoreInteractions(productDao)
    }

    @Test
    fun `searchProducts delegates to DAO`() = runTest {
        val query = "асп"
        val results = listOf(
            ProductEntity(
                id = "4",
                name = "Аспирин",
                description = "Описание",
                category = "Головная боль",
                price = 1.0,
                imageUrl = "",
                isFavorite = false
            )
        )
        `when`(productDao.searchProducts(query)).thenReturn(flowOf(results))

        val result = repository.searchProducts(query).first()

        assertEquals(results, result)
        verify(productDao, times(1)).searchProducts(query)
        verifyNoMoreInteractions(productDao)
    }

    @Test
    fun `updateFavoriteStatus calls DAO with flipped value`() = runTest {
        repository.updateFavoriteStatus("10", true)

        verify(productDao, times(1)).updateFavoriteStatus("10", true)
        verifyNoMoreInteractions(productDao)
    }
}
