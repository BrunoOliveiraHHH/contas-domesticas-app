package br.com.contasdomesticas.app.data.remote.dto

data class MercadoDto(
    val id: Long,
    val nome: String,
    val tipo: String,
    val bairro: String?,
    val ativo: Boolean
)

data class MercadoRequestDto(
    val nome: String? = null,
    val tipo: String? = null,
    val endereco: String? = null,
    val bairro: String? = null
)
