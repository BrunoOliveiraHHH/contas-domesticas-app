package br.com.contasdomesticas.app.ui.calculadora

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import br.com.contasdomesticas.app.util.Calculos

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculadoraScreen(
    onVoltar: () -> Unit,
    viewModel: CalculadoraViewModel = hiltViewModel()
) {
    var aba by remember { mutableStateOf(0) }
    val abas = listOf("Investimento", "IR", "Financiamento", "Preco/un")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Calculadoras") },
                navigationIcon = { IconButton(onClick = onVoltar) { Icon(Icons.Default.ArrowBack, contentDescription = "Voltar") } }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            TabRow(selectedTabIndex = aba) {
                abas.forEachIndexed { index, titulo ->
                    Tab(selected = aba == index, onClick = { aba = index }, text = { Text(titulo) })
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                when (aba) {
                    0 -> CalcInvestimento()
                    1 -> CalcIr(viewModel)
                    2 -> CalcFinanciamento()
                    else -> CalcPreco()
                }
            }
        }
    }
}

private fun moeda(v: Double): String = "R$ %.2f".format(v)

@Composable
private fun CampoNumero(valor: String, onValor: (String) -> Unit, rotulo: String) {
    OutlinedTextField(
        value = valor,
        onValueChange = onValor,
        label = { Text(rotulo) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
    )
}

@Composable
private fun LinhaResultado(rotulo: String, valor: String, destaque: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(rotulo, fontWeight = if (destaque) FontWeight.Bold else FontWeight.Normal)
        Text(
            valor,
            modifier = Modifier.fillMaxWidth(),
            fontWeight = if (destaque) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
private fun CalcInvestimento() {
    var inicial by remember { mutableStateOf("1000") }
    var mensal by remember { mutableStateOf("200") }
    var taxa by remember { mutableStateOf("1") }
    var meses by remember { mutableStateOf("12") }

    val r = Calculos.investimento(
        inicial.toDoubleOrNull() ?: 0.0,
        mensal.toDoubleOrNull() ?: 0.0,
        taxa.toDoubleOrNull() ?: 0.0,
        meses.toIntOrNull() ?: 0
    )

    CampoNumero(inicial, { inicial = it }, "Aporte inicial")
    CampoNumero(mensal, { mensal = it }, "Aporte mensal")
    CampoNumero(taxa, { taxa = it }, "Taxa mensal (%)")
    CampoNumero(meses, { meses = it }, "Meses")
    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
    LinhaResultado("Total investido", moeda(r.totalInvestido))
    LinhaResultado("Juros", moeda(r.juros))
    LinhaResultado("Montante", moeda(r.montante), destaque = true)
}

@Composable
private fun CalcIr(viewModel: CalculadoraViewModel) {
    var rendimento by remember { mutableStateOf("1000") }
    var dias by remember { mutableStateOf("200") }

    CampoNumero(rendimento, { rendimento = it }, "Rendimento (R$)")
    CampoNumero(dias, { dias = it }, "Dias aplicado")
    Button(
        onClick = { viewModel.calcularIr(dias.toIntOrNull() ?: 0) },
        modifier = Modifier.padding(top = 8.dp)
    ) { Text(if (viewModel.carregandoIr) "Calculando..." else "Calcular (via API)") }

    val aliquota = viewModel.aliquotaIr
    viewModel.erroIr?.let { Text(it, modifier = Modifier.padding(top = 8.dp)) }
    if (aliquota != null) {
        val r = rendimento.toDoubleOrNull() ?: 0.0
        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
        LinhaResultado("Aliquota", "%.1f%%".format(aliquota * 100))
        LinhaResultado("Imposto", moeda(r * aliquota), destaque = true)
        LinhaResultado("Liquido", moeda(r * (1 - aliquota)))
    }
}

@Composable
private fun CalcFinanciamento() {
    var valor by remember { mutableStateOf("10000") }
    var taxa by remember { mutableStateOf("1.5") }
    var meses by remember { mutableStateOf("24") }
    var sistema by remember { mutableStateOf(Calculos.SistemaAmortizacao.PRICE) }

    val r = Calculos.financiamento(
        valor.toDoubleOrNull() ?: 0.0,
        taxa.toDoubleOrNull() ?: 0.0,
        (meses.toIntOrNull() ?: 1).coerceAtLeast(1),
        sistema
    )

    CampoNumero(valor, { valor = it }, "Valor financiado")
    CampoNumero(taxa, { taxa = it }, "Taxa mensal (%)")
    CampoNumero(meses, { meses = it }, "Meses")
    Row(modifier = Modifier.padding(top = 8.dp)) {
        TextButton(onClick = { sistema = Calculos.SistemaAmortizacao.PRICE }) {
            Text(if (sistema == Calculos.SistemaAmortizacao.PRICE) "• PRICE" else "PRICE")
        }
        TextButton(onClick = { sistema = Calculos.SistemaAmortizacao.SAC }) {
            Text(if (sistema == Calculos.SistemaAmortizacao.SAC) "• SAC" else "SAC")
        }
    }
    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
    LinhaResultado("1a parcela", moeda(r.parcelas.firstOrNull()?.parcela ?: 0.0))
    LinhaResultado("Ultima parcela", moeda(r.parcelas.lastOrNull()?.parcela ?: 0.0))
    LinhaResultado("Total juros", moeda(r.totalJuros))
    LinhaResultado("Total pago", moeda(r.totalPago), destaque = true)
}

@Composable
private fun CalcPreco() {
    var precoA by remember { mutableStateOf("10") }
    var qtdA by remember { mutableStateOf("500") }
    var precoB by remember { mutableStateOf("18") }
    var qtdB by remember { mutableStateOf("1000") }

    val ua = Calculos.precoPorUnidade(precoA.toDoubleOrNull() ?: 0.0, qtdA.toDoubleOrNull() ?: 0.0)
    val ub = Calculos.precoPorUnidade(precoB.toDoubleOrNull() ?: 0.0, qtdB.toDoubleOrNull() ?: 0.0)

    Text("Opcao A", fontWeight = FontWeight.Bold)
    CampoNumero(precoA, { precoA = it }, "Preco (R$)")
    CampoNumero(qtdA, { qtdA = it }, "Quantidade")
    LinhaResultado("Unitario A", moeda(ua))
    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
    Text("Opcao B", fontWeight = FontWeight.Bold)
    CampoNumero(precoB, { precoB = it }, "Preco (R$)")
    CampoNumero(qtdB, { qtdB = it }, "Quantidade")
    LinhaResultado("Unitario B", moeda(ub))
    if (ua > 0 && ub > 0) {
        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
        val texto = when {
            ua == ub -> "Preco unitario igual"
            ua < ub -> "Opcao A e mais vantajosa (${moeda(ua)} por unidade)"
            else -> "Opcao B e mais vantajosa (${moeda(ub)} por unidade)"
        }
        Text(texto, fontWeight = FontWeight.Bold)
    }
}
