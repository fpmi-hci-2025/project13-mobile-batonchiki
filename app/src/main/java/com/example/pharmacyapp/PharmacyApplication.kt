package com.example.pharmacyapp

import android.app.Application
import android.util.Log
import com.example.pharmacyapp.data.local.AppDatabase
import com.example.pharmacyapp.data.remote.ApiService
import com.example.pharmacyapp.data.repository.ProductRepository
import com.example.pharmacyapp.data.repository.ProductRepositoryImpl
import com.example.pharmacyapp.logger.DefaultLogger
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PharmacyApplication : Application() {


    private val database: AppDatabase by lazy {
        AppDatabase.getInstance(this)
    }


    private val apiService: ApiService by lazy {

        val logging = HttpLoggingInterceptor { message -> Log.d("OkHttp", message) }
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val httpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        Retrofit.Builder()
            .baseUrl("https://gist.githubusercontent.com/")
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }


    val productRepository: ProductRepository by lazy {
        ProductRepositoryImpl(
            productDao = database.productDao(),
            apiService = apiService,
            logger = DefaultLogger()
        )
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("PharmacyApplication", "Application Created")
    }
}