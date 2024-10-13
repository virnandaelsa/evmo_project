package com.example.virnandaelsa_3

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.virnandaelsa_3.databinding.FragPemesananBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class tambah_transaksi : AppCompatActivity() {

    private lateinit var binding: FragPemesananBinding
    private var selectedImageUri: Uri? = null
    private lateinit var tanggal: String
    private lateinit var keterangan: String
    private lateinit var alamat: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragPemesananBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ambil data dari intent
        val serviceId = intent.getStringExtra("SERVICE_ID")
        val productTitle = intent.getStringExtra("PRODUCT_TITLE")
        val productPrice = intent.getIntExtra("PRODUCT_PRICE", 0).toString() // Ubah menjadi String
        val productOwner = intent.getStringExtra("PRODUCT_OWNER")
        val productImageUri = intent.getStringExtra("PRODUCT_IMAGE_URI")

        // Menampilkan data produk ke UI
        binding.txProduk4.text = productTitle
        binding.txHarga4.text = productPrice
        binding.txToko4.text = productOwner

        productImageUri?.let {
            Glide.with(this)
                .load(it) // URL gambar
                .into(binding.imgProduk4) // ImageView untuk menampilkan gambar
        }

        // Tombol untuk menambah transaksi
        binding.btnPesanan.setOnClickListener {
            uploadData(serviceId, productTitle, productPrice, productOwner)
        }
    }

    private fun uploadData(serviceId: String?, productTitle: String?, productPrice: String?, productOwner: String?) {
        // Ambil input dari TextInputEditText
        val tanggal = binding.edTanggalInput.text.toString()
        val keterangan = binding.edKeteranganInput.text.toString()
        val alamat = binding.edAlamatInput.text.toString()

        // Validasi input
        if (tanggal.isEmpty() || keterangan.isEmpty() || alamat.isEmpty()) {
            Toast.makeText(this, "Silakan isi semua field", Toast.LENGTH_SHORT).show()
            return
        }

        // Konversi harga dari String ke Int
        val harga = productPrice?.toIntOrNull() ?: 0 // Ubah harga ke Int, jika tidak ada set 0

        // Membuat objek Jasa yang akan disimpan
        val jasa = Jasa(
            id = serviceId ?: "", // ID Jasa
            tanggal = tanggal,
            keterangan = keterangan,
            alamat = alamat,
            judul = productTitle ?: "",
            harga = harga, // Pastikan ini adalah Int
            toko = productOwner ?: "",
            imageUrl = selectedImageUri.toString() // Jika ada gambar
        )

        // Simpan ke Firebase Realtime Database
        val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("EVMO")

        // Menyimpan data ke Realtime Database dengan ID yang sesuai
        database.child(serviceId ?: "").setValue(jasa)
            .addOnSuccessListener {
                Toast.makeText(this, "Data berhasil disimpan", Toast.LENGTH_SHORT).show()
                // Panggil fungsi untuk navigasi setelah penyimpanan berhasil
                navigateToUploadDP(serviceId, productTitle, productPrice, productOwner, tanggal, keterangan, alamat)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal menyimpan data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
    // Setelah menyimpan data, navigasi ke halaman upload DP
    private fun navigateToUploadDP(serviceId: String?, productTitle: String?, productPrice: String?, productOwner: String?, tanggal: String, keterangan: String, alamat: String) {
        // Halaman tambah transaksi
        val intent = Intent(this, upload_dp::class.java)
        intent.putExtra("SERVICE_ID", serviceId)
        intent.putExtra("PRODUCT_TITLE", productTitle)
        intent.putExtra("PRODUCT_PRICE", productPrice)
        intent.putExtra("PRODUCT_OWNER", productOwner)
        intent.putExtra("TANGGAL", tanggal)
        intent.putExtra("KETERANGAN", keterangan)
        intent.putExtra("ALAMAT", alamat)
        startActivity(intent)
    }
}
