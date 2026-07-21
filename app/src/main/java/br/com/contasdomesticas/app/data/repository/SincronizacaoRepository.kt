package br.com.contasdomesticas.app.data.repository

import br.com.contasdomesticas.app.data.local.TokenStore
import br.com.contasdomesticas.app.data.remote.SyncApi
import br.com.contasdomesticas.app.data.remote.dto.SyncMercadoDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SincronizacaoRepository @Inject constructor(
    private val api: SyncApi,
    private val tokenStore: TokenStore
) {
    suspend fun ultimaSync(): String? = tokenStore.lastSyncMercados()

    /** Puxa o delta desde o ultimo carimbo e avanca a marca de sincronizacao. */
    suspend fun sincronizar(): List<SyncMercadoDto> {
        val desde = tokenStore.lastSyncMercados()
        val registros = api.delta(desde)
        registros.mapNotNull { it.atualizadoEm }.maxOrNull()?.let { tokenStore.salvarLastSync(it) }
        return registros
    }

    suspend fun enviar(registros: List<SyncMercadoDto>): List<SyncMercadoDto> = api.merge(registros)
}
