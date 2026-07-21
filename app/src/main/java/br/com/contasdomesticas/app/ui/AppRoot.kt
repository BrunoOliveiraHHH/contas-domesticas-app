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
import br.com.contasdomesticas.app.ui.home.HomeScreen
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
    NavHost(
        navController = navController,
        startDestination = if (mainViewModel.autenticado) "home" else "login"
    ) {
        composable("login") {
            LoginScreen(onEntrar = {
                navController.navigate("home") {
                    popUpTo("login") { inclusive = true }
                }
            })
        }
        composable("home") {
            HomeScreen(onSair = {
                mainViewModel.sair {
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            })
        }
    }
}
