package br.com.contasdomesticas.app.ui.compra

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
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.PriceChange
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import br.com.contasdomesticas.app.data.remote.dto.CotacaoProdutoDto
import br.com.contasdomesticas.app.data.remote.dto.CotacaoProdutoRequestDto
import br.com.contasdomesticas.app.data.remote.dto.ItemCompraDto
import br.com.contasdomesticas.app.data.remote.dto.ItemCompraRequestDto
import br.com.contasdomesticas.app.data.remote.dto.MercadoDto
import br.com.contasdomesticas.app.data.remote.dto.ProdutoDto
import br.com.contasdomesticas.app.data.remote.dto.UnidadeMedidaDto
import br.com.contasdomesticas.app.ui.components.OpcaoOrdenacao
import br.com.contasdomesticas.app.ui.components.OrdenacaoBar
import br.com.contasdomesticas.app.ui.components.SelectField
import br.com.contasdomesticas.app.ui.components.ordenar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemCompraScreen(
    onVoltar: () -> Unit,
    viewModel: ItemCompraViewModel = hiltViewModel()
) {
    val estado = viewModel.estado
    var mostrarDialog by remember { mutableStateOf(false) }
    var escolhaAlvo by remember { mutableStateOf<ItemCompraDto?>(null) }
    var cotacaoAlvo by remember { mutableStateOf<ItemCompraDto?>(null) }

    fun nomeMercado(id: Long?): String =
        id?.let { m -> estado.mercados.find { it.id == m }?.nome ?: "#$m" } ?: "-"

    var ordemIdx by remember { mutableStateOf(0) }
    var asc by remember { mutableStateOf(true) }
    val opcoes: List<OpcaoOrdenacao<ItemCompraDto>> = remember {
        listOf(
            OpcaoOrdenacao("Produto", compareBy { it.produtoNome ?: "" }),
            OpcaoOrdenacao("Quantidade", compareBy { it.quantidade }),
            OpcaoOrdenacao("Preço", compareBy { it.precoUnitario ?: 0.0 }),
            OpcaoOrdenacao("Comprado", compareBy { it.comprado })
        )
    }
    val itens = estado.itens.ordenar(opcoes, ordemIdx, asc)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Itens da lista") },
                navigationIcon = { IconButton(onClick = onVoltar) { Icon(Icons.Default.ArrowBack, contentDescription = "Voltar") } },
                actions = {
                    IconButton(onClick = { viewModel.reporEstoque() }) {
                        Icon(Icons.Default.Inventory, contentDescription = "Repor estoque")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { mostrarDialog = true }) { Icon(Icons.Default.Add, contentDescription = "Item") }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            OrdenacaoBar(opcoes, ordemIdx, asc, { ordemIdx = it }, { asc = !asc })
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(itens, key = { it.id }) { item ->
                    ListItem(
                        headlineContent = { Text(item.produtoNome ?: "#${item.produtoId}") },
                        supportingContent = {
                            val preco = item.precoUnitario?.let { " · R$ %.2f".format(it) } ?: ""
                            Text("Qtd ${item.quantidade} · ${nomeMercado(item.mercadoEscolhidoId)}$preco")
                        },
                        trailingContent = {
                            Row {
                                IconButton(onClick = { escolhaAlvo = item }) { Icon(Icons.Default.Storefront, contentDescription = "Estabelecimento") }
                                IconButton(onClick = { cotacaoAlvo = item }) { Icon(Icons.Default.PriceChange, contentDescription = "Cotacoes") }
                                IconButton(onClick = { viewModel.removerItem(item.id) }) { Icon(Icons.Default.Delete, contentDescription = "Remover") }
                            }
                        }
                    )
                }
            }
        }
    }

    if (mostrarDialog) {
        ItemDialog(
            produtos = estado.produtos,
            unidades = estado.unidades,
            onConfirmar = { req -> viewModel.adicionarItem(req); mostrarDialog = false },
            onCancelar = { mostrarDialog = false }
        )
    }

    escolhaAlvo?.let { alvo ->
        EscolhaDialog(
            mercados = estado.mercados,
            selecionadoId = alvo.mercadoEscolhidoId,
            onConfirmar = { mercadoId -> viewModel.escolher(alvo.id, mercadoId); escolhaAlvo = null },
            onCancelar = { escolhaAlvo = null }
        )
    }

    cotacaoAlvo?.let { alvo ->
        CotacaoDialog(
            item = alvo,
            mercados = estado.mercados,
            carregarCotacoes = { viewModel.cotacoes(alvo.produtoId) },
            onAdicionar = { req, onFim -> viewModel.adicionarCotacao(alvo.produtoId, req, onFim) },
            onFechar = { cotacaoAlvo = null }
        )
    }
}

