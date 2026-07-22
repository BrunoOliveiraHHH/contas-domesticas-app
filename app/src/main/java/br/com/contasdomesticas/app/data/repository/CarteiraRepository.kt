package br.com.contasdomesticas.app.data.repository

import br.com.contasdomesticas.app.data.local.db.CarteiraDao
import br.com.contasdomesticas.app.data.local.db.toDto
import br.com.contasdomesticas.app.data.local.db.toEntity
import br.com.contasdomesticas.app.data.remote.CarteiraApi
import br.com.contasdomesticas.app.data.remote.dto.CarteiraDto
import br.com.contasdomesticas.app.data.remote.dto.CarteiraRequestDto
import javax.inject.Inject
import javax.inject.Singleton

/** Offline-first: le da API e cacheia no Room; se offline, retorna o cache. */
@Singleton
class CarteiraRepository @Inject constructor(
    private val carteiraApi: CarteiraApi,
    private val dao: CarteiraDao
) {
    suspend fun listar(): List<CarteiraDto> = try {
        val remoto = carteiraApi.listar()
        dao.substituirTudo(remoto.map { it.toEntity() })
        remoto
    } catch (e: Exception) {
        dao.listar().map { it.toDto() }
    }

    suspend fun criar(request: CarteiraRequestDto): CarteiraDto =
        carteiraApi.criar(request).also { dao.upsert(it.toEntity()) }

    suspend fun atualizar(id: Long, request: CarteiraRequestDto): CarteiraDto =
        carteiraApi.atualizar(id, request).also { dao.upsert(it.toEntity()) }

    suspend fun remover(id: Long) {
        carteiraApi.remover(id)
        dao.remover(id)
    }

    /** Baixa da API para o cache (sincronizacao). */
    suspend fun sincronizar() {
        dao.substituirTudo(carteiraApi.listar().map { it.toEntity() })
    }
}
