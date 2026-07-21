package br.com.contasdomesticas.app.data.repository

import br.com.contasdomesticas.app.data.remote.FormaPagamentoApi
import br.com.contasdomesticas.app.data.remote.dto.FormaPagamentoDto
import br.com.contasdomesticas.app.data.remote.dto.FormaPagamentoRequestDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FormaPagamentoRepository @Inject constructor(
    private val api: FormaPagamentoApi
) {
    suspend fun listar(): List<FormaPagamentoDto> = api.listar()
    suspend fun criar(request: FormaPagamentoRequestDto): FormaPagamentoDto = api.criar(request)
    suspend fun remover(id: Long) = api.remover(id)
}
