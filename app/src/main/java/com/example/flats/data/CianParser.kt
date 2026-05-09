package com.example.flats.data

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

data class CianListing(
    val name: String? = null,
    val address: String? = null,
    val price: Double? = null,
    val square: Double? = null,
    val description: String? = null,
    val imageUrls: List<String> = emptyList()
)

object CianParser {

    private val httpClient = HttpClient(OkHttp) {
        install(HttpTimeout) {
            requestTimeoutMillis = 30_000
            connectTimeoutMillis = 15_000
            socketTimeoutMillis = 30_000
        }
        followRedirects = true
    }

    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    private val ldJsonRegex = Regex(
        """<script[^>]*type="application/ld\+json"[^>]*>(.*?)</script>""",
        setOf(RegexOption.DOT_MATCHES_ALL, RegexOption.IGNORE_CASE)
    )
    private val titleTagRegex = Regex("""<title[^>]*>([^<]+)</title>""", RegexOption.IGNORE_CASE)
    private val squareRegex = Regex("""(\d+[.,]?\d*)\s*м²""")
    private val addressPatterns = listOf(
        Regex("""\d+[.,]?\d*\s*м²\s+(.+?)\s+м\.\s+\S"""),
        Regex("""\d+[.,]?\d*\s*м²\s+(.+?)\s+[-—]\s+база"""),
        Regex("""\d+[.,]?\d*\s*м²\s+(.+?)\.\s+Цена"""),
        Regex("""\d+[.,]?\d*\s*м²[,\s]+(.+?)(?=\s+[-—]|\.\s|$)""")
    )

    private val unwantedAddressPatterns = listOf(
        Regex("""^[А-ЯЁа-яё]{1,4}АО$"""),
        Regex("""^(?:р-н|район)\b.*""", RegexOption.IGNORE_CASE),
        Regex(""".+\s+(?:область|обл\.?)$""", RegexOption.IGNORE_CASE),
        Regex("""^.+\s+край$|^край\s+.+$""", RegexOption.IGNORE_CASE),
        Regex("""^республика\s+.+$|^.+\s+республика$""", RegexOption.IGNORE_CASE),
        Regex("""^Россия$""", RegexOption.IGNORE_CASE)
    )

    suspend fun parse(rawUrl: String): CianListing {
        val url = rawUrl.trim().replace("://m.cian.ru", "://www.cian.ru")
        if (!url.contains("cian.ru")) {
            throw IllegalArgumentException("Поддерживаются только ссылки с cian.ru")
        }

        val html = httpClient.get(url) {
            header(
                "User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
                        "(KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36"
            )
            header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
            header("Accept-Language", "ru-RU,ru;q=0.9,en;q=0.8")
        }.bodyAsText()

        val lower = html.lowercase()
        if ("проверьте, что вы не робот" in lower || "captcha" in lower && "<title>" in lower && "ld+json" !in lower) {
            throw Exception("Циан запросил проверку (капча), повторите позже")
        }

        val ldBlocks = ldJsonRegex.findAll(html).mapNotNull { match ->
            try {
                json.parseToJsonElement(match.groupValues[1].trim())
            } catch (_: Exception) {
                null
            }
        }.toList()

        val product = ldBlocks.firstNotNullOfOrNull { findByType(it, setOf("Product")) }
        val place = ldBlocks.firstNotNullOfOrNull {
            findByType(it, setOf("Apartment", "Residence", "RealEstateListing", "House"))
        }

        val ogTitle = extractMeta(html, "property", "og:title")
        val ogDescription = extractMeta(html, "property", "og:description")
        val titleTag = titleTagRegex.find(html)?.groupValues?.get(1)?.trim()?.unescape()
        val metaDescription = extractMeta(html, "name", "description")

        val name = product?.get("name")?.jsonPrimitive?.contentOrNull
            ?: place?.get("name")?.jsonPrimitive?.contentOrNull
            ?: ogTitle
            ?: titleTag

        val description = product?.get("description")?.jsonPrimitive?.contentOrNull
            ?: place?.get("description")?.jsonPrimitive?.contentOrNull
            ?: ogDescription

        val price = product?.get("offers")?.let { extractPrice(it) }

        fun addressFromText(text: String?): String? {
            if (text.isNullOrBlank()) return null
            for (pattern in addressPatterns) {
                val candidate = pattern.find(text)?.groupValues?.get(1)?.trim() ?: continue
                if (candidate.length in 5..200 && candidate.any { it.isLetter() }) return candidate
            }
            return null
        }

        val address = (place?.get("address") ?: product?.get("address"))
            ?.let { extractAddress(it) }
            ?: listOfNotNull(titleTag, metaDescription, ogTitle, ogDescription, description)
                .firstNotNullOfOrNull { addressFromText(it) }

        val images = mutableListOf<String>()
        product?.get("image")?.let { collectImages(it, images) }
        place?.get("image")?.let { collectImages(it, images) }
        if (images.isEmpty()) {
            extractMeta(html, "property", "og:image")?.let { images.add(upgradeCianImageUrl(it)) }
        }

        val areaSource = listOfNotNull(name, description).joinToString(" ")
        val square = squareRegex.find(areaSource)
            ?.groupValues?.get(1)?.replace(",", ".")?.toDoubleOrNull()

        return CianListing(
            name = name?.takeIf { it.isNotBlank() },
            address = address?.let { simplifyAddress(it) }?.takeIf { it.isNotBlank() },
            price = price,
            square = square,
            description = description?.takeIf { it.isNotBlank() },
            imageUrls = images.distinct()
        )
    }

