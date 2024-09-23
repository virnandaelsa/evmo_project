package com.example.virnandaelsa_3

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ListAdapter
import android.widget.SimpleAdapter
import android.widget.Toast
import com.example.virnandaelsa_3.databinding.ActivityEvmoBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import android.util.Log


class Evmo : AppCompatActivity(), View.OnClickListener {

    lateinit var db: DatabaseReference
    lateinit var adapter: ListAdapter
    var alproduk = ArrayList<HashMap<String, Any>>()
    var evmo = Produk()
    var hm = HashMap<String, Any>()
    lateinit var b: ActivityEvmoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityEvmoBinding.inflate(layoutInflater)
        setContentView(b.root)

        // Initialize the Firebase Database reference here
        db = FirebaseDatabase.getInstance().getReference("EVMO")

        b.btntambah.setOnClickListener(this)
        b.btnhapus.setOnClickListener(this)
        b.btnedit.setOnClickListener(this)
        b.lvproduk.setOnItemClickListener(itemClickListener)

        showData() // Call showData here if you want to display the data immediately
    }

    override fun onStart() {
        super.onStart()
        db = FirebaseDatabase.getInstance().getReference("EVMO")
        showData()  // Panggil fungsi untuk menampilkan data saat activity dimulai
    }

    fun showData() {
        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val dataSnapshotIterable = snapshot.children
                val iterator = dataSnapshotIterable.iterator()
                alproduk.clear()
                while (iterator.hasNext()) {
                    evmo = iterator.next().getValue<Produk>()!!
                    hm = HashMap()
                    hm["id"] = evmo.id!!
                    hm["nama"] = evmo.nama!!
                    hm["deskripsi"] = evmo.deskripsi!!
                    hm["harga"] = evmo.harga!!  // Simpan harga sebagai String
                    alproduk.add(hm)
                }

                adapter = SimpleAdapter(
                    this@Evmo,
                    alproduk,
                    R.layout.list_produk,
                    arrayOf("id", "nama", "deskripsi", "harga"),
                    intArrayOf(R.id.id, R.id.nmproduk, R.id.deskripsi, R.id.harga)
                )
                b.lvproduk.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@Evmo,
                    "Connection to database error : ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    val itemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
        hm = alproduk[i]
        b.edid.setText(hm["id"].toString())
        b.edproduk.setText(hm["nama"].toString())
        b.eddeskripsi.setText(hm["deskripsi"].toString())
        b.edharga.setText(hm["harga"].toString())
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.btntambah -> {
                evmo.id = b.edid.text.toString()
                evmo.nama = b.edproduk.text.toString()
                evmo.deskripsi = b.eddeskripsi.text.toString()

                // Konversi harga ke Int, gunakan toIntOrNull untuk menghindari crash jika input bukan angka
                val harga = b.edharga.text.toString().toIntOrNull()
                if (harga != null) {
                    evmo.harga = harga  // Pastikan harga disimpan sebagai Int

                    db.child(evmo.id!!).setValue(evmo).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Produk berhasil ditambahkan", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            Toast.makeText(this, "Gagal menambahkan produk", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Harga harus berupa angka", Toast.LENGTH_SHORT).show()
                }
            }

            R.id.btnhapus -> {
                val id = b.edid.text.toString()
                db.child(id).removeValue().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Produk berhasil dihapus", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Gagal menghapus produk", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            R.id.btnedit -> {
                evmo.id = b.edid.text.toString()
                evmo.nama = b.edproduk.text.toString()
                evmo.deskripsi = b.eddeskripsi.text.toString()

                // Konversi harga ke Int saat edit
                val harga = b.edharga.text.toString().toIntOrNull()
                if (harga != null) {
                    evmo.harga = harga  // Pastikan harga disimpan sebagai Int

                    db.child(evmo.id!!).setValue(evmo).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Produk berhasil diubah", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            Toast.makeText(this, "Gagal mengubah produk", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Harga harus berupa angka", Toast.LENGTH_SHORT).show()
                }
            }
        }
        // Reset form
        b.edid.setText("")
        b.edproduk.setText("")
        b.eddeskripsi.setText("")
        b.edharga.setText("")
    }

    private fun fetchProdukData() {
        db.child("EVMO").child("Produk")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val produk = snapshot.getValue(Produk::class.java)
                        if (produk != null) {
                            Log.d("Firebase", "Product: ${produk.nama}, Price: ${produk.harga}")
                        } else {
                            Log.d("Firebase", "Produk is missing or empty")
                        }
                    } else {
                        Log.d("Firebase", "No Produk data found")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Firebase", "Error: ${error.message}")
                }
            })
    }
}