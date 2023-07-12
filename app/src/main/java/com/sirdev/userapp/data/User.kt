package com.sirdev.userapp.data

import com.google.gson.annotations.SerializedName

data class User(
    val id: Int,
    val email: String,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    val avatar: String
)

data class ApiResponse(
    @SerializedName("page")
    val page: Int,
    @SerializedName("per_page")
    val perPage: Int,
    @SerializedName("total")
    val total: Int,
    @SerializedName("total_pages")
    val totalPages: Int,
    @SerializedName("data")
    val data: List<User>,
    @SerializedName("support")
    val support: Support
)

data class Support(
    @SerializedName("url")
    val url: String,
    @SerializedName("text")
    val text: String
)
