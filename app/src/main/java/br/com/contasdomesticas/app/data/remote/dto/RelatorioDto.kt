package br.com.contasdomesticas.app.data.remote.dto

data class SaldoMesDto(
    val periodo: String,
    val receitas: Double,
    val despesas: Double,
    val saldo: Double
)

data class PorCategoriaItemDto(
    val categoriaId: Long,
    val categoriaNome: String,
    val total: Double,
    val percentual: Double
)
