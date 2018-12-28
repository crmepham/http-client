package com.github.crmepham.client

import com.github.crmepham.exception.HttpClientException
import com.github.crmepham.provider.AuthenticationProvider
import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import org.apache.commons.io.IOUtils
import org.apache.http.NameValuePair
import org.apache.http.client.HttpClient
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.impl.client.LaxRedirectStrategy
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.StringWriter
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

    fun getAsString(uri: String) : String {
        val get = HttpGet( baseUri + uri)
        authenticationProvider?.setAuthorization(get)
        return parseString(execute(get))
    }

    fun <T> get(uri: String, returnType: Class<T>) : T {
        val get = HttpGet( baseUri + uri)
        authenticationProvider?.setAuthorization(get)
        return parseJson(execute(get), returnType)
    }

    fun post(uri: String, entity: Any) {
        val post = HttpPost( baseUri + uri)
        authenticationProvider?.setAuthorization(post)
        post.entity = StringEntity(GSON.toJson(entity))
        execute(post)
    }

    fun <T> post(uri: String, entity: Any, returnType: Class<T>) : T {
        val get = HttpPost( baseUri + uri)
        authenticationProvider?.setAuthorization(get)
        return parseJson(execute(get), returnType)
    }

    fun <T> post(uri: String, parameters: List<NameValuePair>, returnType: Class<T>) : T {
        val post = HttpPost(baseUri + uri)
        post.entity = UrlEncodedFormEntity(parameters, "UTF-8")
        setHeaders(post)
        authenticationProvider?.setAuthorization(post)
        return parseJson(execute(post), returnType)
    }

    private fun setHeaders(request: HttpUriRequest) {
        for (pair: NameValuePair in headers) {
            request.addHeader(pair.name, pair.value)
        }
    }

    private fun execute(request: HttpUriRequest) : InputStream {
        try {
            val response = client.execute(request)
            if (response.statusLine.statusCode == 200) {
                return response.entity.content
            } else {
                throw HttpClientException(response.statusLine.reasonPhrase)
            }
        } catch (e: IOException) {
            throw HttpClientException("Could not execute HTTP request: ", e)
        }
    }

    private fun parseString(inputStream: InputStream) : String {
        val writer = StringWriter()
        IOUtils.copy(inputStream, writer, "UTF-8")
        return writer.toString()
    }

    private fun <T> parseJson(inputStream: InputStream, returnType: Class<T>) : T {
        return GSON.fromJson(JsonReader(InputStreamReader(inputStream)), returnType) ?: throw HttpClientException("Could not parse the response from server.")
    }
}