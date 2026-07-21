package br.com.contasdomesticas.app.data.remote.dto

data class ParcelamentoRequestDto(
    val descricao: String,
    val valorTotal: Double,
    val parcelas: Int,
    val primeiroVencimento: String,
    val carteiraId: Long,
    val categoriaId: Long,
    val formaPagamentoId: Long? = null
)

data class RateioParticipanteRequestDto(
    val usuarioId: Long,
    val percentual: Double? = null
)

data class RateioRequestDto(
    val tipo: String,
    val participantes: List<RateioParticipanteRequestDto>
)

data class RateioParticipanteDto(
    val usuarioId: Long,
    val usuarioLogin: String?,
    val percentual: Double?,
    val valor: Double?
)

data class RateioDto(
    val id: Long,
    val lancamentoId: Long,
    val tipo: String,
    val participantes: List<RateioParticipanteDto>
)

data class AcertoItemDto(
    val usuarioId: Long,
    val usuarioLogin: String?,
    val total: Double
)
