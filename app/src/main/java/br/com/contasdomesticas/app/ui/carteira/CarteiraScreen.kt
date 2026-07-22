package br.com.contasdomesticas.app.ui.carteira

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.material3.AlertDialog
import br.com.contasdomesticas.app.data.remote.dto.CarteiraDto
import br.com.contasdomesticas.app.ui.components.OpcaoOrdenacao
import br.com.contasdomesticas.app.ui.components.OrdenacaoBar
import br.com.contasdomesticas.app.ui.components.ordenar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarteiraScreen(
    onVoltar: () -> Unit,
    viewModel: CarteiraViewModel = hiltViewModel()
) {
    val estado = viewModel.estado
    var mostrarDialog by remember { mutableStateOf(false) }

    var ordemIdx by remember { mutableStateOf(0) }
    var asc by remember { mutableStateOf(true) }
    val opcoes: List<OpcaoOrdenacao<CarteiraDto>> = remember {
        listOf(
            OpcaoOrdenacao("Nome", compareBy { it.nome }),
            OpcaoOrdenacao("Tipo", compareBy { it.tipo }),
            OpcaoOrdenacao("Saldo inicial", compareBy { it.saldoInicial ?: 0.0 })
        )
    }
    val itens = estado.itens.ordenar(opcoes, ordemIdx, asc)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Carteiras") },
                navigationIcon = {
                    IconButton(onClick = onVoltar) { Icon(Icons.Default.ArrowBack, contentDescription = "Voltar") }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { mostrarDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Nova")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            OrdenacaoBar(opcoes, ordemIdx, asc, { ordemIdx = it }, { asc = !asc })
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(itens, key = { it.id }) { carteira ->
                    ListItem(
                        headlineContent = { Text(carteira.nome) },
                        supportingContent = { Text("${carteira.tipo} · R$ %.2f".format(carteira.saldoInicial ?: 0.0)) },
                        trailingContent = {
                            IconButton(onClick = { viewModel.remover(carteira.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Remover")
                            }
                        }
                    )
                }
            }
        }
    }

    if (mostrarDialog) {
        NovaCarteiraDialog(
            onConfirmar = { nome, tipo ->
                viewModel.criar(nome, tipo)
                mostrarDialog = false
            },
            onCancelar = { mostrarDialog = false }
        )
    }
}

@Composable
private fun NovaCarteiraDialog(
    onConfirmar: (String, String) -> Unit,
    onCancelar: () -> Unit
) {
    var nome by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("FAMILIAR") }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Nova carteira") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = nome,
                    onValueChange = { nome = it },
                    label = { Text("Nome") },
                    modifier = Modifier.fillMaxWidth()
                )
                androidx.compose.foundation.layout.Row(modifier = Modifier.padding(top = 12.dp)) {
                    TextButton(onClick = { tipo = "FAMILIAR" }) {
                        Text(if (tipo == "FAMILIAR") "• Familiar" else "Familiar")
                    }
                    TextButton(onClick = { tipo = "INDIVIDUAL" }) {
                        Text(if (tipo == "INDIVIDUAL") "• Individual" else "Individual")
                    }
                }
            }
        },
        confirmButton = { Button(onClick = { if (nome.isNotBlank()) onConfirmar(nome, tipo) }) { Text("Salvar") } },
        dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar") } }
    )
}
