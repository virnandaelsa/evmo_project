package com.example.virnandaelsa_3

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebViewClient
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
        val url = arguments?.getString("url") ?: GlobalVariables.url
        binding.webV.loadUrl(url)
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
        wbSet.setGeolocationEnabled(true)
        wbSet.setAllowFileAccess(true)
        wbSet.setAllowContentAccess(true)
        wbSet.setLoadsImagesAutomatically(true)
        binding.webV.webViewClient = WebViewClient()
        binding.webV.webChromeClient = WebChromeClient()
    }
}
