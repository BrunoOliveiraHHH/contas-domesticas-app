package br.com.contasdomesticas.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import br.com.contasdomesticas.app.data.local.dao.ContaDao
import br.com.contasdomesticas.app.data.local.entity.ContaEntity

/**
 * Banco de dados local (Room) do aplicativo Contas Domesticas.
 */
@Database(
    entities = [ContaEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun contaDao(): ContaDao

    companion object {
        const val DATABASE_NAME = "contasdomesticas.db"
    }
}
