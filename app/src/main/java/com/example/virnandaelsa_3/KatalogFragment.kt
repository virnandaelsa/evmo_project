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

        binding.btnyt.setOnClickListener {
            openWebView("https://www.youtube.com/")
            hideImage()
        }
        binding.btnTt.setOnClickListener {
            openWebView("https://r.search.yahoo.com/_ylt=Awrx.WNvgidn.QEAICfLQwx.;_ylu=Y29sbwNzZzMEcG9zAzEEdnRpZAMEc2VjA3Ny/RV=2/RE=1731852144/RO=10/RU=https%3a%2f%2fwww.tiktok.com%2f/RK=2/RS=rG5OllSLVqZchI4gXtTxM9Enk5Y-")
            hideImage()
        }
    }

    private fun hideImage() {
        binding.btnyt.visibility = View.GONE // Atau View.INVISIBLE tergantung kebutuhan
        binding.btnTt.visibility = View.GONE // Atau View.INVISIBLE tergantung kebutuhan
    }

    override fun onResume() {
        super.onResume()
        // Tampilkan kembali tombol ketika fragment aktif kembali
        binding.btnyt.visibility = View.VISIBLE
        binding.btnTt.visibility = View.VISIBLE
    }


    private fun openWebView(url: String) {
        val webViewFragment = FragmentWebView()
        val bundle = Bundle()
        bundle.putString("url", url)  // Kirim URL ke FragmentWebView
        webViewFragment.arguments = bundle

        // Ganti fragment saat gambar ditekan
        parentFragmentManager.beginTransaction()
            .replace(R.id.frag_webview, webViewFragment)  // Pastikan ID ini sesuai dengan layout Anda
            .addToBackStack(null)
            .commit()
    }

    fun fetchKatalogData() {
        val token = "Bearer 10|hNgLQRQ3z1oun5ZF12XfHnLdzGD5cIXMop96kgIq5e083157"
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