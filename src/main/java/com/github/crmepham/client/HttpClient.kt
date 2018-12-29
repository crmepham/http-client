package com.github.crmepham.client

import com.github.crmepham.exception.HttpClientException
import com.github.crmepham.provider.AuthenticationProvider
import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import org.apache.commons.io.IOUtils
import org.apache.http.NameValuePair
import org.apache.http.client.HttpClient
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.*
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.impl.client.LaxRedirectStrategy
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.StringWriter
import java.lang.reflect.Type
import java.util.*


/**
 * A General-purpose HTTP client that provides the following features:
 *
 * <ul>
 *     <li>Perform a GET, POST, PUT, PATCH or DELETE request and receive the response as a String.</li>
 *     <li>Perform a GET request, specifying the object type that the received JSON will be deserialized into.</li>
 *     <li>Perform a POST, PUT, PATCH or DELETE request, supplying the object to be serialized to JSON and sent in the request body, and receive the response in a object of the specified type.</li>
 *     <li>Perform a GET, POST, PUT or PATCH request, supplying a list of body parameters, and receive the response in a object of the specified type.</li>
 *     <li>For more complex data type deserialization you can supply a GSON TokenType for most of the above features.</li>
 * </ul>
 *
 * @author Chris Mepham
 */
class HttpClient {

    companion object {
        val GSON = Gson()
        const val GENERIC_ERROR : String = "Could not parse the response from server."
    }

    var baseUri: String? = null
    var authenticationProvider: AuthenticationProvider? = null
    var headers: MutableList<NameValuePair> = ArrayList()
    private val client: HttpClient = HttpClientBuilder.create().setRedirectStrategy(LaxRedirectStrategy()).build()

    /**
     * Perform a <em>GET</em> request and return the response
     * body as a <em>String</em>.
     *
     * @param uri The request URI.
     * @return The response body as a <em>String</em>.
     */
    fun get(uri: String) : String {
        val get = HttpGet( baseUri + uri)
        authenticationProvider?.setAuthorization(get)
        return parseString(execute(get))
    }

    /**
     * Perform a <em>POST</em> request and return the response
     * body as a <em>String</em>.
     *
     * @param uri The request URI.
     * @return The response body as a <em>String</em>.
     */
    fun post(uri: String) : String {
        val post = HttpPost( baseUri + uri)
        authenticationProvider?.setAuthorization(post)
        return parseString(execute(post))
    }

    /**
     * Perform a <em>PUT</em> request and return the response
     * body as a <em>String</em>.
     *
     * @param uri The request URI.
     * @return The response body as a <em>String</em>.
     */
    fun put(uri: String) : String {
        val put = HttpPut( baseUri + uri)
        authenticationProvider?.setAuthorization(put)
        return parseString(execute(put))
    }

    /**
     * Perform a <em>PATCH</em> request and return the response
     * body as a <em>String</em>.
     *
     * @param uri The request URI.
     * @return The response body as a <em>String</em>.
     */
    fun patch(uri: String) : String {
        val patch = HttpPatch( baseUri + uri)
        authenticationProvider?.setAuthorization(patch)
        return parseString(execute(patch))
    }

    /**
     * Perform a <em>DELETE</em> request and return the response
     * body as a <em>String</em>.
     *
     * @param uri The request URI.
     * @return The response body as a <em>String</em>.
     */
    fun delete(uri: String) : String {
        val delete = HttpDelete( baseUri + uri)
        authenticationProvider?.setAuthorization(delete)
        return parseString(execute(delete))
    }

    /**
     * Perform a <em>GET</em> request and return the
     * deserialized <em>JSON</em> response body.
     *
     * @param uri The request URI.
     * @param returnType The class type of the deserialized object.
     * @return The response body deserialized into the specified object type.
     */
    fun <T> get(uri: String, returnType: Class<T>) : T {
        val get = HttpGet( baseUri + uri)
        authenticationProvider?.setAuthorization(get)
        return parseJsonClass(execute(get), returnType)
    }

    /**
     * Perform a <em>POST</em> request, supplying the object to be serialized
     * and sent in the request body. Return the deserialized <em>JSON</em>
     * response body.
     *
     * @param uri The request URI.
     * @param returnType The class type of the deserialized object.
     * @return The response body deserialized into the specified object type.
     */
    fun <T> post(uri: String, entity: Any, returnType: Class<T>) : T {
        val get = HttpPost( baseUri + uri)
        authenticationProvider?.setAuthorization(get)
        get.entity = StringEntity(GSON.toJson(entity))
        return parseJsonClass(execute(get), returnType)
    }

