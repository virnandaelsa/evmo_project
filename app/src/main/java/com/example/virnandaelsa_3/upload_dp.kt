package com.example.virnandaelsa_3

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.virnandaelsa_3.databinding.FragDpBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
class upload_dp : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: FragDpBinding
    private lateinit var storageRef: StorageReference
    private lateinit var database: DatabaseReference
    private var selectedImageUri: Uri? = null
    private lateinit var progressDialog: ProgressDialog

    // Variabel untuk menerima data dari notifikasi
    private var transactionId: String? = null
    private var productTitle: String? = null
    private var productPrice: String? = null
    private var productOwner: String? = null
    private var productImageUri: String? = null
    private var tanggal: String? = null
    private var keterangan: String? = null
    private var alamat: String? = null

    // Messaging

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private val notificationManager: NotificationManager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Menggunakan View Binding
        binding = FragDpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBKUpDP.setOnClickListener(this)

        // Inisialisasi Firebase Storage dan Realtime Database
        storageRef = FirebaseStorage.getInstance().reference
        database = FirebaseDatabase.getInstance().getReference("Transaksi")

        // Ambil data yang dikirim dari FirebaseMessage jika ada
        transactionId = intent.getStringExtra("transactionId")
        if (transactionId.isNullOrEmpty()) {
            Log.e("UploadDP", "Transaction ID is null or empty.")
            Toast.makeText(this, "Transaction ID tidak valid.", Toast.LENGTH_SHORT).show()
        } else {
            Log.d("UploadDP", "Received valid transactionId: $transactionId")
        }
        productTitle = intent.getStringExtra("PRODUCT_TITLE")
        productPrice = intent.getStringExtra("PRODUCT_PRICE")
        productOwner = intent.getStringExtra("PRODUCT_OWNER")
        productImageUri = intent.getStringExtra("PRODUCT_IMAGE_URI")
        tanggal = intent.getStringExtra("TANGGAL")
        keterangan = intent.getStringExtra("KETERANGAN")
        alamat = intent.getStringExtra("ALAMAT")

        // Tampilkan data di layout
        binding.txProduk2.text = productTitle ?: "Tidak ada judul"
        binding.txhargadp.text = productPrice?.let { "$it" } ?: "Tidak ada harga"
        binding.txToko2.text = productOwner ?: "Tidak ada pemilik"
        binding.txTgl.text = tanggal ?: "Tidak ada tanggal"
        binding.txket.text = keterangan ?: "Tidak ada keterangan"
        binding.txalamat.text = alamat ?: "Tidak ada alamat"

        // Tampilkan gambar produk menggunakan Glide jika ada
        if (productImageUri != null) {
            Glide.with(this)
                .load(productImageUri)
                .into(binding.imgProduk2)
        }

        // Pilih gambar DP dari galeri
        binding.imgbtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        // Tombol untuk mengunggah DP
        binding.btnKirim.setOnClickListener {
            // Cek izin notifikasi
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS,
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // Periksa apakah gambar DP sudah dipilih
                if (selectedImageUri != null) {
                    uploadDpToFirebase(selectedImageUri!!, transactionId) {
                        startNotification()
                    }
                } else {
                    Toast.makeText(this, "Silakan pilih gambar DP terlebih dahulu.", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Jika izin notifikasi belum diberikan, minta izin
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }


        requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                startNotification()
            } else {
                Snackbar.make(
                    findViewById<View>(android.R.id.content).rootView,
                    "Please grant Notification permission from App Settings",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
        createNotificationChannel()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }

        createToken()
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btnBKUpDP -> {
                // Kembali ke DashboardActivity
                finish() // Menutup Profile dan kembali ke Dashboard
            }
        }
    }

    // Menangani hasil dari aktivitas memilih gambar
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            selectedImageUri = data.data

            // Tampilkan gambar yang dipilih ke ImageView
            Glide.with(this)
                .load(selectedImageUri)
                .into(binding.imgDp)
        }
    }

    // Fungsi untuk mengunggah DP ke Firebase Storage
    private fun uploadDpToFirebase(
        imageUri: Uri,
        transactionId: String?,
        onSuccess: () -> Unit // Terima transactionId untuk memperbarui data
    ) {
        // Periksa jika gambar DP kosong
        if (imageUri == null) {
            Toast.makeText(this, "Gambar DP kosong, upload dibatalkan.", Toast.LENGTH_SHORT).show()
            return // Membatalkan upload dan tidak lanjutkan ke proses berikutnya
        }
        // Tampilkan progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Mengunggah DP...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        // Buat reference untuk gambar DP di Firebase Storage
        val dpRef = storageRef.child("dps/${UUID.randomUUID()}.jpg")

        // Unggah gambar
        dpRef.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                    // Gambar berhasil diunggah, dapatkan URL-nya
                    val dpUrl = uri.toString()
                    Log.d("UploadDP", "DP uploaded successfully. URL: $dpUrl")

                    // Simpan data DP ke transaksi yang sudah ada
                    val dpUpdate = mapOf(
                        "dpUri" to dpUrl // Menambahkan URL DP ke transaksi
                    )

                    // Perbarui transaksi yang sudah ada
                    if (transactionId != null) {
                        Log.d("UploadDP", "Transaction ID is valid: $transactionId")
                        database.child(transactionId).updateChildren(dpUpdate)
                            .addOnSuccessListener {
                                Toast.makeText(this, "DP berhasil diunggah!", Toast.LENGTH_SHORT).show()
                                progressDialog.dismiss()

                                onSuccess()

                                // Navigasi ke aktivitas lain jika perlu
                                startActivity(Intent(this, DashboardActivity::class.java))
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Log.e("UploadDP", "Failed to update transaction: ${e.message}")
                                Toast.makeText(this, "Gagal memperbarui transaksi: ${e.message}", Toast.LENGTH_SHORT).show()
                                progressDialog.dismiss()
                            }
                    } else {
                        Log.e("UploadDP", "Transaction ID is null or invalid.")
                        Toast.makeText(this, "Transaction ID tidak valid.", Toast.LENGTH_SHORT).show()
                        progressDialog.dismiss()
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("UploadDP", "Failed to upload DP: ${e.message}")
                Toast.makeText(this, "Gagal mengunggah DP: ${e.message}", Toast.LENGTH_SHORT).show()
                progressDialog.dismiss()
            }
    }

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
        const val CHANNEL_ID = "dummy_channel"
    }

    private fun createToken() {
        val TAG = "FCM__TOKEN"
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful){
                Log.e(TAG, "FCM token failed", task.exception)
                return@OnCompleteListener
            }
            val token = task.result
            Log.d(TAG, token)
        })
    }

    private fun startNotification() {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.wisma)
            .setContentTitle("Pesanan Telah Berhasil \uD83C\uDF89")
            .setContentText("Terima kasih telah melakukan pemesanan. \nSemoga harimu menyenangkan!!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, builder.build())
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Important Notification Channel",
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            description = "This notification contains important announcement, etc."
        }
        notificationManager.createNotificationChannel(channel)
    }
}