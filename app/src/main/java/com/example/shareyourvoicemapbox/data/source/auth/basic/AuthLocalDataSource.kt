package com.example.shareyourvoicemapbox.data.source.auth.basic

import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

object AuthLocalDataSource {
    val token: String?
        get() = _cache_token

    var _cache_token: String? = null

    fun setToken(token: String) {
        _cache_token = token
    }

    fun clearToken() {
        _cache_token = null
    }
}