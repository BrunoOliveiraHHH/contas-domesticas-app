package br.com.contasdomesticas.app.ui.rateio

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.contasdomesticas.app.data.remote.dto.AcertoItemDto
import br.com.contasdomesticas.app.data.remote.dto.LancamentoDto
import br.com.contasdomesticas.app.data.remote.dto.RateioParticipanteRequestDto
import br.com.contasdomesticas.app.data.remote.dto.RateioRequestDto
import br.com.contasdomesticas.app.data.remote.dto.UsuarioDto
import br.com.contasdomesticas.app.data.repository.DespesaRepository
import br.com.contasdomesticas.app.data.repository.RateioRepository
import br.com.contasdomesticas.app.data.repository.UsuarioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class RateioUiState(
    val despesas: List<LancamentoDto> = emptyList(),
    val usuarios: List<UsuarioDto> = emptyList(),
    val periodo: String = "",
    val acerto: List<AcertoItemDto> = emptyList(),
    val erro: String? = null,
    val mensagem: String? = null
)

@HiltViewModel
class RateioViewModel @Inject constructor(
    private val despesaRepository: DespesaRepository,
    private val usuarioRepository: UsuarioRepository,
    private val rateioRepository: RateioRepository
) : ViewModel() {

    var estado by mutableStateOf(RateioUiState(periodo = periodoAtual()))
        private set

    init {
        carregar()
        carregarAcerto(estado.periodo)
    }

    private fun carregar() {
        viewModelScope.launch {
            runCatching {
                despesaRepository.listar() to usuarioRepository.listar()
            }.onSuccess { (despesas, usuarios) ->
                estado = estado.copy(despesas = despesas, usuarios = usuarios)
            }.onFailure { estado = estado.copy(erro = "Erro ao carregar") }
        }
    }

    fun carregarAcerto(periodo: String) {
        viewModelScope.launch {
            estado = estado.copy(periodo = periodo)
            runCatching { rateioRepository.acerto(periodo) }
                .onSuccess { estado = estado.copy(acerto = it) }
                .onFailure { estado = estado.copy(erro = "Erro ao carregar acerto") }
        }
    }

    fun ratearIgual(despesaId: Long, usuarioIds: List<Long>) {
        viewModelScope.launch {
            val request = RateioRequestDto(
                tipo = "IGUAL",
                participantes = usuarioIds.map { RateioParticipanteRequestDto(usuarioId = it) }
            )
            runCatching { despesaRepository.ratear(despesaId, request) }
                .onSuccess {
                    estado = estado.copy(mensagem = "Despesa rateada")
                    carregarAcerto(estado.periodo)
                }
                .onFailure { estado = estado.copy(erro = "Erro ao ratear") }
        }
    }

    fun limparMensagem() {
        estado = estado.copy(mensagem = null, erro = null)
    }

    private companion object {
        fun periodoAtual(): String =
            SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())
    }
}
