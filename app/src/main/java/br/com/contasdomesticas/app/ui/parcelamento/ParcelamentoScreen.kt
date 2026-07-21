package br.com.contasdomesticas.app.ui.parcelamento

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import br.com.contasdomesticas.app.data.remote.dto.ParcelamentoRequestDto
import br.com.contasdomesticas.app.ui.components.SelectField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParcelamentoScreen(
    onVoltar: () -> Unit,
    viewModel: ParcelamentoViewModel = hiltViewModel()
) {
    val estado = viewModel.estado
    var descricao by remember { mutableStateOf("") }
    var valorTotal by remember { mutableStateOf("") }
    var parcelas by remember { mutableStateOf("2") }
    var primeiroVencimento by remember { mutableStateOf("") }
    var carteira by remember { mutableStateOf<CarteiraDto?>(null) }
    var categoria by remember { mutableStateOf<CategoriaDto?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Compra parcelada") },
                navigationIcon = { IconButton(onClick = onVoltar) { Icon(Icons.Default.ArrowBack, contentDescription = "Voltar") } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = descricao,
                onValueChange = { descricao = it },
                label = { Text("descricao") },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )
            OutlinedTextField(
                value = valorTotal,
                onValueChange = { valorTotal = it },
                label = { Text("valor total") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )
            OutlinedTextField(
                value = parcelas,
                onValueChange = { parcelas = it },
                label = { Text("parcelas (min 2)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )
            OutlinedTextField(
                value = primeiroVencimento,
                onValueChange = { primeiroVencimento = it },
                label = { Text("primeiro vencimento (AAAA-MM-DD)") },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )
            SelectField(
                label = "carteira",
                opcoes = estado.carteiras,
                selecionado = carteira,
                rotulo = { it.nome },
                onSelecionar = { carteira = it },
                modifier = Modifier.padding(top = 8.dp)
            )
            SelectField(
                label = "categoria (despesa)",
                opcoes = estado.categorias,
                selecionado = categoria,
                rotulo = { it.nome },
                onSelecionar = { categoria = it },
                modifier = Modifier.padding(top = 8.dp)
            )
            Button(
                onClick = {
                    val c = carteira
                    val cat = categoria
                    val v = valorTotal.replace(',', '.').toDoubleOrNull()
                    val n = parcelas.toIntOrNull()
                    if (descricao.isNotBlank() && v != null && n != null && n >= 2 &&
                        primeiroVencimento.isNotBlank() && c != null && cat != null
                    ) {
                        viewModel.parcelar(
                            ParcelamentoRequestDto(
                                descricao = descricao,
                                valorTotal = v,
                                parcelas = n,
                                primeiroVencimento = primeiroVencimento,
                                carteiraId = c.id,
                                categoriaId = cat.id
                            )
                        )
                    }
                },
                modifier = Modifier.padding(top = 16.dp)
            ) { Text("Gerar parcelas") }

            estado.erro?.let { Text(it, modifier = Modifier.padding(top = 8.dp)) }

            if (estado.parcelas.isNotEmpty()) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                Text("${estado.parcelas.size} parcelas geradas")
                estado.parcelas.forEach { p ->
                    ListItem(
                        headlineContent = { Text(p.descricao) },
                        supportingContent = { Text("Venc: ${p.dataVencimento ?: "-"}") },
                        trailingContent = { Text("R$ %.2f".format(p.valor)) }
                    )
                }
            }
        }
    }
}
