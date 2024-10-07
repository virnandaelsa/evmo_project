package com.example.virnandaelsa_3

import com.example.virnandaelsa_3.Models.RegisterRequest
import com.example.virnandaelsa_3.Models.RegisterResponse
import com.example.virnandaelsa_3.Models.LoginRequest
import com.example.virnandaelsa_3.Models.LoginResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Call

interface ApiService {
    @POST("register")
    fun register(@Body request: RegisterRequest): Call<RegisterResponse>

    @POST("login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>
}