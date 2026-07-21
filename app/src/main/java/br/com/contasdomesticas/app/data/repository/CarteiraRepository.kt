package br.com.contasdomesticas.app.data.repository

import br.com.contasdomesticas.app.data.remote.CarteiraApi
import br.com.contasdomesticas.app.data.remote.dto.CarteiraDto
import br.com.contasdomesticas.app.data.remote.dto.CarteiraRequestDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CarteiraRepository @Inject constructor(
    private val carteiraApi: CarteiraApi
) {
    suspend fun listar(): List<CarteiraDto> = carteiraApi.listar()
    suspend fun criar(request: CarteiraRequestDto): CarteiraDto = carteiraApi.criar(request)
    suspend fun atualizar(id: Long, request: CarteiraRequestDto): CarteiraDto = carteiraApi.atualizar(id, request)
    suspend fun remover(id: Long) = carteiraApi.remover(id)
}
