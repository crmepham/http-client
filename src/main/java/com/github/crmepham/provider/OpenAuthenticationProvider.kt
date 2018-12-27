package com.github.crmepham.provider

import org.apache.http.client.methods.HttpRequestBase

class OpenAuthenticationProvider : AuthenticationProvider {
    override fun setAuthorization(base: HttpRequestBase) {}
}