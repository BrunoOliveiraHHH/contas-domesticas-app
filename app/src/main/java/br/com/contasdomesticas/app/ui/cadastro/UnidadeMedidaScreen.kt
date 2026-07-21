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
import br.com.contasdomesticas.app.data.remote.dto.UnidadeMedidaRequestDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnidadeMedidaScreen(
    onVoltar: () -> Unit,
    viewModel: UnidadeMedidaViewModel = hiltViewModel()
) {
    val estado = viewModel.estado
    var mostrarDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Unidades de medida") },
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
        UnidadeMedidaDialog(
            onConfirmar = { req -> viewModel.criar(req); mostrarDialog = false },
            onCancelar = { mostrarDialog = false }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun UnidadeMedidaDialog(
    onConfirmar: (UnidadeMedidaRequestDto) -> Unit,
    onCancelar: () -> Unit
) {
    var nome by remember { mutableStateOf("") }
    var sigla by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("UNIDADE") }
    var fatorParaBase by remember { mutableStateOf("") }

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
                    value = sigla,
                    onValueChange = { sigla = it },
                    label = { Text("sigla") },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                )
                androidx.compose.foundation.layout.FlowRow {
                    TextButton(onClick = { tipo = "UNIDADE" }) { Text(if (tipo == "UNIDADE") "• UNIDADE" else "UNIDADE") }
                    TextButton(onClick = { tipo = "PESO" }) { Text(if (tipo == "PESO") "• PESO" else "PESO") }
                    TextButton(onClick = { tipo = "VOLUME" }) { Text(if (tipo == "VOLUME") "• VOLUME" else "VOLUME") }
                    TextButton(onClick = { tipo = "COMPRIMENTO" }) { Text(if (tipo == "COMPRIMENTO") "• COMPRIMENTO" else "COMPRIMENTO") }
                }
                OutlinedTextField(
                    value = fatorParaBase,
                    onValueChange = { fatorParaBase = it },
                    label = { Text("fatorParaBase") },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                )
            }
        },
        confirmButton = {
            Button(onClick = { if (nome.isNotBlank()) onConfirmar(UnidadeMedidaRequestDto(nome = nome, sigla = sigla, tipo = tipo, fatorParaBase = fatorParaBase.toDoubleOrNull())) }) { Text("Salvar") }
        },
        dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar") } }
    )
}
