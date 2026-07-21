package br.com.contasdomesticas.app.data.remote.dto

/**
 * Registro sincronizavel de Mercado (entidade de referencia da sincronizacao).
 * versao/deletado dirigem o merge; atualizadoEm e o carimbo do servidor (delta).
 */
data class SyncMercadoDto(
    val uuid: String,
    val nome: String?,
    val tipo: String?,
    val endereco: String?,
    val bairro: String?,
    val ativo: Boolean?,
    val versao: Long?,
    val deletado: Boolean = false,
    val atualizadoEm: String?
)
