package br.com.contasdomesticas.app.ui.cadastro

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.contasdomesticas.app.data.remote.dto.ProdutoDto
import br.com.contasdomesticas.app.data.remote.dto.ProdutoRequestDto
import br.com.contasdomesticas.app.ui.components.OpcaoOrdenacao
import br.com.contasdomesticas.app.ui.components.OrdenacaoBar
import br.com.contasdomesticas.app.ui.components.ordenar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProdutoScreen(
    onVoltar: () -> Unit,
    viewModel: ProdutoViewModel = hiltViewModel()
) {
    val estado = viewModel.estado
    var mostrarDialog by remember { mutableStateOf(false) }

    var ordemIdx by remember { mutableStateOf(0) }
    var asc by remember { mutableStateOf(true) }
    val opcoes: List<OpcaoOrdenacao<ProdutoDto>> = remember {
        listOf(
            OpcaoOrdenacao("Nome", compareBy { it.nome }),
            OpcaoOrdenacao("Estoque atual", compareBy { it.estoqueAtual ?: 0.0 }),
            OpcaoOrdenacao("Estoque mínimo", compareBy { it.estoqueMinimo ?: 0.0 }),
            OpcaoOrdenacao("A comprar", compareBy { maxOf(0.0, (it.estoqueMinimo ?: 0.0) - (it.estoqueAtual ?: 0.0)) })
        )
    }
    val itens = estado.itens.ordenar(opcoes, ordemIdx, asc)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Produtos") },
                navigationIcon = { IconButton(onClick = onVoltar) { Icon(Icons.Default.ArrowBack, contentDescription = "Voltar") } }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { mostrarDialog = true }) { Icon(Icons.Default.Add, contentDescription = "Novo") }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            OrdenacaoBar(opcoes, ordemIdx, asc, { ordemIdx = it }, { asc = !asc })
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(itens, key = { it.id }) { item ->
                    val min = item.estoqueMinimo ?: 0.0
                    val atual = item.estoqueAtual ?: 0.0
                    val comprar = maxOf(0.0, min - atual)
                    ListItem(
                        headlineContent = { Text(item.nome) },
                        supportingContent = {
                            Text(
                                "Estoque %.0f/%.0f%s".format(
                                    atual,
                                    min,
                                    if (comprar > 0) " · comprar %.0f".format(comprar) else ""
                                )
                            )
                        },
                        trailingContent = {
                            IconButton(onClick = { viewModel.remover(item.id) }) { Icon(Icons.Default.Delete, contentDescription = "Remover") }
                        }
                    )
                }
            }
        }
    }

    if (mostrarDialog) {
        ProdutoDialog(
            onConfirmar = { req -> viewModel.criar(req); mostrarDialog = false },
            onCancelar = { mostrarDialog = false }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ProdutoDialog(
    onConfirmar: (ProdutoRequestDto) -> Unit,
    onCancelar: () -> Unit
) {
    var nome by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var codigoBarras by remember { mutableStateOf("") }
    var estoqueMinimo by remember { mutableStateOf("0") }
    var estoqueAtual by remember { mutableStateOf("0") }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Novo") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = nome,
                    onValueChange = { nome = it },
                    label = { Text("nome") },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                )
                OutlinedTextField(
                    value = descricao,
                    onValueChange = { descricao = it },
                    label = { Text("descricao") },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                )
                OutlinedTextField(
                    value = codigoBarras,
                    onValueChange = { codigoBarras = it },
                    label = { Text("codigoBarras") },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                )
                OutlinedTextField(
                    value = estoqueMinimo,
                    onValueChange = { estoqueMinimo = it },
                    label = { Text("estoque mínimo") },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                )
                OutlinedTextField(
                    value = estoqueAtual,
                    onValueChange = { estoqueAtual = it },
                    label = { Text("estoque atual") },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                if (nome.isNotBlank()) {
                    onConfirmar(
                        ProdutoRequestDto(
                            nome = nome,
                            descricao = descricao,
                            codigoBarras = codigoBarras,
                            estoqueMinimo = estoqueMinimo.replace(',', '.').toDoubleOrNull(),
                            estoqueAtual = estoqueAtual.replace(',', '.').toDoubleOrNull()
                        )
                    )
                }
            }) { Text("Salvar") }
        },
        dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar") } }
    )
}
