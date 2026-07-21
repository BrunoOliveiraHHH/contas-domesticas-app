package br.com.contasdomesticas.app.data.repository

import br.com.contasdomesticas.app.data.remote.CompraApi
import br.com.contasdomesticas.app.data.remote.dto.CotacaoProdutoDto
import br.com.contasdomesticas.app.data.remote.dto.CotacaoProdutoRequestDto
import br.com.contasdomesticas.app.data.remote.dto.EscolhaEstabelecimentoRequestDto
import br.com.contasdomesticas.app.data.remote.dto.FecharListaRequestDto
import br.com.contasdomesticas.app.data.remote.dto.ItemCompraDto
import br.com.contasdomesticas.app.data.remote.dto.ItemCompraRequestDto
import br.com.contasdomesticas.app.data.remote.dto.LancamentoDto
import br.com.contasdomesticas.app.data.remote.dto.ListaCompraDto
import br.com.contasdomesticas.app.data.remote.dto.ListaCompraRequestDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CompraRepository @Inject constructor(
    private val api: CompraApi
) {
    suspend fun listar(status: String? = null): List<ListaCompraDto> = api.listar(status)
    suspend fun criar(request: ListaCompraRequestDto): ListaCompraDto = api.criar(request)
    suspend fun duplicar(id: Long): ListaCompraDto = api.duplicar(id)
    suspend fun fechar(id: Long, categoriaId: Long): List<LancamentoDto> =
        api.fechar(id, FecharListaRequestDto(categoriaId))
    suspend fun remover(id: Long) = api.remover(id)

    suspend fun itens(listaId: Long): List<ItemCompraDto> = api.itens(listaId)
    suspend fun adicionarItem(listaId: Long, request: ItemCompraRequestDto): ItemCompraDto =
        api.adicionarItem(listaId, request)
    suspend fun escolher(itemId: Long, mercadoId: Long): ItemCompraDto =
        api.escolher(itemId, EscolhaEstabelecimentoRequestDto(mercadoId))
    suspend fun removerItem(itemId: Long) = api.removerItem(itemId)

    suspend fun cotacoes(produtoId: Long): List<CotacaoProdutoDto> = api.cotacoes(produtoId)
    suspend fun adicionarCotacao(produtoId: Long, request: CotacaoProdutoRequestDto): CotacaoProdutoDto =
        api.adicionarCotacao(produtoId, request)
}
