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
        val token = "Bearer 2|nttRA9hs8GPfR5xpAurA8p3nrbMp8Stza4k0ajfK6b9e3fde"
        val call = ApiClient.apiService.getKatalog(token)
        call.enqueue(object : Callback<KatalogResponse> {
            override fun onResponse(call: Call<KatalogResponse>, response: Response<KatalogResponse>) {
                if (response.isSuccessful) {
                    val katalogData = response.body()?.data
                    katalogData?.let {
                        val penjualList = it.penjual ?: emptyList()
                        val user = it.user
                        val adapter = KatalogAdapter(this@KatalogActivity, penjualList, user)

                        binding.listViewJasa.adapter = adapter
                    }
                }
            }

            override fun onFailure(call: Call<KatalogResponse>, t: Throwable) {

            }
        })
    }
}