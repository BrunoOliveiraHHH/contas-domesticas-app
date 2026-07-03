package br.com.contasdomesticas.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Registro de auditoria local: cada chamada do app a API gera uma linha
 * (espelha a auditoria do backend).
 */
@Entity(tableName = "auditoria")
data class AuditoriaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val usuario: String? = null,
    val metodoHttp: String,
    val endpoint: String,
    val statusResposta: Int? = null,
    val enderecoIp: String? = null,
    val dataHora: Long
)
