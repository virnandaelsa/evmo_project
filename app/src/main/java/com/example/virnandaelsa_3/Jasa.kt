package com.example.virnandaelsa_3

// Kelas data Jasa
data class Jasa(
    val id: String? = null,           // Corresponds to 'id' in Firebase
    val judul: String? = null,        // Corresponds to 'judul' in Firebase
    val harga: Long = 0L,                // Corresponds to 'harga' in Firebase (should be an Int)
    val toko: String? = null,         // Corresponds to 'toko' in Firebase
    val imageUrl: String? = null,        // Optional, add as needed
    val dpUri: String? = null,        // Optional, add as needed
    val tanggal: String? = null,      // Optional, add as needed
    val keterangan: String? = null,   // Optional, add as needed
    val alamat: String? = null,      // Optional, add as needed
    val transactionId: String? = null
)


