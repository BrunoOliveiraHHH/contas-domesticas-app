package br.com.contasdomesticas.app.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.com.contasdomesticas.app.R

private data class Feature(val labelRes: Int, val rota: String, val icone: ImageVector)

private val FEATURES = listOf(
    Feature(R.string.nav_dashboard, "dashboard", Icons.Default.Dashboard),
    Feature(R.string.nav_receitas, "receitas", Icons.Default.TrendingUp),
    Feature(R.string.nav_despesas, "despesas", Icons.Default.TrendingDown),
    Feature(R.string.nav_recorrencias, "recorrencias", Icons.Default.Autorenew),
    Feature(R.string.nav_parcelamento, "parcelamento", Icons.Default.CreditCard),
    Feature(R.string.nav_divisao, "rateio", Icons.Default.Groups),
    Feature(R.string.nav_investimentos, "investimentos", Icons.Default.Savings),
    Feature(R.string.nav_listas_compra, "listas_compra", Icons.Default.ShoppingCart),
    Feature(R.string.nav_calculadoras, "calculadoras", Icons.Default.Calculate),
    Feature(R.string.nav_carteiras, "carteiras", Icons.Default.AccountBalanceWallet),
    Feature(R.string.nav_categorias, "categorias", Icons.Default.Category),
    Feature(R.string.nav_formas_pagamento, "formas_pagamento", Icons.Default.Payments),
    Feature(R.string.nav_mercados, "mercados", Icons.Default.Store),
    Feature(R.string.nav_unidades, "unidades_medida", Icons.Default.Straighten),
    Feature(R.string.nav_produtos, "produtos", Icons.Default.Inventory2),
    Feature(R.string.nav_configuracao, "configuracao", Icons.Default.Settings),
    Feature(R.string.nav_sincronizacao, "sincronizacao", Icons.Default.Sync)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavegar: (String) -> Unit,
    onSair: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name), fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onSair) { Icon(Icons.Default.Logout, contentDescription = "Sair") }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize().padding(padding).padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(FEATURES, key = { it.rota }) { f ->
                FeatureTile(f, onClick = { onNavegar(f.rota) })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FeatureTile(feature: Feature, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(112.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                feature.icone,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(34.dp)
            )
            Text(
                stringResource(feature.labelRes),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 10.dp)
            )
        }
    }
}
