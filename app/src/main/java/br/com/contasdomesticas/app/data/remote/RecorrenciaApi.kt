package br.com.contasdomesticas.app.data.remote

import br.com.contasdomesticas.app.data.remote.dto.LancamentoDto
import br.com.contasdomesticas.app.data.remote.dto.RecorrenciaDto
import br.com.contasdomesticas.app.data.remote.dto.RecorrenciaRequestDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface RecorrenciaApi {

    @GET("api/v1/recorrencias")
    suspend fun listar(): List<RecorrenciaDto>

    @POST("api/v1/recorrencias")
    suspend fun criar(@Body request: RecorrenciaRequestDto): RecorrenciaDto

    @POST("api/v1/recorrencias/{id}/gerar")
    suspend fun gerar(@Path("id") id: Long, @Query("competencia") competencia: String): LancamentoDto

    @DELETE("api/v1/recorrencias/{id}")
    suspend fun remover(@Path("id") id: Long)
}
