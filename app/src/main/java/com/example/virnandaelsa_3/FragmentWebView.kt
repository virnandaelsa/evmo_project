package com.example.virnandaelsa_3

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.virnandaelsa_3.databinding.FragWebviewBinding

class FragmentWebView : Fragment() {
    private lateinit var wbSet: WebSettings
    private lateinit var binding: FragWebviewBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragWebviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        webSettings()

        // Mendapatkan URL dari arguments atau fallback ke GlobalVariables.url
        val url = arguments?.getString("url") ?: GlobalVariables.url
        if (isNetworkAvailable()) {
            binding.webV.loadUrl(url)
        } else {
            Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun webSettings() {
        wbSet = binding.webV.settings
        wbSet.setDomStorageEnabled(true)
        wbSet.setDisplayZoomControls(false)
        wbSet.setUseWideViewPort(true)
        wbSet.setJavaScriptEnabled(true)
        wbSet.setSavePassword(true)
        wbSet.setCacheMode(WebSettings.LOAD_DEFAULT)
        wbSet.setAllowFileAccess(true)
        wbSet.setAllowContentAccess(true)
        wbSet.setLoadsImagesAutomatically(true)

        // Pengaturan terkait audio dan media sudah dihapus
        // wbSet.mediaPlaybackRequiresUserGesture = false // Pemutaran media tanpa gestur (dihapus)

        // WebView debugging diaktifkan untuk pengembangan
        WebView.setWebContentsDebuggingEnabled(true)

        binding.webV.webViewClient = WebViewClient()
        binding.webV.webChromeClient = WebChromeClient()
    }

    // Mengecek apakah perangkat terhubung ke internet
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
}
