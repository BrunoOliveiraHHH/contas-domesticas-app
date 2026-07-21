package br.com.contasdomesticas.app.data.repository

import br.com.contasdomesticas.app.data.remote.UsuarioApi
import br.com.contasdomesticas.app.data.remote.dto.UsuarioDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UsuarioRepository @Inject constructor(
    private val api: UsuarioApi
) {
    suspend fun listar(): List<UsuarioDto> = api.listar()
}
