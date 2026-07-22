package br.com.contasdomesticas.app.ui.rateio

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
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
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.contasdomesticas.app.data.remote.dto.LancamentoDto
import br.com.contasdomesticas.app.ui.components.SelectField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RateioScreen(
    onVoltar: () -> Unit,
    viewModel: RateioViewModel = hiltViewModel()
) {
    val estado = viewModel.estado
    var despesa by remember { mutableStateOf<LancamentoDto?>(null) }
    val selecionados = remember { mutableStateListOf<Long>() }
    var periodo by remember { mutableStateOf(estado.periodo) }
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
                title = { Text("Divisao de despesas") },
                navigationIcon = { IconButton(onClick = onVoltar) { Icon(Icons.Default.ArrowBack, contentDescription = "Voltar") } }
            )
        },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Dividir despesa (partes iguais)", fontWeight = FontWeight.Bold)
            SelectField(
                label = "despesa",
                opcoes = estado.despesas,
                selecionado = despesa,
                rotulo = { "${it.descricao} (R$ %.2f)".format(it.valor) },
                onSelecionar = { despesa = it },
                modifier = Modifier.padding(top = 8.dp)
            )
            Text("Participantes", modifier = Modifier.padding(top = 8.dp))
            estado.usuarios.forEach { u ->
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
                    FilterChip(
                        selected = selecionados.contains(u.id),
                        onClick = {
                            if (selecionados.contains(u.id)) selecionados.remove(u.id)
                            else selecionados.add(u.id)
                        },
                        label = { Text(u.nomeExibicao.ifBlank { u.login }) }
                    )
                }
            }
            Button(
                onClick = {
                    val d = despesa
                    if (d != null && selecionados.isNotEmpty()) {
                        viewModel.ratearIgual(d.id, selecionados.toList())
                    }
                },
                modifier = Modifier.padding(top = 12.dp)
            ) { Text("Dividir igualmente") }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            Text("Acerto do periodo", fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = periodo,
                onValueChange = { periodo = it },
                label = { Text("periodo (AAAA-MM)") },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )
            OutlinedButton(
                onClick = { viewModel.carregarAcerto(periodo) },
                modifier = Modifier.padding(top = 8.dp)
            ) { Text("Ver acerto") }
            estado.acerto.forEach { item ->
                ListItem(
                    headlineContent = { Text(item.usuarioLogin ?: "#${item.usuarioId}") },
                    trailingContent = {
                        AssistChip(onClick = {}, label = { Text("R$ %.2f".format(item.total)) })
                    }
                )
            }
        }
    }
}
