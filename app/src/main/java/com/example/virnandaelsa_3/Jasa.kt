package com.example.virnandaelsa_3

// Kelas data Jasa
data class Jasa(
    val id: String = "",
    val judul: String = "",
    var harga: Int? = null, // Ubah dari val menjadi var
    val toko: String = "",
    val imageUrl: String? = null
)

