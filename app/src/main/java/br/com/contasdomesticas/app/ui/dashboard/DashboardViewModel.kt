package br.com.contasdomesticas.app.ui.dashboard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.contasdomesticas.app.data.remote.dto.PorCategoriaItemDto
import br.com.contasdomesticas.app.data.remote.dto.SaldoMesDto
import br.com.contasdomesticas.app.data.repository.InvestimentoRepository
import br.com.contasdomesticas.app.data.repository.RelatorioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class DashboardUiState(
    val periodo: String = "",
    val saldo: SaldoMesDto? = null,
    val despesasPorCategoria: List<PorCategoriaItemDto> = emptyList(),
    val patrimonio: Double = 0.0,
    val carregando: Boolean = false,
    val erro: String? = null
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val relatorioRepository: RelatorioRepository,
    private val investimentoRepository: InvestimentoRepository
) : ViewModel() {

    var estado by mutableStateOf(DashboardUiState(periodo = periodoAtual()))
        private set

    init { carregar(estado.periodo) }

    fun carregar(periodo: String) {
        viewModelScope.launch {
            estado = estado.copy(periodo = periodo, carregando = true, erro = null)
            runCatching {
                val saldo = relatorioRepository.saldo(periodo)
                val porCategoria = relatorioRepository.despesasPorCategoria(periodo)
                val patrimonio = investimentoRepository.patrimonio()
                Triple(saldo, porCategoria, patrimonio)
            }.onSuccess { (saldo, porCategoria, patrimonio) ->
                estado = estado.copy(
                    saldo = saldo,
                    despesasPorCategoria = porCategoria,
                    patrimonio = patrimonio,
                    carregando = false
                )
            }.onFailure {
                estado = estado.copy(carregando = false, erro = "Erro ao carregar")
            }
        }
    }

    private companion object {
        fun periodoAtual(): String =
            SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())
    }
}
