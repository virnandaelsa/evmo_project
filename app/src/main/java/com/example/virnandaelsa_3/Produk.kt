package com.example.virnandaelsa_3

data class Produk(
    var alamat: String? = "",
    var harga: Int? = 0,
    var pembayaran: String? = "",
    var judul: String? = "",
    var keterangan: String? = "",
    var pj: String? = "",
    var tanggal: String? = "",
    var foto: String? = "" // Tambahkan atribut untuk URL foto
)
