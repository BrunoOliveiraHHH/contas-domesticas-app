package br.com.contasdomesticas.app.ui.compra

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.contasdomesticas.app.data.remote.dto.CarteiraDto
import br.com.contasdomesticas.app.data.remote.dto.CategoriaDto
import br.com.contasdomesticas.app.data.remote.dto.ListaCompraDto
import br.com.contasdomesticas.app.data.remote.dto.ListaCompraRequestDto
import br.com.contasdomesticas.app.data.repository.CarteiraRepository
import br.com.contasdomesticas.app.data.repository.CategoriaRepository
import br.com.contasdomesticas.app.data.repository.CompraRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ListaCompraUiState(
    val itens: List<ListaCompraDto> = emptyList(),
    val carteiras: List<CarteiraDto> = emptyList(),
    val categorias: List<CategoriaDto> = emptyList(),
    val carregando: Boolean = false,
    val erro: String? = null,
    val mensagem: String? = null
)

@HiltViewModel
class ListaCompraViewModel @Inject constructor(
    private val compraRepository: CompraRepository,
    private val carteiraRepository: CarteiraRepository,
    private val categoriaRepository: CategoriaRepository
) : ViewModel() {

    var estado by mutableStateOf(ListaCompraUiState())
        private set

    init { carregar() }

    fun carregar() {
        viewModelScope.launch {
            estado = estado.copy(carregando = true, erro = null)
            runCatching {
                val itens = compraRepository.listar()
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

    fun criar(request: ListaCompraRequestDto) {
        viewModelScope.launch {
            runCatching { compraRepository.criar(request) }
                .onSuccess { carregar() }
                .onFailure { estado = estado.copy(erro = "Erro ao salvar") }
        }
    }

    fun duplicar(id: Long) {
        viewModelScope.launch {
            runCatching { compraRepository.duplicar(id) }
                .onSuccess { carregar() }
                .onFailure { estado = estado.copy(erro = "Erro ao duplicar") }
        }
    }

    fun fechar(id: Long, categoriaId: Long) {
        viewModelScope.launch {
            runCatching { compraRepository.fechar(id, categoriaId) }
                .onSuccess { despesas ->
                    estado = estado.copy(mensagem = "${despesas.size} despesa(s) gerada(s)")
                    carregar()
                }
                .onFailure { estado = estado.copy(erro = "Erro ao fechar") }
        }
    }

    fun remover(id: Long) {
        viewModelScope.launch {
            runCatching { compraRepository.remover(id) }
                .onSuccess { carregar() }
                .onFailure { estado = estado.copy(erro = "Erro ao remover") }
        }
    }

    fun limparMensagem() {
        estado = estado.copy(mensagem = null, erro = null)
    }
}
