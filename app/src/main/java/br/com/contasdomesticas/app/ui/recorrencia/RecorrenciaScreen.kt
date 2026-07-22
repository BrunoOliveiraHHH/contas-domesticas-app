package br.com.contasdomesticas.app.ui.recorrencia

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.PlayArrow
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
import br.com.contasdomesticas.app.data.remote.dto.RecorrenciaDto
import br.com.contasdomesticas.app.data.remote.dto.RecorrenciaRequestDto
import br.com.contasdomesticas.app.ui.components.SelectField

private val FREQUENCIAS = listOf("SEMANAL", "MENSAL", "ANUAL")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecorrenciaScreen(
    onVoltar: () -> Unit,
    viewModel: RecorrenciaViewModel = hiltViewModel()
) {
    val estado = viewModel.estado
    var mostrarDialog by remember { mutableStateOf(false) }
    var gerarAlvo by remember { mutableStateOf<RecorrenciaDto?>(null) }
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
                title = { Text("Recorrências") },
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
                    headlineContent = { Text(item.descricao) },
                    supportingContent = { Text("${item.tipo} · ${item.frequencia} · R$ %.2f".format(item.valor)) },
                    leadingContent = { AssistChip(onClick = {}, label = { Text(if (item.ativa) "ativa" else "inativa") }) },
                    trailingContent = {
                        IconButton(onClick = { gerarAlvo = item }) { Icon(Icons.Default.PlayArrow, contentDescription = "Gerar") }
                        IconButton(onClick = { viewModel.remover(item.id) }) { Icon(Icons.Default.Delete, contentDescription = "Remover") }
                    }
                )
            }
        }
    }

    if (mostrarDialog) {
        RecorrenciaDialog(
            carteiras = estado.carteiras,
            categorias = estado.categorias,
            onConfirmar = { req -> viewModel.criar(req); mostrarDialog = false },
            onCancelar = { mostrarDialog = false }
        )
    }

    gerarAlvo?.let { alvo ->
        GerarDialog(
            recorrencia = alvo,
            onConfirmar = { competencia -> viewModel.gerar(alvo.id, competencia); gerarAlvo = null },
            onCancelar = { gerarAlvo = null }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RecorrenciaDialog(
    carteiras: List<CarteiraDto>,
    categorias: List<CategoriaDto>,
    onConfirmar: (RecorrenciaRequestDto) -> Unit,
    onCancelar: () -> Unit
) {
    var descricao by remember { mutableStateOf("") }
    var valor by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("DESPESA") }
    var carteira by remember { mutableStateOf<CarteiraDto?>(null) }
    var categoria by remember { mutableStateOf<CategoriaDto?>(null) }
    var frequencia by remember { mutableStateOf("MENSAL") }
    var diaVencimento by remember { mutableStateOf("1") }
    var dataInicio by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Nova recorrencia") },
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
                FlowRow {
                    TextButton(onClick = { tipo = "DESPESA" }) { Text(if (tipo == "DESPESA") "• DESPESA" else "DESPESA") }
                    TextButton(onClick = { tipo = "RECEITA" }) { Text(if (tipo == "RECEITA") "• RECEITA" else "RECEITA") }
                }
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
                SelectField(
                    label = "frequencia",
                    opcoes = FREQUENCIAS,
                    selecionado = frequencia,
                    rotulo = { it },
                    onSelecionar = { frequencia = it },
                    modifier = Modifier.padding(top = 8.dp)
                )
                OutlinedTextField(
                    value = diaVencimento,
                    onValueChange = { diaVencimento = it },
                    label = { Text("dia de vencimento (1-31)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                )
                OutlinedTextField(
                    value = dataInicio,
                    onValueChange = { dataInicio = it },
                    label = { Text("dataInicio (AAAA-MM-DD)") },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val c = carteira
                val cat = categoria
                val v = valor.replace(',', '.').toDoubleOrNull()
                if (descricao.isNotBlank() && v != null && c != null && cat != null && dataInicio.isNotBlank()) {
                    onConfirmar(
                        RecorrenciaRequestDto(
                            descricao = descricao,
                            valor = v,
                            tipo = tipo,
                            carteiraId = c.id,
                            categoriaId = cat.id,
                            frequencia = frequencia,
                            diaVencimento = diaVencimento.toIntOrNull(),
                            dataInicio = dataInicio
                        )
                    )
                }
            }) { Text("Salvar") }
        },
        dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar") } }
    )
}

@Composable
private fun GerarDialog(
    recorrencia: RecorrenciaDto,
    onConfirmar: (String) -> Unit,
    onCancelar: () -> Unit
) {
    var competencia by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Gerar · ${recorrencia.descricao}") },
        text = {
            OutlinedTextField(
                value = competencia,
                onValueChange = { competencia = it },
                label = { Text("competencia (AAAA-MM-DD)") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(onClick = { if (competencia.isNotBlank()) onConfirmar(competencia) }) { Text("Gerar") }
        },
        dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar") } }
    )
}
