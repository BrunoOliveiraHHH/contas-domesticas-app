package br.com.contasdomesticas.app.data.repository

import br.com.contasdomesticas.app.data.remote.ProdutoApi
import br.com.contasdomesticas.app.data.remote.dto.ProdutoDto
import br.com.contasdomesticas.app.data.remote.dto.ProdutoRequestDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProdutoRepository @Inject constructor(
    private val api: ProdutoApi
) {
    suspend fun listar(): List<ProdutoDto> = api.listar()
    suspend fun criar(request: ProdutoRequestDto): ProdutoDto = api.criar(request)
    suspend fun remover(id: Long) = api.remover(id)
}
