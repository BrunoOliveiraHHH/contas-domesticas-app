package br.com.contasdomesticas.app.data.remote.dto

data class ListaCompraDto(
    val id: Long,
    val nome: String,
    val tipo: String,
    val carteiraId: Long,
    val data: String?,
    val status: String
)

data class ListaCompraRequestDto(
    val nome: String,
    val tipo: String,
    val carteiraId: Long,
    val data: String? = null
)

data class ItemCompraDto(
    val id: Long,
    val listaCompraId: Long,
    val produtoId: Long,
    val produtoNome: String?,
    val quantidade: Double,
    val unidadeMedidaId: Long?,
    val mercadoEscolhidoId: Long?,
    val precoUnitario: Double?,
    val comprado: Boolean
)

data class ItemCompraRequestDto(
    val produtoId: Long,
    val quantidade: Double,
    val unidadeMedidaId: Long? = null
)

data class EscolhaEstabelecimentoRequestDto(
    val mercadoId: Long
)

data class FecharListaRequestDto(
    val categoriaId: Long
)

data class CotacaoProdutoDto(
    val id: Long,
    val produtoId: Long,
    val mercadoId: Long,
    val precoUnitario: Double,
    val data: String?,
    val origem: String
)

data class CotacaoProdutoRequestDto(
    val mercadoId: Long,
    val precoUnitario: Double,
    val data: String? = null
)
