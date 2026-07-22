package br.com.contasdomesticas.app.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface CarteiraDao {
    @Query("SELECT * FROM carteira ORDER BY nome")
    suspend fun listar(): List<CarteiraEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(itens: List<CarteiraEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: CarteiraEntity)

    @Query("DELETE FROM carteira WHERE id = :id")
    suspend fun remover(id: Long)

    @Query("DELETE FROM carteira")
    suspend fun limpar()

    @Transaction
    suspend fun substituirTudo(itens: List<CarteiraEntity>) {
        limpar()
        inserir(itens)
    }
}

@Dao
interface CategoriaDao {
    @Query("SELECT * FROM categoria ORDER BY nome")
    suspend fun listar(): List<CategoriaEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(itens: List<CategoriaEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: CategoriaEntity)

    @Query("DELETE FROM categoria WHERE id = :id")
    suspend fun remover(id: Long)

    @Query("DELETE FROM categoria")
    suspend fun limpar()

    @Transaction
    suspend fun substituirTudo(itens: List<CategoriaEntity>) {
        limpar()
        inserir(itens)
    }
}

@Dao
interface LancamentoDao {
    @Query("SELECT * FROM lancamento WHERE tipo = :tipo ORDER BY dataCompetencia DESC")
    suspend fun listarPorTipo(tipo: String): List<LancamentoEntity>

    @Query("SELECT MIN(id) FROM lancamento")
    suspend fun minId(): Long?

    @Query("SELECT * FROM lancamento WHERE id = :id")
    suspend fun porId(id: Long): LancamentoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(itens: List<LancamentoEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: LancamentoEntity)

    @Query("DELETE FROM lancamento WHERE id = :id")
    suspend fun remover(id: Long)

    @Query("DELETE FROM lancamento WHERE tipo = :tipo")
    suspend fun limparPorTipo(tipo: String)

    @Transaction
    suspend fun substituirPorTipo(tipo: String, itens: List<LancamentoEntity>) {
        limparPorTipo(tipo)
        inserir(itens)
    }
}
