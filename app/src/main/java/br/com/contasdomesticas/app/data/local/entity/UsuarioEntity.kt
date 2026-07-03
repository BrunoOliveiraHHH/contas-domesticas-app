package br.com.contasdomesticas.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Usuario no banco local (espelho do modelo da API).
 * Timestamps em epoch millis para facilitar a resolucao de conflitos por "mais recente".
 */
@Entity(
    tableName = "usuario",
    indices = [Index(value = ["login"], unique = true)]
)
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val login: String,
    val nomeExibicao: String,
    val senha: String,
    val criadoEm: Long? = null,
    val criadoPor: String? = null,
    val atualizadoEm: Long? = null,
    val atualizadoPor: String? = null
)
