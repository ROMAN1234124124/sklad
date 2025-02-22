package ru.altrimo.slad2025.network.request

data class LoginRequest(
    private val login: String,
    private val pass: String,
    private val device: String,
)
