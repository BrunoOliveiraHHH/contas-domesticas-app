package br.com.contasdomesticas.app.ui.investimento

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.contasdomesticas.app.data.remote.dto.AporteRequestDto
import br.com.contasdomesticas.app.data.remote.dto.CarteiraDto
import br.com.contasdomesticas.app.data.remote.dto.InvestimentoDto
import br.com.contasdomesticas.app.data.remote.dto.InvestimentoRequestDto
import br.com.contasdomesticas.app.data.repository.CarteiraRepository
import br.com.contasdomesticas.app.data.repository.InvestimentoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class InvestimentoUiState(
    val itens: List<InvestimentoDto> = emptyList(),
    val carteiras: List<CarteiraDto> = emptyList(),
    val patrimonio: Double = 0.0,
    val carregando: Boolean = false,
    val erro: String? = null
)

@HiltViewModel
class InvestimentoViewModel @Inject constructor(
    private val investimentoRepository: InvestimentoRepository,
    private val carteiraRepository: CarteiraRepository
) : ViewModel() {

    var estado by mutableStateOf(InvestimentoUiState())
        private set

    init { carregar() }

    fun carregar() {
        viewModelScope.launch {
            estado = estado.copy(carregando = true, erro = null)
            runCatching {
                val itens = investimentoRepository.listar()
                val carteiras = carteiraRepository.listar()
                val patrimonio = investimentoRepository.patrimonio()
                Triple(itens, carteiras, patrimonio)
            }.onSuccess { (itens, carteiras, patrimonio) ->
                estado = estado.copy(itens = itens, carteiras = carteiras, patrimonio = patrimonio, carregando = false)
            }.onFailure {
                estado = estado.copy(carregando = false, erro = "Erro ao carregar")
            }
        }
    }

    fun criar(request: InvestimentoRequestDto) {
        viewModelScope.launch {
            runCatching { investimentoRepository.criar(request) }
                .onSuccess { carregar() }
                .onFailure { estado = estado.copy(erro = "Erro ao salvar") }
        }
    }

    fun adicionarAporte(id: Long, request: AporteRequestDto) {
        viewModelScope.launch {
            runCatching { investimentoRepository.adicionarAporte(id, request) }
                .onSuccess { carregar() }
                .onFailure { estado = estado.copy(erro = "Erro ao registrar") }
        }
    }

    fun remover(id: Long) {
        viewModelScope.launch {
            runCatching { investimentoRepository.remover(id) }
                .onSuccess { carregar() }
                .onFailure { estado = estado.copy(erro = "Erro ao remover") }
        }
    }
}
