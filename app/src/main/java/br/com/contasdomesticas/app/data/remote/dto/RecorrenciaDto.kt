package br.com.contasdomesticas.app.data.remote.dto

data class RecorrenciaDto(
    val id: Long,
    val descricao: String,
    val valor: Double,
    val tipo: String,
    val carteiraId: Long,
    val categoriaId: Long,
    val formaPagamentoId: Long?,
    val frequencia: String,
    val diaVencimento: Int?,
    val dataInicio: String,
    val dataFim: String?,
    val ativa: Boolean
)

data class RecorrenciaRequestDto(
    val descricao: String,
    val valor: Double,
    val tipo: String,
    val carteiraId: Long,
    val categoriaId: Long,
    val formaPagamentoId: Long? = null,
    val frequencia: String,
    val diaVencimento: Int? = null,
    val dataInicio: String,
    val dataFim: String? = null,
    val ativa: Boolean? = null
)
