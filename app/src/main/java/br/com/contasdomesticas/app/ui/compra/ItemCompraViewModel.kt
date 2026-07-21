package br.com.contasdomesticas.app.ui.compra

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.contasdomesticas.app.data.remote.dto.CotacaoProdutoDto
import br.com.contasdomesticas.app.data.remote.dto.CotacaoProdutoRequestDto
import br.com.contasdomesticas.app.data.remote.dto.ItemCompraDto
import br.com.contasdomesticas.app.data.remote.dto.ItemCompraRequestDto
import br.com.contasdomesticas.app.data.remote.dto.MercadoDto
import br.com.contasdomesticas.app.data.remote.dto.ProdutoDto
import br.com.contasdomesticas.app.data.remote.dto.UnidadeMedidaDto
import br.com.contasdomesticas.app.data.repository.CompraRepository
import br.com.contasdomesticas.app.data.repository.MercadoRepository
import br.com.contasdomesticas.app.data.repository.ProdutoRepository
import br.com.contasdomesticas.app.data.repository.UnidadeMedidaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ItemCompraUiState(
    val itens: List<ItemCompraDto> = emptyList(),
    val produtos: List<ProdutoDto> = emptyList(),
    val mercados: List<MercadoDto> = emptyList(),
    val unidades: List<UnidadeMedidaDto> = emptyList(),
    val carregando: Boolean = false,
    val erro: String? = null
)

@HiltViewModel
class ItemCompraViewModel @Inject constructor(
    private val compraRepository: CompraRepository,
    private val produtoRepository: ProdutoRepository,
    private val mercadoRepository: MercadoRepository,
    private val unidadeMedidaRepository: UnidadeMedidaRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val listaId: Long = savedStateHandle.get<String>("listaId")?.toLongOrNull() ?: 0L

    var estado by mutableStateOf(ItemCompraUiState())
        private set

    init { carregar() }

    fun carregar() {
        viewModelScope.launch {
            estado = estado.copy(carregando = true, erro = null)
            runCatching {
                val itens = compraRepository.itens(listaId)
                val produtos = produtoRepository.listar()
                val mercados = mercadoRepository.listar()
                val unidades = unidadeMedidaRepository.listar()
                ItemCompraUiState(itens, produtos, mercados, unidades, carregando = false)
            }.onSuccess { novo ->
                estado = novo
            }.onFailure {
                estado = estado.copy(carregando = false, erro = "Erro ao carregar")
            }
        }
    }

    fun adicionarItem(request: ItemCompraRequestDto) {
        viewModelScope.launch {
            runCatching { compraRepository.adicionarItem(listaId, request) }
                .onSuccess { carregar() }
                .onFailure { estado = estado.copy(erro = "Erro ao adicionar") }
        }
    }

    fun escolher(itemId: Long, mercadoId: Long) {
        viewModelScope.launch {
            runCatching { compraRepository.escolher(itemId, mercadoId) }
                .onSuccess { carregar() }
                .onFailure { estado = estado.copy(erro = "Erro ao escolher") }
        }
    }

    fun removerItem(itemId: Long) {
        viewModelScope.launch {
            runCatching { compraRepository.removerItem(itemId) }
                .onSuccess { carregar() }
                .onFailure { estado = estado.copy(erro = "Erro ao remover") }
        }
    }

    suspend fun cotacoes(produtoId: Long): List<CotacaoProdutoDto> =
        runCatching { compraRepository.cotacoes(produtoId) }.getOrDefault(emptyList())

    fun adicionarCotacao(produtoId: Long, request: CotacaoProdutoRequestDto, onFim: () -> Unit) {
        viewModelScope.launch {
            runCatching { compraRepository.adicionarCotacao(produtoId, request) }
                .onSuccess { onFim() }
                .onFailure { estado = estado.copy(erro = "Erro ao cotar") }
        }
    }
}
