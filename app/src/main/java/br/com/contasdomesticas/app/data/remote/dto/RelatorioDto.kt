package br.com.contasdomesticas.app.data.remote.dto

data class SaldoMesDto(
    val periodo: String,
    val receitas: Double,
    val despesas: Double,
    val saldo: Double,
    val aPagar: Double = 0.0,
    val atrasadas: Double = 0.0,
    val assinaturas: Double = 0.0
)

data class PorCategoriaItemDto(
    val categoriaId: Long,
    val categoriaNome: String,
    val total: Double,
    val percentual: Double
)
