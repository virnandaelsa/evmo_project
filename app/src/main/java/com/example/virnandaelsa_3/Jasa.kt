package com.example.virnandaelsa_3

// Kelas data Jasa
data class Jasa(
    val id: String = "",
    val tanggal: String? = null,
    val keterangan: String? = null,
    val alamat: String? = null,
    val judul: String = "",
    var harga: Int? = null, // Ubah dari val menjadi var
    val toko: String = "",
    val imageUrl: String? = null,
    val fotoDp : String? = null,
)

