package br.com.contasdomesticas.app.data.remote

import br.com.contasdomesticas.app.data.remote.dto.DespesaRequestDto
import br.com.contasdomesticas.app.data.remote.dto.LancamentoDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface DespesaApi {

    @GET("api/v1/despesas")
    suspend fun listar(): List<LancamentoDto>

    @POST("api/v1/despesas")
    suspend fun criar(@Body request: DespesaRequestDto): LancamentoDto

    @POST("api/v1/despesas/{id}/pagar")
    suspend fun pagar(@Path("id") id: Long): LancamentoDto

    @DELETE("api/v1/despesas/{id}")
    suspend fun remover(@Path("id") id: Long)
}
