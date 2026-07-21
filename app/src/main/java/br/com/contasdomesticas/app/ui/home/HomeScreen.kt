package br.com.contasdomesticas.app.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    onNavegar: (String) -> Unit,
    onSair: () -> Unit
) {
    val itens = listOf(
        "Receitas" to "receitas",
        "Despesas" to "despesas",
        "Investimentos" to "investimentos",
        "Listas de compra" to "listas_compra",
        "Carteiras" to "carteiras",
        "Categorias" to "categorias",
        "Formas de pagamento" to "formas_pagamento",
        "Mercados" to "mercados",
        "Unidades de medida" to "unidades_medida",
        "Produtos" to "produtos"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            "Contas Domesticas",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        itens.forEach { (label, rota) ->
            Button(onClick = { onNavegar(rota) }, modifier = Modifier.fillMaxWidth()) { Text(label) }
        }
        OutlinedButton(
            onClick = onSair,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) { Text("Sair") }
    }
}
