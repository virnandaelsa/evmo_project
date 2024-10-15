package com.example.virnandaelsa_3

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.bumptech.glide.Glide
import com.example.virnandaelsa_3.databinding.FragPemesananBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class tambah_transaksi : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private var selectedImageUri: Uri? = null // Inisialisasi sebagai nullable
    private lateinit var binding: FragPemesananBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Menggunakan View Binding
        binding = FragPemesananBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ambil data yang dikirim dari activity sebelumnya
        val serviceId = intent.getStringExtra("SERVICE_ID")
        val productTitle = intent.getStringExtra("PRODUCT_TITLE")
        val productPrice = intent.getStringExtra("PRODUCT_PRICE")
        val productOwner = intent.getStringExtra("PRODUCT_OWNER")
        val productImageUri = intent.getStringExtra("PRODUCT_IMAGE_URI")

//        Log.d("Tambah Transaksi", "$productPrice")

        // Tampilkan data di layout menggunakan binding
        binding.txProduk4.text = productTitle
        binding.txHarga4.text = "$productPrice"
        binding.txToko4.text = productOwner

        // Tampilkan gambar produk menggunakan Glide jika ada
        if (productImageUri != null) {
            Glide.with(this)
                .load(productImageUri)
                .into(binding.imgProduk4)
        }

        // Tombol untuk menyimpan data transaksi
        binding.btnPesanan.setOnClickListener {
            val tanggal = binding.edTanggalInput.text.toString()
            val keterangan = binding.edKeteranganInput.text.toString()
            val alamat = binding.edAlamatInput.text.toString()

            // Convert price from String to Int
            val productPriceInt = productPrice?.toIntOrNull() // Safely convert to Int

            // Simpan transaksi
            simpanTransaksi(
                serviceId,
                productTitle,
                productPriceInt,
                productOwner,
                productImageUri, // Menggunakan imageUrl dari Firebase
                tanggal,
                keterangan,
                alamat
            )
        }

        // TextInput untuk tanggal dan keterangan
        binding.edTanggalInput.addTextChangedListener { }
        binding.edKeteranganInput.addTextChangedListener { }
        binding.edAlamatInput.addTextChangedListener { }
    }

    // Fungsi untuk menyimpan transaksi ke Firebase
    private fun simpanTransaksi(
        serviceId: String?,
        productTitle: String?,
        productPrice: Int?,
        productOwner: String?,
        imageUrl: String?, // Menggunakan imageUrl yang diterima dari intent
        tanggal: String?,
        keterangan: String?,
        alamat: String?
    ) {
        // Membuat map untuk transaksi
        val transaksi = mapOf(
            "id" to serviceId,
            "judul" to productTitle,
            "harga" to productPrice,
            "toko" to productOwner,
            "imageUrl" to (imageUrl ?: ""), // Jika `dpUri` kosong, berikan nilai default
            "tanggal" to tanggal,
            "keterangan" to keterangan,
            "alamat" to alamat
        )

        // Insert to Firebase
        database = FirebaseDatabase.getInstance().getReference("Transaksi")
        val newTransactionRef = database.push() // Buat referensi baru
        newTransactionRef.setValue(transaksi)
            .addOnSuccessListener {
                // Ambil transaction ID yang baru dibuat
                val transactionId = newTransactionRef.key

                Toast.makeText(this, "Transaksi berhasil disimpan!", Toast.LENGTH_SHORT).show()

                // Navigasi ke upload_dp setelah menyimpan transaksi
                val intent = Intent(this, upload_dp::class.java).apply {
                    putExtra("SERVICE_ID", serviceId)
                    putExtra("PRODUCT_TITLE", productTitle)
                    putExtra("PRODUCT_PRICE", productPrice)
                    putExtra("PRODUCT_OWNER", productOwner)
                    putExtra("PRODUCT_IMAGE_URI", imageUrl) // Kirim dpUri ke upload_dp
                    putExtra("TANGGAL", tanggal)
                    putExtra("KETERANGAN", keterangan)
                    putExtra("ALAMAT", alamat)
                    putExtra("TRANSACTION_ID", transactionId) // Kirim transactionId ke upload_dp
                }
                startActivity(intent)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal menyimpan transaksi: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
