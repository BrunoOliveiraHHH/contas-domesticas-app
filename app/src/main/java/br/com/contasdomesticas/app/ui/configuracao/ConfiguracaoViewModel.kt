package br.com.contasdomesticas.app.ui.configuracao

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.contasdomesticas.app.data.remote.dto.ParametroDto
import br.com.contasdomesticas.app.data.remote.dto.ParametroRequestDto
import br.com.contasdomesticas.app.data.repository.ConfiguracaoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ConfiguracaoUiState(
    val parametros: List<ParametroDto> = emptyList(),
    val carregando: Boolean = false,
    val erro: String? = null
)

@HiltViewModel
class ConfiguracaoViewModel @Inject constructor(
    private val repository: ConfiguracaoRepository
) : ViewModel() {

    var estado by mutableStateOf(ConfiguracaoUiState())
        private set

    init { carregar() }

    fun carregar() {
        viewModelScope.launch {
            estado = estado.copy(carregando = true, erro = null)
            runCatching { repository.listarParametros() }
                .onSuccess { estado = estado.copy(parametros = it, carregando = false) }
                .onFailure { estado = estado.copy(carregando = false, erro = "Erro ao carregar") }
        }
    }

    fun salvar(request: ParametroRequestDto, id: Long?) {
        viewModelScope.launch {
            runCatching {
                if (id != null) repository.atualizarParametro(id, request)
                else repository.criarParametro(request)
            }.onSuccess { carregar() }
                .onFailure { estado = estado.copy(erro = "Erro ao salvar") }
        }
    }

    fun remover(id: Long) {
        viewModelScope.launch {
            runCatching { repository.removerParametro(id) }
                .onSuccess { carregar() }
                .onFailure { estado = estado.copy(erro = "Erro ao remover") }
        }
    }

    fun resolverPreferencia(chave: String, onResultado: (String?) -> Unit) {
        viewModelScope.launch {
            runCatching { repository.resolverPreferencia(chave) }
                .onSuccess { onResultado(it.valor) }
                .onFailure { onResultado(null) }
        }
    }

    fun gravarPreferencia(chave: String, valor: String, onFim: () -> Unit) {
        viewModelScope.launch {
            runCatching { repository.gravarPreferencia(chave, valor) }
                .onSuccess { onFim() }
                .onFailure { estado = estado.copy(erro = "Erro ao gravar preferencia") }
        }
    }
}
