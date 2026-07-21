package br.com.contasdomesticas.app.ui.cadastro

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.contasdomesticas.app.data.remote.dto.ProdutoDto
import br.com.contasdomesticas.app.data.remote.dto.ProdutoRequestDto
import br.com.contasdomesticas.app.data.repository.ProdutoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProdutoUiState(
    val itens: List<ProdutoDto> = emptyList(),
    val carregando: Boolean = false,
    val erro: String? = null
)

@HiltViewModel
class ProdutoViewModel @Inject constructor(
    private val repository: ProdutoRepository
) : ViewModel() {

    var estado by mutableStateOf(ProdutoUiState())
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

    fun criar(request: ProdutoRequestDto) {
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
