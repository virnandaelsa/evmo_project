package com.example.virnandaelsa_3

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.virnandaelsa_3.EncryptionUtils.encrypt
import com.example.virnandaelsa_3.Models.RegisterRequest
import com.example.virnandaelsa_3.Models.RegisterResponse
import com.example.virnandaelsa_3.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.database.FirebaseDatabase
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        binding.btnSignUp.setOnClickListener {
            val nama = binding.edtNama.text.toString().trim()
            val email = binding.edtEmail.text.toString().trim()
            val alamat = binding.edtAlamat.text.toString().trim()
            val no_telp = binding.edtNoTelp.text.toString().trim()
            val username = binding.edtUsername.text.toString().trim()
            val password = binding.edtPassword.text.toString().trim()

            if (nama.isEmpty()) {
                binding.edtNama.error = "Nama harus diisi"
                return@setOnClickListener
            }

            if (email.isEmpty()) {
                binding.edtEmail.error = "Email harus diisi"
                return@setOnClickListener
            }

            if (alamat.isEmpty()) {
                binding.edtAlamat.error = "Alamat harus diisi"
                return@setOnClickListener
            }

            if (no_telp.isEmpty()) {
                binding.edtNoTelp.error = "No. Telepon harus diisi"
                return@setOnClickListener
            }

            if (username.isEmpty()) {
                binding.edtUsername.error = "Username harus diisi"
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                binding.edtPassword.error = "Password harus diisi"
                return@setOnClickListener
            }

            // Register to MySQL without encrypting email
            registerUserMySQL(nama, email, alamat, no_telp, username, password)

            // Register to Firebase with encrypted no_telp and plain email
            registerUserFirebase(nama, email, alamat, no_telp, username, password)
        }

        // Tombol Cancel untuk kembali ke LoginActivity
        binding.btnCancel.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Menutup Activity saat ini agar tidak dapat kembali dengan tombol Back
        }
    }

    fun registerUserMySQL(nama: String, email: String, alamat: String, no_telp: String, username: String, password: String) {
        // Use plain email for MySQL registration
        val request = RegisterRequest(nama, email, alamat, no_telp, username, password)

        ApiClient.apiService.register(request).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                if (response.isSuccessful) {
                    val registerResponse = response.body()
                    if (registerResponse != null && registerResponse.status) {
                        Log.d("RegisterActivity", "Registrasi berhasil")
                    } else {
                        Log.d("RegisterActivity", "Registrasi gagal")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    errorBody?.let {
                        try {
                            val jsonObject = JSONObject(it)
                            val data = jsonObject.getJSONObject("data")

                            if (data.has("email")) {
                                binding.edtEmail.error = data.getJSONArray("email").getString(0)
                            }

                            if (data.has("no_telp")) {
                                binding.edtNoTelp.error = data.getJSONArray("no_telp").getString(0)
                            }

                            if (data.has("password")) {
                                binding.edtPassword.error = data.getJSONArray("password").getString(0)
                            }
                        } catch (e: Exception) {
                            Toast.makeText(this@RegisterActivity, "Gagal parsing error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                Toast.makeText(this@RegisterActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun registerUserFirebase(nama: String, email: String, alamat: String, no_telp: String, username: String, password: String) {

        val encryptedNoTelp = encrypt(no_telp)

        // Log the encrypted phone number for debugging
        Log.d("EncryptedNoTelp", "Encrypted no_telp: $encryptedNoTelp")

        // Create user with Firebase Authentication
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val user_id = auth.currentUser?.uid
                user_id?.let {
                    // Verify if user_id is not null
                    Log.d("UserID", "User ID: $it")

                    // Creating the user object
                    val user = hashMapOf(
                        "nama" to nama,
                        "email" to email,
                        "alamat" to alamat,
                        "no_telp" to encryptedNoTelp,
                        "username" to username
                    )

                    // Check the contents of the user object
                    Log.d("UserData", "User Data: $user")

                    // Save data to Firebase Realtime Database
                    val database = FirebaseDatabase.getInstance()
                    val myRef = database.getReference("users").child(it)

                    myRef.setValue(user).addOnSuccessListener {
                        Toast.makeText(this@RegisterActivity, "Registrasi Berhasil", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }.addOnFailureListener { exception ->
                        Log.e("FirebaseError", "Error saving user data: ${exception.message}")
                        Toast.makeText(this, "Registrasi Gagal", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Gagal membuat akun: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                Log.e("RegisterActivity", "Gagal membuat akun: ${task.exception?.message}")
            }
        }
    }
}