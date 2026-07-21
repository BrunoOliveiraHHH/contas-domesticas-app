package br.com.contasdomesticas.app.data.remote.dto

data class InvestimentoDto(
    val id: Long,
    val nome: String,
    val tipoInvestimento: String,
    val instituicao: String?,
    val carteiraId: Long,
    val indexador: String?,
    val taxaContratada: Double?,
    val dataAplicacao: String,
    val dataVencimento: String?
)

data class InvestimentoRequestDto(
    val nome: String,
    val tipoInvestimento: String,
    val instituicao: String? = null,
    val carteiraId: Long,
    val indexador: String? = null,
    val taxaContratada: Double? = null,
    val dataAplicacao: String,
    val dataVencimento: String? = null
)

data class SaldoInvestimentoDto(
    val investimentoId: Long,
    val nome: String,
    val saldoAplicado: Double
)

data class PatrimonioDto(
    val total: Double
)

data class AporteDto(
    val id: Long,
    val investimentoId: Long,
    val valor: Double,
    val data: String,
    val tipo: String
)

data class AporteRequestDto(
    val valor: Double,
    val data: String,
    val tipo: String
)
