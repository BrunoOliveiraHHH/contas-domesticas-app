package br.com.contasdomesticas.app.data.remote.dto

data class ProdutoDto(
    val id: Long,
    val nome: String,
    val descricao: String?,
    val ativo: Boolean
)

data class ProdutoRequestDto(
    val nome: String? = null,
    val descricao: String? = null,
    val codigoBarras: String? = null
)
