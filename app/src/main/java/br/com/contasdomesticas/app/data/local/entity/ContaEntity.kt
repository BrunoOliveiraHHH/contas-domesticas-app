package br.com.contasdomesticas.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidade base de exemplo para o banco Room.
 * Substitua/expanda conforme o modelo real do dominio nas proximas sprints.
 */
@Entity(tableName = "conta")
data class ContaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val descricao: String
)
