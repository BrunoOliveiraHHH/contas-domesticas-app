package br.com.contasdomesticas.app.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    onCarteiras: () -> Unit,
    onSair: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Contas Domesticas", style = MaterialTheme.typography.headlineSmall)
        Button(onClick = onCarteiras, modifier = Modifier.padding(top = 24.dp)) { Text("Carteiras") }
        Button(onClick = onSair, modifier = Modifier.padding(top = 8.dp)) { Text("Sair") }
    }
}
