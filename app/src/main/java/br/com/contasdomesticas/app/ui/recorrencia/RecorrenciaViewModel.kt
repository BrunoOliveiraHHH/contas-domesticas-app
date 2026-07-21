package br.com.contasdomesticas.app.ui.recorrencia

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.contasdomesticas.app.data.remote.dto.CarteiraDto
import br.com.contasdomesticas.app.data.remote.dto.CategoriaDto
import br.com.contasdomesticas.app.data.remote.dto.RecorrenciaDto
import br.com.contasdomesticas.app.data.remote.dto.RecorrenciaRequestDto
import br.com.contasdomesticas.app.data.repository.CarteiraRepository
import br.com.contasdomesticas.app.data.repository.CategoriaRepository
import br.com.contasdomesticas.app.data.repository.RecorrenciaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RecorrenciaUiState(
    val itens: List<RecorrenciaDto> = emptyList(),
    val carteiras: List<CarteiraDto> = emptyList(),
    val categorias: List<CategoriaDto> = emptyList(),
    val carregando: Boolean = false,
    val erro: String? = null,
    val mensagem: String? = null
)

@HiltViewModel
class RecorrenciaViewModel @Inject constructor(
    private val recorrenciaRepository: RecorrenciaRepository,
    private val carteiraRepository: CarteiraRepository,
    private val categoriaRepository: CategoriaRepository
) : ViewModel() {

    var estado by mutableStateOf(RecorrenciaUiState())
        private set

    init { carregar() }

    fun carregar() {
        viewModelScope.launch {
            estado = estado.copy(carregando = true, erro = null)
            runCatching {
                Triple(
                    recorrenciaRepository.listar(),
                    carteiraRepository.listar(),
                    categoriaRepository.listar()
                )
            }.onSuccess { (itens, carteiras, categorias) ->
                estado = estado.copy(itens = itens, carteiras = carteiras, categorias = categorias, carregando = false)
            }.onFailure {
                estado = estado.copy(carregando = false, erro = "Erro ao carregar")
            }
        }
    }

    fun criar(request: RecorrenciaRequestDto) {
        viewModelScope.launch {
            runCatching { recorrenciaRepository.criar(request) }
                .onSuccess { carregar() }
                .onFailure { estado = estado.copy(erro = "Erro ao salvar") }
        }
    }

    fun gerar(id: Long, competencia: String) {
        viewModelScope.launch {
            runCatching { recorrenciaRepository.gerar(id, competencia) }
                .onSuccess { estado = estado.copy(mensagem = "Lancamento gerado") }
                .onFailure { estado = estado.copy(erro = "Erro ao gerar") }
        }
    }

    fun remover(id: Long) {
        viewModelScope.launch {
            runCatching { recorrenciaRepository.remover(id) }
                .onSuccess { carregar() }
                .onFailure { estado = estado.copy(erro = "Erro ao remover") }
        }
    }

    fun limparMensagem() {
        estado = estado.copy(mensagem = null, erro = null)
    }
}
