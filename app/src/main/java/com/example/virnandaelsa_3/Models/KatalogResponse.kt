package com.example.virnandaelsa_3.Models

data class KatalogResponse(
    val status: Boolean,
    val message: String,
    val data: KatalogData
)

data class KatalogData(
    val kategori: List<Kategori>,
    val detail_katalog: List<DetailKatalog>,
    val role: Int,
    val nama_toko: String,
    val user: User
)

data class Kategori(
    val id_kategori: Int,
    val judul_kategori: String,
    val gambar_kategori: String,
    val created_at: String?,
    val updated_at: String?
)

data class DetailKatalog(
    val id_katalog: Int,
    val detail_penjual_id: Int,
    val judul: String,
    val deskripsi: String,
    val metode_bayar: Int?,
    val created_at: String?,
    val updated_at: String?,
    val detail_katalog: List<DetailKatalogItem>
)

data class DetailKatalogItem(
    val id_detail_katalog: Int,
    val katalog_id: Int,
    val judul_variasi: String,
    val harga: Int,
    val gambar: String,
    val created_at: String?,
    val updated_at: String?,
    val pivot: Pivot
)

data class Pivot(
    val id_katalog: Int
)

data class User(
    val id_user: Int,
    val nama: String,
    val no_telp: String,
    val email: String,
    val username: String,
    val foto: String?,
    val role: Int,
    val alamat: String,
    val created_at: String,
    val updated_at: String
)
