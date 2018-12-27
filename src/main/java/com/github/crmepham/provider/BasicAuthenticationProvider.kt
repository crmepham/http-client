package com.github.crmepham.provider

import org.apache.http.auth.Credentials
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.methods.HttpRequestBase
import org.apache.http.impl.auth.BasicScheme

class BasicAuthenticationProvider : AuthenticationProvider {

    private val basicAuthenticationProvider = BasicAuthenticationProvider()
    private var username: String? = null
    private var password: String? = null
    private var credentials: Credentials? = null

    override fun setAuthorization(base : HttpRequestBase) {
        base.addHeader(BasicScheme().authenticate(credentials, base, null))
    }

    fun username(username: String?) : BasicAuthenticationProvider {
        this.username = username
        return this
    }

    fun password(password: String?) : BasicAuthenticationProvider {
        this.password = password
        return this
    }

    fun build() : BasicAuthenticationProvider {
        this.credentials = UsernamePasswordCredentials(username, password)
        return basicAuthenticationProvider
    }

}