package br.com.contasdomesticas.app.data.remote.dto

data class CarteiraDto(
    val id: Long,
    val nome: String,
    val tipo: String,
    val donoId: Long?,
    val saldoInicial: Double?,
    val moeda: String?,
    val ativa: Boolean
)

data class CarteiraRequestDto(
    val nome: String,
    val tipo: String,
    val donoId: Long? = null,
    val saldoInicial: Double? = null
)
