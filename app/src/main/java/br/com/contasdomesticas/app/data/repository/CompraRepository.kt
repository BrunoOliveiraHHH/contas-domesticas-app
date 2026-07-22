package br.com.contasdomesticas.app.data.repository

import br.com.contasdomesticas.app.data.local.db.ListaCompraDao
import br.com.contasdomesticas.app.data.local.db.OutboxDao
import br.com.contasdomesticas.app.data.local.db.OutboxOp
import br.com.contasdomesticas.app.data.local.db.toDto
import br.com.contasdomesticas.app.data.local.db.toEntity
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
import com.squareup.moshi.Moshi
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private const val ENTIDADE = "LISTA_COMPRA"

/**
 * Lista de compras: CRUD offline (listar/criar/remover via cache + outbox).
 * Itens, cotacoes, fechar e duplicar exigem conexao (operacoes do servidor).
 */
@Singleton
class CompraRepository @Inject constructor(
    private val api: CompraApi,
    private val dao: ListaCompraDao,
    private val outbox: OutboxDao,
    moshi: Moshi
) {
    private val adapter = moshi.adapter(ListaCompraRequestDto::class.java)

    suspend fun listar(status: String? = null): List<ListaCompraDto> = try {
        val remoto = api.listar(status)
        dao.substituirTudo(remoto.map { it.toEntity() })
        remoto
    } catch (e: Exception) {
        val cache = dao.listar().map { it.toDto() }
        if (status != null) cache.filter { it.status == status } else cache
    }

    suspend fun criar(request: ListaCompraRequestDto): ListaCompraDto = try {
        api.criar(request).also { dao.upsert(it.toEntity()) }
    } catch (e: IOException) {
        val tempId = (dao.minId() ?: 0L).coerceAtMost(0L) - 1
        val local = ListaCompraDto(
            id = tempId,
            nome = request.nome,
            tipo = request.tipo,
            carteiraId = request.carteiraId,
            data = request.data,
            status = "ABERTA"
        )
        dao.upsert(local.toEntity())
        outbox.enfileirar(OutboxOp(entidade = ENTIDADE, operacao = "CREATE", alvoId = tempId, payload = adapter.toJson(request), criadoEm = System.currentTimeMillis()))
        local
    }

    suspend fun remover(id: Long) {
        try {
            api.remover(id)
            dao.remover(id)
        } catch (e: IOException) {
            dao.remover(id)
            if (id < 0) outbox.removerCreatePendente(ENTIDADE, id)
            else outbox.enfileirar(OutboxOp(entidade = ENTIDADE, operacao = "DELETE", alvoId = id, payload = null, criadoEm = System.currentTimeMillis()))
        }
    }

    // --- Operacoes que exigem conexao ---

    suspend fun duplicar(id: Long): ListaCompraDto = api.duplicar(id).also { dao.upsert(it.toEntity()) }

    suspend fun fechar(id: Long, categoriaId: Long): List<LancamentoDto> =
        api.fechar(id, FecharListaRequestDto(categoriaId))

    suspend fun itens(listaId: Long): List<ItemCompraDto> = api.itens(listaId)
    suspend fun adicionarItem(listaId: Long, request: ItemCompraRequestDto): ItemCompraDto =
        api.adicionarItem(listaId, request)
    suspend fun reporEstoque(listaId: Long): List<ItemCompraDto> = api.reporEstoque(listaId)
    suspend fun escolher(itemId: Long, mercadoId: Long): ItemCompraDto =
        api.escolher(itemId, EscolhaEstabelecimentoRequestDto(mercadoId))
    suspend fun removerItem(itemId: Long) = api.removerItem(itemId)

    suspend fun cotacoes(produtoId: Long): List<CotacaoProdutoDto> = api.cotacoes(produtoId)
    suspend fun adicionarCotacao(produtoId: Long, request: CotacaoProdutoRequestDto): CotacaoProdutoDto =
        api.adicionarCotacao(produtoId, request)
}
