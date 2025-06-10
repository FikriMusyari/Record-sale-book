package com.afi.record.presentation.viewmodel

import android.content.Context
import android.util.Base64
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(@ApplicationContext context: Context) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secure_auth_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    companion object {
        private const val JWT_TOKEN_KEY = "jwt_token"
    }

    fun saveToken(token: String) {
        sharedPreferences.edit {
            putString(JWT_TOKEN_KEY, token)
        }
    }

    fun getToken(): String? {
        return sharedPreferences.getString(JWT_TOKEN_KEY, null)
    }

    fun getUserId(): Int? {
        val token = getToken() ?: return null
        val parts = token.split(".")
        if (parts.size != 3) return null

        return try {
            val payload = parts[1]
            val decodedBytes = Base64.decode(payload, Base64.URL_SAFE or Base64.NO_PADDING
                    or Base64.NO_WRAP)
            val payloadString = String(decodedBytes, Charsets.UTF_8)
            val jsonObject = JSONObject(payloadString)


            if (jsonObject.has("id")) {
                val userIdObj = jsonObject.get("id")
                when (userIdObj) {
                    is Int -> userIdObj
                    is String -> userIdObj.toIntOrNull()
                    else -> null
                }
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }
}