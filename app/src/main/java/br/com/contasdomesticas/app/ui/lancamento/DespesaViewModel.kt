package br.com.contasdomesticas.app.ui.lancamento

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.contasdomesticas.app.data.remote.dto.CarteiraDto
import br.com.contasdomesticas.app.data.remote.dto.CategoriaDto
import br.com.contasdomesticas.app.data.remote.dto.DespesaRequestDto
import br.com.contasdomesticas.app.data.remote.dto.LancamentoDto
import br.com.contasdomesticas.app.data.repository.CarteiraRepository
import br.com.contasdomesticas.app.data.repository.CategoriaRepository
import br.com.contasdomesticas.app.data.repository.DespesaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DespesaUiState(
    val itens: List<LancamentoDto> = emptyList(),
    val carteiras: List<CarteiraDto> = emptyList(),
    val categorias: List<CategoriaDto> = emptyList(),
    val carregando: Boolean = false,
    val erro: String? = null
)

@HiltViewModel
class DespesaViewModel @Inject constructor(
    private val despesaRepository: DespesaRepository,
    private val carteiraRepository: CarteiraRepository,
    private val categoriaRepository: CategoriaRepository
) : ViewModel() {

    var estado by mutableStateOf(DespesaUiState())
        private set

    init { carregar() }

    fun carregar() {
        viewModelScope.launch {
            estado = estado.copy(carregando = true, erro = null)
            runCatching {
                val itens = despesaRepository.listar()
                val carteiras = carteiraRepository.listar()
                val categorias = categoriaRepository.listar().filter { it.tipo == "DESPESA" }
                Triple(itens, carteiras, categorias)
            }.onSuccess { (itens, carteiras, categorias) ->
                estado = estado.copy(itens = itens, carteiras = carteiras, categorias = categorias, carregando = false)
            }.onFailure {
                estado = estado.copy(carregando = false, erro = "Erro ao carregar")
            }
        }
    }

    fun criar(request: DespesaRequestDto) {
        viewModelScope.launch {
            runCatching { despesaRepository.criar(request) }
                .onSuccess { carregar() }
                .onFailure { estado = estado.copy(erro = "Erro ao salvar") }
        }
    }

    fun pagar(id: Long) {
        viewModelScope.launch {
            runCatching { despesaRepository.pagar(id) }
                .onSuccess { carregar() }
                .onFailure { estado = estado.copy(erro = "Erro ao pagar") }
        }
    }

    fun remover(id: Long) {
        viewModelScope.launch {
            runCatching { despesaRepository.remover(id) }
                .onSuccess { carregar() }
                .onFailure { estado = estado.copy(erro = "Erro ao remover") }
        }
    }
}
