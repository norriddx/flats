@file:OptIn(ExperimentalSerializationApi::class)

package com.example.flats.data

import com.example.flats.BuildConfig
import io.github.jan.supabase.annotations.SupabaseInternal
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.serializer.KotlinXSerializer
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy

object SupabaseClient {
    @OptIn(SupabaseInternal::class)
    val client = createSupabaseClient(
        supabaseUrl = BuildConfig.SUPABASE_URL,
        supabaseKey = BuildConfig.SUPABASE_KEY
    ) {
        install(Auth)
        install(Postgrest) {
            serializer = KotlinXSerializer(Json {
                encodeDefaults = false
                namingStrategy = JsonNamingStrategy.SnakeCase
            })
        }
        install(Storage)

        httpEngine = OkHttp.create()
        httpConfig {
            install(HttpTimeout) {
                requestTimeoutMillis = 60_000
                connectTimeoutMillis = 30_000
                socketTimeoutMillis = 60_000
            }
        }
    }
}