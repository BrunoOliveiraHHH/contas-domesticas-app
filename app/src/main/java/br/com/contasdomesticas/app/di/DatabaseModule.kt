package br.com.contasdomesticas.app.di

import android.content.Context
import androidx.room.Room
import br.com.contasdomesticas.app.data.local.AppDatabase
import br.com.contasdomesticas.app.data.local.dao.AuditoriaDao
import br.com.contasdomesticas.app.data.local.dao.UsuarioDao
import br.com.contasdomesticas.app.data.local.db.CarteiraDao
import br.com.contasdomesticas.app.data.local.db.CategoriaDao
import br.com.contasdomesticas.app.data.local.db.LancamentoDao
import br.com.contasdomesticas.app.data.local.db.ListaCompraDao
import br.com.contasdomesticas.app.data.local.db.OutboxDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Provisiona o banco Room e seus DAOs via Hilt.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideUsuarioDao(database: AppDatabase): UsuarioDao = database.usuarioDao()

    @Provides
    fun provideAuditoriaDao(database: AppDatabase): AuditoriaDao = database.auditoriaDao()

    @Provides
    fun provideCarteiraDao(database: AppDatabase): CarteiraDao = database.carteiraDao()

    @Provides
    fun provideCategoriaDao(database: AppDatabase): CategoriaDao = database.categoriaDao()

    @Provides
    fun provideLancamentoDao(database: AppDatabase): LancamentoDao = database.lancamentoDao()

    @Provides
    fun provideListaCompraDao(database: AppDatabase): ListaCompraDao = database.listaCompraDao()

    @Provides
    fun provideOutboxDao(database: AppDatabase): OutboxDao = database.outboxDao()
}
