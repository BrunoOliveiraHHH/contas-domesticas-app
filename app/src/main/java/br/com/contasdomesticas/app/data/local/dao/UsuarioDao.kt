package br.com.contasdomesticas.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import br.com.contasdomesticas.app.data.local.entity.UsuarioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {

    @Query("SELECT * FROM usuario ORDER BY nomeExibicao")
    fun observarTodos(): Flow<List<UsuarioEntity>>

    @Query("SELECT * FROM usuario WHERE id = :id")
    suspend fun buscarPorId(id: Long): UsuarioEntity?

    @Query("SELECT * FROM usuario WHERE login = :login LIMIT 1")
    suspend fun buscarPorLogin(login: String): UsuarioEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(usuario: UsuarioEntity): Long

    @Update
    suspend fun atualizar(usuario: UsuarioEntity)

    @Delete
    suspend fun remover(usuario: UsuarioEntity)
}
