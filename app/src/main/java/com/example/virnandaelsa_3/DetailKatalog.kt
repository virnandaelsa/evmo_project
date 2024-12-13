package com.example.virnandaelsa_3

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.virnandaelsa_3.databinding.FragDtkatalogBinding

class DetailKatalog: AppCompatActivity() {

    private lateinit var binding: FragDtkatalogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragDtkatalogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ambil data dari Intent
        val serviceId = intent.getStringExtra("SERVICE_ID") ?: ""
        val imageUrl = intent.getStringExtra("PRODUCT_IMAGE_URI") ?: ""
        val title = intent.getStringExtra("PRODUCT_TITLE") ?: "Judul tidak tersedia"
        val price = intent.getStringExtra("PRODUCT_PRICE") ?: "Harga tidak tersedia"
        val owner = intent.getStringExtra("PRODUCT_OWNER") ?: "Toko tidak diketahui"
        val description = intent.getStringExtra("PRODUCT_DESCRIPTION") ?: "Deskripsi tidak tersedia"

        // Set data ke view
        binding.judulDtktlg.text = title
        binding.hargaDtktlg.text = price
        binding.tokoDtktlg.text = owner
        binding.deskripsiDtktlg.text = description

        // Tampilkan gambar menggunakan Glide
        Glide.with(this)
            .load(imageUrl)
            .into(binding.gambarDtktlg)

//        // Tombol kembali
//        binding.buttonBack.setOnClickListener {
//            finish()
//        }

        // Tombol pesan
        binding.btnPesan.setOnClickListener {
            val intent = Intent(this, tambah_transaksi::class.java).apply {
                putExtra("SERVICE_ID", serviceId)
                putExtra("PRODUCT_TITLE", title)
                putExtra("PRODUCT_PRICE", price)
                putExtra("PRODUCT_OWNER", owner)
                putExtra("PRODUCT_IMAGE_URI", imageUrl ?: "")

            }
            startActivity(intent)
        }
    }
}
