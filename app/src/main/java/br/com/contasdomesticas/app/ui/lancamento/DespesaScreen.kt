package br.com.contasdomesticas.app.ui.lancamento

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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.contasdomesticas.app.data.remote.dto.CarteiraDto
import br.com.contasdomesticas.app.data.remote.dto.CategoriaDto
import br.com.contasdomesticas.app.data.remote.dto.LancamentoDto
import br.com.contasdomesticas.app.ui.components.OpcaoOrdenacao
import br.com.contasdomesticas.app.ui.components.OrdenacaoBar
import br.com.contasdomesticas.app.ui.components.SelectField
import br.com.contasdomesticas.app.ui.components.ordenar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DespesaScreen(
    onVoltar: () -> Unit,
    viewModel: DespesaViewModel = hiltViewModel()
) {
    val estado = viewModel.estado
    var mostrarDialog by remember { mutableStateOf(false) }
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(estado.mensagem, estado.erro) {
        val msg = estado.mensagem ?: estado.erro
        if (msg != null) {
            snackbar.showSnackbar(msg)
            viewModel.limparMensagem()
        }
    }

    var ordemIdx by remember { mutableStateOf(0) }
    var asc by remember { mutableStateOf(true) }
    val opcoes: List<OpcaoOrdenacao<LancamentoDto>> = remember(estado.categorias) {
        val nomeCat = { id: Long -> estado.categorias.find { it.id == id }?.nome ?: "" }
        val ordemStatus = mapOf("ATRASADO" to 0, "PENDENTE" to 1, "PAGO" to 2)
        listOf(
            OpcaoOrdenacao("Nome", compareBy { it.descricao }),
            OpcaoOrdenacao("Valor", compareBy { it.valor }),
            OpcaoOrdenacao("Vencimento", compareBy { it.dataVencimento ?: "" }),
            OpcaoOrdenacao("Status", compareBy { ordemStatus[it.status ?: ""] ?: 9 }),
            OpcaoOrdenacao("Categoria", compareBy { nomeCat(it.categoriaId) })
        )
    }
    val itens = estado.itens.ordenar(opcoes, ordemIdx, asc)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Despesas") },
                navigationIcon = { IconButton(onClick = onVoltar) { Icon(Icons.Default.ArrowBack, contentDescription = "Voltar") } }
            )
        },
        snackbarHost = { SnackbarHost(snackbar) },
        floatingActionButton = {
            FloatingActionButton(onClick = { mostrarDialog = true }) { Icon(Icons.Default.Add, contentDescription = "Nova") }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            OrdenacaoBar(opcoes, ordemIdx, asc, { ordemIdx = it }, { asc = !asc })
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(itens, key = { it.id }) { item ->
                    val cat = estado.categorias.find { it.id == item.categoriaId }?.nome ?: "-"
                ListItem(
                    headlineContent = { Text(item.descricao) },
                    supportingContent = { Text("$cat · R$ %.2f · %s".format(item.valor, item.dataCompetencia)) },
                    leadingContent = { item.status?.let { AssistChip(onClick = {}, label = { Text(it) }) } },
                    trailingContent = {
                        Row {
                            if (item.status != "PAGO") {
                                IconButton(onClick = { viewModel.pagar(item.id) }) { Icon(Icons.Default.Check, contentDescription = "Pagar") }
                            }
                            IconButton(onClick = { viewModel.remover(item.id) }) { Icon(Icons.Default.Delete, contentDescription = "Remover") }
                        }
                    }
                )
                }
            }
        }
    }

    if (mostrarDialog) {
        DespesaDialog(
            carteiras = estado.carteiras,
            categorias = estado.categorias,
            viewModel = viewModel,
            onFechar = { mostrarDialog = false }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DespesaDialog(
    carteiras: List<CarteiraDto>,
    categorias: List<CategoriaDto>,
    viewModel: DespesaViewModel,
    onFechar: () -> Unit
) {
    var nome by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf<CategoriaDto?>(null) }
    var carteira by remember { mutableStateOf<CarteiraDto?>(null) }
    var tipo by remember { mutableStateOf("UNICA") } // UNICA | RECORRENTE | PARCELADA

    // Unica / Recorrente
    var valor by remember { mutableStateOf("") }
    var dataVencimento by remember { mutableStateOf("") }
    var diaVencimento by remember { mutableStateOf("10") }

    // Parcelada
    var parcelas by remember { mutableStateOf("2") }
    var modoValor by remember { mutableStateOf("TOTAL") } // TOTAL | PARCELA
    var valorEntrada by remember { mutableStateOf("") }
    var primeiroVencimento by remember { mutableStateOf("") }

    val nParcelas = parcelas.toIntOrNull() ?: 0
    val entrada = valorEntrada.replace(',', '.').toDoubleOrNull() ?: 0.0
    val valorTotal = if (modoValor == "TOTAL") entrada else entrada * nParcelas
    val valorParcela = if (modoValor == "TOTAL") (if (nParcelas > 0) entrada / nParcelas else 0.0) else entrada

    fun rotuloTipo(t: String) = when (t) {
        "UNICA" -> "Única"
        "RECORRENTE" -> "Recorrente fixa"
        else -> "Parcelada"
    }

    AlertDialog(
        onDismissRequest = onFechar,
        title = { Text("Nova despesa") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = nome,
                    onValueChange = { nome = it },
                    label = { Text("nome") },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                )
                SelectField(
                    label = "categoria",
                    opcoes = categorias,
                    selecionado = categoria,
                    rotulo = { it.nome },
                    onSelecionar = { categoria = it },
                    modifier = Modifier.padding(top = 8.dp)
                )
                SelectField(
                    label = "carteira",
                    opcoes = carteiras,
                    selecionado = carteira,
                    rotulo = { it.nome },
                    onSelecionar = { carteira = it },
                    modifier = Modifier.padding(top = 8.dp)
                )
                FlowRow {
                    listOf("UNICA", "RECORRENTE", "PARCELADA").forEach { t ->
                        TextButton(onClick = { tipo = t }) {
                            Text(if (tipo == t) "• ${rotuloTipo(t)}" else rotuloTipo(t))
                        }
                    }
                }

                when (tipo) {
                    "UNICA" -> {
                        OutlinedTextField(
                            value = valor,
                            onValueChange = { valor = it },
                            label = { Text("valor") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                        )
                        OutlinedTextField(
                            value = dataVencimento,
                            onValueChange = { dataVencimento = it },
                            label = { Text("vencimento (AAAA-MM-DD)") },
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                        )
                    }
                    "RECORRENTE" -> {
                        OutlinedTextField(
                            value = valor,
                            onValueChange = { valor = it },
                            label = { Text("valor mensal") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                        )
                        OutlinedTextField(
                            value = diaVencimento,
                            onValueChange = { diaVencimento = it },
                            label = { Text("dia de vencimento (1-31)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                        )
                    }
                    else -> {
                        OutlinedTextField(
                            value = parcelas,
                            onValueChange = { parcelas = it },
                            label = { Text("nº de parcelas") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                        )
                        FlowRow {
                            TextButton(onClick = { modoValor = "TOTAL" }) { Text(if (modoValor == "TOTAL") "• Valor total" else "Valor total") }
                            TextButton(onClick = { modoValor = "PARCELA" }) { Text(if (modoValor == "PARCELA") "• Valor por parcela" else "Valor por parcela") }
                        }
                        OutlinedTextField(
                            value = valorEntrada,
                            onValueChange = { valorEntrada = it },
                            label = { Text(if (modoValor == "TOTAL") "valor total" else "valor por parcela") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                        )
                        OutlinedTextField(
                            value = primeiroVencimento,
                            onValueChange = { primeiroVencimento = it },
                            label = { Text("primeiro vencimento (AAAA-MM-DD)") },
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                        )
                        Text(
                            "${nParcelas}x de R$ %.2f · total R$ %.2f".format(valorParcela, valorTotal),
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val cat = categoria
                val c = carteira
                if (nome.isBlank() || cat == null || c == null) return@Button
                when (tipo) {
                    "UNICA" -> {
                        val v = valor.replace(',', '.').toDoubleOrNull() ?: return@Button
                        viewModel.criarUnica(nome, cat.id, c.id, v, dataVencimento.ifBlank { null })
                    }
                    "RECORRENTE" -> {
                        val v = valor.replace(',', '.').toDoubleOrNull() ?: return@Button
                        viewModel.criarRecorrente(nome, cat.id, c.id, v, diaVencimento.toIntOrNull())
                    }
                    else -> {
                        if (nParcelas < 2 || valorTotal <= 0 || primeiroVencimento.isBlank()) return@Button
                        viewModel.criarParcelada(nome, cat.id, c.id, valorTotal, nParcelas, primeiroVencimento)
                    }
                }
                onFechar()
            }) { Text("Salvar") }
        },
        dismissButton = { TextButton(onClick = onFechar) { Text("Cancelar") } }
    )
}
