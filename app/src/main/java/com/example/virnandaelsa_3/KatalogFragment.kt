package com.example.virnandaelsa_3

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.virnandaelsa_3.Models.KatalogResponse
import com.example.virnandaelsa_3.databinding.ActivityKatalogFragmentBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class KatalogFragment : Fragment() {
    private lateinit var binding: ActivityKatalogFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = ActivityKatalogFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fetchKatalogData()
    }

    private fun fetchKatalogData() {
        // Ambil token dari SharedPreferences
        val sharedPreferences = requireContext().getSharedPreferences("token", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("auth_token", null)

        Log.d("Katalog Fragment", "Token: $token")

        if (token != null) {
            val authToken = "Bearer $token"
            val call = ApiClient.apiService.getKatalog(authToken)

            //Log.d("Katalog Fragment", "Call: $call")

            call.enqueue(object : Callback<KatalogResponse> {
                override fun onResponse(call: Call<KatalogResponse>, response: Response<KatalogResponse>) {
                    if (response.isSuccessful) {
                        val katalogData = response.body()?.data
                        katalogData?.let {
                            val penjualList = it.penjual ?: emptyList()
                            val user = it.user
                            val adapter = KatalogAdapter(requireContext(), penjualList, user)

                            Log.d("Katalog Fragment", "Penjual List: $penjualList")

                            // Set adapter untuk listView di Fragment
                            binding.listViewJasa.adapter = adapter
                        }
                    } else {
                        Toast.makeText(requireContext(), "Gagal memuat katalog", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<KatalogResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(requireContext(), "Token tidak ditemukan", Toast.LENGTH_SHORT).show()
        }
    }
}
