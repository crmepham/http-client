package com.github.crmepham.provider

import org.apache.http.client.methods.HttpRequestBase

interface AuthenticationProvider {

    fun setAuthorization(base : HttpRequestBase)
}