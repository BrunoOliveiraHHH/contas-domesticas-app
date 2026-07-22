package br.com.contasdomesticas.app.data.remote.dto

data class ProdutoDto(
    val id: Long,
    val nome: String,
    val descricao: String?,
    val ativo: Boolean,
    val estoqueMinimo: Double? = null,
    val estoqueAtual: Double? = null
)

data class ProdutoRequestDto(
    val nome: String? = null,
    val descricao: String? = null,
    val codigoBarras: String? = null,
    val estoqueMinimo: Double? = null,
    val estoqueAtual: Double? = null
)
