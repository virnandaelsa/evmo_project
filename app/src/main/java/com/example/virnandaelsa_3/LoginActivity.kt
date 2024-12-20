package com.example.virnandaelsa_3

import android.content.Context
import android.content.Intent
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
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
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

        binding.btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
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

                        val token = loginResponse.token

                        Log.d("LoginActivity", "Token: $token")

                        val sharedPreferences = getSharedPreferences("token", Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putString("auth_token", token)
                        editor.apply()
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
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("users")

        // Mencari pengguna berdasarkan username
        myRef.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Mengambil data pengguna
                    for (snapshot in dataSnapshot.children) {
                        val email = snapshot.child("email").getValue(String::class.java)
                        if (email != null) {
                            loginUserWithEmailAndPassword(email, password)
                        }
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "Username tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@LoginActivity, "Gagal mencari username: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun loginUserWithEmailAndPassword(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    val sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                    with(sharedPref.edit()) {
                        putString("user_id", userId)
                        apply()
                    }
                    Toast.makeText(this, "Login berhasil!", Toast.LENGTH_SHORT).show()
                    // Redirect ke halaman utama
                    val intent = Intent(this, DashboardActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Login gagal: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

}