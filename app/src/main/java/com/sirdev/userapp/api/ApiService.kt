package com.sirdev.userapp.api

import com.sirdev.userapp.data.ApiResponse
import com.sirdev.userapp.data.User
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("api/users")
    fun getUsers(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int)
    : Call<ApiResponse>
}