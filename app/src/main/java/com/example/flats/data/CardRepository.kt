package com.example.flats.data

import android.content.Context
import android.net.Uri
import com.example.flats.data.model.Card
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import java.util.UUID

object CardRepository {

    private val client = SupabaseClient.client

    suspend fun insertCard(card: Card): Card {
        return client.postgrest
            .from("card")
            .insert(card) {
                select()
            }
            .decodeSingle<Card>()
    }

    suspend fun uploadImage(context: Context, uri: Uri): String {
        val userId = client.auth.currentUserOrNull()?.id
            ?: throw Exception("Пользователь не авторизован")

        val bytes = context.contentResolver.openInputStream(uri)?.readBytes()
            ?: throw Exception("Не удалось прочитать файл")

        val fileName = "$userId/${UUID.randomUUID()}.jpg"

        client.storage.from("card-images").upload(fileName, bytes)

        return client.storage.from("card-images").publicUrl(fileName)
    }

    suspend fun getCards(): List<Card> {
        val userId = client.auth.currentUserOrNull()?.id
            ?: throw Exception("Пользователь не авторизован")

        return client.postgrest
            .from("card")
            .select {
                filter { eq("user_id", userId) }
            }
            .decodeList<Card>()
    }

    fun currentUserId(): String {
        return client.auth.currentUserOrNull()?.id
            ?: throw Exception("Пользователь не авторизован")
    }
}