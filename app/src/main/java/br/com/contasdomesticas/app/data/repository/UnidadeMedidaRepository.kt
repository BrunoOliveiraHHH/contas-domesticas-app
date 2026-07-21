package br.com.contasdomesticas.app.data.repository

import br.com.contasdomesticas.app.data.remote.UnidadeMedidaApi
import br.com.contasdomesticas.app.data.remote.dto.UnidadeMedidaDto
import br.com.contasdomesticas.app.data.remote.dto.UnidadeMedidaRequestDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UnidadeMedidaRepository @Inject constructor(
    private val api: UnidadeMedidaApi
) {
    suspend fun listar(): List<UnidadeMedidaDto> = api.listar()
    suspend fun criar(request: UnidadeMedidaRequestDto): UnidadeMedidaDto = api.criar(request)
    suspend fun remover(id: Long) = api.remover(id)
}
