package br.com.contasdomesticas.app.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.contasdomesticas.app.data.local.TokenStore
import br.com.contasdomesticas.app.data.repository.AuthRepository
import br.com.contasdomesticas.app.data.sync.SyncManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val tokenStore: TokenStore,
    private val authRepository: AuthRepository,
    private val syncManager: SyncManager
) : ViewModel() {

    var pronto by mutableStateOf(false)
        private set
    var autenticado by mutableStateOf(false)
        private set

    init {
        viewModelScope.launch {
            tokenStore.carregarCache()
            autenticado = authRepository.autenticado()
            pronto = true
            // Sincroniza o cache local com a API ao iniciar (flush do outbox + rebaixar).
            if (autenticado) {
                runCatching { syncManager.sincronizar() }
            }
        }
    }

    fun sair(onFeito: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            autenticado = false
            onFeito()
        }
    }
}
