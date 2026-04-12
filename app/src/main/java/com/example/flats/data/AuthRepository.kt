package com.example.flats.data

import com.example.flats.data.model.Criteria
import com.example.flats.data.model.User
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest

object AuthRepository {

    private val client = SupabaseClient.client

    private data class DefaultCriterion(val name: String, val type: String)

    private val defaultCriteria = listOf(
        DefaultCriterion("Нет шума", "checklist"),
        DefaultCriterion("Есть парковка", "checklist"),
        DefaultCriterion("Хорошие соседи", "checklist"),
        DefaultCriterion("Удобное местоположение", "checklist"),
        DefaultCriterion("Есть балкон", "checklist"),
        DefaultCriterion("Есть лифт", "checklist"),
        DefaultCriterion("Интерьер", "score"),
        DefaultCriterion("Инфраструктура", "score"),
        DefaultCriterion("Цена", "score")
    )

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

        val criteria = defaultCriteria.map { def ->
            Criteria(userId = uid, name = def.name, type = def.type)
        }
        client.postgrest.from("criteria").insert(criteria)
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