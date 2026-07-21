package br.com.contasdomesticas.app.data.remote

import br.com.contasdomesticas.app.data.remote.dto.CategoriaDto
import br.com.contasdomesticas.app.data.remote.dto.CategoriaRequestDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.POST
import retrofit2.http.Path

interface CategoriaApi {

    @GET("api/v1/categorias")
    suspend fun listar(): List<CategoriaDto>

    @POST("api/v1/categorias")
    suspend fun criar(@Body request: CategoriaRequestDto): CategoriaDto

    @PUT("api/v1/categorias/{id}")
    suspend fun atualizar(@Path("id") id: Long, @Body request: CategoriaRequestDto): CategoriaDto

    @DELETE("api/v1/categorias/{id}")
    suspend fun remover(@Path("id") id: Long)
}
