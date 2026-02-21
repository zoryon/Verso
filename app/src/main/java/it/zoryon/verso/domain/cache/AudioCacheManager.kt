package it.zoryon.verso.domain.cache

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.ConcurrentHashMap

data class CachedAudio(
    val url: String,
    val timestamp: Long = System.currentTimeMillis()
)

class AudioCacheManager(private val maxAgeMillis: Long = 5 * 60 * 1000) {
    private val cache = ConcurrentHashMap<String, CachedAudio>()
    private val mutex = Mutex()

    suspend fun getOrPut(videoId: String, fetch: suspend () -> String?): String? {
        return mutex.withLock {
            val cached = cache[videoId]
            val now = System.currentTimeMillis()

            if (cached != null && now - cached.timestamp <= maxAgeMillis) {
                return cached.url
            }

            val url = fetch()
            if (url != null) {
                cache[videoId] = CachedAudio(url)
            }

            url
        }
    }

    suspend fun clearExpired() {
        val now = System.currentTimeMillis()
        mutex.withLock {
            cache.entries.removeIf { now - it.value.timestamp > maxAgeMillis }
        }
    }
}