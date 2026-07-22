package br.com.contasdomesticas.app.ui.dashboard

import android.annotation.SuppressLint
import android.graphics.Color as AndroidColor
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.ceil

private fun money(v: Double): String = "R$ %.2f".format(v)

// Numero em JSON (sempre com ponto, independente do locale)
private fun n(v: Double): String = String.format(Locale.US, "%.2f", v)

// ===== Builders de option ECharts (mesma linguagem do front) =====

private fun gaugeOpt(pct: Double, cor: String): String {
    val v = pct.coerceIn(0.0, 100.0).toInt()
    return """{"series":[{"type":"gauge","startAngle":210,"endAngle":-30,"min":0,"max":100,"splitNumber":5,""" +
        """"radius":"86%","center":["50%","58%"],""" +
        """"progress":{"show":true,"width":8,"roundCap":true,"itemStyle":{"color":"$cor"}},""" +
        """"axisLine":{"lineStyle":{"width":8,"color":[[1,"#e6e6ee"]]}},""" +
        """"axisTick":{"show":true,"splitNumber":5,"distance":-8,"length":4,"lineStyle":{"color":"#b9b9cc","width":1}},""" +
        """"splitLine":{"show":true,"distance":-8,"length":8,"lineStyle":{"color":"#b9b9cc","width":1.5}},""" +
        """"axisLabel":{"show":true,"distance":12,"fontSize":8,"color":"#9a9ab5"},""" +
        """"pointer":{"show":false},"anchor":{"show":false},"detail":{"show":false},"title":{"show":false},""" +
        """"data":[{"value":$v}]}]}"""
}

private fun donutOpt(parte: Double, total: Double, cor: String): String {
    val p = parte.coerceAtLeast(0.0)
    val resto = (total - p).coerceAtLeast(0.0).let { if (it == 0.0 && p == 0.0) 1.0 else it }
    val pctTxt = if (total > 0.0) "${(p / total * 100).toInt()}%" else "0%"
    return """{"series":[{"type":"pie","radius":["62%","84%"],"center":["50%","52%"],""" +
        """"label":{"show":false},"labelLine":{"show":false},"data":[""" +
        """{"value":${n(p)},"itemStyle":{"color":"$cor"},"label":{"show":true,"position":"center","formatter":"$pctTxt","fontSize":16,"fontWeight":"bold","color":"$cor"}},""" +
        """{"value":${n(resto)},"itemStyle":{"color":"#e6e6ee"}}]}]}"""
}

private fun barsOpt(valores: List<Double>, cores: List<String>, labels: List<String> = emptyList()): String {
    val data = valores.mapIndexed { i, v ->
        """{"value":${n(v.coerceAtLeast(0.0))},"itemStyle":{"color":"${cores.getOrElse(i) { cores.first() }}","borderRadius":[3,3,0,0]}}"""
    }.joinToString(",")
    val cats = (if (labels.isNotEmpty()) labels else valores.indices.map { (it + 1).toString() })
        .joinToString(",") { JSONObject.quote(it) }
    return """{"grid":{"left":36,"right":8,"top":10,"bottom":18},""" +
        """"xAxis":{"type":"category","data":[$cats],"axisTick":{"show":false},"axisLine":{"lineStyle":{"color":"rgba(136,136,170,.35)"}},"axisLabel":{"fontSize":8,"color":"#9a9ab5"}},""" +
        """"yAxis":{"type":"value","splitNumber":3,"axisLabel":{"fontSize":8,"color":"#9a9ab5"},"splitLine":{"show":true,"lineStyle":{"color":"rgba(136,136,170,.20)","type":"dashed"}}},""" +
        """"series":[{"type":"bar","barWidth":"46%","data":[$data]}]}"""
}

private fun categoriasOpt(cats: List<Pair<String, Double>>): String {
    val cores = listOf("#613178", "#d82a76", "#8b5a96", "#3b82f6", "#43a047", "#f59e0b")
    val data = cats.take(6).joinToString(",") {
        """{"name":${JSONObject.quote(it.first)},"value":${n(it.second)}}"""
    }
    val cor = cores.joinToString(",") { "\"$it\"" }
    return """{"color":[$cor],"series":[{"type":"pie","radius":["55%","82%"],"center":["50%","55%"],""" +
        """"label":{"show":false},"labelLine":{"show":false},"data":[$data]}]}"""
}

