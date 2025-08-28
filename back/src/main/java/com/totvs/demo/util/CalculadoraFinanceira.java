package com.totvs.demo.util;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class CalculadoraFinanceira {

    public static double calcularAmortizacao(int consolidada, double valorEmprestimo, int numParcelas) {
        if (consolidada != 0) {
            return valorEmprestimo / numParcelas;
        } else {
            return 0;
        }
    }

    public static double calcularJuros(
            double taxaJuros,
            int baseDias,
            double saldo,
            double acumulado,
            LocalDate dataInicio,
            LocalDate dataFim) {

        System.out.println("----------------------------");

        System.out.println("[Calcular Juros] Variaveis: taxaJuros " + taxaJuros);
        System.out.println("[Calcular Juros] Variaveis: baseDias " + baseDias);
        System.out.println("[Calcular Juros] Variaveis: saldo " + saldo);
        System.out.println("[Calcular Juros] Variaveis: acumulado " + acumulado);
        System.out.println("[Calcular Juros] Variaveis: dataInicio " + dataInicio);
        System.out.println("[Calcular Juros] Variaveis: dataFim " + dataFim);

        // converter taxa percentual para decimal
        double taxaDecimal = taxaJuros / 100.0;
        System.out.println("[Calcular Juros] Taxa em decimal: " + taxaDecimal);

        // calcular diferença de dias (incluir último dia)
        long dias = ChronoUnit.DAYS.between(dataInicio, dataFim);
        System.out.println("[Calcular Juros] Dias entre as datas (incluindo ultimo dia): " + dias);

        // calcular potência
        double potencia = Math.pow((1 + taxaDecimal), ((double) dias / baseDias));
        System.out.println("[Calcular Juros] Potencia (1 + taxa)^(dias/base): " + potencia);

        // subtrair 1
        double jurosParcial = potencia - 1;
        System.out.println("[Calcular Juros] Juros parcial: " + jurosParcial);

        // multiplicar pelo saldo + acumulado
        double jurosTotal = (potencia - 1) * (saldo + acumulado);
        System.out.println("[Calcular Juros] Juros total: " + jurosTotal);

        System.out.println("----------------------------");

        return jurosTotal;
    }

    public static double calcularPago(double provisao, double acumuladoMesPassado, double consolidada) {
        double result = acumuladoMesPassado + provisao;

        System.out.println("----------------------------");
        System.out.println("[Calcular Pago] Variaveis: provisao " + provisao);
        System.out.println("[Calcular Pago] Variaveis: acumuladoMesPassado " + acumuladoMesPassado);
        System.out.println("[Calcular Pago] Variaveis: consolidada " + consolidada);
        System.out.println("[Calcular Pago] Pago Total: " + result);
        System.out.println("----------------------------");

        if (consolidada != 0) {
            return result;
        } else {
            return 0;
        }
    }
}
