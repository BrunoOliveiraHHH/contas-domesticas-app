package br.com.contasdomesticas.app.data.repository

import br.com.contasdomesticas.app.data.local.db.LancamentoDao
import br.com.contasdomesticas.app.data.local.db.OutboxDao
import br.com.contasdomesticas.app.data.local.db.OutboxOp
import br.com.contasdomesticas.app.data.local.db.toDto
import br.com.contasdomesticas.app.data.local.db.toEntity
import br.com.contasdomesticas.app.data.remote.ReceitaApi
import br.com.contasdomesticas.app.data.remote.dto.LancamentoDto
import br.com.contasdomesticas.app.data.remote.dto.ReceitaRequestDto
import com.squareup.moshi.Moshi
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private const val TIPO = "RECEITA"

/** Offline-first com CRUD: le/escreve no Room; quando offline, enfileira no outbox. */
@Singleton
class ReceitaRepository @Inject constructor(
    private val api: ReceitaApi,
    private val dao: LancamentoDao,
    private val outbox: OutboxDao,
    moshi: Moshi
) {
    private val adapter = moshi.adapter(ReceitaRequestDto::class.java)

    suspend fun listar(): List<LancamentoDto> = try {
        val remoto = api.listar()
        dao.substituirPorTipo(TIPO, remoto.map { it.toEntity() })
        remoto
    } catch (e: Exception) {
        dao.listarPorTipo(TIPO).map { it.toDto() }
    }

    suspend fun criar(request: ReceitaRequestDto): LancamentoDto = try {
        api.criar(request).also { dao.upsert(it.toEntity()) }
    } catch (e: IOException) {
        val tempId = (dao.minId() ?: 0L).coerceAtMost(0L) - 1
        val local = LancamentoDto(
            id = tempId,
            tipo = TIPO,
            descricao = request.descricao,
            valor = request.valor,
            dataCompetencia = request.dataCompetencia,
            dataVencimento = null,
            dataPagamento = null,
            dataInicio = request.dataInicio,
            dataFim = request.dataFim,
            status = "PENDENTE",
            carteiraId = request.carteiraId,
            categoriaId = request.categoriaId
        )
        dao.upsert(local.toEntity())
        outbox.enfileirar(OutboxOp(entidade = TIPO, operacao = "CREATE", alvoId = tempId, payload = adapter.toJson(request), criadoEm = System.currentTimeMillis()))
        local
    }

    suspend fun remover(id: Long) {
        try {
            api.remover(id)
            dao.remover(id)
        } catch (e: IOException) {
            dao.remover(id)
            if (id < 0) outbox.removerCreatePendente(TIPO, id)
            else outbox.enfileirar(OutboxOp(entidade = TIPO, operacao = "DELETE", alvoId = id, payload = null, criadoEm = System.currentTimeMillis()))
        }
    }
}
