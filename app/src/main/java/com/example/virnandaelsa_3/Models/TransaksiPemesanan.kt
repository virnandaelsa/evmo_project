package com.example.virnandaelsa_3.Models

data class TransaksiPemesanan(
        val id: String?,
        val judul: String?,
        val harga: Int,
        val toko: String?,
        val imageUrl: String?,
        val tanggal: String?,
        val keterangan: String?,
        val alamat: String?,
        val username: String?
)

