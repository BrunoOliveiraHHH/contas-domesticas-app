package br.com.contasdomesticas.app.data.repository

import br.com.contasdomesticas.app.data.remote.CategoriaApi
import br.com.contasdomesticas.app.data.remote.dto.CategoriaDto
import br.com.contasdomesticas.app.data.remote.dto.CategoriaRequestDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoriaRepository @Inject constructor(
    private val api: CategoriaApi
) {
    suspend fun listar(): List<CategoriaDto> = api.listar()
    suspend fun criar(request: CategoriaRequestDto): CategoriaDto = api.criar(request)
    suspend fun remover(id: Long) = api.remover(id)
}
