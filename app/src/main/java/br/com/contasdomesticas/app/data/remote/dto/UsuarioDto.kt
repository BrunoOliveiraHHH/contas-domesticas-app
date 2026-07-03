package br.com.contasdomesticas.app.data.remote.dto

/**
 * Representacao do usuario retornada pela API.
 */
data class UsuarioDto(
    val id: Long,
    val login: String,
    val nomeExibicao: String,
    val criadoEm: String? = null,
    val criadoPor: String? = null,
    val atualizadoEm: String? = null,
    val atualizadoPor: String? = null
)

/**
 * Payload de criacao/atualizacao de usuario enviado a API.
 */
data class UsuarioRequestDto(
    val login: String,
    val nomeExibicao: String,
    val senha: String
)
