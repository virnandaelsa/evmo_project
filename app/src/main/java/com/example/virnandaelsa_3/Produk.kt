package com.example.virnandaelsa_3

data class Produk(
    var alamat: String? = "",
    var harga: Int? = 0,
    var pembayaran: String? = "",
    var nama: String? = "",
    var keterangan: String? = "",
    var toko: String? = "",
    var tanggal: String? = "",
    var imageUrl: String? = "" // Tambahkan atribut untuk URL foto
)
