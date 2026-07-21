package br.com.contasdomesticas.app.data.remote

import br.com.contasdomesticas.app.data.remote.dto.AcertoItemDto
import retrofit2.http.GET
import retrofit2.http.Query

interface RateioApi {

    @GET("api/v1/rateios/acerto")
    suspend fun acerto(@Query("periodo") periodo: String): List<AcertoItemDto>
}
