package br.com.contasdomesticas.app.data.repository

import br.com.contasdomesticas.app.data.local.db.CategoriaDao
import br.com.contasdomesticas.app.data.local.db.toDto
import br.com.contasdomesticas.app.data.local.db.toEntity
import br.com.contasdomesticas.app.data.remote.CategoriaApi
import br.com.contasdomesticas.app.data.remote.dto.CategoriaDto
import br.com.contasdomesticas.app.data.remote.dto.CategoriaRequestDto
import javax.inject.Inject
import javax.inject.Singleton

/** Offline-first: le da API e cacheia no Room; se offline, retorna o cache. */
@Singleton
class CategoriaRepository @Inject constructor(
    private val api: CategoriaApi,
    private val dao: CategoriaDao
) {
    suspend fun listar(): List<CategoriaDto> = try {
        val remoto = api.listar()
        dao.substituirTudo(remoto.map { it.toEntity() })
        remoto
    } catch (e: Exception) {
        dao.listar().map { it.toDto() }
    }

    suspend fun criar(request: CategoriaRequestDto): CategoriaDto =
        api.criar(request).also { dao.upsert(it.toEntity()) }

    suspend fun remover(id: Long) {
        api.remover(id)
        dao.remover(id)
    }

    /** Baixa da API para o cache (sincronizacao). */
    suspend fun sincronizar() {
        dao.substituirTudo(api.listar().map { it.toEntity() })
    }
}
