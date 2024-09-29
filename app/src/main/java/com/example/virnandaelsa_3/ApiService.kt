package com.example.virnandaelsa_3

import com.example.virnandaelsa_3.Models.RegisterRequest
import com.example.virnandaelsa_3.Models.RegisterResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Call

interface ApiService {
    @POST("register")
    fun register(@Body request: RegisterRequest): Call<RegisterResponse>
}