package br.com.contasdomesticas.app.ui.lancamento

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.contasdomesticas.app.data.remote.dto.CarteiraDto
import br.com.contasdomesticas.app.data.remote.dto.CategoriaDto
import br.com.contasdomesticas.app.data.remote.dto.LancamentoDto
import br.com.contasdomesticas.app.data.remote.dto.ReceitaRequestDto
import br.com.contasdomesticas.app.data.repository.CarteiraRepository
import br.com.contasdomesticas.app.data.repository.CategoriaRepository
import br.com.contasdomesticas.app.data.repository.ReceitaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReceitaUiState(
    val itens: List<LancamentoDto> = emptyList(),
    val carteiras: List<CarteiraDto> = emptyList(),
    val categorias: List<CategoriaDto> = emptyList(),
    val carregando: Boolean = false,
    val erro: String? = null
)

@HiltViewModel
class ReceitaViewModel @Inject constructor(
    private val receitaRepository: ReceitaRepository,
    private val carteiraRepository: CarteiraRepository,
    private val categoriaRepository: CategoriaRepository
) : ViewModel() {

    var estado by mutableStateOf(ReceitaUiState())
        private set

    init { carregar() }

    fun carregar() {
        viewModelScope.launch {
            estado = estado.copy(carregando = true, erro = null)
            runCatching {
                val itens = receitaRepository.listar()
                val carteiras = carteiraRepository.listar()
                val categorias = categoriaRepository.listar().filter { it.tipo == "RECEITA" }
                Triple(itens, carteiras, categorias)
            }.onSuccess { (itens, carteiras, categorias) ->
                estado = estado.copy(itens = itens, carteiras = carteiras, categorias = categorias, carregando = false)
            }.onFailure {
                estado = estado.copy(carregando = false, erro = "Erro ao carregar")
            }
        }
    }

    fun criar(request: ReceitaRequestDto) {
        viewModelScope.launch {
            runCatching { receitaRepository.criar(request) }
                .onSuccess { carregar() }
                .onFailure { estado = estado.copy(erro = "Erro ao salvar") }
        }
    }

    fun remover(id: Long) {
        viewModelScope.launch {
            runCatching { receitaRepository.remover(id) }
                .onSuccess { carregar() }
                .onFailure { estado = estado.copy(erro = "Erro ao remover") }
        }
    }
}
