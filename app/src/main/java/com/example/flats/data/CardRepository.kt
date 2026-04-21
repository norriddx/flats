package com.example.flats.data

import android.content.Context
import android.net.Uri
import com.example.flats.data.model.Card
import com.example.flats.data.model.CardCriteriaScore
import com.example.flats.data.model.Criteria
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.delay
import java.util.UUID

object CardRepository {

    private val client = SupabaseClient.client

    private suspend fun <T> withRetry(
        attempts: Int = 3,
        initialDelayMs: Long = 1000,
        block: suspend () -> T
    ): T {
        var lastError: Exception? = null
        var delayMs = initialDelayMs
        repeat(attempts) { attempt ->
            try {
                return block()
            } catch (e: kotlinx.coroutines.CancellationException) {
                throw e
            } catch (e: Exception) {
                lastError = e
                if (attempt < attempts - 1) {
                    delay(delayMs)
                    delayMs *= 2
                }
            }
        }
        throw lastError ?: Exception("Неизвестная ошибка")
    }

    suspend fun insertCard(card: Card): Card = withRetry {
        client.postgrest
            .from("card")
            .insert(card) {
                select()
            }
            .decodeSingle<Card>()
    }

    suspend fun insertCardCriteriaScores(scores: List<CardCriteriaScore>) {
        if (scores.isEmpty()) return
        withRetry {
            client.postgrest.from("card_criteria_score").insert(scores)
        }
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

        val maxSide = 1600
        val sampleSize = maxOf(1, maxOf(options.outWidth, options.outHeight) / maxSide)

        val decodeOptions = android.graphics.BitmapFactory.Options().apply {
            inSampleSize = sampleSize
        }
        var bitmap = context.contentResolver.openInputStream(uri)?.use {
            android.graphics.BitmapFactory.decodeStream(it, null, decodeOptions)
        } ?: throw Exception("Не удалось прочитать файл")

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
        bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 85, stream)
        val bytes = stream.toByteArray()

        val fileName = "$userId/${UUID.randomUUID()}.jpg"

        withRetry {
            client.storage.from("card-images").upload(fileName, bytes)
        }

        return client.storage.from("card-images").publicUrl(fileName)
    }

    suspend fun getCards(): List<Card> = withRetry {
        val userId = client.auth.currentUserOrNull()?.id
            ?: throw Exception("Пользователь не авторизован")

        client.postgrest
            .from("card")
            .select {
                filter {
                    eq("user_id", userId)
                    eq("is_archived", false)
                }
                order("card_id", io.github.jan.supabase.postgrest.query.Order.DESCENDING)
            }
            .decodeList<Card>()
    }

    suspend fun getArchivedCards(): List<Card> = withRetry {
        val userId = client.auth.currentUserOrNull()?.id
            ?: throw Exception("Пользователь не авторизован")

        client.postgrest
            .from("card")
            .select {
                filter {
                    eq("user_id", userId)
                    eq("is_archived", true)
                }
                order("card_id", io.github.jan.supabase.postgrest.query.Order.DESCENDING)
            }
            .decodeList<Card>()
    }

    suspend fun getCardById(cardId: Long): Card = withRetry {
        client.postgrest
            .from("card")
            .select {
                filter { eq("card_id", cardId) }
            }
            .decodeSingle<Card>()
    }

    suspend fun deleteCard(cardId: Long) {
        withRetry {
            client.postgrest.from("card").delete {
                filter { eq("card_id", cardId) }
            }
        }
    }

    suspend fun archiveCard(cardId: Long) {
        withRetry {
            client.postgrest.from("card").update({
                set("is_archived", true)
            }) {
                filter { eq("card_id", cardId) }
            }
        }
    }

    suspend fun clearArchive() {
        withRetry {
            val userId = client.auth.currentUserOrNull()?.id
                ?: throw Exception("Пользователь не авторизован")
            client.postgrest.from("card").delete {
                filter {
                    eq("user_id", userId)
                    eq("is_archived", true)
                }
            }
        }
    }

    suspend fun getCriteria(): List<Criteria> = withRetry {
        val userId = client.auth.currentUserOrNull()?.id
            ?: throw Exception("Пользователь не авторизован")

        client.postgrest
            .from("criteria")
            .select {
                filter { eq("user_id", userId) }
            }
            .decodeList<Criteria>()
    }

    suspend fun getAllScores(): List<CardCriteriaScore> = withRetry {
        client.postgrest
            .from("card_criteria_score")
            .select()
            .decodeList<CardCriteriaScore>()
    }

    suspend fun toggleFavourite(cardId: Long, isFavourite: Boolean) {
        withRetry {
            client.postgrest.from("card").update({
                set("is_favourite", !isFavourite)
            }) {
                filter { eq("card_id", cardId) }
            }
        }
    }

    suspend fun updateCard(card: Card): Card = withRetry {
        client.postgrest
            .from("card")
            .update(
                {
                    set("user_id", card.userId)
                    set("name", card.name)
                    set("address", card.address)
                    set("price", card.price)
                    set("square", card.square)
                    set("description", card.description)
                    set("is_favourite", card.isFavourite)
                    set("is_archived", card.isArchived)
                    set("is_draft", card.isDraft)
                    set("utilities_included", card.utilitiesIncluded)
                    set("image_urls", card.imageUrls)
                    set("price_period", card.pricePeriod)
                    set("contact", card.contact)
                }
            ) {
                select()
                filter { eq("card_id", card.cardId) }
            }
            .decodeSingle<Card>()
    }

    suspend fun deleteCardCriteriaScores(cardId: Long) {
        withRetry {
            client.postgrest.from("card_criteria_score").delete {
                filter { eq("card_id", cardId) }
            }
        }
    }

    fun currentUserId(): String {
        return client.auth.currentUserOrNull()?.id
            ?: throw Exception("Пользователь не авторизован")
    }
}