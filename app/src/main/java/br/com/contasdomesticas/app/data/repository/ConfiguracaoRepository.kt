package br.com.contasdomesticas.app.data.repository

import br.com.contasdomesticas.app.data.remote.ConfiguracaoApi
import br.com.contasdomesticas.app.data.remote.dto.ParametroDto
import br.com.contasdomesticas.app.data.remote.dto.ParametroRequestDto
import br.com.contasdomesticas.app.data.remote.dto.PreferenciaDto
import br.com.contasdomesticas.app.data.remote.dto.PreferenciaRequestDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConfiguracaoRepository @Inject constructor(
    private val api: ConfiguracaoApi
) {
    suspend fun listarParametros(): List<ParametroDto> = api.listarParametros()
    suspend fun criarParametro(request: ParametroRequestDto): ParametroDto = api.criarParametro(request)
    suspend fun atualizarParametro(id: Long, request: ParametroRequestDto): ParametroDto =
        api.atualizarParametro(id, request)
    suspend fun removerParametro(id: Long) = api.removerParametro(id)

    suspend fun impostoIr(dias: Int): Double = api.impostoIr(dias).aliquota

    suspend fun resolverPreferencia(chave: String): PreferenciaDto = api.resolverPreferencia(chave)
    suspend fun gravarPreferencia(chave: String, valor: String): PreferenciaDto =
        api.gravarPreferencia(chave, PreferenciaRequestDto(valor))
}
