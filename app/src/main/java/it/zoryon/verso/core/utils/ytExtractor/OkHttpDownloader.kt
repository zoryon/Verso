package it.zoryon.verso.core.utils.ytExtractor

import okhttp3.OkHttpClient
import okhttp3.Headers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.schabi.newpipe.extractor.downloader.Downloader
import org.schabi.newpipe.extractor.downloader.Request
import org.schabi.newpipe.extractor.downloader.Response
import java.io.IOException

class OkHttpDownloader : Downloader() {
    private val client = OkHttpClient.Builder()
        .followRedirects(true)
        .build()

    @Throws(IOException::class)
    override fun execute(request: Request): Response {
        val url = request.url()
        val method = request.httpMethod()
        val data = request.dataToSend()

        val headersBuilder = Headers.Builder()
        request.headers().forEach { (key, values) ->
            values.forEach { value -> headersBuilder.add(key, value) }
        }

        val requestBody = if (method == "POST") {
            data?.toRequestBody("application/json".toMediaTypeOrNull())
                ?: "".toRequestBody(null) // Create a void body if new pipe doesn't return one
        } else {
            null
        }

        val okRequest = okhttp3.Request.Builder()
            .url(url)
            .method(method, requestBody)
            .headers(headersBuilder.build())
            .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36")
            .build()

        val okResponse = client.newCall(okRequest).execute()
        val responseBody = okResponse.body?.string()

        return Response(
            okResponse.code,
            okResponse.message,
            okResponse.headers.toMultimap(),
            responseBody,
            okResponse.request.url.toString()
        )
    }
}
