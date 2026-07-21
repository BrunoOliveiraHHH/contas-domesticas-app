package br.com.contasdomesticas.app.ui.parcelamento

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.contasdomesticas.app.data.remote.dto.CarteiraDto
import br.com.contasdomesticas.app.data.remote.dto.CategoriaDto
import br.com.contasdomesticas.app.data.remote.dto.LancamentoDto
import br.com.contasdomesticas.app.data.remote.dto.ParcelamentoRequestDto
import br.com.contasdomesticas.app.data.repository.CarteiraRepository
import br.com.contasdomesticas.app.data.repository.CategoriaRepository
import br.com.contasdomesticas.app.data.repository.DespesaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ParcelamentoUiState(
    val carteiras: List<CarteiraDto> = emptyList(),
    val categorias: List<CategoriaDto> = emptyList(),
    val parcelas: List<LancamentoDto> = emptyList(),
    val erro: String? = null
)

@HiltViewModel
class ParcelamentoViewModel @Inject constructor(
    private val despesaRepository: DespesaRepository,
    private val carteiraRepository: CarteiraRepository,
    private val categoriaRepository: CategoriaRepository
) : ViewModel() {

    var estado by mutableStateOf(ParcelamentoUiState())
        private set

    init { carregar() }

    private fun carregar() {
        viewModelScope.launch {
            runCatching {
                val carteiras = carteiraRepository.listar()
                val categorias = categoriaRepository.listar().filter { it.tipo == "DESPESA" }
                carteiras to categorias
            }.onSuccess { (carteiras, categorias) ->
                estado = estado.copy(carteiras = carteiras, categorias = categorias)
            }.onFailure { estado = estado.copy(erro = "Erro ao carregar") }
        }
    }

    fun parcelar(request: ParcelamentoRequestDto) {
        viewModelScope.launch {
            runCatching { despesaRepository.parcelar(request) }
                .onSuccess { estado = estado.copy(parcelas = it, erro = null) }
                .onFailure { estado = estado.copy(erro = "Erro ao parcelar") }
        }
    }
}
