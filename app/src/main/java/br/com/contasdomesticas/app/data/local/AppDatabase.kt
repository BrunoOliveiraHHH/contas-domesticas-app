package br.com.contasdomesticas.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import br.com.contasdomesticas.app.data.local.dao.AuditoriaDao
import br.com.contasdomesticas.app.data.local.dao.UsuarioDao
import br.com.contasdomesticas.app.data.local.db.CarteiraDao
import br.com.contasdomesticas.app.data.local.db.CarteiraEntity
import br.com.contasdomesticas.app.data.local.db.CategoriaDao
import br.com.contasdomesticas.app.data.local.db.CategoriaEntity
import br.com.contasdomesticas.app.data.local.db.LancamentoDao
import br.com.contasdomesticas.app.data.local.db.LancamentoEntity
import br.com.contasdomesticas.app.data.local.db.ListaCompraDao
import br.com.contasdomesticas.app.data.local.db.ListaCompraEntity
import br.com.contasdomesticas.app.data.local.db.OutboxDao
import br.com.contasdomesticas.app.data.local.db.OutboxOp
import br.com.contasdomesticas.app.data.local.entity.AuditoriaEntity
import br.com.contasdomesticas.app.data.local.entity.UsuarioEntity

/**
 * Banco de dados local (Room) do aplicativo Contas Domesticas.
 * Cache offline que sincroniza com a API.
 */
@Database(
    entities = [
        UsuarioEntity::class,
        AuditoriaEntity::class,
        CarteiraEntity::class,
        CategoriaEntity::class,
        LancamentoEntity::class,
        ListaCompraEntity::class,
        OutboxOp::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao

    abstract fun auditoriaDao(): AuditoriaDao

    abstract fun carteiraDao(): CarteiraDao

    abstract fun categoriaDao(): CategoriaDao

    abstract fun lancamentoDao(): LancamentoDao

    abstract fun listaCompraDao(): ListaCompraDao

    abstract fun outboxDao(): OutboxDao

    companion object {
        const val DATABASE_NAME = "contasdomesticas.db"
    }
}
