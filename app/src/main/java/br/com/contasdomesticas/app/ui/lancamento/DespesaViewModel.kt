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
import br.com.contasdomesticas.app.data.remote.dto.ParcelamentoRequestDto
import br.com.contasdomesticas.app.data.remote.dto.RecorrenciaRequestDto
import br.com.contasdomesticas.app.data.repository.CarteiraRepository
import br.com.contasdomesticas.app.data.repository.CategoriaRepository
import br.com.contasdomesticas.app.data.repository.DespesaRepository
import br.com.contasdomesticas.app.data.repository.RecorrenciaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class DespesaUiState(
    val itens: List<LancamentoDto> = emptyList(),
    val carteiras: List<CarteiraDto> = emptyList(),
    val categorias: List<CategoriaDto> = emptyList(),
    val carregando: Boolean = false,
    val erro: String? = null,
    val mensagem: String? = null
)

@HiltViewModel
class DespesaViewModel @Inject constructor(
    private val despesaRepository: DespesaRepository,
    private val recorrenciaRepository: RecorrenciaRepository,
    private val carteiraRepository: CarteiraRepository,
    private val categoriaRepository: CategoriaRepository
) : ViewModel() {

    var estado by mutableStateOf(DespesaUiState())
        private set

    init { carregar() }

    private fun hoje(): String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

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

    /** Despesa unica (um lancamento). */
    fun criarUnica(nome: String, categoriaId: Long, carteiraId: Long, valor: Double, dataVencimento: String?) {
        viewModelScope.launch {
            runCatching {
                despesaRepository.criar(
                    DespesaRequestDto(
                        descricao = nome,
                        valor = valor,
                        dataCompetencia = hoje(),
                        carteiraId = carteiraId,
                        categoriaId = categoriaId,
                        dataVencimento = dataVencimento
                    )
                )
            }.onSuccess { estado = estado.copy(mensagem = "Despesa salva"); carregar() }
                .onFailure { estado = estado.copy(erro = "Erro ao salvar") }
        }
    }

    /** Despesa recorrente fixa (mensal) -> cria uma recorrencia. */
    fun criarRecorrente(nome: String, categoriaId: Long, carteiraId: Long, valor: Double, diaVencimento: Int?) {
        viewModelScope.launch {
            runCatching {
                recorrenciaRepository.criar(
                    RecorrenciaRequestDto(
                        descricao = nome,
                        valor = valor,
                        tipo = "DESPESA",
                        carteiraId = carteiraId,
                        categoriaId = categoriaId,
                        frequencia = "MENSAL",
                        diaVencimento = diaVencimento,
                        dataInicio = hoje(),
                        ativa = true
                    )
                )
            }.onSuccess { estado = estado.copy(mensagem = "Despesa recorrente criada") }
                .onFailure { estado = estado.copy(erro = "Erro ao salvar") }
        }
    }

    /** Despesa parcelada -> gera N parcelas. */
    fun criarParcelada(
        nome: String,
        categoriaId: Long,
        carteiraId: Long,
        valorTotal: Double,
        parcelas: Int,
        primeiroVencimento: String
    ) {
        viewModelScope.launch {
            runCatching {
                despesaRepository.parcelar(
                    ParcelamentoRequestDto(
                        descricao = nome,
                        valorTotal = valorTotal,
                        parcelas = parcelas,
                        primeiroVencimento = primeiroVencimento,
                        carteiraId = carteiraId,
                        categoriaId = categoriaId
                    )
                )
            }.onSuccess { estado = estado.copy(mensagem = "${it.size} parcelas geradas"); carregar() }
                .onFailure { estado = estado.copy(erro = "Erro ao parcelar") }
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

    fun limparMensagem() {
        estado = estado.copy(mensagem = null, erro = null)
    }
}
