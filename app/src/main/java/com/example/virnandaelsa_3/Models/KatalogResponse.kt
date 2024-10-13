package com.example.virnandaelsa_3.Models

data class KatalogResponse(
    val status: Boolean,
    val message: String,
    val data: KatalogData
)

data class KatalogData(
    val kategori: List<Kategori>,
    val penjual: List<Penjual>?,
    val detail_katalog: List<DetailKatalog>,
    val role: Int
)

data class Kategori(
    val id_kategori: Int,
    val judul_kategori: String,
    val gambar_kategori: String
)

data class Penjual(
    val id_katalog: Int,
    val detail_penjual_id: Int,
    val judul: String,
    val deskripsi: String,
    val metode_bayar: Int,
    val detail_katalog: List<DetailKatalogItem>
)

data class DetailKatalog(
    val id_katalog: Int,
    val detail_penjual_id: Int,
    val judul: String,
    val deskripsi: String,
    val metode_bayar: Int,
    val detail_katalog: List<DetailKatalogItem>
)

data class DetailKatalogItem(
    val id_detail_katalog: Int,
    val katalog_id: Int,
    val judul_variasi: String,
    val harga: Int,
    val gambar: String
)