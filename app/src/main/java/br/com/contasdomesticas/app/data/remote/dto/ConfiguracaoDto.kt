package br.com.contasdomesticas.app.data.remote.dto

data class ParametroDto(
    val id: Long,
    val chave: String,
    val valor: Double,
    val vigenciaInicio: String,
    val descricao: String?
)

data class ParametroRequestDto(
    val chave: String,
    val valor: Double,
    val vigenciaInicio: String,
    val descricao: String? = null
)

data class PreferenciaDto(
    val id: Long?,
    val chave: String,
    val valor: String,
    val usuarioId: Long?
)

data class PreferenciaRequestDto(
    val valor: String,
    val usuarioId: Long? = null
)
