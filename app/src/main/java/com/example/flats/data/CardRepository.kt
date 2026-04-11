package com.example.flats.data

import android.content.Context
import android.net.Uri
import com.example.flats.data.model.Card
import com.example.flats.data.model.CardCriteriaScore
import com.example.flats.data.model.Criteria
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

    suspend fun insertCardCriteriaScores(scores: List<CardCriteriaScore>) {
        if (scores.isEmpty()) return
        client.postgrest.from("card_criteria_score").insert(scores)
    }

    suspend fun uploadImage(context: Context, uri: Uri): String {
        val userId = client.auth.currentUserOrNull()?.id
            ?: throw Exception("Пользователь не авторизован")

        val bitmap = if (android.os.Build.VERSION.SDK_INT >= 28) {
            android.graphics.ImageDecoder.decodeBitmap(
                android.graphics.ImageDecoder.createSource(context.contentResolver, uri)
            ) { decoder, _, _ -> decoder.setTargetSampleSize(2) }
        } else {
            android.provider.MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }

        val stream = java.io.ByteArrayOutputStream()
        bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 70, stream)
        val bytes = stream.toByteArray()

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

    suspend fun getCriteria(): List<Criteria> {
        val userId = client.auth.currentUserOrNull()?.id
            ?: throw Exception("Пользователь не авторизован")

        return client.postgrest
            .from("criteria")
            .select {
                filter { eq("user_id", userId) }
            }
            .decodeList<Criteria>()
    }

    fun currentUserId(): String {
        return client.auth.currentUserOrNull()?.id
            ?: throw Exception("Пользователь не авторизован")
    }
}