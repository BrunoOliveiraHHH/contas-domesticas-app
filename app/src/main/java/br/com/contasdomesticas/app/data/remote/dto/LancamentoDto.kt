package br.com.contasdomesticas.app.data.remote.dto

data class LancamentoDto(
    val id: Long,
    val tipo: String,
    val descricao: String,
    val valor: Double,
    val dataCompetencia: String,
    val dataVencimento: String?,
    val dataPagamento: String?,
    val status: String?,
    val carteiraId: Long,
    val categoriaId: Long
)

data class ReceitaRequestDto(
    val descricao: String,
    val valor: Double,
    val dataCompetencia: String,
    val carteiraId: Long,
    val categoriaId: Long
)

data class DespesaRequestDto(
    val descricao: String,
    val valor: Double,
    val dataCompetencia: String,
    val carteiraId: Long,
    val categoriaId: Long,
    val dataVencimento: String? = null
)