@Composable
private fun ItemDialog(
    produtos: List<ProdutoDto>,
    unidades: List<UnidadeMedidaDto>,
    onConfirmar: (ItemCompraRequestDto) -> Unit,
    onCancelar: () -> Unit
) {
    var produto by remember { mutableStateOf<ProdutoDto?>(null) }
    var quantidade by remember { mutableStateOf("1") }
    var unidade by remember { mutableStateOf<UnidadeMedidaDto?>(null) }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Novo item") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                SelectField(
                    label = "produto",
                    opcoes = produtos,
                    selecionado = produto,
                    rotulo = { it.nome },
                    onSelecionar = { produto = it },
                    modifier = Modifier.padding(top = 8.dp)
                )
                OutlinedTextField(
                    value = quantidade,
                    onValueChange = { quantidade = it },
                    label = { Text("quantidade") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                )
                SelectField(
                    label = "unidade",
                    opcoes = unidades,
                    selecionado = unidade,
                    rotulo = { it.sigla },
                    onSelecionar = { unidade = it },
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val p = produto
                val q = quantidade.replace(',', '.').toDoubleOrNull()
                if (p != null && q != null) {
                    onConfirmar(ItemCompraRequestDto(produtoId = p.id, quantidade = q, unidadeMedidaId = unidade?.id))
                }
            }) { Text("Adicionar") }
        },
        dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar") } }
    )
}

@Composable
private fun EscolhaDialog(
    mercados: List<MercadoDto>,
    selecionadoId: Long?,
    onConfirmar: (Long) -> Unit,
    onCancelar: () -> Unit
) {
    var mercado by remember { mutableStateOf(mercados.find { it.id == selecionadoId }) }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Escolher estabelecimento") },
        text = {
            SelectField(
                label = "estabelecimento",
                opcoes = mercados,
                selecionado = mercado,
                rotulo = { it.nome },
                onSelecionar = { mercado = it }
            )
        },
        confirmButton = {
            Button(onClick = { mercado?.let { onConfirmar(it.id) } }) { Text("Confirmar") }
        },
        dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar") } }
    )
}

@Composable
private fun CotacaoDialog(
    item: ItemCompraDto,
    mercados: List<MercadoDto>,
    carregarCotacoes: suspend () -> List<CotacaoProdutoDto>,
    onAdicionar: (CotacaoProdutoRequestDto, onFim: () -> Unit) -> Unit,
    onFechar: () -> Unit
) {
    var cotacoes by remember { mutableStateOf<List<CotacaoProdutoDto>>(emptyList()) }
    var recarregar by remember { mutableStateOf(0) }
    var mercado by remember { mutableStateOf<MercadoDto?>(null) }
    var preco by remember { mutableStateOf("") }

    LaunchedEffect(recarregar) { cotacoes = carregarCotacoes() }

    fun nomeMercado(id: Long): String = mercados.find { it.id == id }?.nome ?: "#$id"

    AlertDialog(
        onDismissRequest = onFechar,
        title = { Text("Cotacoes · ${item.produtoNome ?: "#${item.produtoId}"}") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                cotacoes.forEach { c ->
                    ListItem(
                        headlineContent = { Text(nomeMercado(c.mercadoId)) },
                        trailingContent = { Text("R$ %.2f".format(c.precoUnitario)) }
                    )
                }
                if (cotacoes.isEmpty()) Text("Sem cotacoes")
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                SelectField(
                    label = "estabelecimento",
                    opcoes = mercados,
                    selecionado = mercado,
                    rotulo = { it.nome },
                    onSelecionar = { mercado = it }
                )
                OutlinedTextField(
                    value = preco,
                    onValueChange = { preco = it },
                    label = { Text("preco unitario") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val m = mercado
                val p = preco.replace(',', '.').toDoubleOrNull()
                if (m != null && p != null) {
                    onAdicionar(CotacaoProdutoRequestDto(mercadoId = m.id, precoUnitario = p)) {
                        preco = ""
                        recarregar++
                    }
                }
            }) { Text("Adicionar") }
        },
        dismissButton = { TextButton(onClick = onFechar) { Text("Fechar") } }
    )
}
