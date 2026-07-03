package br.com.contasdomesticas.app.data.remote

import br.com.contasdomesticas.app.data.remote.dto.UsuarioDto
import br.com.contasdomesticas.app.data.remote.dto.UsuarioRequestDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Endpoints de usuario da API (usados na sincronizacao).
 */
interface UsuarioApi {

    @GET("api/v1/usuarios")
    suspend fun listar(): List<UsuarioDto>

    @GET("api/v1/usuarios/{id}")
    suspend fun buscar(@Path("id") id: Long): UsuarioDto

    @POST("api/v1/usuarios")
    suspend fun criar(@Body request: UsuarioRequestDto): UsuarioDto
}
