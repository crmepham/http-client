package com.github.crmepham.client

import com.github.crmepham.provider.BasicAuthenticationProvider
import com.github.crmepham.provider.OauthAuthenticationProvider
import org.apache.http.NameValuePair
import org.apache.http.message.BasicNameValuePair
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test


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
                                            .headers(getSharedHeaders())
                                            .authenticationProvider(OauthAuthenticationProvider()
                                                    .clientId(CLIENT_ID)
                                                    .clientSecret(CLIENT_SECRET)
                                                    .uri(OAUTH_AUTH_URI)
                                                    .redirectUri(REDIRECT_URI)
                                                    .headers(getSharedHeaders())
                                                    .buildWithAuthorizationToken(AUTH_TOKEN))
                                            .build()

        val firstResponse = httpClient.get("me", Map::class.java)

        val response = httpClient.get("me", Map::class.java)

        assertThat(httpClient.authenticationProvider).isNotNull
        assertThat(response).isNotNull
    }

    @Test
    fun testOauthAuthenticationWithAccessToken() {

        val token = OauthToken()
        token.accessToken = "16633882-klzcsXDIBJ0bTbAnuzo76xN4G2s"
        token.refreshToken = "16633882-TsVMYkxm60jPjtM6erZyJcQjAgo"
        token.expiresIn = 3600
        token.scope = "edit flair history identity modconfig modflair modlog modposts modwiki mysubreddits privatemessages read report save submit subscribe vote wikiedit wikiread"
        token.type = "bearer"

        val httpClient = HttpClientBuilder().uri(OAUTH_BASE_URI)
                                            .headers(getSharedHeaders())
                                            .authenticationProvider(OauthAuthenticationProvider()
                                                    .clientId(CLIENT_ID)
                                                    .clientSecret(CLIENT_SECRET)
                                                    .uri(OAUTH_AUTH_URI)
                                                    .redirectUri(REDIRECT_URI)
                                                    .headers(getSharedHeaders())
                                                    .buildWithAccessToken(token))
                                            .build()

        val response = httpClient.get("me", Map::class.java)

        assertThat(httpClient.authenticationProvider).isNotNull
        assertThat(response).isNotNull
    }

    @Test
    fun testOauthAuthenticationWithRefreshToken() {

        val httpClient = HttpClientBuilder().uri(OAUTH_BASE_URI)
                                            .headers(getSharedHeaders())
                                            .authenticationProvider(OauthAuthenticationProvider()
                                                    .clientId(CLIENT_ID)
                                                    .clientSecret(CLIENT_SECRET)
                                                    .uri(OAUTH_AUTH_URI)
                                                    .redirectUri(REDIRECT_URI)
                                                    .headers(getSharedHeaders())
                                                    .buildWithRefreshToken("16633882-TsVMYkxm60jPjtM6erZyJcQjAgo"))
                                            .build()

        val response = httpClient.get("me", Map::class.java)

        assertThat(httpClient.authenticationProvider).isNotNull
        assertThat(response).isNotNull
    }

    private fun getSharedHeaders() : MutableList<NameValuePair> {
        return mutableListOf(
                BasicNameValuePair("Content-Type", "application/x-www-form-urlencoded"),
                BasicNameValuePair("User-Agent", "Just testing"))
    }
}