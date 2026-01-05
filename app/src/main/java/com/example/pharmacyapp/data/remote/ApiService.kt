package com.example.pharmacyapp.data.remote
import com.example.pharmacyapp.data.local.ProductEntity
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface ApiService {


    @GET
    suspend fun getProductsFromUrl(@Url url: String): Response<List<ProductEntity>>

}