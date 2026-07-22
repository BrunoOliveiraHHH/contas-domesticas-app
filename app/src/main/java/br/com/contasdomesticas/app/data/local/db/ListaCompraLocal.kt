package br.com.contasdomesticas.app.data.local.db

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Transaction
import br.com.contasdomesticas.app.data.remote.dto.ListaCompraDto

@Entity(tableName = "lista_compra")
data class ListaCompraEntity(
    @PrimaryKey val id: Long,
    val nome: String,
    val tipo: String,
    val carteiraId: Long,
    val data: String?,
    val status: String
)

fun ListaCompraDto.toEntity() = ListaCompraEntity(id, nome, tipo, carteiraId, data, status)
fun ListaCompraEntity.toDto() = ListaCompraDto(id, nome, tipo, carteiraId, data, status)

@Dao
interface ListaCompraDao {
    @Query("SELECT * FROM lista_compra ORDER BY id DESC")
    suspend fun listar(): List<ListaCompraEntity>

    @Query("SELECT MIN(id) FROM lista_compra")
    suspend fun minId(): Long?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(itens: List<ListaCompraEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: ListaCompraEntity)

    @Query("DELETE FROM lista_compra WHERE id = :id")
    suspend fun remover(id: Long)

    @Query("DELETE FROM lista_compra")
    suspend fun limpar()

    @Transaction
    suspend fun substituirTudo(itens: List<ListaCompraEntity>) {
        limpar()
        inserir(itens)
    }
}
