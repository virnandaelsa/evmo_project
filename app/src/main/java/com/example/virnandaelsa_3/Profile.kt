package com.example.virnandaelsa_3

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import com.bumptech.glide.Glide
import com.example.virnandaelsa_3.databinding.FragProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

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
        db = FirebaseDatabase.getInstance().getReference("users")


        // Load data pelanggan
        loadCustomerData()
    }

    private fun loadCustomerData() {

        val sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val userId = sharedPref.getString("user_id", null)

        Log.d("EdProfile", "$userId")

        if (userId != null) {
            // Mengambil data pelanggan berdasarkan userId
            db.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val customer = snapshot.getValue(Customer::class.java)
                        if (customer != null) {
                            Log.d("EdProfile", "Customer data: $customer")
                            binding.txName.setText(customer.nama ?: "N/A")
                            binding.txTelepon.setText(customer.no_telp ?: "N/A")

                            // Decrypt email before setting it
                            val decryptedEmail = customer.email?.let {
                                EncryptionUtils.decrypt(it) // Assuming EncryptionUtils.decrypt() is your decryption function
                            }

                            binding.txEmail.setText(decryptedEmail ?: "N/A")
                            binding.txAlamatProf.setText(customer.alamat ?: "N/A")

                            customer.imageUrl?.let {
                                // Memuat gambar menggunakan Glide
                                Glide.with(this@Profile)
                                    .load(it)
                                    .into(binding.ImgProf)
                            }

                            this@Profile.customer = customer // Store the customer object if needed
                        }
                    } else {
                        Toast.makeText(this@Profile, "No customer data found", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        this@Profile,
                        "Database error: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        } else {
            Toast.makeText(this@Profile, "User ID not found", Toast.LENGTH_SHORT).show()
        }

//        db.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                if (snapshot.exists()) {
//                    for (dataSnapshot in snapshot.children) {
//                        val customer = dataSnapshot.getValue(Customer::class.java)
//                        if (customer != null) {
//                            // Mengisi data ke tampilan
//                            binding.txName.setText(customer.nama ?: "N/A")
//                            binding.txTelepon.setText(customer.no_telp ?: "N/A")
//                            binding.txEmail.setText(customer.email ?: "N/A")
//                            binding.txAlamatProf.setText(customer.alamat ?: "N/A")
//
//                            // Set gambar jika ada URL
//                            customer.imageUrl?.let {
//                                // Memuat gambar menggunakan Glide
//                                Glide.with(this@Profile)
//                                    .load(it)
//                                    .into(binding.ImgProf)
//                            }
//
//                            this@Profile.customer = customer // Menyimpan customer untuk penggunaan di update
//                            break // Ambil satu customer saja
//                        } else {
//                            Log.e("Profile", "Customer data is null")
//                        }
//                    }
//                } else {
//                    Toast.makeText(this@Profile, "No customer data found", Toast.LENGTH_SHORT).show()
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Toast.makeText(this@Profile, "Database error: ${error.message}", Toast.LENGTH_SHORT).show()
//            }
//        })
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btnBkProfile -> {
                // Kembali ke DashboardActivity
                finish() // Menutup Profile dan kembali ke Dashboard
            }

            R.id.btnUpdate -> {
                // Mengupdate data customer
                customer.nama = binding.txName.text.toString()
                customer.no_telp = binding.txTelepon.text.toString()
                val encryptedEmail =
                    EncryptionUtils.encrypt(binding.txEmail.text.toString()) // Encrypt email before saving
                customer.email = encryptedEmail
                customer.alamat = binding.txAlamatProf.text.toString()

                // Pastikan nama tidak kosong
                if (customer.nama!!.isNotBlank()) {
                    // Jika ada gambar yang di-upload, upload ke Firebase Storage terlebih dahulu
                    imageUri?.let {
                        uploadImageToFirebase(it) // Upload gambar dan simpan semua data
                    } ?: run {
                        // Jika tidak ada gambar baru, langsung simpan data lainnya
                        saveCustomerData() // Panggil fungsi untuk menyimpan semua data
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
        val storageReference =
            FirebaseStorage.getInstance().reference.child("profileImages/${System.currentTimeMillis()}.jpg")

        // Meng-upload gambar
        storageReference.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                // Gambar berhasil di-upload
                taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                    // Mendapatkan URL gambar yang di-upload
                    Log.d("Profile", "Image URL: $uri")

                    // Simpan URL di database bersama dengan data customer lainnya
                    customer.imageUrl = uri.toString() // Set URL gambar ke objek customer
                    saveCustomerData() // Panggil fungsi untuk menyimpan semua data customer termasuk gambar
                }
                Toast.makeText(this, "Upload successful", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveCustomerData() {
        val sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val userId = sharedPref.getString("user_id", null)

        Log.d("Profile", "$userId")

        if (userId != null) {
            customer.nama = binding.txName.text.toString()
            customer.no_telp = binding.txTelepon.text.toString()

            // Encrypt the email before saving it
            val encryptedEmail =
                EncryptionUtils.encrypt(binding.txEmail.text.toString()) // Encrypt email before saving
            customer.email = encryptedEmail // Store the encrypted email

            customer.alamat = binding.txAlamatProf.text.toString()

            // Save all customer data with UID as key in Firebase Database
            db.child(userId).setValue(customer)
                .addOnSuccessListener {
                    Toast.makeText(this, "Data updated successfully", Toast.LENGTH_SHORT).show()
                    finish() // Close the activity after data is updated
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to update data: ${e.message}", Toast.LENGTH_SHORT)
                        .show()
                }
        } else {
            Toast.makeText(this, "User ID not found. Please log in.", Toast.LENGTH_SHORT).show()
        }
    }
}
