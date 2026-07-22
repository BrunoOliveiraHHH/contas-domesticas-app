package br.com.contasdomesticas.app.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.contasdomesticas.app.data.remote.dto.SaldoMesDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onVoltar: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val estado = viewModel.estado

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
            SaldoLinha(estado.saldo)

            Card(modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Patrimonio investido", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "R$ %.2f".format(estado.patrimonio),
                        style = MaterialTheme.typography.headlineSmall
                    )
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
                                Text("R$ %.2f".format(c.total), fontWeight = FontWeight.Medium)
                                Text(
                                    " ${c.percentual.toInt()}%",
                                    modifier = Modifier.padding(start = 8.dp)
                                )
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
private fun SaldoLinha(saldo: SaldoMesDto?) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SaldoCard("Receitas", saldo?.receitas ?: 0.0, Modifier.weight(1f))
        SaldoCard("Despesas", saldo?.despesas ?: 0.0, Modifier.weight(1f))
        SaldoCard("Saldo", saldo?.saldo ?: 0.0, Modifier.weight(1f))
    }
}

@Composable
private fun SaldoCard(rotulo: String, valor: Double, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(rotulo, style = MaterialTheme.typography.labelMedium)
            Text("R$ %.2f".format(valor), style = MaterialTheme.typography.titleMedium)
        }
    }
}
