package com.example.virnandaelsa_3

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.virnandaelsa_3.databinding.FragPesananBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class PesananSaya : Fragment() {

    private lateinit var binding: FragPesananBinding
    private lateinit var listView: ListView
    private lateinit var transaksiList: MutableList<Transaksi>
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Menginflate layout untuk fragment
        binding = FragPesananBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listView = binding.ListViewPesanan
        transaksiList = mutableListOf()

        database = FirebaseDatabase.getInstance().getReference("Transaksi")

        // Mengambil data dari Firebase Realtime Database
        fetchTransaksiData()
    }

    private fun fetchTransaksiData() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val username = currentUser?.displayName ?: currentUser?.email ?: "Unknown"  // Menggunakan email sebagai fallback
        
        // Pastikan username tidak kosong
        if (username.isNotEmpty()) {
            database.orderByChild("username") // Menyaring berdasarkan username
                .equalTo(username) // Mencari transaksi dengan username yang sesuai
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        transaksiList.clear()
                        if (snapshot.exists()) { // Jika ada data transaksi yang ditemukan
                            for (dataSnapshot in snapshot.children) {
                                val transactionId = dataSnapshot.key ?: ""
                                val judul = dataSnapshot.child("judul").getValue(String::class.java) ?: ""
                                val harga = dataSnapshot.child("harga").getValue(Long::class.java) ?: 0L
                                val toko = dataSnapshot.child("toko").getValue(String::class.java) ?: ""
                                val imageUrl = dataSnapshot.child("imageUrl").getValue(String::class.java) ?: ""
                                val keterangan = dataSnapshot.child("keterangan").getValue(String::class.java) ?: ""
                                val tanggal = dataSnapshot.child("tanggal").getValue(String::class.java) ?: ""
                                val alamat = dataSnapshot.child("alamat").getValue(String::class.java) ?: ""
                                val dpImageUrl = dataSnapshot.child("dpUri").getValue(String::class.java) ?: ""

                                val transaksi = Transaksi(transactionId, judul, harga, toko, imageUrl, keterangan, tanggal, alamat, dpImageUrl)
                                transaksiList.add(transaksi)
                            }
                            listView.adapter = TransaksiAdapter(requireContext(), transaksiList)
                        } else {
                            // Jika tidak ada transaksi ditemukan
                            Toast.makeText(requireContext(), "Tidak ada transaksi untuk pengguna ini", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(requireContext(), "Gagal mengambil data: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        } else {
            Toast.makeText(requireContext(), "Pengguna tidak terdaftar", Toast.LENGTH_SHORT).show()
        }
    }

    // Adapter untuk menampilkan daftar transaksi
    inner class TransaksiAdapter(
        private val context: Context,
        private val transaksiList: List<Transaksi>
    ) : ArrayAdapter<Transaksi>(context, R.layout.item_pesanan, transaksiList) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_pesanan, parent, false)
            val transaksi = transaksiList[position]

            // Bind data ke tampilan item_pesanan
            val titleTextView = view.findViewById<TextView>(R.id.txProduk5)
            val priceTextView = view.findViewById<TextView>(R.id.texHarga)
            val imageView = view.findViewById<ImageView>(R.id.imgProduk3)
            val lihatButton = view.findViewById<Button>(R.id.btnLihat) // Tombol "Lihat"

            titleTextView.text = transaksi.judul
            priceTextView.text = "Rp ${transaksi.harga}" // Menampilkan harga dalam format rupiah

            // Memuat gambar dengan Glide
            Glide.with(context)
                .load(transaksi.imageUrl)
                .error(R.drawable.wisma) // Gambar placeholder jika gagal memuat
                .into(imageView)

            // Set listener untuk tombol "Lihat"
            lihatButton.setOnClickListener {
                // Logika untuk melihat detail transaksi
                Toast.makeText(context, "Lihat detail untuk ${transaksi.judul}", Toast.LENGTH_SHORT).show()

                // Membuka aktivitas detail transaksi
                val intent = Intent(context, DetailPesanan::class.java).apply {
                    putExtra("TRANSACTION_ID", transaksi.transactionId)
                    putExtra("JUDUL", transaksi.judul)
                    putExtra("HARGA", transaksi.harga)
                    putExtra("TOKO", transaksi.toko)
                    putExtra("IMAGE_URL", transaksi.imageUrl)
                    putExtra("KETERANGAN", transaksi.keterangan)
                    putExtra("TANGGAL", transaksi.tanggal)
                    putExtra("ALAMAT", transaksi.alamat)
                }
                context.startActivity(intent)
            }

            return view
        }
    }
}
