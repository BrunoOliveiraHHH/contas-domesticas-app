package br.com.contasdomesticas.app.data.remote

import br.com.contasdomesticas.app.data.remote.dto.UnidadeMedidaDto
import br.com.contasdomesticas.app.data.remote.dto.UnidadeMedidaRequestDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.POST
import retrofit2.http.Path

interface UnidadeMedidaApi {

    @GET("api/v1/unidades-medida")
    suspend fun listar(): List<UnidadeMedidaDto>

    @POST("api/v1/unidades-medida")
    suspend fun criar(@Body request: UnidadeMedidaRequestDto): UnidadeMedidaDto

    @PUT("api/v1/unidades-medida/{id}")
    suspend fun atualizar(@Path("id") id: Long, @Body request: UnidadeMedidaRequestDto): UnidadeMedidaDto

    @DELETE("api/v1/unidades-medida/{id}")
    suspend fun remover(@Path("id") id: Long)
}
