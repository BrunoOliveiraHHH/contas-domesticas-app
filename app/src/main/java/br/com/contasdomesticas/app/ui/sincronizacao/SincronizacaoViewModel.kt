package br.com.contasdomesticas.app.ui.sincronizacao

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.contasdomesticas.app.data.remote.dto.SyncMercadoDto
import br.com.contasdomesticas.app.data.repository.SincronizacaoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SincronizacaoUiState(
    val ultimaSync: String? = null,
    val registros: List<SyncMercadoDto> = emptyList(),
    val sincronizando: Boolean = false,
    val erro: String? = null,
    val mensagem: String? = null
)

@HiltViewModel
class SincronizacaoViewModel @Inject constructor(
    private val repository: SincronizacaoRepository
) : ViewModel() {

    var estado by mutableStateOf(SincronizacaoUiState())
        private set

    init {
        viewModelScope.launch {
            estado = estado.copy(ultimaSync = repository.ultimaSync())
        }
    }

    fun sincronizar() {
        viewModelScope.launch {
            estado = estado.copy(sincronizando = true, erro = null, mensagem = null)
            runCatching { repository.sincronizar() }
                .onSuccess { registros ->
                    estado = estado.copy(
                        registros = registros,
                        ultimaSync = repository.ultimaSync(),
                        sincronizando = false,
                        mensagem = "${registros.size} registro(s) sincronizado(s)"
                    )
                }
                .onFailure { estado = estado.copy(sincronizando = false, erro = "Erro ao sincronizar") }
        }
    }

    fun enviar() {
        viewModelScope.launch {
            runCatching { repository.enviar(estado.registros) }
                .onSuccess { estado = estado.copy(mensagem = "Merge enviado") }
                .onFailure { estado = estado.copy(erro = "Erro ao enviar") }
        }
    }

    fun limparMensagem() {
        estado = estado.copy(mensagem = null, erro = null)
    }
}
