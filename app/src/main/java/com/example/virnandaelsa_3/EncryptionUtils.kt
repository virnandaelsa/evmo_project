package com.example.virnandaelsa_3

import android.util.Base64
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec
import javax.crypto.spec.IvParameterSpec

object EncryptionUtils {
    private const val SECRET_KEY = "1234567812345678"

    // Function to encrypt data
    fun encrypt(data: String): String {
        val key = SecretKeySpec(SECRET_KEY.toByteArray(), "AES")

        val iv = ByteArray(16)
        SecureRandom().nextBytes(iv)
        val ivParameterSpec = IvParameterSpec(iv)

        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, key, ivParameterSpec)

        // Encrypt the data
        val encryptedData = cipher.doFinal(data.toByteArray())

        val ivAndEncryptedData = iv + encryptedData

        return Base64.encodeToString(ivAndEncryptedData, Base64.DEFAULT)
    }

    // Function to decrypt data
    fun decrypt(data: String): String {
        val key = SecretKeySpec(SECRET_KEY.toByteArray(), "AES")

        // Decode the Base64-encoded data
        val ivAndEncryptedData = Base64.decode(data, Base64.DEFAULT)

        val iv = ivAndEncryptedData.copyOfRange(0, 16)
        val encryptedData = ivAndEncryptedData.copyOfRange(16, ivAndEncryptedData.size)

        val ivParameterSpec = IvParameterSpec(iv)

        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, key, ivParameterSpec)

        // Decrypt the data
        val decryptedData = cipher.doFinal(encryptedData)

        return String(decryptedData)
    }
}
