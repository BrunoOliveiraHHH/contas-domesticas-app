package br.com.contasdomesticas.app.util

import kotlin.math.pow

/** Funcoes puras de calculo financeiro (espelhadas no front). */
object Calculos {

    data class ResultadoInvestimento(
        val totalInvestido: Double,
        val montante: Double,
        val juros: Double
    )

    /** Juros compostos com aportes mensais. taxaMensalPercent em % (1 = 1%). */
    fun investimento(
        inicial: Double,
        mensal: Double,
        taxaMensalPercent: Double,
        meses: Int
    ): ResultadoInvestimento {
        val i = taxaMensalPercent / 100.0
        var montante = inicial
        repeat(meses) { montante = montante * (1 + i) + mensal }
        val totalInvestido = inicial + mensal * meses
        return ResultadoInvestimento(totalInvestido, montante, montante - totalInvestido)
    }

    data class ParcelaFinanciamento(
        val numero: Int,
        val juros: Double,
        val amortizacao: Double,
        val parcela: Double,
        val saldo: Double
    )

    data class ResultadoFinanciamento(
        val parcelas: List<ParcelaFinanciamento>,
        val totalPago: Double,
        val totalJuros: Double
    )

    enum class SistemaAmortizacao { PRICE, SAC }

    fun financiamento(
        valor: Double,
        taxaMensalPercent: Double,
        meses: Int,
        sistema: SistemaAmortizacao
    ): ResultadoFinanciamento {
        val i = taxaMensalPercent / 100.0
        val parcelas = mutableListOf<ParcelaFinanciamento>()
        var saldo = valor
        var totalPago = 0.0

        if (sistema == SistemaAmortizacao.PRICE) {
            val parcela = if (i == 0.0) valor / meses else valor * i / (1 - (1 + i).pow(-meses))
            for (n in 1..meses) {
                val juros = saldo * i
                val amortizacao = parcela - juros
                saldo = (saldo - amortizacao).coerceAtLeast(0.0)
                totalPago += parcela
                parcelas.add(ParcelaFinanciamento(n, juros, amortizacao, parcela, saldo))
            }
        } else {
            val amortizacao = valor / meses
            for (n in 1..meses) {
                val juros = saldo * i
                val parcela = amortizacao + juros
                saldo = (saldo - amortizacao).coerceAtLeast(0.0)
                totalPago += parcela
                parcelas.add(ParcelaFinanciamento(n, juros, amortizacao, parcela, saldo))
            }
        }
        return ResultadoFinanciamento(parcelas, totalPago, totalPago - valor)
    }

    /** Preco por unidade base (para comparar embalagens). */
    fun precoPorUnidade(preco: Double, quantidade: Double): Double =
        if (quantidade <= 0) 0.0 else preco / quantidade
}
