package com.github.crmepham.provider

import com.github.crmepham.client.HttpClient
import com.github.crmepham.client.HttpClientBuilder
import com.github.crmepham.client.OauthToken
import org.apache.http.HttpHeaders.AUTHORIZATION
import org.apache.http.NameValuePair
import org.apache.http.client.methods.HttpRequestBase
import org.apache.http.message.BasicNameValuePair

class OauthAuthenticationProvider : AuthenticationProvider {

    companion object {
        const val GRANT_TYPE : String = "grant_type"
        const val REDIRECT_URI : String = "redirect_uri"
        const val AUTHORIZATION_CODE : String = "authorization_code"
        const val CODE : String = "code"
        const val ACCESS_TOKEN : String = "access_token"
        const val REFRESH_TOKEN : String = "refresh_token"
        const val REFRESH_BUFFER_MS = 10L * 1000L
    }

    private var clientId: String? = null
    private var clientSecret: String? = null
    private var redirectUri: String? = null
    private var oauthToken: OauthToken? = null
    private var headers: MutableList<NameValuePair> = ArrayList()
    private var uri: String? = null

    fun clientId(clientId: String) : OauthAuthenticationProvider {
        this.clientId = clientId
        return this
    }

    fun clientSecret(clientSecret: String) : OauthAuthenticationProvider {
        this.clientSecret = clientSecret
        return this
    }

    fun redirectUri(redirectUri: String) : OauthAuthenticationProvider {
        this.redirectUri = redirectUri
        return this
    }

    fun headers(headers: MutableList<NameValuePair>) : OauthAuthenticationProvider {
        this.headers = headers
        return this
    }

    fun uri(uri: String) : OauthAuthenticationProvider {
        this.uri = uri
        return this
    }

    fun buildWithAuthorizationToken(token: String) : OauthAuthenticationProvider {
        val parameters = listOf<NameValuePair>(
                BasicNameValuePair(GRANT_TYPE, AUTHORIZATION_CODE),
                BasicNameValuePair(REDIRECT_URI, this.redirectUri),
                BasicNameValuePair(CODE, token))
        return prepareToken(parameters)
    }

    fun buildWithRefreshToken(token: String) : OauthAuthenticationProvider {
        val parameters = listOf<NameValuePair>(
                BasicNameValuePair(GRANT_TYPE, REFRESH_TOKEN),
                BasicNameValuePair(REFRESH_TOKEN, token))
        return prepareToken(parameters)
    }

    fun buildWithAccessToken(token: OauthToken) : OauthAuthenticationProvider {
        return build(token)
    }

    override fun setAuthorization(base : HttpRequestBase) {
        refreshToken(oauthToken)
        base.addHeader(AUTHORIZATION, "Bearer " + oauthToken?.accessToken)
    }

    private fun refreshToken(token: OauthToken?) {
        if (isTokenExpired(token)) {
            getNewToken(token?.refreshToken)
        }
    }

    private fun getNewToken(token: String?) {
        val parameters = listOf<NameValuePair>(
                BasicNameValuePair(GRANT_TYPE, REFRESH_TOKEN),
                BasicNameValuePair(REFRESH_TOKEN, token))
        this.oauthToken = basicAuthenticationClient().post(ACCESS_TOKEN, parameters, OauthToken::class.java)
    }

    private fun isTokenExpired(token: OauthToken?): Boolean {
        val created = token?.created
        val expiresIn = token?.expiresIn?.times(1000)
        val combined = created?.plus(expiresIn!!)
        return combined!! > (System.currentTimeMillis() - OauthAuthenticationProvider.REFRESH_BUFFER_MS)
    }

    private fun basicAuthenticationClient(): HttpClient {
        return HttpClientBuilder().uri(uri)
                                  .headers(headers)
                                  .authenticationProvider(BasicAuthenticationProvider()
                                          .username(this.clientId)
                                          .password(this.clientSecret)
                                          .build())
                                  .build()
    }

    private fun prepareToken(parameters: List<NameValuePair>): OauthAuthenticationProvider {
        this.oauthToken = basicAuthenticationClient().post(ACCESS_TOKEN, parameters, OauthToken::class.java)
        this.oauthToken?.created = System.currentTimeMillis()
        return this
    }

    private fun build(token: OauthToken) : OauthAuthenticationProvider {
        this.oauthToken = token
        return this
    }
}