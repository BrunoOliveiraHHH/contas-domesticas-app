package br.com.contasdomesticas.app.data.local.db

import br.com.contasdomesticas.app.data.remote.dto.CarteiraDto
import br.com.contasdomesticas.app.data.remote.dto.CategoriaDto
import br.com.contasdomesticas.app.data.remote.dto.LancamentoDto

fun CarteiraDto.toEntity() = CarteiraEntity(id, nome, tipo, donoId, saldoInicial, moeda, ativa)
fun CarteiraEntity.toDto() = CarteiraDto(id, nome, tipo, donoId, saldoInicial, moeda, ativa)

fun CategoriaDto.toEntity() = CategoriaEntity(id, nome, tipo, ativa)
fun CategoriaEntity.toDto() = CategoriaDto(id, nome, tipo, ativa)

fun LancamentoDto.toEntity() = LancamentoEntity(
    id, tipo, descricao, valor, dataCompetencia, dataVencimento, dataPagamento,
    dataInicio, dataFim, status, carteiraId, categoriaId
)
fun LancamentoEntity.toDto() = LancamentoDto(
    id, tipo, descricao, valor, dataCompetencia, dataVencimento, dataPagamento,
    dataInicio, dataFim, status, carteiraId, categoriaId
)
