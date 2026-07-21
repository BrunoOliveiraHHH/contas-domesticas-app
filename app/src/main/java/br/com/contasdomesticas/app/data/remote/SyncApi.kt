package br.com.contasdomesticas.app.data.remote

import br.com.contasdomesticas.app.data.remote.dto.SyncMercadoDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface SyncApi {

    @GET("api/v1/sync/mercados")
    suspend fun delta(@Query("desde") desde: String? = null): List<SyncMercadoDto>

    @POST("api/v1/sync/mercados")
    suspend fun merge(@Body registros: List<SyncMercadoDto>): List<SyncMercadoDto>
}
