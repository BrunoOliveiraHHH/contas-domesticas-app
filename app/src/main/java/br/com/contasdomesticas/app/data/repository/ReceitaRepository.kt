package br.com.contasdomesticas.app.data.repository

import br.com.contasdomesticas.app.data.remote.ReceitaApi
import br.com.contasdomesticas.app.data.remote.dto.LancamentoDto
import br.com.contasdomesticas.app.data.remote.dto.ReceitaRequestDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReceitaRepository @Inject constructor(
    private val api: ReceitaApi
) {
    suspend fun listar(): List<LancamentoDto> = api.listar()
    suspend fun criar(request: ReceitaRequestDto): LancamentoDto = api.criar(request)
    suspend fun remover(id: Long) = api.remover(id)
}
