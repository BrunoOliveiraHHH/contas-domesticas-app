package br.com.contasdomesticas.app.data.sync

import br.com.contasdomesticas.app.data.local.db.CarteiraDao
import br.com.contasdomesticas.app.data.local.db.CategoriaDao
import br.com.contasdomesticas.app.data.local.db.LancamentoDao
import br.com.contasdomesticas.app.data.local.db.ListaCompraDao
import br.com.contasdomesticas.app.data.local.db.OutboxDao
import br.com.contasdomesticas.app.data.local.db.toEntity
import br.com.contasdomesticas.app.data.remote.CarteiraApi
import br.com.contasdomesticas.app.data.remote.CategoriaApi
import br.com.contasdomesticas.app.data.remote.CompraApi
import br.com.contasdomesticas.app.data.remote.DespesaApi
import br.com.contasdomesticas.app.data.remote.ReceitaApi
import br.com.contasdomesticas.app.data.remote.dto.DespesaRequestDto
import br.com.contasdomesticas.app.data.remote.dto.ListaCompraRequestDto
import br.com.contasdomesticas.app.data.remote.dto.ReceitaRequestDto
import com.squareup.moshi.Moshi
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Sincroniza o cache local (Room) com a API:
 * 1) reproduz as operacoes offline pendentes (outbox) na API;
 * 2) rebaixa os dados do servidor para o cache.
 */
@Singleton
class SyncManager @Inject constructor(
    private val carteiraApi: CarteiraApi,
    private val categoriaApi: CategoriaApi,
    private val receitaApi: ReceitaApi,
    private val despesaApi: DespesaApi,
    private val compraApi: CompraApi,
    private val carteiraDao: CarteiraDao,
    private val categoriaDao: CategoriaDao,
    private val lancamentoDao: LancamentoDao,
    private val listaDao: ListaCompraDao,
    private val outboxDao: OutboxDao,
    moshi: Moshi
) {
    private val receitaAdapter = moshi.adapter(ReceitaRequestDto::class.java)
    private val despesaAdapter = moshi.adapter(DespesaRequestDto::class.java)
    private val listaAdapter = moshi.adapter(ListaCompraRequestDto::class.java)

    /** True se ainda ha operacoes pendentes (offline). */
    suspend fun pendentes(): Int = outboxDao.quantidade()

    suspend fun sincronizar() {
        flush()
        rebaixar()
    }

    /** Reproduz a fila offline na API, em ordem. Para no primeiro erro de rede. */
    private suspend fun flush() {
        for (op in outboxDao.pendentes()) {
            try {
                when ("${op.entidade}:${op.operacao}") {
                    "RECEITA:CREATE" -> {
                        val req = receitaAdapter.fromJson(op.payload!!)!!
                        val dto = receitaApi.criar(req)
                        lancamentoDao.remover(op.alvoId)
                        lancamentoDao.upsert(dto.toEntity())
                    }
                    "RECEITA:DELETE" -> {
                        receitaApi.remover(op.alvoId)
                        lancamentoDao.remover(op.alvoId)
                    }
                    "DESPESA:CREATE" -> {
                        val req = despesaAdapter.fromJson(op.payload!!)!!
                        val dto = despesaApi.criar(req)
                        lancamentoDao.remover(op.alvoId)
                        lancamentoDao.upsert(dto.toEntity())
                    }
                    "DESPESA:DELETE" -> {
                        despesaApi.remover(op.alvoId)
                        lancamentoDao.remover(op.alvoId)
                    }
                    "DESPESA:PAGAR" -> {
                        val dto = despesaApi.pagar(op.alvoId)
                        lancamentoDao.upsert(dto.toEntity())
                    }
                    "LISTA_COMPRA:CREATE" -> {
                        val req = listaAdapter.fromJson(op.payload!!)!!
                        val dto = compraApi.criar(req)
                        listaDao.remover(op.alvoId)
                        listaDao.upsert(dto.toEntity())
                    }
                    "LISTA_COMPRA:DELETE" -> {
                        compraApi.remover(op.alvoId)
                        listaDao.remover(op.alvoId)
                    }
                }
                outboxDao.remover(op.opId)
            } catch (e: IOException) {
                // ainda offline — mantem a fila e para
                break
            } catch (e: Exception) {
                // op invalida (ex.: 4xx do servidor) — descarta para nao travar a fila
                outboxDao.remover(op.opId)
            }
        }
    }

    /** Rebaixa os dados do servidor para o cache (best-effort). */
    private suspend fun rebaixar() {
        runCatching { carteiraDao.substituirTudo(carteiraApi.listar().map { it.toEntity() }) }
        runCatching { categoriaDao.substituirTudo(categoriaApi.listar().map { it.toEntity() }) }
        runCatching { lancamentoDao.substituirPorTipo("RECEITA", receitaApi.listar().map { it.toEntity() }) }
        runCatching { lancamentoDao.substituirPorTipo("DESPESA", despesaApi.listar().map { it.toEntity() }) }
        runCatching { listaDao.substituirTudo(compraApi.listar(null).map { it.toEntity() }) }
    }
}