private data class KpiItem(val rotulo: String, val valor: String, val option: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onVoltar: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val estado = viewModel.estado
    val s = estado.saldo

    val receitas = s?.receitas ?: 0.0
    val despesas = s?.despesas ?: 0.0
    val saldo = s?.saldo ?: 0.0
    val aPagar = s?.aPagar ?: 0.0
    val atrasadas = s?.atrasadas ?: 0.0
    val assinaturas = s?.assinaturas ?: 0.0
    val saldoProjetado = saldo - aPagar
    val comprometimento = if (receitas > 0) despesas / receitas * 100 else 0.0
    val poupanca = if (receitas > 0) saldo / receitas * 100 else 0.0
    val pctPago = if (despesas > 0) (despesas - aPagar) / despesas * 100 else 0.0
    val reserva = if (despesas > 0) estado.patrimonio / despesas else 0.0
    val maior = estado.despesasPorCategoria.firstOrNull()
    val fluxo = receitas + despesas
    val cats = estado.despesasPorCategoria.map { it.categoriaNome to it.total }

    val kpis = listOf(
        KpiItem("Receitas", money(receitas), donutOpt(receitas, fluxo, "#43a047")),
        KpiItem("Despesas", money(despesas), donutOpt(despesas, fluxo, "#d82a76")),
        KpiItem("A pagar", money(aPagar), donutOpt(aPagar, despesas, "#f59e0b")),
        KpiItem("Atrasadas", money(atrasadas), donutOpt(atrasadas, if (aPagar > 0) aPagar else despesas, "#ef4444")),
        KpiItem("Saldo", money(saldo), barsOpt(listOf(receitas, despesas), listOf("#8b5a96", "#d82a76"), listOf("Rec.", "Desp."))),
        KpiItem("Saldo projetado", money(saldoProjetado), barsOpt(listOf(saldo, saldoProjetado), listOf("#8b5a96", "#14b8a6"), listOf("Saldo", "Proj."))),
        KpiItem("Comprometimento", "${comprometimento.toInt()}%", gaugeOpt(comprometimento, "#3b82f6")),
        KpiItem("Taxa de poupança", "${poupanca.toInt()}%", gaugeOpt(poupanca, "#43a047")),
        KpiItem("% pago do mês", "${pctPago.toInt()}%", gaugeOpt(pctPago, "#613178")),
        KpiItem("Assinaturas/fixas", money(assinaturas), donutOpt(assinaturas, despesas, "#14b8a6")),
        KpiItem("Reserva de emergência", "%.1f meses".format(reserva), gaugeOpt((reserva / 6 * 100).coerceAtMost(100.0), "#613178")),
        KpiItem("Maior categoria", maior?.categoriaNome ?: "—", categoriasOpt(cats))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard · ${estado.periodo}") },
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
            // KPIs 2 por linha (mesmo tamanho), cada um com chart ECharts
            kpis.chunked(2).forEach { par ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    par.forEach { item -> KpiCard(item, Modifier.weight(1f)) }
                    if (par.size == 1) Spacer(Modifier.weight(1f))
                }
            }

            SimuladorQuitacao(sobraMensal = if (saldo > 0) saldo else 0.0)

            Card(modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Patrimonio investido", style = MaterialTheme.typography.titleMedium)
                    Text(money(estado.patrimonio), style = MaterialTheme.typography.headlineSmall)
                }
            }

            Card(modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Despesas por categoria", style = MaterialTheme.typography.titleMedium)
                    if (estado.despesasPorCategoria.isEmpty()) {
                        Text("Sem despesas no periodo.", modifier = Modifier.padding(top = 8.dp))
                    }
                    estado.despesasPorCategoria.forEach { c ->
                        Column(modifier = Modifier.padding(top = 8.dp)) {
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Text(c.categoriaNome, modifier = Modifier.weight(1f))
                                Text(money(c.total), fontWeight = FontWeight.Medium)
                                Text(" ${c.percentual.toInt()}%", modifier = Modifier.padding(start = 8.dp))
                            }
                            LinearProgressIndicator(
                                progress = { (c.percentual / 100.0).toFloat().coerceIn(0f, 1f) },
                                modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun KpiCard(item: KpiItem, modifier: Modifier = Modifier) {
    Card(modifier = modifier.height(140.dp)) {
        Column(modifier = Modifier.padding(12.dp).fillMaxSize()) {
            Text(
                item.rotulo,
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                item.valor,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(6.dp))
            EChartView(item.option, Modifier.fillMaxWidth().weight(1f))
        }
    }
}

// WebView com ECharts (echarts.min.js empacotado em assets/)
@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun EChartView(optionJson: String, modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            WebView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                settings.javaScriptEnabled = true
                settings.loadWithOverviewMode = true
                setBackgroundColor(AndroidColor.TRANSPARENT)
                isVerticalScrollBarEnabled = false
                isHorizontalScrollBarEnabled = false
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView, url: String?) {
                        view.evaluateJavascript("render(${JSONObject.quote(optionJson)})", null)
                    }
                }
                tag = optionJson
                loadUrl("file:///android_asset/echart.html")
            }
        },
        update = { web ->
            if (web.tag != optionJson) {
                web.tag = optionJson
                web.evaluateJavascript("render(${JSONObject.quote(optionJson)})", null)
            }
        }
    )
}

@Composable
private fun SimuladorQuitacao(sobraMensal: Double) {
    var valor by remember { mutableStateOf("") }
    val v = valor.replace(',', '.').toDoubleOrNull() ?: 0.0

    val texto = when {
        v <= 0 -> "Informe um valor a quitar."
        sobraMensal <= 0 -> "Sem sobra mensal no momento — não dá para projetar."
        else -> {
            val meses = ceil(v / sobraMensal).toInt()
            val alvo = Calendar.getInstance().apply { add(Calendar.MONTH, meses) }
            val rotulo = SimpleDateFormat("MM/yyyy", Locale.getDefault()).format(alvo.time)
            "Guardando ~${money(sobraMensal)}/mês, quita em ~$meses ${if (meses == 1) "mês" else "meses"} (previsão: $rotulo)."
        }
    }

    Card(modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Simulador de quitação", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = valor,
                onValueChange = { valor = it },
                label = { Text("valor a quitar") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )
            Text(texto, modifier = Modifier.padding(top = 8.dp))
        }
    }
}
