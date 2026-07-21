package br.com.contasdomesticas.app.data.remote

import br.com.contasdomesticas.app.data.remote.dto.LoginRequestDto
import br.com.contasdomesticas.app.data.remote.dto.RefreshRequestDto
import br.com.contasdomesticas.app.data.remote.dto.TokenDto
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequestDto): TokenDto

    @POST("api/v1/auth/refresh")
    suspend fun refresh(@Body request: RefreshRequestDto): TokenDto

    @POST("api/v1/auth/logout")
    suspend fun logout(@Body request: RefreshRequestDto)
}
