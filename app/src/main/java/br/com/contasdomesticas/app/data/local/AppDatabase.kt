package br.com.contasdomesticas.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import br.com.contasdomesticas.app.data.local.dao.AuditoriaDao
import br.com.contasdomesticas.app.data.local.dao.UsuarioDao
import br.com.contasdomesticas.app.data.local.entity.AuditoriaEntity
import br.com.contasdomesticas.app.data.local.entity.UsuarioEntity

/**
 * Banco de dados local (Room) do aplicativo Contas Domesticas.
 */
@Database(
    entities = [UsuarioEntity::class, AuditoriaEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao

    abstract fun auditoriaDao(): AuditoriaDao

    companion object {
        const val DATABASE_NAME = "contasdomesticas.db"
    }
}
