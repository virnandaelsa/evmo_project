package com.example.virnandaelsa_3

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.FragmentTransaction
import com.example.virnandaelsa_3.databinding.DashboardBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.graphics.Color

class DashboardActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    // Deklarasi binding
    lateinit var binding: DashboardBinding
    lateinit var edProfile: EdProfile
    lateinit var fragPesanan: PesananSaya
    lateinit var fragKatalog: KatalogFragment
    lateinit var fragWebView: FragmentWebView // Deklarasi fragWebView
    lateinit var ft : FragmentTransaction

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inisialisasi binding
        binding = DashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Menggunakan binding untuk mengakses elemen layout dan mengatur listener
        binding.bnv1.setOnNavigationItemSelectedListener(this)

        // Inisialisasi fragment
        edProfile = EdProfile()
        fragPesanan = PesananSaya()
        fragKatalog = KatalogFragment()
        fragWebView = FragmentWebView() // Inisialisasi fragWebView

        if (savedInstanceState == null) {
            ft = supportFragmentManager.beginTransaction()
            ft.replace(R.id.fragmentLayout, fragKatalog).commit()
            binding.fragmentLayout.setBackgroundColor(Color.argb(245, 255, 255, 255)) // Mengatur background
            binding.fragmentLayout.visibility = View.VISIBLE

            // Sinkronkan BottomNavigationView dengan item Home yang aktif
            binding.bnv1.selectedItemId = R.id.itemHome
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Logika saat item di bottom navigation dipilih
        when (item.itemId) {
            R.id.itemLaporan -> {
                ft = supportFragmentManager.beginTransaction()
                ft.replace(R.id.fragmentLayout, fragPesanan).commit()
                binding.fragmentLayout.setBackgroundColor(Color.argb(245, 255, 255, 255))
                binding.fragmentLayout.visibility = View.VISIBLE
            }
            R.id.itemUser -> {
                ft = supportFragmentManager.beginTransaction()
                ft.replace(R.id.fragmentLayout, edProfile).commit()
                binding.fragmentLayout.setBackgroundColor(Color.argb(245, 255, 255, 255))
                binding.fragmentLayout.visibility = View.VISIBLE
            }
            R.id.itemHome -> {
                ft = supportFragmentManager.beginTransaction()
                ft.replace(R.id.fragmentLayout, fragKatalog).commit()
                binding.fragmentLayout.setBackgroundColor(Color.argb(245, 255, 255, 255))
                binding.fragmentLayout.visibility = View.VISIBLE
            }
        }
        return true
    }
}
