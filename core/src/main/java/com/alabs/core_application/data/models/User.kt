package com.alabs.core_application.data.models

import com.google.gson.annotations.SerializedName


data class RefreshTokenRequestDTO(
    @SerializedName("refresh_token") val refresh_token: String,
    @SerializedName("grant_type") val grant_type: String,
    @SerializedName("operator") val operator: String
)


data class AuthRefreshTokenDTO(
    @SerializedName("access_token") val access_token: String,
    @SerializedName("token_type") val token_type: String?,
    @SerializedName("refresh_token") val refresh_token: String,
    @SerializedName("expires_in") val expires_in: String,
    @SerializedName("scope") val scope: String,
    @SerializedName("session") val session: String?,
    @SerializedName("operator") val operator: String,
    @SerializedName("status") val status: String,
    @SerializedName("jti") val jti: String
)
