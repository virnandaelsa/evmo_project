package com.example.virnandaelsa_3

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.virnandaelsa_3.databinding.ActivityUploadDpBinding
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat
import java.util.Locale

class upload_dp : AppCompatActivity() {
    private lateinit var binding: ActivityUploadDpBinding
    private lateinit var storageRef: StorageReference
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inisialisasi binding dan Firebase
        binding = ActivityUploadDpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi Firebase
        val db = FirebaseFirestore.getInstance()
        storageRef = FirebaseStorage.getInstance().reference

        // Mendapatkan referensi dokumen di Firestore
        val docRef = db.collection("EVMO").document("123")

        // Mengambil data dari database
        docRef.get().addOnSuccessListener { documentSnapshot: DocumentSnapshot ->
            if (documentSnapshot.exists()) {
                // Ambil data dari Firestore
                populateFields(documentSnapshot)
            } else {
                Toast.makeText(this, "Document does not exist", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Error fetching data: ${e.message}", Toast.LENGTH_SHORT).show()
        }

        // Tombol untuk memilih gambar
        binding.imageButton.setOnClickListener {
            openGallery()
        }

        // Tombol untuk mengupload gambar
        binding.btnUploadPayment.setOnClickListener {
            if (selectedImageUri != null) {
                uploadImage(selectedImageUri!!)
            } else {
                Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun populateFields(documentSnapshot: DocumentSnapshot) {
        val alamat = documentSnapshot.getString("alamat")
        val harga = documentSnapshot.getString("harga") ?: "0"
        val pembayaran = documentSnapshot.getString("pembayaran")
        val fotoUrl = documentSnapshot.getString("foto")
        val judul = documentSnapshot.getString("judul")
        val keterangan = documentSnapshot.getString("keterangan")
        val pj = documentSnapshot.getString("toko")

        // Ambil 'tanggal' sebagai Timestamp
        val timestamp = documentSnapshot.getTimestamp("tanggal")
        val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
        val tanggal = timestamp?.let { sdf.format(it.toDate()) }

        // Mengisi data ke dalam TextView
        binding.txJudul.text = judul
        binding.txHarga.text = "Rp $harga"
        binding.txToko.text = pj
        binding.txTanggal.text = tanggal
        binding.txKet.text = keterangan
        binding.txAlamat.text = alamat
        binding.txBayar.text = "Silahkan melakukan pembayaran ke rekening $pembayaran"
        binding.txRekening.text = pembayaran

        // Menggunakan Glide untuk menampilkan gambar dari URL
        Glide.with(this).load(fotoUrl).into(binding.imageView2)
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK) {
            selectedImageUri = data?.data
            if (selectedImageUri != null) {
                Glide.with(this).load(selectedImageUri).into(binding.imageViewbukti)
            } else {
                Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadImage(imageUri: Uri) {
        val imageRef = storageRef.child("uploads/${System.currentTimeMillis()}.jpg")
        imageRef.putFile(imageUri)
            .addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    saveImageUrlToFirestore(uri.toString())
                }.addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to get download URL: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { e ->
                Toast.makeText(this, "Image upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveImageUrlToFirestore(imageUrl: String) {
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("EVMO").document("123")

        docRef.update("bukti_dp", imageUrl)
            .addOnSuccessListener {
                Toast.makeText(this, "Image URL saved successfully", Toast.LENGTH_SHORT).show()
                Glide.with(this).load(imageUrl).into(binding.imageViewbukti) // Update image view
            }.addOnFailureListener { e ->
                Toast.makeText(this, "Failed to save image URL: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
