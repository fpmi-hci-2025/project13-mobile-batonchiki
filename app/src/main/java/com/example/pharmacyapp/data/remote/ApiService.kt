package com.example.pharmacyapp.data.remote

import retrofit2.http.GET

interface ApiService {

    @GET("api/items")
    suspend fun getItems(): List<ApiItemDto>
}
