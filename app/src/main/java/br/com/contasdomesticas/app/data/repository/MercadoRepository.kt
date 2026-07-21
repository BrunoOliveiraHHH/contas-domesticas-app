package br.com.contasdomesticas.app.data.repository

import br.com.contasdomesticas.app.data.remote.MercadoApi
import br.com.contasdomesticas.app.data.remote.dto.MercadoDto
import br.com.contasdomesticas.app.data.remote.dto.MercadoRequestDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MercadoRepository @Inject constructor(
    private val api: MercadoApi
) {
    suspend fun listar(): List<MercadoDto> = api.listar()
    suspend fun criar(request: MercadoRequestDto): MercadoDto = api.criar(request)
    suspend fun remover(id: Long) = api.remover(id)
}
