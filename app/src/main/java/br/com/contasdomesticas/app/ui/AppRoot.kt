package br.com.contasdomesticas.app.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.com.contasdomesticas.app.ui.cadastro.CategoriaScreen
import br.com.contasdomesticas.app.ui.cadastro.FormaPagamentoScreen
import br.com.contasdomesticas.app.ui.cadastro.MercadoScreen
import br.com.contasdomesticas.app.ui.cadastro.ProdutoScreen
import br.com.contasdomesticas.app.ui.cadastro.UnidadeMedidaScreen
import br.com.contasdomesticas.app.ui.carteira.CarteiraScreen
import br.com.contasdomesticas.app.ui.home.HomeScreen
import br.com.contasdomesticas.app.ui.compra.ItemCompraScreen
import br.com.contasdomesticas.app.ui.compra.ListaCompraScreen
import br.com.contasdomesticas.app.ui.configuracao.ConfiguracaoScreen
import br.com.contasdomesticas.app.ui.investimento.InvestimentoScreen
import br.com.contasdomesticas.app.ui.lancamento.DespesaScreen
import br.com.contasdomesticas.app.ui.lancamento.ReceitaScreen
import br.com.contasdomesticas.app.ui.login.LoginScreen

@Composable
fun AppRoot(mainViewModel: MainViewModel = hiltViewModel()) {
    if (!mainViewModel.pronto) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val navController = rememberNavController()
    val voltar: () -> Unit = { navController.popBackStack() }

    NavHost(
        navController = navController,
        startDestination = if (mainViewModel.autenticado) "home" else "login"
    ) {
        composable("login") {
            LoginScreen(onEntrar = {
                navController.navigate("home") { popUpTo("login") { inclusive = true } }
            })
        }
        composable("home") {
            HomeScreen(
                onNavegar = { rota -> navController.navigate(rota) },
                onSair = {
                    mainViewModel.sair {
                        navController.navigate("login") { popUpTo("home") { inclusive = true } }
                    }
                }
            )
        }
        composable("carteiras") { CarteiraScreen(onVoltar = voltar) }
        composable("categorias") { CategoriaScreen(onVoltar = voltar) }
        composable("formas_pagamento") { FormaPagamentoScreen(onVoltar = voltar) }
        composable("mercados") { MercadoScreen(onVoltar = voltar) }
        composable("unidades_medida") { UnidadeMedidaScreen(onVoltar = voltar) }
        composable("produtos") { ProdutoScreen(onVoltar = voltar) }
        composable("receitas") { ReceitaScreen(onVoltar = voltar) }
        composable("despesas") { DespesaScreen(onVoltar = voltar) }
        composable("investimentos") { InvestimentoScreen(onVoltar = voltar) }
        composable("listas_compra") {
            ListaCompraScreen(
                onVoltar = voltar,
                onAbrirLista = { id -> navController.navigate("listas_compra/$id") }
            )
        }
        composable("listas_compra/{listaId}") { ItemCompraScreen(onVoltar = voltar) }
        composable("configuracao") { ConfiguracaoScreen(onVoltar = voltar) }
    }
}
