package com.example.flats.data

import com.example.flats.data.model.User
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest

object AuthRepository {

    private val client = SupabaseClient.client

    suspend fun signUp(
        email: String,
        password: String,
        username: String
    ) {
        client.auth.signUpWith(Email) {
            this.email = email
            this.password = password
        }

        client.auth.retrieveUserForCurrentSession()

        val uid = client.auth.currentUserOrNull()?.id
            ?: throw Exception("Регистрация не вернула пользователя")

        val newUser = User(
            userId = uid,
            username = username,
            email = email
        )

        client.postgrest.from("user").insert(newUser)
    }

    suspend fun signIn(email: String, password: String) {
        client.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
    }

    suspend fun signOut() {
        client.auth.signOut()
    }

    fun currentUserId(): String? {
        return client.auth.currentUserOrNull()?.id
    }

    fun isLoggedIn(): Boolean {
        return client.auth.currentUserOrNull() != null
    }
}