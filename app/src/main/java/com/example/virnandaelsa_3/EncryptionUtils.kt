package com.example.virnandaelsa_3

import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import android.util.Base64
import java.security.Key
import javax.crypto.spec.SecretKeySpec

object EncryptionUtils {
    private var secretKey: SecretKey? = null

    // Generate or reuse a Secret Key (securely store the key in production)
    private const val SECRET_KEY = "1234567812345678" // This is a dummy example key, replace with a secure key

    // Function to encrypt the email
    fun encrypt(data: String): String {
        val key = SecretKeySpec(SECRET_KEY.toByteArray(), "AES")
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val encryptedData = cipher.doFinal(data.toByteArray())
        return Base64.encodeToString(encryptedData, Base64.DEFAULT)
    }

    // Function to decrypt the email (for example in the Profile display function)
    fun decrypt(data: String): String {
        val key = SecretKeySpec(SECRET_KEY.toByteArray(), "AES")
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.DECRYPT_MODE, key)
        val decodedData = Base64.decode(data, Base64.DEFAULT)
        val decryptedData = cipher.doFinal(decodedData)
        return String(decryptedData)
    }

}
