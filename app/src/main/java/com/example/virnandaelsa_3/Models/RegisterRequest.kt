package com.example.virnandaelsa_3.Models

data class RegisterRequest(
    val nama: String,
    val email: String,
    val alamat: String,
    val no_telp: String,
    val username: String,
    val password: String
)
