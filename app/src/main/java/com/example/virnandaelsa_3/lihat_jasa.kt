package com.example.virnandaelsa_3

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.virnandaelsa_3.databinding.FragJasaBinding
import com.google.firebase.database.*

class lihat_jasa : AppCompatActivity() {

    private lateinit var binding: FragJasaBinding
    private lateinit var listView: ListView
    private lateinit var jasaList: MutableList<Jasa>
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragJasaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listView = binding.listViewJasa
        jasaList = mutableListOf()

        database = FirebaseDatabase.getInstance().getReference("EVMO")

        // Mengambil data dari Firebase Realtime Database
        fetchJasaData()
    }

    private fun fetchJasaData() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                jasaList.clear()
                for (dataSnapshot in snapshot.children) {
                    val id = dataSnapshot.child("id").getValue(String::class.java) ?: ""
                    val judul = dataSnapshot.child("judul").getValue(String::class.java) ?: ""
                    val harga = dataSnapshot.child("harga").getValue(Long::class.java) ?: 0L
                    val toko = dataSnapshot.child("toko").getValue(String::class.java) ?: ""
                    val imageUrl = dataSnapshot.child("imageUrl").getValue(String::class.java) ?: ""

                    val jasa = Jasa(id, judul, harga, toko, imageUrl)
                    jasaList.add(jasa)
                }
                listView.adapter = JasaAdapter(this@lihat_jasa, jasaList)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@lihat_jasa, "Gagal mengambil data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Adapter untuk menampilkan daftar jasa
    inner class JasaAdapter(
        private val context: Context,
        private val jasaList: List<Jasa>
    ) : ArrayAdapter<Jasa>(context, R.layout.item_jasa, jasaList) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_jasa, parent, false)
            val jasa = jasaList[position]

            // Bind data ke tampilan item_jasa
            val titleTextView = view.findViewById<TextView>(R.id.tx_judul)
            val priceTextView = view.findViewById<TextView>(R.id.tx_harga)
            val ownerTextView = view.findViewById<TextView>(R.id.tx_namapj)
            val imageView = view.findViewById<ImageView>(R.id.imageJasa)
            val buttonPesan = view.findViewById<Button>(R.id.buttonPesan)

            titleTextView.text = jasa.judul
            priceTextView.text = "Rp ${jasa.harga}" // Menampilkan harga dalam format rupiah
            ownerTextView.text = jasa.toko

            // Memuat gambar dengan Glide
            Glide.with(context)
                .load(jasa.imageUrl)
                .error(R.drawable.wisma) // Gambar placeholder jika gagal memuat
                .into(imageView)

            // Set click listener untuk tombol pesan
            buttonPesan.setOnClickListener {
                val intent = Intent(context, tambah_transaksi::class.java).apply {
                    putExtra("SERVICE_ID", jasa.id)
                    putExtra("PRODUCT_TITLE", jasa.judul)
                    putExtra("PRODUCT_PRICE", jasa.harga.toString())
                    putExtra("PRODUCT_OWNER", jasa.toko)
                    putExtra("PRODUCT_IMAGE_URI", jasa.imageUrl)
                }
                context.startActivity(intent)
            }

            // Pastikan tidak ada klik listener untuk imageView
            imageView.setOnClickListener(null)

            return view
        }
    }
}
