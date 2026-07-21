package br.com.contasdomesticas.app.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore("cd_prefs")

/**
 * Guarda o token com persistencia (DataStore) e um cache em memoria para o
 * interceptor (sincrono) injetar o Bearer sem bloquear.
 */
@Singleton
class TokenStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    @Volatile
    var accessToken: String? = null
        private set

    suspend fun carregarCache() {
        accessToken = context.dataStore.data.map { it[ACCESS] }.first()
    }

    suspend fun salvar(access: String, refresh: String?) {
        accessToken = access
        context.dataStore.edit { prefs ->
            prefs[ACCESS] = access
            if (refresh != null) prefs[REFRESH] = refresh
        }
    }

    suspend fun refreshToken(): String? = context.dataStore.data.map { it[REFRESH] }.first()

    suspend fun lastSyncMercados(): String? =
        context.dataStore.data.map { it[LAST_SYNC_MERCADOS] }.first()

    suspend fun salvarLastSync(carimbo: String) {
        context.dataStore.edit { it[LAST_SYNC_MERCADOS] = carimbo }
    }

    suspend fun limpar() {
        accessToken = null
        // Preserva o carimbo de sincronizacao ao sair (apenas remove tokens).
        context.dataStore.edit { prefs ->
            prefs.remove(ACCESS)
            prefs.remove(REFRESH)
        }
    }

    private companion object {
        val ACCESS = stringPreferencesKey("access_token")
        val REFRESH = stringPreferencesKey("refresh_token")
        val LAST_SYNC_MERCADOS = stringPreferencesKey("last_sync_mercados")
    }
}
