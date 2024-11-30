package com.example.virnandaelsa_3

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.virnandaelsa_3.databinding.FragProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
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

                            // Dekripsi nomor telepon yang disimpan terenkripsi di Firebase
                            val decryptedPhone = customer.no_telp?.let {
                                EncryptionUtils.decrypt(it) // Decrypt phone number
                            }
                            binding.txTelepon.setText(decryptedPhone ?: "N/A")

                            // Email tidak didekripsi, langsung ditampilkan
                            binding.txEmail.setText(customer.email ?: "N/A")
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

                // Enkripsi nomor telepon sebelum disimpan
                val encryptedPhone = EncryptionUtils.encrypt(binding.txTelepon.text.toString())
                customer.no_telp = encryptedPhone

                // Tidak perlu mengenkripsi email karena email tetap tidak terenkripsi
                customer.email = binding.txEmail.text.toString()

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
            // Get the name from the UI
            customer.nama = binding.txName.text.toString()

            // Encrypt the phone number before saving
            val encryptedNoTelp = EncryptionUtils.encrypt(binding.txTelepon.text.toString())
            customer.no_telp = encryptedNoTelp  // Save the encrypted phone number

            // Save the other details (email, address) as they are
            customer.email = binding.txEmail.text.toString()
            customer.alamat = binding.txAlamatProf.text.toString()

            // Save all customer data with UID as key in Firebase Database
            db.child(userId).setValue(customer)
                .addOnCompleteListener {
                    Toast.makeText(this, "Data updated successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to update data", Toast.LENGTH_SHORT).show()
                }
        }
    }
}