    /**
     * Perform a <em>PUT</em> request, supplying the object to serialized
     * and sent in the request body. Return the deserialized <em>JSON</em>
     * response body.
     *
     * @param uri The request URI.
     * @param returnType The class type of the deserialized object.
     * @return The response body deserialized into the specified object type.
     */
    fun <T> put(uri: String, entity: Any, returnType: Class<T>) : T {
        val put = HttpPut( baseUri + uri)
        authenticationProvider?.setAuthorization(put)
        put.entity = StringEntity(GSON.toJson(entity))
        return parseJsonClass(execute(put), returnType)
    }

    /**
     * Perform a <em>PATCH</em> request, supplying the object to serialized
     * and sent in the request body. Return the deserialized <em>JSON</em>
     * response body.
     *
     * @param uri The request URI.
     * @param returnType The class type of the deserialized object.
     * @return The response body deserialized into the specified object type.
     */
    fun <T> patch(uri: String, entity: Any, returnType: Class<T>) : T {
        val patch = HttpPatch( baseUri + uri)
        authenticationProvider?.setAuthorization(patch)
        patch.entity = StringEntity(GSON.toJson(entity))
        return parseJsonClass(execute(patch), returnType)
    }

    /**
     * Perform a <em>POST</em> request, supplying the object to be serialized
     * and sent in the request body. Return the deserialized <em>JSON</em>
     * response body in the supplied data <em>Type</em>.
     *
     * @param uri The request URI.
     * @param returnType The class type of the deserialized object.
     * @return The response body deserialized into the specified object type.
     */
    fun post(uri: String, entity: Any, returnType: Type) : Type {
        val post = HttpPost( baseUri + uri)
        authenticationProvider?.setAuthorization(post)
        post.entity = StringEntity(GSON.toJson(entity))
        return parseJsonType(execute(post), returnType)
    }

    /**
     * Perform a <em>PUT</em> request, supplying the object to be serialized
     * and sent in the request body. Return the deserialized <em>JSON</em>
     * response body in the supplied data <em>Type</em>.
     *
     * @param uri The request URI.
     * @param returnType The class type of the deserialized object.
     * @return The response body deserialized into the specified object type.
     */
    fun put(uri: String, entity: Any, returnType: Type) : Type {
        val put = HttpPut( baseUri + uri)
        authenticationProvider?.setAuthorization(put)
        put.entity = StringEntity(GSON.toJson(entity))
        return parseJsonType(execute(put), returnType)
    }

    /**
     * Perform a <em>PATCH</em> request, supplying the object to be serialized
     * and sent in the request body. Return the deserialized <em>JSON</em>
     * response body in the supplied data <em>Type</em>.
     *
     * @param uri The request URI.
     * @param returnType The class type of the deserialized object.
     * @return The response body deserialized into the specified object type.
     */
    fun patch(uri: String, entity: Any, returnType: Type) : Type {
        val patch = HttpPatch( baseUri + uri)
        authenticationProvider?.setAuthorization(patch)
        patch.entity = StringEntity(GSON.toJson(entity))
        return parseJsonType(execute(patch), returnType)
    }

    /**
     * Perform a <em>GET</em> request, supplying the <em>Type</em> that
     * the <em>JSON</em> response will be deserialized into. Use this method
     * for complex data types such as a List of Objects.
     *
     * @param uri The request URI.
     * @param returnType The complex Type of the deserialized type.
     * @return The response body deserialized into the specified Type.
     */
    fun get(uri: String, returnType: Type) : Type {
        val get = HttpGet( baseUri + uri)
        authenticationProvider?.setAuthorization(get)
        return parseJsonType(execute(get), returnType)
    }

    /**
     * Perform a <em>POST</em> request, supplying the <em>Type</em> that
     * the <em>JSON</em> response will be deserialized into. Use this method
     * for complex data types such as a List of Objects.
     *
     * @param uri The request URI.
     * @param returnType The complex Type of the deserialized type.
     * @return The response body deserialized into the specified Type.
     */
    fun post(uri: String, returnType: Type) : Type {
        val post = HttpPost( baseUri + uri)
        authenticationProvider?.setAuthorization(post)
        return parseJsonType(execute(post), returnType)
    }

    /**
     * Perform a <em>PUT</em> request, supplying the <em>Type</em> that
     * the <em>JSON</em> response will be deserialized into. Use this method
     * for complex data types such as a List of Objects.
     *
     * @param uri The request URI.
     * @param returnType The complex Type of the deserialized type.
     * @return The response body deserialized into the specified Type.
     */
    fun put(uri: String, returnType: Type) : Type {
        val put = HttpPut( baseUri + uri)
        authenticationProvider?.setAuthorization(put)
        return parseJsonType(execute(put), returnType)
    }

    /**
     * Perform a <em>PATCH</em> request, supplying the <em>Type</em> that
     * the <em>JSON</em> response will be deserialized into. Use this method
     * for complex data types such as a List of Objects.
     *
     * @param uri The request URI.
     * @param returnType The complex Type of the deserialized type.
     * @return The response body deserialized into the specified Type.
     */
    fun patch(uri: String, returnType: Type) : Type {
        val patch = HttpPatch( baseUri + uri)
        authenticationProvider?.setAuthorization(patch)
        return parseJsonType(execute(patch), returnType)
    }

