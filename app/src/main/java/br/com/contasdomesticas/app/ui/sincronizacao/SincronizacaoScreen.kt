package br.com.contasdomesticas.app.ui.sincronizacao

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SincronizacaoScreen(
    onVoltar: () -> Unit,
    viewModel: SincronizacaoViewModel = hiltViewModel()
) {
    val estado = viewModel.estado
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
                title = { Text("Sincronização") },
                navigationIcon = { IconButton(onClick = onVoltar) { Icon(Icons.Default.ArrowBack, contentDescription = "Voltar") } }
            )
        },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Ultima sincronizacao", fontWeight = FontWeight.Bold)
                    Text(estado.ultimaSync ?: "nunca")
                    Text(
                        "Delta de mercados (entidade de referencia). Merge por versao com tombstone (deletado).",
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Button(
                onClick = { viewModel.sincronizar() },
                enabled = !estado.sincronizando,
                modifier = Modifier.padding(top = 12.dp)
            ) {
                Icon(Icons.Default.Sync, contentDescription = null)
                Text(
                    if (estado.sincronizando) "  Sincronizando..." else "  Sincronizar agora",
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            if (estado.registros.isNotEmpty()) {
                OutlinedButton(
                    onClick = { viewModel.enviar() },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Icon(Icons.Default.Upload, contentDescription = null)
                    Text("  Reenviar (merge)", modifier = Modifier.padding(start = 4.dp))
                }
            }

            LazyColumn(modifier = Modifier.fillMaxSize().padding(top = 12.dp)) {
                items(estado.registros, key = { it.uuid }) { r ->
                    ListItem(
                        headlineContent = { Text(r.nome ?: r.uuid) },
                        supportingContent = { Text("${r.tipo ?: "-"} · v${r.versao ?: 0} · ${r.atualizadoEm ?: "-"}") },
                        trailingContent = {
                            AssistChip(
                                onClick = {},
                                label = { Text(if (r.deletado) "removido" else "ativo") }
                            )
                        }
                    )
                }
            }
        }
    }
}
