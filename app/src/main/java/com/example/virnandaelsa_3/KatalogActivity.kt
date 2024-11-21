package com.example.virnandaelsa_3

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.virnandaelsa_3.Models.KatalogResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.virnandaelsa_3.databinding.ActivityKatalog2Binding

class KatalogActivity : AppCompatActivity() {
    private lateinit var binding: ActivityKatalog2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKatalog2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        fetchKatalogData()
    }

    fun fetchKatalogData() {
        val sharedPreferences = getSharedPreferences("token", MODE_PRIVATE)
        val token = sharedPreferences.getString("auth_token", null)
        Log.d("Katalog Activity", "Token: $token")
        if (token != null) {
            val authToken = "Bearer $token"
            val call = ApiClient.apiService.getKatalog(authToken)
            call.enqueue(object : Callback<KatalogResponse> {
                override fun onResponse(call: Call<KatalogResponse>, response: Response<KatalogResponse>) {
                    if (response.isSuccessful) {
                        val katalogData = response.body()?.data
                        katalogData?.let {
                            val penjualList = it.detail_katalog ?: emptyList()
                            val user = it.user

                            //val adapter = KatalogAdapter(this@KatalogActivity, penjualList, user)
                            //binding.listViewJasa.adapter = adapter
                        }
                    }
                }

                override fun onFailure(call: Call<KatalogResponse>, t: Throwable) {

                }
            })
        }
    }
}