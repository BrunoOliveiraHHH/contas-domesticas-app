package br.com.contasdomesticas.app.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Entidades Room (cache local do app). Espelham os DTOs da API. */

@Entity(tableName = "carteira")
data class CarteiraEntity(
    @PrimaryKey val id: Long,
    val nome: String,
    val tipo: String,
    val donoId: Long?,
    val saldoInicial: Double?,
    val moeda: String?,
    val ativa: Boolean
)

@Entity(tableName = "categoria")
data class CategoriaEntity(
    @PrimaryKey val id: Long,
    val nome: String,
    val tipo: String,
    val ativa: Boolean
)

@Entity(tableName = "lancamento")
data class LancamentoEntity(
    @PrimaryKey val id: Long,
    val tipo: String,
    val descricao: String,
    val valor: Double,
    val dataCompetencia: String,
    val dataVencimento: String?,
    val dataPagamento: String?,
    val dataInicio: String?,
    val dataFim: String?,
    val status: String?,
    val carteiraId: Long,
    val categoriaId: Long
)
