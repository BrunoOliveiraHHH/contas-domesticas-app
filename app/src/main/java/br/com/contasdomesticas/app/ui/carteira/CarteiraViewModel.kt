package br.com.contasdomesticas.app.ui.carteira

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.contasdomesticas.app.data.remote.dto.CarteiraDto
import br.com.contasdomesticas.app.data.remote.dto.CarteiraRequestDto
import br.com.contasdomesticas.app.data.repository.CarteiraRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CarteiraUiState(
    val itens: List<CarteiraDto> = emptyList(),
    val carregando: Boolean = false,
    val erro: String? = null
)

@HiltViewModel
class CarteiraViewModel @Inject constructor(
    private val repository: CarteiraRepository
) : ViewModel() {

    var estado by mutableStateOf(CarteiraUiState())
        private set

    init { carregar() }

    fun carregar() {
        viewModelScope.launch {
            estado = estado.copy(carregando = true, erro = null)
            runCatching { repository.listar() }
                .onSuccess { estado = estado.copy(itens = it, carregando = false) }
                .onFailure { estado = estado.copy(carregando = false, erro = "Erro ao carregar carteiras") }
        }
    }

    fun criar(nome: String, tipo: String) {
        viewModelScope.launch {
            runCatching { repository.criar(CarteiraRequestDto(nome = nome, tipo = tipo)) }
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
