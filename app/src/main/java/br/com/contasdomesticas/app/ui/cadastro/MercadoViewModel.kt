package br.com.contasdomesticas.app.ui.cadastro

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.contasdomesticas.app.data.remote.dto.MercadoDto
import br.com.contasdomesticas.app.data.remote.dto.MercadoRequestDto
import br.com.contasdomesticas.app.data.repository.MercadoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MercadoUiState(
    val itens: List<MercadoDto> = emptyList(),
    val carregando: Boolean = false,
    val erro: String? = null
)

@HiltViewModel
class MercadoViewModel @Inject constructor(
    private val repository: MercadoRepository
) : ViewModel() {

    var estado by mutableStateOf(MercadoUiState())
        private set

    init { carregar() }

    fun carregar() {
        viewModelScope.launch {
            estado = estado.copy(carregando = true, erro = null)
            runCatching { repository.listar() }
                .onSuccess { estado = estado.copy(itens = it, carregando = false) }
                .onFailure { estado = estado.copy(carregando = false, erro = "Erro ao carregar") }
        }
    }

    fun criar(request: MercadoRequestDto) {
        viewModelScope.launch {
            runCatching { repository.criar(request) }
                .onSuccess { carregar() }
                .onFailure { estado = estado.copy(erro = "Erro ao salvar") }
        }
    }

    fun remover(id: Long) {
        viewModelScope.launch {
            runCatching { repository.remover(id) }
                .onSuccess { carregar() }
                .onFailure { estado = estado.copy(erro = "Erro ao remover") }
        }
    }
}
