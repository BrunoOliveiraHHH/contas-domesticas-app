package br.com.contasdomesticas.app.ui.lancamento

import androidx.compose.foundation.layout.Column
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
import br.com.contasdomesticas.app.data.remote.dto.CarteiraDto
import br.com.contasdomesticas.app.data.remote.dto.CategoriaDto
import br.com.contasdomesticas.app.data.remote.dto.DespesaRequestDto
import br.com.contasdomesticas.app.ui.components.SelectField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DespesaScreen(
    onVoltar: () -> Unit,
    viewModel: DespesaViewModel = hiltViewModel()
) {
    val estado = viewModel.estado
    var mostrarDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Despesas") },
                navigationIcon = { IconButton(onClick = onVoltar) { Icon(Icons.Default.ArrowBack, contentDescription = "Voltar") } }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { mostrarDialog = true }) { Icon(Icons.Default.Add, contentDescription = "Nova") }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
            items(estado.itens, key = { it.id }) { item ->
                ListItem(
                    headlineContent = { Text(item.descricao) },
                    supportingContent = { Text("R$ %.2f · %s".format(item.valor, item.dataCompetencia)) },
                    leadingContent = { item.status?.let { AssistChip(onClick = {}, label = { Text(it) }) } },
                    trailingContent = {
                        if (item.status != "PAGO") {
                            IconButton(onClick = { viewModel.pagar(item.id) }) { Icon(Icons.Default.Check, contentDescription = "Pagar") }
                        }
                        IconButton(onClick = { viewModel.remover(item.id) }) { Icon(Icons.Default.Delete, contentDescription = "Remover") }
                    }
                )
            }
        }
    }

    if (mostrarDialog) {
        DespesaDialog(
            carteiras = estado.carteiras,
            categorias = estado.categorias,
            onConfirmar = { req -> viewModel.criar(req); mostrarDialog = false },
            onCancelar = { mostrarDialog = false }
        )
    }
}

@Composable
private fun DespesaDialog(
    carteiras: List<CarteiraDto>,
    categorias: List<CategoriaDto>,
    onConfirmar: (DespesaRequestDto) -> Unit,
    onCancelar: () -> Unit
) {
    var descricao by remember { mutableStateOf("") }
    var valor by remember { mutableStateOf("") }
    var dataCompetencia by remember { mutableStateOf("") }
    var dataVencimento by remember { mutableStateOf("") }
    var carteira by remember { mutableStateOf<CarteiraDto?>(null) }
    var categoria by remember { mutableStateOf<CategoriaDto?>(null) }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Nova despesa") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = descricao,
                    onValueChange = { descricao = it },
                    label = { Text("descricao") },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                )
                OutlinedTextField(
                    value = valor,
                    onValueChange = { valor = it },
                    label = { Text("valor") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                )
                OutlinedTextField(
                    value = dataCompetencia,
                    onValueChange = { dataCompetencia = it },
                    label = { Text("dataCompetencia (AAAA-MM-DD)") },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                )
                OutlinedTextField(
                    value = dataVencimento,
                    onValueChange = { dataVencimento = it },
                    label = { Text("dataVencimento (AAAA-MM-DD, opcional)") },
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
                    label = "categoria",
                    opcoes = categorias,
                    selecionado = categoria,
                    rotulo = { it.nome },
                    onSelecionar = { categoria = it },
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val c = carteira
                val cat = categoria
                val v = valor.replace(',', '.').toDoubleOrNull()
                if (descricao.isNotBlank() && v != null && dataCompetencia.isNotBlank() && c != null && cat != null) {
                    onConfirmar(
                        DespesaRequestDto(
                            descricao = descricao,
                            valor = v,
                            dataCompetencia = dataCompetencia,
                            carteiraId = c.id,
                            categoriaId = cat.id,
                            dataVencimento = dataVencimento.ifBlank { null }
                        )
                    )
                }
            }) { Text("Salvar") }
        },
        dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar") } }
    )
}
