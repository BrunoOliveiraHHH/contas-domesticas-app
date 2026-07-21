package br.com.contasdomesticas.app.data.repository

import br.com.contasdomesticas.app.data.local.TokenStore
import br.com.contasdomesticas.app.data.remote.AuthApi
import br.com.contasdomesticas.app.data.remote.dto.LoginRequestDto
import br.com.contasdomesticas.app.data.remote.dto.RefreshRequestDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val authApi: AuthApi,
    private val tokenStore: TokenStore
) {
    suspend fun login(login: String, senha: String) {
        val tokens = authApi.login(LoginRequestDto(login, senha))
        tokenStore.salvar(tokens.accessToken, tokens.refreshToken)
    }

    suspend fun logout() {
        runCatching { tokenStore.refreshToken()?.let { authApi.logout(RefreshRequestDto(it)) } }
        tokenStore.limpar()
    }

    fun autenticado(): Boolean = tokenStore.accessToken != null
}
