package br.com.contasdomesticas.app.data.remote.dto

data class FormaPagamentoDto(
    val id: Long,
    val nome: String,
    val tipo: String,
    val ativa: Boolean
)

data class FormaPagamentoRequestDto(
    val nome: String? = null,
    val tipo: String? = null,
    val diaFechamento: Int? = null,
    val diaVencimento: Int? = null
)
