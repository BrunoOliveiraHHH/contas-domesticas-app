package br.com.contasdomesticas.app.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.ceil

private fun money(v: Double): String = "R$ %.2f".format(v)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onVoltar: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val estado = viewModel.estado
    val s = estado.saldo

    val receitas = s?.receitas ?: 0.0
    val despesas = s?.despesas ?: 0.0
    val saldo = s?.saldo ?: 0.0
    val aPagar = s?.aPagar ?: 0.0
    val atrasadas = s?.atrasadas ?: 0.0
    val assinaturas = s?.assinaturas ?: 0.0
    val comprometimento = if (receitas > 0) despesas / receitas * 100 else 0.0
    val poupanca = if (receitas > 0) saldo / receitas * 100 else 0.0
    val pctPago = if (despesas > 0) (despesas - aPagar) / despesas * 100 else 0.0
    val reserva = if (despesas > 0) estado.patrimonio / despesas else 0.0
    val maior = estado.despesasPorCategoria.firstOrNull()

    val kpis = listOf(
        "Receitas" to money(receitas),
        "Despesas" to money(despesas),
        "A pagar" to money(aPagar),
        "Atrasadas" to money(atrasadas),
        "Saldo" to money(saldo),
        "Saldo projetado" to money(saldo - aPagar),
        "Comprometimento" to "${comprometimento.toInt()}%",
        "Taxa de poupança" to "${poupanca.toInt()}%",
        "% pago do mês" to "${pctPago.toInt()}%",
        "Assinaturas/fixas" to money(assinaturas),
        "Reserva de emergência" to "%.1f meses".format(reserva),
        "Maior categoria" to (maior?.let { "${it.categoriaNome} · ${money(it.total)}" } ?: "—")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard · ${estado.periodo}") },
                navigationIcon = { IconButton(onClick = onVoltar) { Icon(Icons.Default.ArrowBack, contentDescription = "Voltar") } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(16.dp)
        ) {
            // KPIs 2 por linha (mesmo tamanho)
            kpis.chunked(2).forEach { par ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    par.forEach { (rotulo, valor) -> KpiCard(rotulo, valor, Modifier.weight(1f)) }
                    if (par.size == 1) Spacer(Modifier.weight(1f))
                }
            }

            SimuladorQuitacao(sobraMensal = if (saldo > 0) saldo else 0.0)

            Card(modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Patrimonio investido", style = MaterialTheme.typography.titleMedium)
                    Text(money(estado.patrimonio), style = MaterialTheme.typography.headlineSmall)
                }
            }

            Card(modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Despesas por categoria", style = MaterialTheme.typography.titleMedium)
                    if (estado.despesasPorCategoria.isEmpty()) {
                        Text("Sem despesas no periodo.", modifier = Modifier.padding(top = 8.dp))
                    }
                    estado.despesasPorCategoria.forEach { c ->
                        Column(modifier = Modifier.padding(top = 8.dp)) {
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Text(c.categoriaNome, modifier = Modifier.weight(1f))
                                Text(money(c.total), fontWeight = FontWeight.Medium)
                                Text(" ${c.percentual.toInt()}%", modifier = Modifier.padding(start = 8.dp))
                            }
                            LinearProgressIndicator(
                                progress = { (c.percentual / 100.0).toFloat().coerceIn(0f, 1f) },
                                modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun KpiCard(rotulo: String, valor: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier.height(84.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(rotulo, style = MaterialTheme.typography.labelMedium)
            Text(valor, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun SimuladorQuitacao(sobraMensal: Double) {
    var valor by remember { mutableStateOf("") }
    val v = valor.replace(',', '.').toDoubleOrNull() ?: 0.0

    val texto = when {
        v <= 0 -> "Informe um valor a quitar."
        sobraMensal <= 0 -> "Sem sobra mensal no momento — não dá para projetar."
        else -> {
            val meses = ceil(v / sobraMensal).toInt()
            val alvo = Calendar.getInstance().apply { add(Calendar.MONTH, meses) }
            val rotulo = SimpleDateFormat("MM/yyyy", Locale.getDefault()).format(alvo.time)
            "Guardando ~${money(sobraMensal)}/mês, quita em ~$meses ${if (meses == 1) "mês" else "meses"} (previsão: $rotulo)."
        }
    }

    Card(modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Simulador de quitação", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = valor,
                onValueChange = { valor = it },
                label = { Text("valor a quitar") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )
            Text(texto, modifier = Modifier.padding(top = 8.dp))
        }
    }
}
