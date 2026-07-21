package br.com.contasdomesticas.app.data.remote.dto

data class LoginRequestDto(val login: String, val senha: String)

data class RefreshRequestDto(val refreshToken: String)

data class TokenDto(
    val accessToken: String,
    val refreshToken: String?,
    val tipo: String?,
    val expiraEm: Long?
)
