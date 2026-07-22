package br.com.contasdomesticas.app.ui.investimento

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.contasdomesticas.app.data.remote.dto.AporteRequestDto
import br.com.contasdomesticas.app.data.remote.dto.CarteiraDto
import br.com.contasdomesticas.app.data.remote.dto.InvestimentoDto
import br.com.contasdomesticas.app.data.remote.dto.InvestimentoRequestDto
import br.com.contasdomesticas.app.ui.components.OpcaoOrdenacao
import br.com.contasdomesticas.app.ui.components.OrdenacaoBar
import br.com.contasdomesticas.app.ui.components.SelectField
import br.com.contasdomesticas.app.ui.components.ordenar

private val TIPOS_INVESTIMENTO = listOf(
    "RENDA_FIXA", "RENDA_VARIAVEL", "FUNDO", "CRIPTO", "PREVIDENCIA", "POUPANCA", "RESERVA_EMERGENCIA"
)
private val INDEXADORES = listOf("SELIC", "CDI", "IPCA", "PRE")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvestimentoScreen(
    onVoltar: () -> Unit,
    viewModel: InvestimentoViewModel = hiltViewModel()
) {
    val estado = viewModel.estado
    var mostrarDialog by remember { mutableStateOf(false) }
    var aporteAlvo by remember { mutableStateOf<InvestimentoDto?>(null) }

    var ordemIdx by remember { mutableStateOf(0) }
    var asc by remember { mutableStateOf(true) }
    val opcoes: List<OpcaoOrdenacao<InvestimentoDto>> = remember {
        listOf(
            OpcaoOrdenacao("Nome", compareBy { it.nome }),
            OpcaoOrdenacao("Tipo", compareBy { it.tipoInvestimento }),
            OpcaoOrdenacao("Instituição", compareBy { it.instituicao ?: "" }),
            OpcaoOrdenacao("Taxa", compareBy { it.taxaContratada ?: 0.0 }),
            OpcaoOrdenacao("Aplicação", compareBy { it.dataAplicacao })
        )
    }
    val itens = estado.itens.ordenar(opcoes, ordemIdx, asc)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Investimentos") },
                navigationIcon = { IconButton(onClick = onVoltar) { Icon(Icons.Default.ArrowBack, contentDescription = "Voltar") } }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { mostrarDialog = true }) { Icon(Icons.Default.Add, contentDescription = "Novo") }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Text(
                "Patrimonio: R$ %.2f".format(estado.patrimonio),
                modifier = Modifier.padding(16.dp)
            )
            OrdenacaoBar(opcoes, ordemIdx, asc, { ordemIdx = it }, { asc = !asc })
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(itens, key = { it.id }) { item ->
                    ListItem(
                        headlineContent = { Text(item.nome) },
                        supportingContent = { Text("${item.tipoInvestimento}${item.instituicao?.let { " · $it" } ?: ""}") },
                        trailingContent = {
                            Row {
                                IconButton(onClick = { aporteAlvo = item }) { Icon(Icons.Default.Savings, contentDescription = "Aporte") }
                                IconButton(onClick = { viewModel.remover(item.id) }) { Icon(Icons.Default.Delete, contentDescription = "Remover") }
                            }
                        }
                    )
                }
            }
        }
    }

    if (mostrarDialog) {
        InvestimentoDialog(
            carteiras = estado.carteiras,
            onConfirmar = { req -> viewModel.criar(req); mostrarDialog = false },
            onCancelar = { mostrarDialog = false }
        )
    }

    aporteAlvo?.let { alvo ->
        AporteDialog(
            investimento = alvo,
            onConfirmar = { req -> viewModel.adicionarAporte(alvo.id, req); aporteAlvo = null },
            onCancelar = { aporteAlvo = null }
        )
    }
}

@Composable
private fun InvestimentoDialog(
    carteiras: List<CarteiraDto>,
    onConfirmar: (InvestimentoRequestDto) -> Unit,
    onCancelar: () -> Unit
) {
    var nome by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("RENDA_FIXA") }
    var instituicao by remember { mutableStateOf("") }
    var indexador by remember { mutableStateOf<String?>(null) }
    var taxa by remember { mutableStateOf("") }
    var dataAplicacao by remember { mutableStateOf("") }
    var carteira by remember { mutableStateOf<CarteiraDto?>(null) }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Novo investimento") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = nome,
                    onValueChange = { nome = it },
                    label = { Text("nome") },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                )
                SelectField(
                    label = "tipo",
                    opcoes = TIPOS_INVESTIMENTO,
                    selecionado = tipo,
                    rotulo = { it },
                    onSelecionar = { tipo = it },
                    modifier = Modifier.padding(top = 8.dp)
                )
                OutlinedTextField(
                    value = instituicao,
                    onValueChange = { instituicao = it },
                    label = { Text("instituicao") },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                )
                SelectField(
                    label = "carteira",
                    opcoes = carteiras,
                    selecionado = carteira,
                    rotulo = { it.nome },
                    onSelecionar = { carteira = it },
                    modifier = Modifier.padding(top = 8.dp)
                )
                SelectField(
                    label = "indexador",
                    opcoes = INDEXADORES,
                    selecionado = indexador,
                    rotulo = { it },
                    onSelecionar = { indexador = it },
                    modifier = Modifier.padding(top = 8.dp)
                )
                OutlinedTextField(
                    value = taxa,
                    onValueChange = { taxa = it },
                    label = { Text("taxa contratada (%)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                )
                OutlinedTextField(
                    value = dataAplicacao,
                    onValueChange = { dataAplicacao = it },
                    label = { Text("dataAplicacao (AAAA-MM-DD)") },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val c = carteira
                if (nome.isNotBlank() && dataAplicacao.isNotBlank() && c != null) {
                    onConfirmar(
                        InvestimentoRequestDto(
                            nome = nome,
                            tipoInvestimento = tipo,
                            instituicao = instituicao.ifBlank { null },
                            carteiraId = c.id,
                            indexador = indexador,
                            taxaContratada = taxa.replace(',', '.').toDoubleOrNull(),
                            dataAplicacao = dataAplicacao
                        )
                    )
                }
            }) { Text("Salvar") }
        },
        dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar") } }
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AporteDialog(
    investimento: InvestimentoDto,
    onConfirmar: (AporteRequestDto) -> Unit,
    onCancelar: () -> Unit
) {
    var tipo by remember { mutableStateOf("APORTE") }
    var valor by remember { mutableStateOf("") }
    var data by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Aporte / resgate · ${investimento.nome}") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                FlowRow {
                    TextButton(onClick = { tipo = "APORTE" }) { Text(if (tipo == "APORTE") "• APORTE" else "APORTE") }
                    TextButton(onClick = { tipo = "RESGATE" }) { Text(if (tipo == "RESGATE") "• RESGATE" else "RESGATE") }
                }
                OutlinedTextField(
                    value = valor,
                    onValueChange = { valor = it },
                    label = { Text("valor") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                )
                OutlinedTextField(
                    value = data,
                    onValueChange = { data = it },
                    label = { Text("data (AAAA-MM-DD)") },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val v = valor.replace(',', '.').toDoubleOrNull()
                if (v != null && data.isNotBlank()) {
                    onConfirmar(AporteRequestDto(valor = v, data = data, tipo = tipo))
                }
            }) { Text("Registrar") }
        },
        dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar") } }
    )
}
