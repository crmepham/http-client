package com.github.crmepham.exception

import java.lang.RuntimeException

class HttpClientException(message: String, e: Throwable?) : RuntimeException(message, e) {

    constructor(message: String) : this(message, null)
}