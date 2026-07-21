package br.com.contasdomesticas.app.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.contasdomesticas.app.data.local.TokenStore
import br.com.contasdomesticas.app.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val tokenStore: TokenStore,
    private val authRepository: AuthRepository
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
