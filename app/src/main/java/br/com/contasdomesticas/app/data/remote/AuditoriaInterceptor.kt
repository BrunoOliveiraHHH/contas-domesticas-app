package br.com.contasdomesticas.app.data.remote

import android.util.Log
import br.com.contasdomesticas.app.data.local.dao.AuditoriaDao
import br.com.contasdomesticas.app.data.local.entity.AuditoriaEntity
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Registra cada chamada a API na tabela de auditoria local (Room),
 * espelhando a auditoria feita pelo backend. Roda na thread do OkHttp.
 */
@Singleton
class AuditoriaInterceptor @Inject constructor(
    private val auditoriaDao: AuditoriaDao
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        try {
            auditoriaDao.inserirBloqueante(
                AuditoriaEntity(
                    usuario = null, // preenchido quando houver sessao autenticada
                    metodoHttp = request.method,
                    endpoint = request.url.encodedPath,
                    statusResposta = response.code,
                    dataHora = System.currentTimeMillis()
                )
            )
        } catch (e: Exception) {
            // Auditoria nunca deve quebrar a chamada.
            Log.w("AuditoriaInterceptor", "Falha ao registrar auditoria: ${e.message}")
        }
        return response
    }
}
