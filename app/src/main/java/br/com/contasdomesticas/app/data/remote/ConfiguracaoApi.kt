package br.com.contasdomesticas.app.data.remote

import br.com.contasdomesticas.app.data.remote.dto.ImpostoIrDto
import br.com.contasdomesticas.app.data.remote.dto.ParametroDto
import br.com.contasdomesticas.app.data.remote.dto.ParametroRequestDto
import br.com.contasdomesticas.app.data.remote.dto.PreferenciaDto
import br.com.contasdomesticas.app.data.remote.dto.PreferenciaRequestDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ConfiguracaoApi {

    @GET("api/v1/parametros")
    suspend fun listarParametros(): List<ParametroDto>

    @POST("api/v1/parametros")
    suspend fun criarParametro(@Body request: ParametroRequestDto): ParametroDto

    @PUT("api/v1/parametros/{id}")
    suspend fun atualizarParametro(@Path("id") id: Long, @Body request: ParametroRequestDto): ParametroDto

    @DELETE("api/v1/parametros/{id}")
    suspend fun removerParametro(@Path("id") id: Long)

    @GET("api/v1/parametros/imposto-ir")
    suspend fun impostoIr(@Query("dias") dias: Int): ImpostoIrDto

    @GET("api/v1/preferencias/{chave}")
    suspend fun resolverPreferencia(
        @Path("chave") chave: String,
        @Query("usuarioId") usuarioId: Long? = null
    ): PreferenciaDto

    @PUT("api/v1/preferencias/{chave}")
    suspend fun gravarPreferencia(
        @Path("chave") chave: String,
        @Body request: PreferenciaRequestDto
    ): PreferenciaDto
}
