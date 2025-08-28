package com.totvs.demo.dto;

import java.time.LocalDate;

public class ParcelaDTO {
    private LocalDate dataCompetencia;
    private String valorEmprestimo;
    private double saldoDevedor;
    private String consolidada;
    private double total;
    private double amortizacao;
    private double saldo;
    private double provisao;
    private double acumulado;
    private double pago;

    public ParcelaDTO(
            LocalDate dataCompetencia,
            String valorEmprestimo,
            double saldoDevedor,
            String consolidada,
            double total,
            double amortizacao,
            double saldo,
            double provisao,
            double acumulado,
            double pago) {
        this.dataCompetencia = dataCompetencia;
        this.valorEmprestimo = valorEmprestimo;
        this.saldoDevedor = saldoDevedor;
        this.consolidada = consolidada;
        this.total = total;
        this.amortizacao = amortizacao;
        this.saldo = saldo;
        this.provisao = provisao;
        this.acumulado = acumulado;
        this.pago = pago;
    }

    public LocalDate getDataCompetencia() {
        return dataCompetencia;
    }

    public void setDataCompetencia(LocalDate dataCompetencia) {
        this.dataCompetencia = dataCompetencia;
    }

    public String getValorEmprestimo() {
        return valorEmprestimo;
    }

    public void setValorEmprestimo(String valorEmprestimo) {
        this.valorEmprestimo = valorEmprestimo;
    }

    public double getSaldoDevedor() {
        return saldoDevedor;
    }

    public void setSaldoDevedor(double saldoDevedor) {
        this.saldoDevedor = saldoDevedor;
    }

    public String getConsolidada() {
        return consolidada;
    }

    public void setConsolidada(String consolidada) {
        this.consolidada = consolidada;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getAmortizacao() {
        return amortizacao;
    }

    public void setAmortizacao(double amortizacao) {
        this.amortizacao = amortizacao;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public double getProvisao() {
        return provisao;
    }

    public void setProvisao(double provisao) {
        this.provisao = provisao;
    }

    public double getacumulado() {
        return acumulado;
    }

    public void setacumulado(double acumulado) {
        this.acumulado = acumulado;
    }

    public double getPago() {
        return pago;
    }

    public void setPago(double pago) {
        this.pago = pago;
    }
}