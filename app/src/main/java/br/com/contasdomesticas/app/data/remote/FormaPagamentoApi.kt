package br.com.contasdomesticas.app.data.remote

import br.com.contasdomesticas.app.data.remote.dto.FormaPagamentoDto
import br.com.contasdomesticas.app.data.remote.dto.FormaPagamentoRequestDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.POST
import retrofit2.http.Path

interface FormaPagamentoApi {

    @GET("api/v1/formas-pagamento")
    suspend fun listar(): List<FormaPagamentoDto>

    @POST("api/v1/formas-pagamento")
    suspend fun criar(@Body request: FormaPagamentoRequestDto): FormaPagamentoDto

    @PUT("api/v1/formas-pagamento/{id}")
    suspend fun atualizar(@Path("id") id: Long, @Body request: FormaPagamentoRequestDto): FormaPagamentoDto

    @DELETE("api/v1/formas-pagamento/{id}")
    suspend fun remover(@Path("id") id: Long)
}
