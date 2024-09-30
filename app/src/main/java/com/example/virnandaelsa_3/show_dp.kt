package com.example.virnandaelsa_3


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.virnandaelsa_3.databinding.ActivityShowDpBinding
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale

class show_dp : AppCompatActivity() {
    private lateinit var binding: ActivityShowDpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inisialisasi binding
        binding = ActivityShowDpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Mendapatkan referensi ke database Firebase
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("EVMO").document("123")

        // Mengambil data dari database
        docRef.get().addOnSuccessListener { documentSnapshot: DocumentSnapshot ->
            if (documentSnapshot.exists()) {
                val alamat = documentSnapshot.getString("alamat")
                val harga = documentSnapshot.getString("harga")!!
                val pembayaran = documentSnapshot.getString("pembayaran")
                val fotoproduk = documentSnapshot.getString("foto")
                val fotodp = documentSnapshot.getString("bukti_dp")
                val judul = documentSnapshot.getString("judul")
                val keterangan = documentSnapshot.getString("keterangan")
                val pj = documentSnapshot.getString("toko")

                // Ambil 'tanggal' sebagai Timestamp
                val timestamp = documentSnapshot.getTimestamp("tanggal")

                // Format timestamp ke string (misalnya: "dd-MM-yyyy HH:mm")
                val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
                val tanggal = timestamp?.let { sdf.format(it.toDate()) }

                // Mengisi data ke dalam TextView dan ImageView menggunakan binding
                binding.txJudul.text = judul
                binding.txHarga.text = "Rp $harga"
                binding.txToko.text = pj
                binding.txTanggal.text = tanggal // Menggunakan tanggal yang sudah diformat
                binding.txKet.text = keterangan
                binding.txAlamat.text = alamat
                binding.txRekening.text = pembayaran

                // Menggunakan Glide untuk menampilkan gambar dari URL
                Glide.with(this).load(fotoproduk).into(binding.imageView2)
                Glide.with(this).load(fotodp).into(binding.imVDP)
            }
        }.addOnFailureListener { e: Exception? ->
            // Handle error
        }
    }
}