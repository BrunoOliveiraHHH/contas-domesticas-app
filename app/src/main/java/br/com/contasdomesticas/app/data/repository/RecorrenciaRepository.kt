package br.com.contasdomesticas.app.data.repository

import br.com.contasdomesticas.app.data.remote.RecorrenciaApi
import br.com.contasdomesticas.app.data.remote.dto.LancamentoDto
import br.com.contasdomesticas.app.data.remote.dto.RecorrenciaDto
import br.com.contasdomesticas.app.data.remote.dto.RecorrenciaRequestDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecorrenciaRepository @Inject constructor(
    private val api: RecorrenciaApi
) {
    suspend fun listar(): List<RecorrenciaDto> = api.listar()
    suspend fun criar(request: RecorrenciaRequestDto): RecorrenciaDto = api.criar(request)
    suspend fun gerar(id: Long, competencia: String): LancamentoDto = api.gerar(id, competencia)
    suspend fun remover(id: Long) = api.remover(id)
}
