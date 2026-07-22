package br.com.contasdomesticas.app.ui.configuracao

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.contasdomesticas.app.data.remote.dto.ParametroDto
import br.com.contasdomesticas.app.data.remote.dto.ParametroRequestDto
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfiguracaoScreen(
    onVoltar: () -> Unit,
    viewModel: ConfiguracaoViewModel = hiltViewModel()
) {
    val estado = viewModel.estado
    var editar by remember { mutableStateOf<ParametroDto?>(null) }
    var novo by remember { mutableStateOf(false) }
    val snackbar = remember { SnackbarHostState() }
    val escopo = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configuração") },
                navigationIcon = { IconButton(onClick = onVoltar) { Icon(Icons.Default.ArrowBack, contentDescription = "Voltar") } }
            )
        },
        snackbarHost = { SnackbarHost(snackbar) },
        floatingActionButton = {
            FloatingActionButton(onClick = { novo = true }) { Icon(Icons.Default.Add, contentDescription = "Novo parametro") }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
            item {
                Text(
                    "Parametros (indices e aliquotas)",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp)
                )
            }
            items(estado.parametros, key = { it.id }) { p ->
                ListItem(
                    headlineContent = { Text("${p.chave} = ${p.valor}") },
                    supportingContent = { Text("Vigencia ${p.vigenciaInicio}${p.descricao?.let { " · $it" } ?: ""}") },
                    trailingContent = {
                        IconButton(onClick = { editar = p }) { Icon(Icons.Default.Edit, contentDescription = "Editar") }
                        IconButton(onClick = { viewModel.remover(p.id) }) { Icon(Icons.Default.Delete, contentDescription = "Remover") }
                    }
                )
            }
            item { HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp)) }
            item {
                PreferenciaPanel(
                    onResolver = { chave, cb -> viewModel.resolverPreferencia(chave, cb) },
                    onGravar = { chave, valor ->
                        viewModel.gravarPreferencia(chave, valor) {
                            escopo.launch { snackbar.showSnackbar("Preferencia gravada") }
                        }
                    }
                )
            }
        }
    }

    if (novo) {
        ParametroDialog(
            inicial = null,
            onConfirmar = { req -> viewModel.salvar(req, null); novo = false },
            onCancelar = { novo = false }
        )
    }
    editar?.let { alvo ->
        ParametroDialog(
            inicial = alvo,
            onConfirmar = { req -> viewModel.salvar(req, alvo.id); editar = null },
            onCancelar = { editar = null }
        )
    }
}

@Composable
private fun ParametroDialog(
    inicial: ParametroDto?,
    onConfirmar: (ParametroRequestDto) -> Unit,
    onCancelar: () -> Unit
) {
    var chave by remember { mutableStateOf(inicial?.chave ?: "") }
    var valor by remember { mutableStateOf(inicial?.valor?.toString() ?: "") }
    var vigencia by remember { mutableStateOf(inicial?.vigenciaInicio ?: "") }
    var descricao by remember { mutableStateOf(inicial?.descricao ?: "") }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text(if (inicial == null) "Novo parametro" else "Editar parametro") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = chave,
                    onValueChange = { chave = it },
                    label = { Text("chave (ex: SELIC, CDI, IPCA)") },
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
                    value = vigencia,
                    onValueChange = { vigencia = it },
                    label = { Text("vigenciaInicio (AAAA-MM-DD)") },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                )
                OutlinedTextField(
                    value = descricao,
                    onValueChange = { descricao = it },
                    label = { Text("descricao") },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val v = valor.replace(',', '.').toDoubleOrNull()
                if (chave.isNotBlank() && v != null && vigencia.isNotBlank()) {
                    onConfirmar(
                        ParametroRequestDto(
                            chave = chave,
                            valor = v,
                            vigenciaInicio = vigencia,
                            descricao = descricao.ifBlank { null }
                        )
                    )
                }
            }) { Text("Salvar") }
        },
        dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar") } }
    )
}

@Composable
private fun PreferenciaPanel(
    onResolver: (String, (String?) -> Unit) -> Unit,
    onGravar: (String, String) -> Unit
) {
    var chave by remember { mutableStateOf("") }
    var valor by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text("Preferencia", fontWeight = FontWeight.Bold)
        OutlinedTextField(
            value = chave,
            onValueChange = { chave = it },
            label = { Text("chave (ex: tema, moeda_padrao)") },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )
        OutlinedTextField(
            value = valor,
            onValueChange = { valor = it },
            label = { Text("valor") },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = { if (chave.isNotBlank()) onResolver(chave) { v -> valor = v ?: "" } }
            ) { Text("Resolver") }
            Button(
                onClick = { if (chave.isNotBlank()) onGravar(chave, valor) }
            ) { Text("Gravar") }
        }
    }
}
