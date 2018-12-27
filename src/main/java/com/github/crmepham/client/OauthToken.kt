package com.github.crmepham.client

import com.google.gson.annotations.SerializedName

class OauthToken {

    @SerializedName("access_token")
    var accessToken : String? = null

    @SerializedName("token_type")
    var type: String? = null

    @SerializedName("refresh_token")
    var refreshToken : String? = null

    @SerializedName("expires_in")
    var expiresIn : Int? = null

    var created : Long? = null

    var scope : String? = null
}