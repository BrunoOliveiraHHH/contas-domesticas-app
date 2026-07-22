package br.com.contasdomesticas.app.data.repository

import br.com.contasdomesticas.app.data.local.db.LancamentoDao
import br.com.contasdomesticas.app.data.local.db.OutboxDao
import br.com.contasdomesticas.app.data.local.db.OutboxOp
import br.com.contasdomesticas.app.data.local.db.toDto
import br.com.contasdomesticas.app.data.local.db.toEntity
import br.com.contasdomesticas.app.data.remote.DespesaApi
import br.com.contasdomesticas.app.data.remote.dto.DespesaRequestDto
import br.com.contasdomesticas.app.data.remote.dto.LancamentoDto
import br.com.contasdomesticas.app.data.remote.dto.ParcelamentoRequestDto
import br.com.contasdomesticas.app.data.remote.dto.RateioDto
import br.com.contasdomesticas.app.data.remote.dto.RateioRequestDto
import com.squareup.moshi.Moshi
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private const val TIPO = "DESPESA"

/** Offline-first com CRUD (criar/pagar/remover). Parcelar/ratear exigem conexao. */
@Singleton
class DespesaRepository @Inject constructor(
    private val api: DespesaApi,
    private val dao: LancamentoDao,
    private val outbox: OutboxDao,
    moshi: Moshi
) {
    private val adapter = moshi.adapter(DespesaRequestDto::class.java)

    suspend fun listar(): List<LancamentoDto> = try {
        val remoto = api.listar()
        dao.substituirPorTipo(TIPO, remoto.map { it.toEntity() })
        remoto
    } catch (e: Exception) {
        dao.listarPorTipo(TIPO).map { it.toDto() }
    }

    suspend fun criar(request: DespesaRequestDto): LancamentoDto = try {
        api.criar(request).also { dao.upsert(it.toEntity()) }
    } catch (e: IOException) {
        val tempId = (dao.minId() ?: 0L).coerceAtMost(0L) - 1
        val local = LancamentoDto(
            id = tempId,
            tipo = TIPO,
            descricao = request.descricao,
            valor = request.valor,
            dataCompetencia = request.dataCompetencia,
            dataVencimento = request.dataVencimento,
            dataPagamento = null,
            status = "PENDENTE",
            carteiraId = request.carteiraId,
            categoriaId = request.categoriaId
        )
        dao.upsert(local.toEntity())
        outbox.enfileirar(OutboxOp(entidade = TIPO, operacao = "CREATE", alvoId = tempId, payload = adapter.toJson(request), criadoEm = System.currentTimeMillis()))
        local
    }

    suspend fun pagar(id: Long): LancamentoDto = try {
        api.pagar(id).also { dao.upsert(it.toEntity()) }
    } catch (e: IOException) {
        // otimista: marca pago localmente e enfileira (so faz sentido para itens ja sincronizados)
        val atual = dao.porId(id)?.toDto()
        val pago = atual?.copy(status = "PAGO")
        if (pago != null) dao.upsert(pago.toEntity())
        if (id > 0) outbox.enfileirar(OutboxOp(entidade = TIPO, operacao = "PAGAR", alvoId = id, payload = null, criadoEm = System.currentTimeMillis()))
        pago ?: throw e
    }

    /** Requer conexao (gera N parcelas no servidor). */
    suspend fun parcelar(request: ParcelamentoRequestDto): List<LancamentoDto> =
        api.parcelar(request).also { dao.inserir(it.map { d -> d.toEntity() }) }

    /** Requer conexao. */
    suspend fun ratear(id: Long, request: RateioRequestDto): RateioDto = api.ratear(id, request)

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
