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
import br.com.contasdomesticas.app.data.remote.dto.MercadoRequestDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MercadoScreen(
    onVoltar: () -> Unit,
    viewModel: MercadoViewModel = hiltViewModel()
) {
    val estado = viewModel.estado
    var mostrarDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mercados") },
                navigationIcon = { IconButton(onClick = onVoltar) { Icon(Icons.Default.ArrowBack, contentDescription = "Voltar") } }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { mostrarDialog = true }) { Icon(Icons.Default.Add, contentDescription = "Novo") }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
            items(estado.itens, key = { it.id }) { item ->
                ListItem(
                    headlineContent = { Text(item.nome) },
                    trailingContent = {
                        IconButton(onClick = { viewModel.remover(item.id) }) { Icon(Icons.Default.Delete, contentDescription = "Remover") }
                    }
                )
            }
        }
    }

    if (mostrarDialog) {
        MercadoDialog(
            onConfirmar = { req -> viewModel.criar(req); mostrarDialog = false },
            onCancelar = { mostrarDialog = false }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MercadoDialog(
    onConfirmar: (MercadoRequestDto) -> Unit,
    onCancelar: () -> Unit
) {
    var nome by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("SUPERMERCADO") }
    var endereco by remember { mutableStateOf("") }
    var bairro by remember { mutableStateOf("") }

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
                    TextButton(onClick = { tipo = "SUPERMERCADO" }) { Text(if (tipo == "SUPERMERCADO") "• SUPERMERCADO" else "SUPERMERCADO") }
                    TextButton(onClick = { tipo = "ARMAZEM" }) { Text(if (tipo == "ARMAZEM") "• ARMAZEM" else "ARMAZEM") }
                    TextButton(onClick = { tipo = "MERCEARIA" }) { Text(if (tipo == "MERCEARIA") "• MERCEARIA" else "MERCEARIA") }
                    TextButton(onClick = { tipo = "CONSTRUCAO" }) { Text(if (tipo == "CONSTRUCAO") "• CONSTRUCAO" else "CONSTRUCAO") }
                    TextButton(onClick = { tipo = "FARMACIA" }) { Text(if (tipo == "FARMACIA") "• FARMACIA" else "FARMACIA") }
                    TextButton(onClick = { tipo = "OUTRO" }) { Text(if (tipo == "OUTRO") "• OUTRO" else "OUTRO") }
                }
                OutlinedTextField(
                    value = endereco,
                    onValueChange = { endereco = it },
                    label = { Text("endereco") },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                )
                OutlinedTextField(
                    value = bairro,
                    onValueChange = { bairro = it },
                    label = { Text("bairro") },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                )
            }
        },
        confirmButton = {
            Button(onClick = { if (nome.isNotBlank()) onConfirmar(MercadoRequestDto(nome = nome, tipo = tipo, endereco = endereco, bairro = bairro)) }) { Text("Salvar") }
        },
        dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar") } }
    )
}
