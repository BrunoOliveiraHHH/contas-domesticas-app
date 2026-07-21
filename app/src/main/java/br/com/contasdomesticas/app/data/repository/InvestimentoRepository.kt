package br.com.contasdomesticas.app.data.repository

import br.com.contasdomesticas.app.data.remote.InvestimentoApi
import br.com.contasdomesticas.app.data.remote.dto.AporteDto
import br.com.contasdomesticas.app.data.remote.dto.AporteRequestDto
import br.com.contasdomesticas.app.data.remote.dto.InvestimentoDto
import br.com.contasdomesticas.app.data.remote.dto.InvestimentoRequestDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InvestimentoRepository @Inject constructor(
    private val api: InvestimentoApi
) {
    suspend fun listar(): List<InvestimentoDto> = api.listar()
    suspend fun patrimonio(): Double = api.patrimonio().total
    suspend fun criar(request: InvestimentoRequestDto): InvestimentoDto = api.criar(request)
    suspend fun remover(id: Long) = api.remover(id)
    suspend fun aportes(id: Long): List<AporteDto> = api.aportes(id)
    suspend fun adicionarAporte(id: Long, request: AporteRequestDto): AporteDto =
        api.adicionarAporte(id, request)
}
