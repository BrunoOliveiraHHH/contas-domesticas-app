package br.com.contasdomesticas.app.data.local.db

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query

/**
 * Fila de operacoes pendentes (escrita offline). Cada op e reproduzida na API
 * quando houver conexao (SyncManager) e depois removida.
 *
 * entidade: RECEITA | DESPESA | LISTA_COMPRA
 * operacao: CREATE | DELETE | PAGAR
 * alvoId:   id local temporario (CREATE, negativo) ou id do servidor (DELETE/PAGAR)
 * payload:  JSON do request (apenas CREATE)
 */
@Entity(tableName = "outbox")
data class OutboxOp(
    @PrimaryKey(autoGenerate = true) val opId: Long = 0,
    val entidade: String,
    val operacao: String,
    val alvoId: Long,
    val payload: String?,
    val criadoEm: Long
)

@Dao
interface OutboxDao {
    @Insert
    suspend fun enfileirar(op: OutboxOp): Long

    @Query("SELECT * FROM outbox ORDER BY opId ASC")
    suspend fun pendentes(): List<OutboxOp>

    @Query("SELECT COUNT(*) FROM outbox")
    suspend fun quantidade(): Int

    @Query("DELETE FROM outbox WHERE opId = :opId")
    suspend fun remover(opId: Long)

    // Remove uma op de CREATE ainda nao sincronizada (quando o item local temporario e apagado).
    @Query("DELETE FROM outbox WHERE entidade = :entidade AND operacao = 'CREATE' AND alvoId = :alvoId")
    suspend fun removerCreatePendente(entidade: String, alvoId: Long)
}
