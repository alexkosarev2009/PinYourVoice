package com.example.shareyourvoicemapbox.data.source.auth.bearer

import android.content.SharedPreferences
import android.util.Base64
import androidx.core.content.edit
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenStorage @Inject constructor(
    private val prefs: SharedPreferences
) {
    fun save(token: String) {
        prefs.edit { putString("jwt", token) }
    }
    fun get(): String? {
        return prefs.getString("jwt", null)
    }
    fun clear() {
        prefs.edit { remove("jwt") }
    }
    fun isTokenExpired(): Boolean {
        val token = get() ?: return true

        return try {
            val parts = token.split(".")
            val payload = parts[1]

            val decoded = Base64.decode(payload, Base64.URL_SAFE)
            val json = JSONObject(String(decoded))

            val exp = json.getLong("exp")
            val now = System.currentTimeMillis() / 1000

            now >= exp
        } catch (e: Exception) {
            true
        }
    }
}