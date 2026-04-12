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

        val options = android.graphics.BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        context.contentResolver.openInputStream(uri)?.use {
            android.graphics.BitmapFactory.decodeStream(it, null, options)
        }

        val maxSide = 800
        val sampleSize = maxOf(1, maxOf(options.outWidth, options.outHeight) / maxSide)

        val decodeOptions = android.graphics.BitmapFactory.Options().apply {
            inSampleSize = sampleSize
        }
        var bitmap = context.contentResolver.openInputStream(uri)?.use {
            android.graphics.BitmapFactory.decodeStream(it, null, decodeOptions)
        } ?: throw Exception("Не удалось прочитать файл")

        // применяем EXIF ориентацию
        val rotation = context.contentResolver.openInputStream(uri)?.use { input ->
            val exif = androidx.exifinterface.media.ExifInterface(input)
            when (exif.getAttributeInt(
                androidx.exifinterface.media.ExifInterface.TAG_ORIENTATION,
                androidx.exifinterface.media.ExifInterface.ORIENTATION_NORMAL
            )) {
                androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                else -> 0f
            }
        } ?: 0f

        if (rotation != 0f) {
            val matrix = android.graphics.Matrix().apply { postRotate(rotation) }
            bitmap = android.graphics.Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        }

        val stream = java.io.ByteArrayOutputStream()
        bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 60, stream)
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

    suspend fun toggleFavourite(cardId: Long, isFavourite: Boolean) {
        client.postgrest.from("card").update({
            set("is_favourite", !isFavourite)
        }) {
            filter { eq("card_id", cardId) }
        }
    }

    fun currentUserId(): String {
        return client.auth.currentUserOrNull()?.id
            ?: throw Exception("Пользователь не авторизован")
    }
}