    private fun extractMeta(html: String, attrName: String, attrValue: String): String? {
        val escaped = Regex.escape(attrValue)
        val r1 = Regex(
            """<meta[^>]*\b$attrName=["']$escaped["'][^>]*\bcontent=["']([^"']*)["']""",
            RegexOption.IGNORE_CASE
        )
        val r2 = Regex(
            """<meta[^>]*\bcontent=["']([^"']*)["'][^>]*\b$attrName=["']$escaped["']""",
            RegexOption.IGNORE_CASE
        )
        return (r1.find(html)?.groupValues?.get(1) ?: r2.find(html)?.groupValues?.get(1))?.unescape()
    }

    private fun findByType(element: JsonElement, types: Set<String>): JsonObject? {
        return when (element) {
            is JsonObject -> {
                val t = element["@type"]?.jsonPrimitive?.contentOrNull
                if (t in types) element
                else element.values.firstNotNullOfOrNull { findByType(it, types) }
            }
            is JsonArray -> element.firstNotNullOfOrNull { findByType(it, types) }
            else -> null
        }
    }

    private fun simplifyAddress(address: String): String {
        val parts = address.split(",").map { it.trim() }.filter { it.isNotBlank() }
        val filtered = parts.filterNot { part ->
            unwantedAddressPatterns.any { it.matches(part) }
        }
        return if (filtered.isEmpty()) address else filtered.joinToString(", ")
    }

    private fun extractPrice(element: JsonElement): Double? {
        return when (element) {
            is JsonObject -> {
                element["price"]?.let { p ->
                    when (p) {
                        is JsonPrimitive -> p.contentOrNull
                            ?.replace(" ", "")
                            ?.replace("\u00A0", "")
                            ?.replace(",", ".")
                            ?.toDoubleOrNull()
                        else -> null
                    }
                } ?: element.values.firstNotNullOfOrNull { extractPrice(it) }
            }
            is JsonArray -> element.firstNotNullOfOrNull { extractPrice(it) }
            else -> null
        }
    }

    private fun extractAddress(element: JsonElement): String? {
        return when (element) {
            is JsonObject -> {
                val parts = listOfNotNull(
                    element["addressLocality"]?.jsonPrimitive?.contentOrNull,
                    element["streetAddress"]?.jsonPrimitive?.contentOrNull
                ).filter { it.isNotBlank() }
                if (parts.isNotEmpty()) parts.joinToString(", ")
                else element.values.firstNotNullOfOrNull { extractAddress(it) }
            }
            is JsonArray -> element.firstNotNullOfOrNull { extractAddress(it) }
            is JsonPrimitive -> element.contentOrNull
            else -> null
        }
    }

    private fun collectImages(element: JsonElement, into: MutableList<String>) {
        when (element) {
            is JsonObject -> {
                val url = element["url"]?.jsonPrimitive?.contentOrNull
                if (url != null && url.startsWith("http")) into.add(upgradeCianImageUrl(url))
                else element.values.forEach { collectImages(it, into) }
            }
            is JsonArray -> element.forEach { collectImages(it, into) }
            is JsonPrimitive -> element.contentOrNull?.let {
                if (it.startsWith("http")) into.add(upgradeCianImageUrl(it))
            }
            else -> {}
        }
    }

    private fun upgradeCianImageUrl(url: String): String {
        if (!url.contains("cian", ignoreCase = true)) return url
        return url.replace(Regex("""-[2-9](\.\w+)(\?.*)?$"""), "-1$1$2")
    }

    private fun String.unescape(): String = this
        .replace("&quot;", "\"")
        .replace("&#039;", "'")
        .replace("&apos;", "'")
        .replace("&amp;", "&")
        .replace("&lt;", "<")
        .replace("&gt;", ">")
        .replace("&nbsp;", " ")
        .replace("\u00A0", " ")
}