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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarteiraScreen(
    onVoltar: () -> Unit,
    viewModel: CarteiraViewModel = hiltViewModel()
) {
    val estado = viewModel.estado
    var mostrarDialog by remember { mutableStateOf(false) }

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
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
            items(estado.itens, key = { it.id }) { carteira ->
                ListItem(
                    headlineContent = { Text(carteira.nome) },
                    supportingContent = { Text(carteira.tipo) },
                    trailingContent = {
                        IconButton(onClick = { viewModel.remover(carteira.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Remover")
                        }
                    }
                )
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
