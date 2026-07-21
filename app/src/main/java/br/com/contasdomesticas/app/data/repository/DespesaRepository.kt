package br.com.contasdomesticas.app.data.repository

import br.com.contasdomesticas.app.data.remote.DespesaApi
import br.com.contasdomesticas.app.data.remote.dto.DespesaRequestDto
import br.com.contasdomesticas.app.data.remote.dto.LancamentoDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DespesaRepository @Inject constructor(
    private val api: DespesaApi
) {
    suspend fun listar(): List<LancamentoDto> = api.listar()
    suspend fun criar(request: DespesaRequestDto): LancamentoDto = api.criar(request)
    suspend fun pagar(id: Long): LancamentoDto = api.pagar(id)
    suspend fun remover(id: Long) = api.remover(id)
}
