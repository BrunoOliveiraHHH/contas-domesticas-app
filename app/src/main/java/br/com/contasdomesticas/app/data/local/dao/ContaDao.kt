package br.com.contasdomesticas.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import br.com.contasdomesticas.app.data.local.entity.ContaEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO base de exemplo. Expandir conforme necessidade nas proximas sprints.
 */
@Dao
interface ContaDao {

    @Query("SELECT * FROM conta ORDER BY id DESC")
    fun listar(): Flow<List<ContaEntity>>

    @Insert
    suspend fun inserir(conta: ContaEntity): Long
}
