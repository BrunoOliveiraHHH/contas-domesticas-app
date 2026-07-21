package br.com.contasdomesticas.app.data.remote

import br.com.contasdomesticas.app.data.remote.dto.AporteDto
import br.com.contasdomesticas.app.data.remote.dto.AporteRequestDto
import br.com.contasdomesticas.app.data.remote.dto.InvestimentoDto
import br.com.contasdomesticas.app.data.remote.dto.InvestimentoRequestDto
import br.com.contasdomesticas.app.data.remote.dto.PatrimonioDto
import br.com.contasdomesticas.app.data.remote.dto.SaldoInvestimentoDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface InvestimentoApi {

    @GET("api/v1/investimentos")
    suspend fun listar(): List<InvestimentoDto>

    @GET("api/v1/investimentos/patrimonio")
    suspend fun patrimonio(): PatrimonioDto

    @GET("api/v1/investimentos/{id}/saldo")
    suspend fun saldo(@Path("id") id: Long): SaldoInvestimentoDto

    @POST("api/v1/investimentos")
    suspend fun criar(@Body request: InvestimentoRequestDto): InvestimentoDto

    @DELETE("api/v1/investimentos/{id}")
    suspend fun remover(@Path("id") id: Long)

    @GET("api/v1/investimentos/{id}/aportes")
    suspend fun aportes(@Path("id") id: Long): List<AporteDto>

    @POST("api/v1/investimentos/{id}/aportes")
    suspend fun adicionarAporte(@Path("id") id: Long, @Body request: AporteRequestDto): AporteDto
}
