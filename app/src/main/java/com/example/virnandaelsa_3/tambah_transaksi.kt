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
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import java.util.*

class tambah_transaksi : AppCompatActivity(), View.OnClickListener {

    private lateinit var database: DatabaseReference
    private var selectedImageUri: Uri? = null // Inisialisasi sebagai nullable
    private lateinit var binding: FragPemesananBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Menggunakan View Binding
        binding = FragPemesananBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBKPemesanan.setOnClickListener(this)

        // Ambil data dari intent

        val serviceId = intent.getStringExtra("SERVICE_ID") ?: ""
        val productTitle = intent.getStringExtra("PRODUCT_TITLE") ?: "Produk Tidak Diketahui"
        val productPrice = intent.getStringExtra("PRODUCT_PRICE") ?: "0"
        val productOwner = intent.getStringExtra("PRODUCT_OWNER") ?: "Toko Tidak Diketahui"
        val productImageUri = intent.getStringExtra("PRODUCT_IMAGE_URI") ?: ""

        // Tampilkan data di layout
        binding.txProduk4.text = productTitle
        binding.txHarga4.text = productPrice
        binding.txToko4.text = productOwner

        // Tampilkan gambar produk menggunakan Glide
        Glide.with(this)
            .load(productImageUri)
            .into(binding.imgProduk4)

        // Pilih tanggal dan waktu
        binding.edTanggalInput.setOnClickListener {
            showDateTimePickerDialog()
        }

        // Tombol untuk menyimpan transaksi
        binding.btnPesanan.setOnClickListener {
            val tanggal = binding.edTanggalInput.text.toString()
            val keterangan = binding.edKeteranganInput.text.toString()
            val alamat = binding.edAlamatInput.text.toString()
            val productPriceInt = productPrice.replace("[^\\d]".toRegex(), "").toIntOrNull() ?: 0

            // Validasi input sebelum menyimpan transaksi
            if (tanggal.isEmpty() || keterangan.isEmpty() || alamat.isEmpty()) {
                Toast.makeText(this, "Harap isi semua kolom!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            simpanTransaksi(
                serviceId,
                productTitle,
                productPriceInt,
                productOwner,
                productImageUri,
                tanggal,
                keterangan,
                alamat
            )
        }
    }

    private fun showDateTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"

            // Setelah tanggal dipilih, munculkan TimePickerDialog
            TimePickerDialog(this, { _, selectedHour, selectedMinute ->
                val formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                val selectedDateTime = "$selectedDate $formattedTime"

                binding.edTanggalInput.setText(selectedDateTime)
            }, hour, minute, true).show()

        }, year, month, day).show()
    }

    private fun simpanTransaksi(
        serviceId: String?,
        productTitle: String?,
        productPrice: Int,
        productOwner: String?,
        imageUrl: String?,
        tanggal: String?,
        keterangan: String?,
        alamat: String?
    ) {
        // Log data transaksi sebelum disimpan
        Log.d("TambahTransaksi", "Menyimpan transaksi dengan data: \n" +
                "serviceId: $serviceId\n" +
                "productTitle: $productTitle\n" +
                "productPrice: $productPrice\n" +
                "productOwner: $productOwner\n" +
                "imageUrl: $imageUrl\n" +
                "tanggal: $tanggal\n" +
                "keterangan: $keterangan\n" +
                "alamat: $alamat")

        val currentUser = FirebaseAuth.getInstance().currentUser
        val username = currentUser?.displayName ?: currentUser?.email ?: "Unknown"  // Use email as fallback

        val transaksi = mapOf(
            "id" to serviceId,
            "judul" to productTitle,
            "harga" to productPrice,
            "toko" to productOwner,
            "imageUrl" to (imageUrl ?: ""),
            "tanggal" to tanggal,
            "keterangan" to keterangan,
            "alamat" to alamat,
            "username" to username
        )

        database = FirebaseDatabase.getInstance().getReference("Transaksi")
        val newTransactionRef = database.push()

        newTransactionRef.setValue(transaksi)
            .addOnSuccessListener {
                val transactionId = newTransactionRef.key
                // Log ketika transaksi berhasil disimpan
                Log.d("TambahTransaksi", "Transaksi berhasil disimpan dengan ID: $transactionId")

                Toast.makeText(this, "Transaksi berhasil disimpan!", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, upload_dp::class.java).apply {
                    putExtra("transactionId", transactionId)
                    putExtra("SERVICE_ID", serviceId)
                    putExtra("PRODUCT_TITLE", productTitle)
                    putExtra("PRODUCT_PRICE", productPrice.toString())
                    putExtra("PRODUCT_OWNER", productOwner)
                    putExtra("PRODUCT_IMAGE_URI", imageUrl)
                    putExtra("TANGGAL", tanggal)
                    putExtra("KETERANGAN", keterangan)
                    putExtra("ALAMAT", alamat)
                }

                // Log pengiriman data ke aktivitas berikutnya
                Log.d("TambahTransaksi", "Mengirimkan intent ke aktivitas upload_dp dengan transactionId: $transactionId")

                startActivity(intent)
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseError", "Gagal menyimpan transaksi", e)
                Toast.makeText(this, "Gagal menyimpan transaksi: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btnBKPemesanan-> {
                // Kembali ke DashboardActivity
                finish() // Menutup Profile dan kembali ke Dashboard
            }
        }
    }
}
