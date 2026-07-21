package br.com.contasdomesticas.app.ui.compra

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.PointOfSale
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.contasdomesticas.app.data.remote.dto.CarteiraDto
import br.com.contasdomesticas.app.data.remote.dto.CategoriaDto
import br.com.contasdomesticas.app.data.remote.dto.ListaCompraDto
import br.com.contasdomesticas.app.data.remote.dto.ListaCompraRequestDto
import br.com.contasdomesticas.app.ui.components.SelectField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaCompraScreen(
    onVoltar: () -> Unit,
    onAbrirLista: (Long) -> Unit,
    viewModel: ListaCompraViewModel = hiltViewModel()
) {
    val estado = viewModel.estado
    var mostrarDialog by remember { mutableStateOf(false) }
    var fecharAlvo by remember { mutableStateOf<ListaCompraDto?>(null) }
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(estado.mensagem, estado.erro) {
        val msg = estado.mensagem ?: estado.erro
        if (msg != null) {
            snackbar.showSnackbar(msg)
            viewModel.limparMensagem()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Listas de compra") },
                navigationIcon = { IconButton(onClick = onVoltar) { Icon(Icons.Default.ArrowBack, contentDescription = "Voltar") } }
            )
        },
        snackbarHost = { SnackbarHost(snackbar) },
        floatingActionButton = {
            FloatingActionButton(onClick = { mostrarDialog = true }) { Icon(Icons.Default.Add, contentDescription = "Nova") }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
            items(estado.itens, key = { it.id }) { item ->
                ListItem(
                    headlineContent = { Text(item.nome) },
                    supportingContent = { Text("${item.tipo}${item.data?.let { " · $it" } ?: ""}") },
                    leadingContent = { AssistChip(onClick = {}, label = { Text(item.status) }) },
                    trailingContent = {
                        IconButton(onClick = { onAbrirLista(item.id) }) { Icon(Icons.Default.ListAlt, contentDescription = "Itens") }
                        IconButton(onClick = { viewModel.duplicar(item.id) }) { Icon(Icons.Default.ContentCopy, contentDescription = "Duplicar") }
                        if (item.status == "ABERTA") {
                            IconButton(onClick = { fecharAlvo = item }) { Icon(Icons.Default.PointOfSale, contentDescription = "Fechar") }
                        }
                        IconButton(onClick = { viewModel.remover(item.id) }) { Icon(Icons.Default.Delete, contentDescription = "Remover") }
                    }
                )
            }
        }
    }

    if (mostrarDialog) {
        ListaCompraDialog(
            carteiras = estado.carteiras,
            onConfirmar = { req -> viewModel.criar(req); mostrarDialog = false },
            onCancelar = { mostrarDialog = false }
        )
    }

    fecharAlvo?.let { alvo ->
        FecharListaDialog(
            categorias = estado.categorias,
            onConfirmar = { categoriaId -> viewModel.fechar(alvo.id, categoriaId); fecharAlvo = null },
            onCancelar = { fecharAlvo = null }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ListaCompraDialog(
    carteiras: List<CarteiraDto>,
    onConfirmar: (ListaCompraRequestDto) -> Unit,
    onCancelar: () -> Unit
) {
    var nome by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("MANTIMENTOS") }
    var data by remember { mutableStateOf("") }
    var carteira by remember { mutableStateOf<CarteiraDto?>(null) }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Nova lista") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = nome,
                    onValueChange = { nome = it },
                    label = { Text("nome") },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                )
                FlowRow {
                    TextButton(onClick = { tipo = "MANTIMENTOS" }) { Text(if (tipo == "MANTIMENTOS") "• MANTIMENTOS" else "MANTIMENTOS") }
                    TextButton(onClick = { tipo = "CONSTRUCAO" }) { Text(if (tipo == "CONSTRUCAO") "• CONSTRUCAO" else "CONSTRUCAO") }
                }
                SelectField(
                    label = "carteira",
                    opcoes = carteiras,
                    selecionado = carteira,
                    rotulo = { it.nome },
                    onSelecionar = { carteira = it },
                    modifier = Modifier.padding(top = 8.dp)
                )
                OutlinedTextField(
                    value = data,
                    onValueChange = { data = it },
                    label = { Text("data (AAAA-MM-DD, opcional)") },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val c = carteira
                if (nome.isNotBlank() && c != null) {
                    onConfirmar(
                        ListaCompraRequestDto(
                            nome = nome,
                            tipo = tipo,
                            carteiraId = c.id,
                            data = data.ifBlank { null }
                        )
                    )
                }
            }) { Text("Salvar") }
        },
        dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar") } }
    )
}

@Composable
private fun FecharListaDialog(
    categorias: List<CategoriaDto>,
    onConfirmar: (Long) -> Unit,
    onCancelar: () -> Unit
) {
    var categoria by remember { mutableStateOf<CategoriaDto?>(null) }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Fechar em despesas") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Gera uma despesa por estabelecimento escolhido nos itens.")
                SelectField(
                    label = "categoria da despesa",
                    opcoes = categorias,
                    selecionado = categoria,
                    rotulo = { it.nome },
                    onSelecionar = { categoria = it },
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        },
        confirmButton = {
            Button(onClick = { categoria?.let { onConfirmar(it.id) } }) { Text("Fechar") }
        },
        dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar") } }
    )
}
