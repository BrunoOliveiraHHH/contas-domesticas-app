package br.com.contasdomesticas.app.ui.calculadora

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.contasdomesticas.app.data.repository.ConfiguracaoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Apenas o IR usa a API (aliquota parametrizada); o resto e calculo puro na tela. */
@HiltViewModel
class CalculadoraViewModel @Inject constructor(
    private val configuracaoRepository: ConfiguracaoRepository
) : ViewModel() {

    var aliquotaIr by mutableStateOf<Double?>(null)
        private set
    var carregandoIr by mutableStateOf(false)
        private set
    var erroIr by mutableStateOf<String?>(null)
        private set

    fun calcularIr(dias: Int) {
        viewModelScope.launch {
            carregandoIr = true
            erroIr = null
            runCatching { configuracaoRepository.impostoIr(dias) }
                .onSuccess { aliquotaIr = it; carregandoIr = false }
                .onFailure { erroIr = "Erro ao consultar aliquota"; carregandoIr = false }
        }
    }
}
