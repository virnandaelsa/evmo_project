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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragPemesananBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ambil data dari intent
        val serviceId = intent.getStringExtra("SERVICE_ID")
        val productTitle = intent.getStringExtra("PRODUCT_TITLE")
        val productPrice = intent.getStringExtra("PRODUCT_PRICE")
        val productOwner = intent.getStringExtra("PRODUCT_OWNER")
        val productImageUri = intent.getStringExtra("PRODUCT_IMAGE_URI")

        // Menampilkan data produk ke UI
        binding.txProduk2.text = productTitle
        binding.txHarga2.text = productPrice
        binding.txToko1.text = productOwner

        productImageUri?.let {
            Glide.with(this)
                .load(it) // URL gambar
                .into(binding.imgProduk2) // ImageView untuk menampilkan gambar
        }

        // Tombol untuk menambah transaksi
        binding.btnPesanan.setOnClickListener {
            uploadData(serviceId, productTitle, productPrice, productOwner, productImageUri)
        }

        // Fungsi pemilihan gambar
        binding.imageView6.setOnClickListener {
            selectImageFromGallery()
        }
    }

    private fun uploadData(serviceId: String?, productTitle: String?, productPrice: String?, productOwner: String?, productImageUri: String?) {
        // Ambil input dari TextInputEditText
        val tanggal = binding.edTanggal.text.toString()
        val keterangan = binding.edKet.text.toString()
        val alamat = binding.edAlamat.text.toString()

        // Validasi input
        if (tanggal.isEmpty() || keterangan.isEmpty() || alamat.isEmpty()) {
            Toast.makeText(this, "Silakan isi semua field", Toast.LENGTH_SHORT).show()
            return
        }

        // Simpan ke Firebase Realtime Database
        val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("EVMO")

        // Membuat objek data yang akan disimpan
        val data = hashMapOf(
            "tanggal" to tanggal,
            "keterangan" to keterangan,
            "alamat" to alamat,
            "serviceId" to (serviceId ?: ""),
            "productTitle" to (productTitle ?: ""),
            "productPrice" to (productPrice ?: ""),
            "productOwner" to (productOwner ?: ""),
            "productImageUri" to (productImageUri ?: "")
        )

        // Menyimpan data ke Realtime Database
        serviceId?.let {
            database.child(it).setValue(data)
                .addOnSuccessListener {
                    Toast.makeText(this, "Data berhasil disimpan", Toast.LENGTH_SHORT).show()
                    navigateToUploadDP(serviceId, productTitle, productPrice, productOwner, productImageUri)
                }.addOnFailureListener { e ->
                    Toast.makeText(this, "Gagal menyimpan data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } ?: run {
            Toast.makeText(this, "ID Jasa tidak valid", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToUploadDP(serviceId: String?, productTitle: String?, productPrice: String?, productOwner: String?, productImageUri: String?) {
        val intent = Intent(this, tambah_transaksi::class.java).apply {
            putExtra("SERVICE_ID", serviceId)
            putExtra("PRODUCT_TITLE", productTitle)
            putExtra("PRODUCT_PRICE", productPrice)
            putExtra("PRODUCT_OWNER", productOwner)
            putExtra("PRODUCT_IMAGE_URI", productImageUri)
        }
        startActivity(intent)
    }

    private fun selectImageFromGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Pilih Gambar"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data
            binding.imageView6.setImageURI(selectedImageUri) // Tampilkan gambar yang dipilih
        }
    }

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }
}
