package com.github.crmepham.client

import com.github.crmepham.exception.HttpClientException
import com.github.crmepham.provider.AuthenticationProvider
import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import org.apache.http.NameValuePair
import org.apache.http.client.HttpClient
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.impl.client.LaxRedirectStrategy
import java.io.IOException
import java.io.InputStreamReader
import java.util.*


/**
 * A General-purpose HTTP client that provides
 * the following features:
 *
 * <ul>
 *     <li></li>
 * </ul>
 *
 * @author Chris Mepham
 */
class HttpClient {

    companion object {
        val GSON = Gson()
    }

    var baseUri: String? = null
    var authenticationProvider: AuthenticationProvider? = null
    var headers: MutableList<NameValuePair> = ArrayList()
    private val client: HttpClient = HttpClientBuilder.create().setRedirectStrategy(LaxRedirectStrategy()).build()

    fun <T> get(uri: String, returnType: Class<T>) : T {
        val get = HttpGet( baseUri + uri)
        authenticationProvider?.setAuthorization(get)
        return parse(execute(get), returnType)
    }

    fun <T> post(uri: String, parameters: List<NameValuePair>, returnType: Class<T>) : T {
        val post = HttpPost(baseUri + uri)
        post.entity = UrlEncodedFormEntity(parameters, "UTF-8")
        setHeaders(post)
        authenticationProvider?.setAuthorization(post)
        return parse(execute(post), returnType)
    }

    private fun setHeaders(request: HttpUriRequest) {
        for (pair: NameValuePair in headers) {
            request.addHeader(pair.name, pair.value)
        }
    }

    private fun execute(request: HttpUriRequest) : JsonReader {
        try {
            val response = client.execute(request)
            if (response.statusLine.statusCode == 200) {
                val content = response.entity.content
                return JsonReader(InputStreamReader(content))
            } else {
                throw HttpClientException(response.statusLine.reasonPhrase)
            }
        } catch (e: IOException) {
            throw HttpClientException("Could not execute HTTP request: ", e)
        }
    }

    private fun <T> parse(jsonReader: JsonReader, returnType: Class<T>) : T {
        return GSON.fromJson(jsonReader, returnType) ?: throw HttpClientException("Could not parse the response from server")
    }
}