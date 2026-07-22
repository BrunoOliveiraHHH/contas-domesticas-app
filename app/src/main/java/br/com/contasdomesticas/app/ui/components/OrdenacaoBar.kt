package br.com.contasdomesticas.app.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.AssistChip
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** Uma opção de ordenação: rótulo exibido + comparador aplicado à lista. */
data class OpcaoOrdenacao<T>(val rotulo: String, val comparador: Comparator<T>)

/** Aplica a ordenação escolhida (campo + direção) a uma lista. */
fun <T> List<T>.ordenar(opcoes: List<OpcaoOrdenacao<T>>, indice: Int, ascendente: Boolean): List<T> {
    val c = opcoes.getOrNull(indice)?.comparador ?: return this
    return sortedWith(if (ascendente) c else c.reversed())
}

/**
 * Barra com seletor de ordenação: escolhe por qual campo ordenar e alterna
 * crescente/decrescente. Fica acima da lista.
 */
@Composable
fun <T> OrdenacaoBar(
    opcoes: List<OpcaoOrdenacao<T>>,
    indice: Int,
    ascendente: Boolean,
    onSelecionar: (Int) -> Unit,
    onAlternarDirecao: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Sort, contentDescription = null)
        Spacer(Modifier.width(8.dp))
        Text("Ordenar por", style = MaterialTheme.typography.labelLarge)
        Spacer(Modifier.width(8.dp))
        Box {
            AssistChip(
                onClick = { expanded = true },
                label = { Text(opcoes.getOrNull(indice)?.rotulo ?: "—") },
                trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) }
            )
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                opcoes.forEachIndexed { i, o ->
                    DropdownMenuItem(
                        text = { Text(o.rotulo) },
                        onClick = {
                            onSelecionar(i)
                            expanded = false
                        }
                    )
                }
            }
        }
        Spacer(Modifier.weight(1f))
        IconButton(onClick = onAlternarDirecao) {
            Icon(
                if (ascendente) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                contentDescription = if (ascendente) "Crescente" else "Decrescente"
            )
        }
    }
}
