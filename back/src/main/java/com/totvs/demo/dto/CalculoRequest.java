package com.totvs.demo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;

public class CalculoRequest {

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataInicial;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataFinal;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate primeiroPagamento;

    private String valorEmprestimo;
    private String taxaJuros;

    public LocalDate getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(LocalDate dataInicial) {
        this.dataInicial = dataInicial;
    }

    public LocalDate getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(LocalDate dataFinal) {
        this.dataFinal = dataFinal;
    }

    public LocalDate getPrimeiroPagamento() {
        return primeiroPagamento;
    }

    public void setPrimeiroPagamento(LocalDate primeiroPagamento) {
        this.primeiroPagamento = primeiroPagamento;
    }

    public String getValorEmprestimo() {
        return valorEmprestimo;
    }

    public void setValorEmprestimo(String valorEmprestimo) {
        this.valorEmprestimo = valorEmprestimo;
    }

    public String getTaxaJuros() {
        return taxaJuros;
    }

    public void setTaxaJuros(String taxaJuros) {
        this.taxaJuros = taxaJuros;
    }
}
