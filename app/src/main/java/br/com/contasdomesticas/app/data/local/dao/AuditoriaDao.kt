package br.com.contasdomesticas.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import br.com.contasdomesticas.app.data.local.entity.AuditoriaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AuditoriaDao {

    @Query("SELECT * FROM auditoria ORDER BY dataHora DESC")
    fun observarTodas(): Flow<List<AuditoriaEntity>>

    /** Insercao suspensa (uso geral no app). */
    @Insert
    suspend fun inserir(auditoria: AuditoriaEntity): Long

    /**
     * Insercao bloqueante para uso dentro do interceptor OkHttp,
     * que ja roda em uma thread de background do OkHttp (nunca a main thread).
     */
    @Insert
    fun inserirBloqueante(auditoria: AuditoriaEntity): Long
}
