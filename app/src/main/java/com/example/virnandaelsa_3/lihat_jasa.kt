package com.example.virnandaelsa_3

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.virnandaelsa_3.databinding.FragJasaBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class lihat_jasa : AppCompatActivity() {
    private lateinit var binding: FragJasaBinding
    private lateinit var database: DatabaseReference
    private lateinit var jasaList: MutableList<Jasa>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragJasaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi Realtime Database
        database = FirebaseDatabase.getInstance().getReference("EVMO")
        jasaList = mutableListOf()

        // Mengambil data dari Realtime Database
        fetchJasaData()
    }

    private fun fetchJasaData() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                jasaList.clear()
                for (jasaSnapshot in snapshot.children) {
                    val jasa = jasaSnapshot.getValue(Jasa::class.java)
                    jasa?.let {
                        jasaList.add(it)
                    }
                }
                // Menampilkan data di ListView dengan Custom Adapter
                val adapter = JasaAdapter(this@lihat_jasa, jasaList)
                binding.listViewJasa.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@lihat_jasa, "Error fetching data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
    class JasaAdapter(
        context: Context,
        private val jasaList: List<Jasa>
    ) : ArrayAdapter<Jasa>(context, 0, jasaList) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            // Mendapatkan item jasa yang ingin ditampilkan
            val jasa = getItem(position)

            // Memastikan view untuk item jasa
            val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_jasa, parent, false)

            // Menghubungkan data dengan tampilan
            val imageView = view.findViewById<ImageView>(R.id.imageJasa)
            val textViewJudul = view.findViewById<TextView>(R.id.tx_judul)
            val textViewHarga = view.findViewById<TextView>(R.id.tx_harga)
            val textViewToko = view.findViewById<TextView>(R.id.tx_namapj)
            val buttonPesan = view.findViewById<Button>(R.id.buttonPesan)

            // Set data
            textViewJudul.text = jasa?.judul
            // Konversi harga dari Integer ke String
            textViewHarga.text = "Rp ${jasa?.harga?.toString() ?: "0"}" // Jika harga null, tampilkan "0"
            textViewToko.text = jasa?.toko

            // Load gambar jika ada
            val fotoUrl = jasa?.imageUrl
                    if (!fotoUrl.isNullOrEmpty()) {
                        Glide.with(context)
                            .load(fotoUrl) // Memuat gambar menggunakan Glide
                            .into(imageView)
                    } else {
                        imageView.setImageResource(R.drawable.wisma) // Gambar default jika tidak ada URL
                    }

            // Set listener untuk tombol pesan
            buttonPesan.setOnClickListener {
                buttonPesan.setOnClickListener {
                    jasa?.let {
                        val intent = Intent(context, tambah_transaksi::class.java)
                        intent.putExtra("SERVICE_ID", it.id) // ID jasa
                        intent.putExtra("PRODUCT_TITLE", it.judul) // Judul produk
                        intent.putExtra("PRODUCT_PRICE", it.harga.toString())  // Harga produk
                        intent.putExtra("PRODUCT_OWNER", it.toko)
                        intent.putExtra("PRODUCT_IMAGE_URI", it.imageUrl) // URL gambar produk
                        context.startActivity(intent)
                    }
                }

            }

            return view
        }
    }
}

