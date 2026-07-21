package br.com.contasdomesticas.app.data.remote

import br.com.contasdomesticas.app.data.remote.dto.LancamentoDto
import br.com.contasdomesticas.app.data.remote.dto.ReceitaRequestDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ReceitaApi {

    @GET("api/v1/receitas")
    suspend fun listar(): List<LancamentoDto>

    @POST("api/v1/receitas")
    suspend fun criar(@Body request: ReceitaRequestDto): LancamentoDto

    @DELETE("api/v1/receitas/{id}")
    suspend fun remover(@Path("id") id: Long)
}
