package com.github.crmepham.client

import com.github.crmepham.provider.BasicAuthenticationProvider
import com.github.crmepham.provider.OauthAuthenticationProvider
import com.google.gson.reflect.TypeToken
import org.apache.http.NameValuePair
import org.apache.http.message.BasicNameValuePair
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.util.*


class HttpClientTest {

    companion object {
        val CLIENT_ID :             String = ""
        val CLIENT_SECRET :         String = ""
        val REDIRECT_URI :          String = ""
        val AUTH_TOKEN :            String = ""
        val BASIC_BASE_URI :        String = ""
        val OAUTH_AUTH_URI :        String = ""
        val OAUTH_BASE_URI :        String = ""
    }

    @Test
    fun testGetType() {
        val returnType = object : TypeToken<List<OauthToken>>() {}.type
        val httpClient = HttpClientBuilder().uri("http://ptsv2.com/t/").build()
        val result = httpClient.get("n7gmb-1546017023/post", returnType)
        assertThat(result).isNotNull
    }

    @Test
    fun testGet() {
        val httpClient = HttpClientBuilder().uri("http://ptsv2.com/t/").build()
        val result = httpClient.get("n7gmb-1546017023/post")
        assertThat(result).isNotNull()
    }

    @Test
    fun testBasicAuthentication() {
        val httpClient = HttpClientBuilder().uri(BASIC_BASE_URI)
                                            .authenticationProvider(BasicAuthenticationProvider()
                                                    .username("john123")
                                                    .password("password")
                                                    .build())
                                            .build()
        val response = httpClient.get("accounts/get-all", List::class.java)
        println(response)
    }

    @Test
    fun testOauthAuthenticationWithAuthorizationCode() {

        val httpClient = HttpClientBuilder().uri(OAUTH_BASE_URI)
                                            .headers(sharedHeaders())
                                            .authenticationProvider(OauthAuthenticationProvider()
                                                    .clientId(CLIENT_ID)
                                                    .clientSecret(CLIENT_SECRET)
                                                    .uri(OAUTH_AUTH_URI)
                                                    .redirectUri(REDIRECT_URI)
                                                    .headers(sharedHeaders())
                                                    .buildWithAuthorizationToken(AUTH_TOKEN))
                                            .build()

        val response = httpClient.get("me", Map::class.java)

        assertThat(httpClient.authenticationProvider).isNotNull
        assertThat(response).isNotNull
    }

    @Test
    fun testOauthAuthenticationWithAccessToken() {

        val httpClient = HttpClientBuilder().uri(OAUTH_BASE_URI)
                                            .headers(sharedHeaders())
                                            .authenticationProvider(OauthAuthenticationProvider()
                                                    .clientId(CLIENT_ID)
                                                    .clientSecret(CLIENT_SECRET)
                                                    .uri(OAUTH_AUTH_URI)
                                                    .redirectUri(REDIRECT_URI)
                                                    .headers(sharedHeaders())
                                                    .buildWithAccessToken(oauthToken()))
                                            .build()

        val response = httpClient.get("me", Map::class.java)

        assertThat(httpClient.authenticationProvider).isNotNull
        assertThat(response).isNotNull
    }

    @Test
    fun testOauthAuthenticationWithRefreshToken() {

        val httpClient = HttpClientBuilder().uri(OAUTH_BASE_URI)
                                            .headers(sharedHeaders())
                                            .authenticationProvider(OauthAuthenticationProvider()
                                                    .clientId(CLIENT_ID)
                                                    .clientSecret(CLIENT_SECRET)
                                                    .uri(OAUTH_AUTH_URI)
                                                    .redirectUri(REDIRECT_URI)
                                                    .headers(sharedHeaders())
                                                    .buildWithRefreshToken("16633882-TsVMYkxm60jPjtM6erZyJcQjAgo"))
                                            .build()

        val response = httpClient.get("me", Map::class.java)

        assertThat(httpClient.authenticationProvider).isNotNull
        assertThat(response).isNotNull
    }

    private fun oauthToken() : OauthToken {
        val token = OauthToken()
        token.accessToken = "16633882-klzcsXDIBJ0bTbAnuzo76xN4G2s"
        token.refreshToken = "16633882-TsVMYkxm60jPjtM6erZyJcQjAgo"
        token.expiresIn = 3600
        token.scope = "edit flair history identity modconfig modflair modlog modposts modwiki mysubreddits privatemessages read report save submit subscribe vote wikiedit wikiread"
        token.type = "bearer"
        token.created = System.currentTimeMillis()
        return token
    }

    private fun sharedHeaders() : MutableList<NameValuePair> {
        return mutableListOf(
                BasicNameValuePair("Content-Type", "application/x-www-form-urlencoded"),
                BasicNameValuePair("User-Agent", "Just testing"))
    }
}