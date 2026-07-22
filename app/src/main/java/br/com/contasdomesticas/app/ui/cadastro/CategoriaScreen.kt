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
import br.com.contasdomesticas.app.data.remote.dto.CategoriaDto
import br.com.contasdomesticas.app.data.remote.dto.CategoriaRequestDto
import br.com.contasdomesticas.app.ui.components.OpcaoOrdenacao
import br.com.contasdomesticas.app.ui.components.OrdenacaoBar
import br.com.contasdomesticas.app.ui.components.ordenar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriaScreen(
    onVoltar: () -> Unit,
    viewModel: CategoriaViewModel = hiltViewModel()
) {
    val estado = viewModel.estado
    var mostrarDialog by remember { mutableStateOf(false) }

    var ordemIdx by remember { mutableStateOf(0) }
    var asc by remember { mutableStateOf(true) }
    val opcoes: List<OpcaoOrdenacao<CategoriaDto>> = remember {
        listOf(
            OpcaoOrdenacao("Nome", compareBy { it.nome }),
            OpcaoOrdenacao("Tipo", compareBy { it.tipo })
        )
    }
    val itens = estado.itens.ordenar(opcoes, ordemIdx, asc)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Categorias") },
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
                    ListItem(
                        headlineContent = { Text(item.nome) },
                        supportingContent = { Text(item.tipo) },
                        trailingContent = {
                            IconButton(onClick = { viewModel.remover(item.id) }) { Icon(Icons.Default.Delete, contentDescription = "Remover") }
                        }
                    )
                }
            }
        }
    }

    if (mostrarDialog) {
        CategoriaDialog(
            onConfirmar = { req -> viewModel.criar(req); mostrarDialog = false },
            onCancelar = { mostrarDialog = false }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CategoriaDialog(
    onConfirmar: (CategoriaRequestDto) -> Unit,
    onCancelar: () -> Unit
) {
    var nome by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("RECEITA") }

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
                androidx.compose.foundation.layout.FlowRow {
                    TextButton(onClick = { tipo = "RECEITA" }) { Text(if (tipo == "RECEITA") "• RECEITA" else "RECEITA") }
                    TextButton(onClick = { tipo = "DESPESA" }) { Text(if (tipo == "DESPESA") "• DESPESA" else "DESPESA") }
                    TextButton(onClick = { tipo = "INVESTIMENTO" }) { Text(if (tipo == "INVESTIMENTO") "• INVESTIMENTO" else "INVESTIMENTO") }
                }
            }
        },
        confirmButton = {
            Button(onClick = { if (nome.isNotBlank()) onConfirmar(CategoriaRequestDto(nome = nome, tipo = tipo)) }) { Text("Salvar") }
        },
        dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar") } }
    )
}
