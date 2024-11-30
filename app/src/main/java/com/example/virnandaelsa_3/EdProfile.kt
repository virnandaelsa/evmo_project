package com.example.virnandaelsa_3

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.virnandaelsa_3.databinding.FragEdprofileBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EdProfile : Fragment(), View.OnClickListener {

    private lateinit var binding: FragEdprofileBinding
    private lateinit var thisParent: DashboardActivity
    private lateinit var db: DatabaseReference
    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        thisParent = requireActivity() as DashboardActivity
        binding = FragEdprofileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Menginisialisasi Firebase Database
        db = FirebaseDatabase.getInstance().getReference("users")

        // Listener untuk tombol
        binding.btnUpdate.setOnClickListener(this)
        binding.ImgProfed.setOnClickListener(this)
        binding.btnKeluar.setOnClickListener(this)// Set click listener untuk ImageView

        loadCustomerData()
    }

    private fun loadCustomerData() {
        val sharedPref = requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
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
                            binding.txNama1.setText(customer.nama ?: "N/A")
                            binding.txNama.setText(customer.nama ?: "N/A")
                            val decryptedPhone = customer.no_telp?.let {
                                EncryptionUtils.decrypt(it) // Decrypt phone number
                            }
                            binding.txNomor.setText(decryptedPhone ?: "N/A")
                            binding.txGmail.setText(customer.email ?: "N/A")
                            binding.txAlamat1.setText(customer.alamat ?: "N/A")

                            // Set gambar jika ada URL
                            customer.imageUrl?.let {
                                Glide.with(this@EdProfile)
                                    .load(it)
                                    .into(binding.ImgProfed)
                            }
                        }
                    } else {
                        Toast.makeText(requireContext(), "No customer data found", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Database error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(requireContext(), "User ID not found", Toast.LENGTH_SHORT).show()
        }

//        db.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                if (snapshot.exists()) {
//                    for (dataSnapshot in snapshot.children) {
//                        val customer = dataSnapshot.getValue(Customer::class.java)
//                        if (customer != null) {
//                            binding.txNama1.setText(customer.name ?: "N/A")
//                            binding.txNama.setText(customer.name ?: "N/A")
//                            binding.txNomor.setText(customer.phone ?: "N/A")
//                            binding.txGmail.setText(customer.email ?: "N/A")
//                            binding.txAlamat1.setText(customer.alamat ?: "N/A")
//
//                            // Set gambar jika ada URL
//                            customer.imageUrl?.let {
//                                // Memuat gambar menggunakan Glide
//                                Glide.with(this@EdProfile)
//                                    .load(it)
//                                    .into(binding.ImgProfed)
//                            }
//
//                            break // Ambil satu customer saja
//                        } else {
//                            Log.e("EdProfile", "Customer data is null")
//                        }
//                    }
//                } else {
//                    Toast.makeText(requireContext(), "No customer data found", Toast.LENGTH_SHORT).show()
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Toast.makeText(requireContext(), "Database error: ${error.message}", Toast.LENGTH_SHORT).show()
//            }
//        })
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btnUpdate -> {
                // Hanya berpindah ke ProfileActivity tanpa melakukan update
                val intent = Intent(requireContext(), Profile::class.java)
                startActivity(intent)
            }
            R.id.ImgProfed -> {
                // Meminta izin untuk membaca penyimpanan
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        PICK_IMAGE_REQUEST
                    )
                } else {
                    openGallery()
                }
            }
            R.id.btnKeluar -> {
                // Hanya berpindah ke ProfileActivity tanpa melakukan update
                val intent = Intent(requireContext(), LoginActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.data
            imageUri?.let {
                Log.d("EdProfile", "Image URI: $imageUri")
                // Menggunakan Glide untuk menampilkan gambar di ImageView
                try {
                    Glide.with(this)
                        .load(it)
                        .into(binding.ImgProfed)
                } catch (e: Exception) {
                    Log.e("EdProfile", "Error loading image: ${e.message}")
                    Toast.makeText(requireContext(), "Failed to load image", Toast.LENGTH_SHORT).show()
                }
            } ?: Log.e("EdProfile", "Image URI is null")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PICK_IMAGE_REQUEST && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openGallery()
        } else {
            Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }
}
