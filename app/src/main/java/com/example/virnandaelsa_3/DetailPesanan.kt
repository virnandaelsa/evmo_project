package com.example.virnandaelsa_3

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.virnandaelsa_3.databinding.FragDtpesananBinding
import com.google.firebase.database.*

class DetailPesanan : AppCompatActivity() {

    private lateinit var binding: FragDtpesananBinding
    private lateinit var database: DatabaseReference
    private lateinit var transactionId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragDtpesananBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Mendapatkan ID transaksi dari intent
        transactionId = intent.getStringExtra("TRANSACTION_ID") ?: ""

        database = FirebaseDatabase.getInstance().getReference("Transaksi")
        fetchTransaksiDetail(transactionId)
    }

    private fun fetchTransaksiDetail(id: String) {
        // Menggunakan child dengan transactionId untuk mengambil data yang benar
        database.child(id).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val judul = snapshot.child("judul").getValue(String::class.java) ?: "Tidak Ditemukan"
                    val harga = snapshot.child("harga").getValue(Long::class.java) ?: 0L
                    val toko = snapshot.child("toko").getValue(String::class.java) ?: "Tidak Ditemukan"
                    val imageUrl = snapshot.child("imageUrl").getValue(String::class.java) ?: ""
                    val keterangan = snapshot.child("keterangan").getValue(String::class.java) ?: ""
                    val tanggal = snapshot.child("tanggal").getValue(String::class.java) ?: ""
                    val alamat = snapshot.child("alamat").getValue(String::class.java) ?: ""
                    val dpImageUrl = snapshot.child("dpUri").getValue(String::class.java) ?: ""

                    // Tampilkan detail di layout menggunakan View Binding
                    binding.txProduk3.text = judul
                    binding.txHarga3.text = "Rp $harga"
                    binding.txToko3.text = toko
                    binding.txKet3.text = keterangan
                    binding.txTanggal3.text = tanggal
                    binding.txAlamat3.text = alamat

                    // Muat gambar produk
                    Glide.with(this@DetailPesanan)
                        .load(imageUrl)
                        .error(R.drawable.wisma) // Gambar placeholder jika gagal memuat
                        .into(binding.imgDtPesanan)

                    // Muat gambar DP
                    Glide.with(this@DetailPesanan)
                        .load(dpImageUrl)
                        .error(R.drawable.wisma) // Gambar placeholder jika gagal memuat
                        .into(binding.imgDT)
                } else {
                    // Handle case jika snapshot tidak ada
                    Toast.makeText(this@DetailPesanan, "Data tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Menangani error
                Toast.makeText(this@DetailPesanan, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
