package com.example.virnandaelsa_3

import android.os.Bundle
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
        val token = "Bearer 29|3D6CmJZCvpMgxXq4k7cYVZOIbybqDn8DaKAIFOKb637f690b"
        val call = ApiClient.apiService.getKatalog(token)
        call.enqueue(object : Callback<KatalogResponse> {
            override fun onResponse(call: Call<KatalogResponse>, response: Response<KatalogResponse>) {
                if (response.isSuccessful) {
                    val katalogData = response.body()?.data
                    katalogData?.let {
                        val penjualList = it.penjual ?: emptyList()
                        val adapter = KatalogAdapter(this@KatalogActivity, penjualList)

                        binding.listViewJasa.adapter = adapter
                    }
                }
            }

            override fun onFailure(call: Call<KatalogResponse>, t: Throwable) {
                // Handle failure
            }
        })
    }
}