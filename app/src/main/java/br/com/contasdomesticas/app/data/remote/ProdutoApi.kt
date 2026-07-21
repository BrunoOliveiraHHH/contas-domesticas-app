package br.com.contasdomesticas.app.data.remote

import br.com.contasdomesticas.app.data.remote.dto.ProdutoDto
import br.com.contasdomesticas.app.data.remote.dto.ProdutoRequestDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.POST
import retrofit2.http.Path

interface ProdutoApi {

    @GET("api/v1/produtos")
    suspend fun listar(): List<ProdutoDto>

    @POST("api/v1/produtos")
    suspend fun criar(@Body request: ProdutoRequestDto): ProdutoDto

    @PUT("api/v1/produtos/{id}")
    suspend fun atualizar(@Path("id") id: Long, @Body request: ProdutoRequestDto): ProdutoDto

    @DELETE("api/v1/produtos/{id}")
    suspend fun remover(@Path("id") id: Long)
}
