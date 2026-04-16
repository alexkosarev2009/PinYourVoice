package com.example.shareyourvoicemapbox.data.source.auth

import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

object AuthLocalDataSource {
    val token: String?
        get() = _cache_token

    var _cache_token: String? = null

    @OptIn(ExperimentalEncodingApi::class)
    fun setToken(login: String, password: String) {
        val decodeAuth = "$login:$password"
        _cache_token = "Basic ${Base64.encode(decodeAuth.toByteArray())}"
    }

    fun clearToken() {
        _cache_token = null
    }
}