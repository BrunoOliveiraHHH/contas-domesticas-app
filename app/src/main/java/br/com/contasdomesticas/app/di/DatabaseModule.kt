package br.com.contasdomesticas.app.di

import android.content.Context
import androidx.room.Room
import br.com.contasdomesticas.app.data.local.AppDatabase
import br.com.contasdomesticas.app.data.local.dao.ContaDao
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
        ).build()

    @Provides
    fun provideContaDao(database: AppDatabase): ContaDao = database.contaDao()
}
