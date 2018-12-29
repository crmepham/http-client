package com.github.crmepham.client

import com.github.crmepham.provider.AuthenticationProvider
import org.apache.http.NameValuePair
import org.apache.http.message.BasicNameValuePair

/** Builds an instance of <em>HttpClient</em>. */
class HttpClientBuilder {

    private val client = HttpClient()

    fun uri(uri: String?) : HttpClientBuilder {
        client.baseUri = uri
        return this
    }

    fun header(key: String, value: String) : HttpClientBuilder {
        client.headers.add(BasicNameValuePair(key, value))
        return this
    }

    fun headers(headers: MutableList<NameValuePair>) : HttpClientBuilder {
        client.headers = headers
        return this
    }

    fun authenticationProvider(authenticationProvider: AuthenticationProvider) : HttpClientBuilder {
        client.authenticationProvider = authenticationProvider
        return this
    }

    fun build() : HttpClient = client

}