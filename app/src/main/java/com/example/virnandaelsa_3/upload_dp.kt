package com.example.virnandaelsa_3

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.virnandaelsa_3.databinding.FragDpBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class upload_dp : AppCompatActivity() {

    private lateinit var binding: FragDpBinding
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragDpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ambil data dari intent
        val serviceId = intent.getStringExtra("SERVICE_ID")
        val productTitle = intent.getStringExtra("PRODUCT_TITLE")
        val productPrice = intent.getStringExtra("PRODUCT_PRICE")
        val productOwner = intent.getStringExtra("PRODUCT_OWNER")
        val tanggal = intent.getStringExtra("TANGGAL")
        val keterangan = intent.getStringExtra("KETERANGAN")
        val alamat = intent.getStringExtra("ALAMAT")

        // Menampilkan data produk ke UI
        binding.txProduk2.text = productTitle
        binding.txHarga2.text = productPrice
        binding.txToko2.text = productOwner
        binding.txTgl.text = tanggal // Menampilkan tanggal di TextView
        binding.txket.text = keterangan // Menampilkan keterangan di TextView
        binding.txalamat.text = alamat // Menampilkan alamat di TextView

        // Fungsi untuk memilih gambar DP menggunakan ImageButton
        binding.imgbtn.setOnClickListener {
            selectImageFromGallery()
        }

        // Tombol untuk mengupload bukti DP
        binding.btnKirim.setOnClickListener {
            if (serviceId != null) {
                uploadImage(serviceId)
            } else {
                Toast.makeText(this, "ID Layanan tidak tersedia", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadImage(serviceId: String) {
        selectedImageUri?.let { uri ->
            val storageReference: StorageReference = FirebaseStorage.getInstance().reference
                .child("dp/${serviceId}_${System.currentTimeMillis()}.${getFileExtension(uri)}")

            storageReference.putFile(uri)
                .addOnSuccessListener {
                    storageReference.downloadUrl.addOnSuccessListener { downloadUri ->
                        // Setelah berhasil upload, simpan ke Firebase Realtime Database
                        saveDataToRealtimeDatabase(serviceId, downloadUri.toString())
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Gagal mengupload bukti DP: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } ?: run {
            Toast.makeText(this, "Silakan pilih gambar untuk diupload", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveDataToRealtimeDatabase(serviceId: String, imageUrl: String) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("uploads")
        val uploadId = databaseReference.push().key

        val uploadData = UploadData(serviceId, imageUrl)
        uploadId?.let {
            databaseReference.child(it).setValue(uploadData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Data berhasil disimpan", Toast.LENGTH_SHORT).show()
                    // Reset image view setelah upload berhasil
                    binding.imgbtn.setImageURI(null) // Ganti ini dengan default image jika perlu
                    selectedImageUri = null
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Gagal menyimpan data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun selectImageFromGallery() {
        val intent = Intent().apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
        }
        startActivityForResult(Intent.createChooser(intent, "Pilih Gambar"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data
            // Menampilkan gambar yang dipilih pada ImageButton
            binding.imgbtn.setImageURI(selectedImageUri)
        }
    }

    private fun getFileExtension(uri: Uri): String {
        return uri.toString().substringAfterLast(".")
    }

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }

    data class UploadData(
        val serviceId: String,
        val imageUrl: String
    )
}
