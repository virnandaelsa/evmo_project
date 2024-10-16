package com.example.virnandaelsa_3

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.virnandaelsa_3.databinding.FragDpBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*

class upload_dp : AppCompatActivity() {

    private lateinit var binding: FragDpBinding
    private lateinit var storageRef: StorageReference
    private lateinit var database: DatabaseReference
    private var selectedImageUri: Uri? = null
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Menggunakan View Binding
        binding = FragDpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi Firebase Storage dan Realtime Database
        storageRef = FirebaseStorage.getInstance().reference
        database = FirebaseDatabase.getInstance().getReference("Transaksi")

        // Ambil data yang dikirim dari tambah_transaksi
        val serviceId = intent.getStringExtra("SERVICE_ID")
        val productTitle = intent.getStringExtra("PRODUCT_TITLE")
        val productPrice = intent.getStringExtra("PRODUCT_PRICE")
        val productOwner = intent.getStringExtra("PRODUCT_OWNER")
        val productImageUri = intent.getStringExtra("PRODUCT_IMAGE_URI")
        val tanggal = intent.getStringExtra("TANGGAL")
        val keterangan = intent.getStringExtra("KETERANGAN")
        val alamat = intent.getStringExtra("ALAMAT")
        val transactionId = intent.getStringExtra("TRANSACTION_ID") // Ambil transactionId

        // Tampilkan data di layout
        binding.txProduk2.text = productTitle
        binding.txhargadp.text = "$productPrice"
        Log.d("Upload DP", "$productPrice")
        binding.txToko2.text = productOwner
        binding.txTgl.text = tanggal
        binding.txket.text = keterangan
        binding.txalamat.text = alamat

        // Tampilkan gambar produk menggunakan Glide jika ada
        if (productImageUri != null) {
            Glide.with(this)
                .load(productImageUri)
                .into(binding.imgProduk2)
        }

        // Pilih gambar DP dari galeri
        binding.imgbtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        // Tombol untuk mengunggah DP
        binding.btnKirim.setOnClickListener {
            if (selectedImageUri != null) {
                uploadDpToFirebase(selectedImageUri!!, transactionId) // Kirim transactionId ke fungsi upload
            } else {
                Toast.makeText(this, "Silakan pilih gambar DP terlebih dahulu.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Menangani hasil dari aktivitas memilih gambar
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            selectedImageUri = data.data

            // Tampilkan gambar yang dipilih ke ImageView
            Glide.with(this)
                .load(selectedImageUri)
                .into(binding.imgDp)
        }
    }

    // Fungsi untuk mengunggah DP ke Firebase Storage
    private fun uploadDpToFirebase(
        imageUri: Uri,
        transactionId: String? // Terima transactionId untuk memperbarui data
    ) {
        // Tampilkan progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Mengunggah DP...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        // Buat reference untuk gambar DP di Firebase Storage
        val dpRef = storageRef.child("dps/${UUID.randomUUID()}.jpg")

        // Unggah gambar
        dpRef.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                    // Gambar berhasil diunggah, dapatkan URL-nya
                    val dpUrl = uri.toString()

                    // Simpan data DP ke transaksi yang sudah ada
                    val dpUpdate = mapOf(
                        "dpUri" to dpUrl // Menambahkan URL DP ke transaksi
                    )

                    // Perbarui transaksi yang sudah ada
                    if (transactionId != null) {
                        database.child(transactionId).updateChildren(dpUpdate)
                            .addOnSuccessListener {
                                Toast.makeText(this, "DP berhasil diunggah!", Toast.LENGTH_SHORT).show()
                                progressDialog.dismiss()

                                // Navigasi ke aktivitas lain jika perlu
                                startActivity(Intent(this, DashboardActivity::class.java))
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Gagal memperbarui transaksi: ${e.message}", Toast.LENGTH_SHORT).show()
                                progressDialog.dismiss()
                            }
                    } else {
                        Toast.makeText(this, "Transaction ID tidak valid.", Toast.LENGTH_SHORT).show()
                        progressDialog.dismiss()
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal mengunggah DP: ${e.message}", Toast.LENGTH_SHORT).show()
                progressDialog.dismiss()
            }
    }

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }
}
