package br.com.contasdomesticas.app.data.remote

import br.com.contasdomesticas.app.data.remote.dto.MercadoDto
import br.com.contasdomesticas.app.data.remote.dto.MercadoRequestDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.POST
import retrofit2.http.Path

interface MercadoApi {

    @GET("api/v1/mercados")
    suspend fun listar(): List<MercadoDto>

    @POST("api/v1/mercados")
    suspend fun criar(@Body request: MercadoRequestDto): MercadoDto

    @PUT("api/v1/mercados/{id}")
    suspend fun atualizar(@Path("id") id: Long, @Body request: MercadoRequestDto): MercadoDto

    @DELETE("api/v1/mercados/{id}")
    suspend fun remover(@Path("id") id: Long)
}
