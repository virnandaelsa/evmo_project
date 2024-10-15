package com.example.virnandaelsa_3

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.virnandaelsa_3.Models.KatalogResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.virnandaelsa_3.databinding.ActivityKatalogFragmentBinding

class KatalogFragment : Fragment() {
    private lateinit var binding: ActivityKatalogFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = ActivityKatalogFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fetchKatalogData()
    }

    fun fetchKatalogData() {
        val token = "Bearer 1|8iPRAhf8mYWnBUqZAczomAOtPvNMT0tq6hggLocF781856f6"
        val call = ApiClient.apiService.getKatalog(token)
        call.enqueue(object : Callback<KatalogResponse> {
            override fun onResponse(call: Call<KatalogResponse>, response: Response<KatalogResponse>) {
                if (response.isSuccessful) {
                    val katalogData = response.body()?.data
                    katalogData?.let {
                        val penjualList = it.penjual ?: emptyList()
                        val user = it.user
                        val adapter = KatalogAdapter(requireContext(), penjualList, user)

                        binding.listViewJasa.adapter = adapter
                    }
                }
            }

            override fun onFailure(call: Call<KatalogResponse>, t: Throwable) {

            }
        })
    }
}