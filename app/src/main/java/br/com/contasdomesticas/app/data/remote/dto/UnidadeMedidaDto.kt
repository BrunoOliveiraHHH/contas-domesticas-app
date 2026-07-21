package br.com.contasdomesticas.app.data.remote.dto

data class UnidadeMedidaDto(
    val id: Long,
    val nome: String,
    val sigla: String,
    val tipo: String
)

data class UnidadeMedidaRequestDto(
    val nome: String? = null,
    val sigla: String? = null,
    val tipo: String? = null,
    val fatorParaBase: Double? = null
)
