package com.example.virnandaelsa_3

data class Transaksi(
    val transactionId: String,
    val judul: String,
    val harga: Long,
    val toko: String,
    val imageUrl: String,
    val keterangan: String,
    val tanggal: String,
    val alamat: String,
    val dpUri: String // Tambahkan URL foto DP
)



