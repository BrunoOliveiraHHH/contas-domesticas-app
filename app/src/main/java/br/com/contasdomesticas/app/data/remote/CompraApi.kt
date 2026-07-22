package br.com.contasdomesticas.app.data.remote

import br.com.contasdomesticas.app.data.remote.dto.CotacaoProdutoDto
import br.com.contasdomesticas.app.data.remote.dto.CotacaoProdutoRequestDto
import br.com.contasdomesticas.app.data.remote.dto.EscolhaEstabelecimentoRequestDto
import br.com.contasdomesticas.app.data.remote.dto.FecharListaRequestDto
import br.com.contasdomesticas.app.data.remote.dto.ItemCompraDto
import br.com.contasdomesticas.app.data.remote.dto.ItemCompraRequestDto
import br.com.contasdomesticas.app.data.remote.dto.LancamentoDto
import br.com.contasdomesticas.app.data.remote.dto.ListaCompraDto
import br.com.contasdomesticas.app.data.remote.dto.ListaCompraRequestDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface CompraApi {

    @GET("api/v1/listas-compra")
    suspend fun listar(@Query("status") status: String? = null): List<ListaCompraDto>

    @POST("api/v1/listas-compra")
    suspend fun criar(@Body request: ListaCompraRequestDto): ListaCompraDto

    @POST("api/v1/listas-compra/{id}/duplicar")
    suspend fun duplicar(@Path("id") id: Long): ListaCompraDto

    @POST("api/v1/listas-compra/{id}/fechar")
    suspend fun fechar(@Path("id") id: Long, @Body request: FecharListaRequestDto): List<LancamentoDto>

    @DELETE("api/v1/listas-compra/{id}")
    suspend fun remover(@Path("id") id: Long)

    @GET("api/v1/listas-compra/{listaId}/itens")
    suspend fun itens(@Path("listaId") listaId: Long): List<ItemCompraDto>

    @POST("api/v1/listas-compra/{listaId}/itens")
    suspend fun adicionarItem(@Path("listaId") listaId: Long, @Body request: ItemCompraRequestDto): ItemCompraDto

    @POST("api/v1/listas-compra/{listaId}/repor-estoque")
    suspend fun reporEstoque(@Path("listaId") listaId: Long): List<ItemCompraDto>

    @PUT("api/v1/itens/{id}/escolha")
    suspend fun escolher(@Path("id") id: Long, @Body request: EscolhaEstabelecimentoRequestDto): ItemCompraDto

    @DELETE("api/v1/itens/{id}")
    suspend fun removerItem(@Path("id") id: Long)

    @GET("api/v1/produtos/{id}/cotacoes")
    suspend fun cotacoes(@Path("id") produtoId: Long): List<CotacaoProdutoDto>

    @POST("api/v1/produtos/{id}/cotacoes")
    suspend fun adicionarCotacao(@Path("id") produtoId: Long, @Body request: CotacaoProdutoRequestDto): CotacaoProdutoDto
}
