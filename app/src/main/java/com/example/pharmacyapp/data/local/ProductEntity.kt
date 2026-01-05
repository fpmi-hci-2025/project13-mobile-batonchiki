package com.example.pharmacyapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val description: String,
    val category: String,
    val price: Double,
    val imageUrl: String,
    var isFavorite: Boolean = false
)