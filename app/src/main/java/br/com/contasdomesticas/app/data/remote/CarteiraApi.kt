package br.com.contasdomesticas.app.data.remote

import br.com.contasdomesticas.app.data.remote.dto.CarteiraDto
import br.com.contasdomesticas.app.data.remote.dto.CarteiraRequestDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.POST
import retrofit2.http.Path

interface CarteiraApi {

    @GET("api/v1/carteiras")
    suspend fun listar(): List<CarteiraDto>

    @POST("api/v1/carteiras")
    suspend fun criar(@Body request: CarteiraRequestDto): CarteiraDto

    @PUT("api/v1/carteiras/{id}")
    suspend fun atualizar(@Path("id") id: Long, @Body request: CarteiraRequestDto): CarteiraDto

    @DELETE("api/v1/carteiras/{id}")
    suspend fun remover(@Path("id") id: Long)
}
