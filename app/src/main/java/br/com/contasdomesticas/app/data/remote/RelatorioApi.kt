package br.com.contasdomesticas.app.data.remote

import br.com.contasdomesticas.app.data.remote.dto.PorCategoriaItemDto
import br.com.contasdomesticas.app.data.remote.dto.SaldoMesDto
import retrofit2.http.GET
import retrofit2.http.Query

interface RelatorioApi {

    @GET("api/v1/relatorios/saldo")
    suspend fun saldo(
        @Query("periodo") periodo: String,
        @Query("carteira") carteira: Long? = null
    ): SaldoMesDto

    @GET("api/v1/relatorios/por-categoria")
    suspend fun porCategoria(
        @Query("periodo") periodo: String,
        @Query("tipo") tipo: String? = null
    ): List<PorCategoriaItemDto>
}
