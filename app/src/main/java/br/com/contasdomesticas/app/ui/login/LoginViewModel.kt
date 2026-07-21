package br.com.contasdomesticas.app.ui.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.contasdomesticas.app.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginState(
    val login: String = "",
    val senha: String = "",
    val carregando: Boolean = false,
    val erro: String? = null
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    var estado by mutableStateOf(LoginState())
        private set

    fun onLogin(valor: String) { estado = estado.copy(login = valor, erro = null) }
    fun onSenha(valor: String) { estado = estado.copy(senha = valor, erro = null) }

    fun entrar(onSucesso: () -> Unit) {
        if (estado.login.isBlank() || estado.senha.isBlank()) {
            estado = estado.copy(erro = "Informe login e senha")
            return
        }
        estado = estado.copy(carregando = true, erro = null)
        viewModelScope.launch {
            runCatching { authRepository.login(estado.login, estado.senha) }
                .onSuccess {
                    estado = estado.copy(carregando = false)
                    onSucesso()
                }
                .onFailure {
                    estado = estado.copy(carregando = false, erro = "Login ou senha invalidos")
                }
        }
    }
}
