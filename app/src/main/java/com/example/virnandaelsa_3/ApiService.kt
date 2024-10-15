package com.example.virnandaelsa_3

import com.example.virnandaelsa_3.Models.KatalogResponse
import com.example.virnandaelsa_3.Models.RegisterRequest
import com.example.virnandaelsa_3.Models.RegisterResponse
import com.example.virnandaelsa_3.Models.LoginRequest
import com.example.virnandaelsa_3.Models.LoginResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface ApiService {
    @POST("register")
    fun register(@Body request: RegisterRequest): Call<RegisterResponse>

    @POST("login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @GET("katalog")
    fun getKatalog(@Header("Authorization") token: String): Call<KatalogResponse>
}