    /**
     * Perform a <em>POST</em> request and disregard the response.
     *
     * @param uri The request URI.
     * @param entity The object that will serialized into <em>JSON</em>
     */
    fun post(uri: String, entity: Any) {
        val post = HttpPost( baseUri + uri)
        authenticationProvider?.setAuthorization(post)
        post.entity = StringEntity(GSON.toJson(entity))
        execute(post)
    }

    /**
     * Perform a <em>POST</em> request, supplying body parameters and
     * return the deserialized <em>JSON</em> response body.
     *
     * @param uri The request URI.
     * @param parameters The body parameters.
     * @param returnType The class type of the deserialized object.
     */
    fun <T> post(uri: String, parameters: List<NameValuePair>, returnType: Class<T>) : T {
        val post = HttpPost(baseUri + uri)
        post.entity = UrlEncodedFormEntity(parameters, "UTF-8")
        setHeaders(post)
        authenticationProvider?.setAuthorization(post)
        return parseJsonClass(execute(post), returnType)
    }

    /**
     * Perform a <em>PUT</em> request, supplying body parameters and
     * return the deserialized <em>JSON</em> response body.
     *
     * @param uri The request URI.
     * @param parameters The body parameters.
     * @param returnType The class type of the deserialized object.
     */
    fun <T> put(uri: String, parameters: List<NameValuePair>, returnType: Class<T>) : T {
        val put = HttpPut(baseUri + uri)
        put.entity = UrlEncodedFormEntity(parameters, "UTF-8")
        setHeaders(put)
        authenticationProvider?.setAuthorization(put)
        return parseJsonClass(execute(put), returnType)
    }

    /**
     * Perform a <em>PATCH</em> request, supplying body parameters and
     * return the deserialized <em>JSON</em> response body.
     *
     * @param uri The request URI.
     * @param parameters The body parameters.
     * @param returnType The class type of the deserialized object.
     */
    fun <T> patch(uri: String, parameters: List<NameValuePair>, returnType: Class<T>) : T {
        val patch = HttpPatch(baseUri + uri)
        patch.entity = UrlEncodedFormEntity(parameters, "UTF-8")
        setHeaders(patch)
        authenticationProvider?.setAuthorization(patch)
        return parseJsonClass(execute(patch), returnType)
    }

    /**
     * Perform a <em>POST</em> request, supplying body parameters and
     * return the deserialized <em>JSON</em> response body in the supplied
     * data <em>Type</em>.
     *
     * @param uri The request URI.
     * @param parameters The body parameters.
     * @param returnType The class type of the deserialized object.
     */
    fun post(uri: String, parameters: List<NameValuePair>, returnType: Type) : Type {
        val post = HttpPost(baseUri + uri)
        post.entity = UrlEncodedFormEntity(parameters, "UTF-8")
        setHeaders(post)
        authenticationProvider?.setAuthorization(post)
        return parseJsonType(execute(post), returnType)
    }

    /**
     * Perform a <em>PUT</em> request, supplying body parameters and
     * return the deserialized <em>JSON</em> response body in the supplied
     * data <em>Type</em>.
     *
     * @param uri The request URI.
     * @param parameters The body parameters.
     * @param returnType The class type of the deserialized object.
     */
    fun put(uri: String, parameters: List<NameValuePair>, returnType: Type) : Type {
        val put = HttpPut(baseUri + uri)
        put.entity = UrlEncodedFormEntity(parameters, "UTF-8")
        setHeaders(put)
        authenticationProvider?.setAuthorization(put)
        return parseJsonType(execute(put), returnType)
    }

    /**
     * Perform a <em>PATCH</em> request, supplying body parameters and
     * return the deserialized <em>JSON</em> response body in the supplied
     * data <em>Type</em>.
     *
     * @param uri The request URI.
     * @param parameters The body parameters.
     * @param returnType The class type of the deserialized object.
     */
    fun patch(uri: String, parameters: List<NameValuePair>, returnType: Type) : Type {
        val patch = HttpPatch(baseUri + uri)
        patch.entity = UrlEncodedFormEntity(parameters, "UTF-8")
        setHeaders(patch)
        authenticationProvider?.setAuthorization(patch)
        return parseJsonType(execute(patch), returnType)
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

    private fun <T> parseJsonType(inputStream: InputStream, returnType: Type) : T {
        return GSON.fromJson(JsonReader(InputStreamReader(inputStream)), returnType) ?: throw HttpClientException(GENERIC_ERROR)
    }

    private fun <T> parseJsonClass(inputStream: InputStream, returnType: Class<T>) : T {
        return GSON.fromJson(JsonReader(InputStreamReader(inputStream)), returnType) ?: throw HttpClientException(GENERIC_ERROR)
    }
}