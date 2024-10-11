package com.example.virnandaelsa_3

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.virnandaelsa_3.Models.LoginRequest
import com.example.virnandaelsa_3.Models.LoginResponse
import com.example.virnandaelsa_3.databinding.ActivityLoginBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        binding.btnLogin.setOnClickListener {
            val username = binding.edUsername.text.toString().trim()
            val password = binding.edPassword.text.toString().trim()

            if (username.isEmpty()) {
                binding.edPassword.error = "Username harus diisi"
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                binding.edPassword.error = "Password harus diisi"
                return@setOnClickListener
            }

            loginUserMySQL(username, password)
            loginUserFirebase(username, password)
        }
    }

    fun loginUserMySQL(username: String, password: String) {
        val loginRequest = LoginRequest(username, password)

        ApiClient.apiService.login(loginRequest).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null && loginResponse.status == true) {
                        Log.d("LoginActivity", "Login berhasil")
                    } else {
                        Log.d("LoginActivity", "Login gagal")
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "Response Error", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun loginUserFirebase(username: String, password: String) {
        db.collection("users")
            .whereEqualTo("username", username)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    for (document in documents) {
                        val email = document.getString("email")
                        if (email != null) {
                            loginUserWithEmailAndPassword(email, password)
                        }
                    }
                } else {
                    Toast.makeText(this, "Username tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal mencari username: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loginUserWithEmailAndPassword(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Login berhasil!", Toast.LENGTH_SHORT).show()
                    // Redirect ke halaman utama
                } else {
                    Toast.makeText(this, "Login gagal: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}