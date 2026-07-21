package br.com.contasdomesticas.app.data.repository

import br.com.contasdomesticas.app.data.remote.RateioApi
import br.com.contasdomesticas.app.data.remote.dto.AcertoItemDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RateioRepository @Inject constructor(
    private val api: RateioApi
) {
    suspend fun acerto(periodo: String): List<AcertoItemDto> = api.acerto(periodo)
}
