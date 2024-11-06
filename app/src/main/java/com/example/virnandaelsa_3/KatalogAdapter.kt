package com.example.virnandaelsa_3

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.bumptech.glide.Glide
import com.example.virnandaelsa_3.Models.DetailKatalog
import com.example.virnandaelsa_3.Models.Penjual
import com.example.virnandaelsa_3.databinding.ItemJasaBinding

class KatalogAdapter(val context: Context, val penjualList: List<DetailKatalog>, val user: String) : BaseAdapter() {

    override fun getCount(): Int {
        return penjualList.size
    }

    override fun getItem(p0: Int): Any {
        return penjualList[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val binding: ItemJasaBinding
        val view: View

        if (p1 == null) {
            binding = ItemJasaBinding.inflate(LayoutInflater.from(context), p2, false)
            view = binding.root
            view.tag = binding
        } else {
            binding = p1.tag as ItemJasaBinding
            view = p1
        }

        val penjual = getItem(p0) as DetailKatalog
        val detailKatalogItem = penjual.detail_katalog.firstOrNull()
        val baseUrl = "http://10.0.2.2:8000/images/gambar_detail_katalog/"
        val imageUrl = detailKatalogItem?.gambar?.let { baseUrl + it }

        binding.txJudul.text = penjual.judul
        binding.txHarga.text = "Rp ${detailKatalogItem?.harga}"
        binding.txNamapj.text = "$user"
        Glide.with(context)
            .load(imageUrl)
            .into(binding.imageJasa)

        binding.buttonPesan.setOnClickListener {
            val intent = Intent(context, tambah_transaksi::class.java).apply {
                putExtra("SERVICE_ID", penjual.id_katalog.toString())
                putExtra("PRODUCT_TITLE", penjual.judul)
                putExtra("PRODUCT_PRICE", "Rp ${detailKatalogItem?.harga.toString()}")
                putExtra("PRODUCT_OWNER", "$user")
                putExtra("PRODUCT_IMAGE_URI", "$imageUrl")
            }
            context.startActivity(intent)
        }
        return view
    }

}