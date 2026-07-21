package br.com.contasdomesticas.app.data.repository

import br.com.contasdomesticas.app.data.remote.RelatorioApi
import br.com.contasdomesticas.app.data.remote.dto.PorCategoriaItemDto
import br.com.contasdomesticas.app.data.remote.dto.SaldoMesDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RelatorioRepository @Inject constructor(
    private val api: RelatorioApi
) {
    suspend fun saldo(periodo: String): SaldoMesDto = api.saldo(periodo)
    suspend fun despesasPorCategoria(periodo: String): List<PorCategoriaItemDto> =
        api.porCategoria(periodo, "DESPESA")
}
