package com.example.pharmacyapp.data.remote

import com.google.gson.annotations.SerializedName

data class ApiItemDto(
    @SerializedName("item_id") val itemId: String,
    val name: String,
    val description: String? = null,
    val price: Double,
    @SerializedName("has_image") val hasImage: Boolean = false,
    @SerializedName("image_mime") val imageMime: String? = null
)
