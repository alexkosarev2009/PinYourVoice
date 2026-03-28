package com.example.shareyourvoicemapbox.data.exceptions

sealed class AppException(message: String) : Exception(message) {
    class UserNotFoundException : AppException("User not found")
    class MarkerNotFoundException : AppException("Marker not found")

}