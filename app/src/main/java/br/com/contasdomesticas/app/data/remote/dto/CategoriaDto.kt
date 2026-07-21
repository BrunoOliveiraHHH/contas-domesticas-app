package br.com.contasdomesticas.app.data.remote.dto

data class CategoriaDto(
    val id: Long,
    val nome: String,
    val tipo: String,
    val ativa: Boolean
)

data class CategoriaRequestDto(
    val nome: String? = null,
    val tipo: String? = null
)
