package com.jayr.firecrud.data.repository.auth

interface AuthService {
    suspend fun register(email: String, password: String)
    suspend fun login(email: String, password: String):String
}