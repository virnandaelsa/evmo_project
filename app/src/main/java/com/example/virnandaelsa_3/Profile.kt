package com.example.virnandaelsa_3

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.virnandaelsa_3.databinding.FragProfileBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.bumptech.glide.Glide // Pastikan menambahkan Glide di build.gradle

class Profile : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: FragProfileBinding
    private lateinit var db: DatabaseReference
    private lateinit var customer: Customer
    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Menginisialisasi ViewBinding
        binding = FragProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Mengatur click listener untuk tombol kembali dan update
        binding.btnBkProfile.setOnClickListener(this)
        binding.btnUpdate.setOnClickListener(this)
        binding.ImgProf.setOnClickListener { openGallery() } // Set click listener untuk ImageView

        // Menginisialisasi Firebase Database
        db = FirebaseDatabase.getInstance().getReference("Customer")

        // Load data pelanggan
        loadCustomerData()
    }

    private fun loadCustomerData() {
        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (dataSnapshot in snapshot.children) {
                        val customer = dataSnapshot.getValue(Customer::class.java)
                        if (customer != null) {
                            // Mengisi data ke tampilan
                            binding.txName.setText(customer.name ?: "N/A")
                            binding.txTelepon.setText(customer.phone ?: "N/A")
                            binding.txEmail.setText(customer.email ?: "N/A")
                            binding.txAlamatProf.setText(customer.alamat ?: "N/A")

                            // Set gambar jika ada URL
                            customer.imageUrl?.let {
                                // Memuat gambar menggunakan Glide
                                Glide.with(this@Profile)
                                    .load(it)
                                    .into(binding.ImgProf)
                            }

                            this@Profile.customer = customer // Menyimpan customer untuk penggunaan di update
                            break // Ambil satu customer saja
                        } else {
                            Log.e("Profile", "Customer data is null")
                        }
                    }
                } else {
                    Toast.makeText(this@Profile, "No customer data found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@Profile, "Database error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btnBkProfile -> {
                // Kembali ke DashboardActivity
                finish() // Menutup Profile dan kembali ke Dashboard
            }
            R.id.btnUpdate -> {
                // Mengupdate data customer
                customer.name = binding.txName.text.toString()
                customer.phone = binding.txTelepon.text.toString()
                customer.email = binding.txEmail.text.toString()
                customer.alamat = binding.txAlamatProf.text.toString()

                // Pastikan nama tidak kosong
                if (customer.name!!.isNotBlank()) {
                    // Menyimpan pembaruan ke Firebase
                    db.child(customer.name!!).setValue(customer)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Data updated successfully", Toast.LENGTH_SHORT).show()
                            // Jika ada gambar yang di-upload, upload ke Firebase Storage
                            imageUri?.let { uri -> uploadImageToFirebase(uri) }
                            finish() // Tutup activity setelah update
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Failed to update data: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(this, "Nama tidak boleh kosong", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun openGallery() {
        // Memilih gambar dari galeri
        val intent = Intent().apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
        }
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            imageUri = data.data
            imageUri?.let {
                binding.ImgProf.setImageURI(it) // Menampilkan gambar di ImageView
            }
        }
    }

    private fun uploadImageToFirebase(imageUri: Uri) {
        // Buat referensi ke Firebase Storage
        val storageReference = FirebaseStorage.getInstance().reference.child("profileImages/${System.currentTimeMillis()}.jpg")

        // Meng-upload gambar
        storageReference.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                // Gambar berhasil di-upload
                taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                    // Mendapatkan URL gambar yang di-upload
                    Log.d("Profile", "Image URL: $uri")

                    // Simpan URL di database
                    saveImageUrlToDatabase(uri.toString())
                }
                Toast.makeText(this, "Upload successful", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveImageUrlToDatabase(imageUrl: String) {
        // Menyimpan URL gambar ke dalam database
        db.child(customer.name ?: "defaultName").child("imageUrl").setValue(imageUrl)
            .addOnSuccessListener {
                Toast.makeText(this, "Image URL saved to database", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to save image URL: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
