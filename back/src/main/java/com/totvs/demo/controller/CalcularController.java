package com.totvs.demo.controller;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.totvs.demo.dto.CalculoRequest;
import com.totvs.demo.dto.ParcelaDTO;
import com.totvs.demo.util.CalculadoraFinanceira;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/calcular")
public class CalcularController {
        // Variaveis constantes conforme solicitado ao PDF
        private static final int BASE_DE_DIAS = 360;
        private static final int QUANTIDADE_DE_PARCELAS = 120;

        @PostMapping
        public List<ParcelaDTO> calcular(@RequestBody CalculoRequest request, HttpServletRequest httpRequest) {
                System.out.println("--------------------");
                System.out.println("Request Received from: " + httpRequest.getRemoteAddr());
                System.out.println("DataInicial: " + request.getDataInicial());
                System.out.println("DataFinal: " + request.getDataFinal());
                System.out.println("PrimeiroPagamento: " + request.getPrimeiroPagamento());
                System.out.println("ValorEmprestimo: " + request.getValorEmprestimo());
                System.out.println("TaxaJuros: " + request.getTaxaJuros());
                System.out.println("--------------------");

                // Adiciona mais um mes para finalizar a tabela com chave de ouro
                // request.setDataFinal(request.getDataFinal().plusMonths(1));

                List<ParcelaDTO> parcelas = new ArrayList<>();
                LocalDate primeiroPagamento = request.getPrimeiroPagamento();
                LocalDate dataAtual = request.getDataInicial();
                // LocalDate dataFinal = request.getDataFinal();
                String valorEmprestimo = request.getValorEmprestimo();
                double valorEmprestimoDouble = Double.parseDouble(request.getValorEmprestimo());
                double saldo = valorEmprestimoDouble;

                double acumuladoMesPassado = 0;
                LocalDate competenciaAnterior = dataAtual;
                double saldoPassado = saldo;

                // Primeira parcela padrão
                {
                        // Primeira parcela
                        parcelas.add(
                                        new ParcelaDTO(dataAtual, valorEmprestimo, valorEmprestimoDouble, "", 0, 0,
                                                        valorEmprestimoDouble,
                                                        0, 0, 0));
                        valorEmprestimo = "0";

                        // Se ja começar no final do mes pula pro proximo mes
                        if (dataAtual.getDayOfMonth() == dataAtual.lengthOfMonth()) {
                                YearMonth proximoMes = YearMonth.from(dataAtual).plusMonths(1);
                                int ultimoDia = proximoMes.lengthOfMonth();
                                int dia = Math.min(dataAtual.getDayOfMonth(), ultimoDia);
                                dataAtual = LocalDate.of(proximoMes.getYear(), proximoMes.getMonth(), dia);
                        }
                        // Não começa no final do mes, iniciamos mais uma parcela do final do mes
                        else {
                                LocalDate fimMes = dataAtual.withDayOfMonth(dataAtual.lengthOfMonth());
                                double provisao = CalculadoraFinanceira.calcularJuros(
                                                Double.parseDouble(request.getTaxaJuros()),
                                                BASE_DE_DIAS, saldo, 0, dataAtual, fimMes);
                                double acumulado = provisao;
                                double saldoDevedor = saldo + acumulado;
                                parcelas.add(
                                                new ParcelaDTO(fimMes, valorEmprestimo, saldoDevedor, "", 0, 0,
                                                                saldo, provisao, acumulado,
                                                                0));
                                acumuladoMesPassado = acumulado;
                                competenciaAnterior = fimMes;

                                YearMonth proximoMes = YearMonth.from(dataAtual).plusMonths(1);
                                int ultimoDia = proximoMes.lengthOfMonth();
                                int dia = Math.min(dataAtual.getDayOfMonth(), ultimoDia);
                                dataAtual = LocalDate.of(proximoMes.getYear(), proximoMes.getMonth(), dia);
                        }
                }

                int consolidada = 0;
                Boolean iniciouPagamento = false;

                // Calculo das parcelas
                // while (!dataAtual.isAfter(dataFinal)) { // Era assim, mas como no
                // excel ele nao se importa com a data final
                // ajustei pra so finalizar as parcelas quando saldo for 0
                while (saldo > 0) {
                        System.out.println("--------------");
                        System.out.println("[Data Pagamento] Iniciou pagamento: " + iniciouPagamento);
                        System.out.println("[Data Pagamento] Mesmo mes: "
                                        + (primeiroPagamento.getMonth() == dataAtual.getMonth()
                                                        && primeiroPagamento.getYear() == dataAtual.getYear()));
                        System.out.println("[Data Pagamento] Mes maior: " + dataAtual.isAfter(primeiroPagamento));
                        System.out.println("[Data Pagamento] Data atual: " + dataAtual);
                        System.out.println("[Data Pagamento] Primeiro Pagamento: " + primeiroPagamento);
                        System.out.println("--------------");

                        // Check para ver se o pagamento iniciou
                        if (!iniciouPagamento
                                        &&
                                        (// Se o mes do primeiro pagamento é o mesmo da data atual
                                        (primeiroPagamento.getMonth() == dataAtual.getMonth()
                                                        && primeiroPagamento.getYear() == dataAtual.getYear())
                                                        ||
                                                        // Ou se for superior
                                                        dataAtual.isAfter(primeiroPagamento))) {
                                dataAtual = primeiroPagamento;
                                iniciouPagamento = true;
                        }

                        // Consolidada so é adicionada se pagamento foi iniciado
                        if (iniciouPagamento) {
                                consolidada++;
                                if (consolidada > QUANTIDADE_DE_PARCELAS)
                                        break;
                        }

                        String consolidadeStr = "";
                        double amortizacao = 0;
                        double provisao = 0;
                        double acumulado = 0;
                        double pago = 0;
                        double saldoDevedor = 0;
                        // Calculo de amortizacao e reducao de saldo apenas se consolidada existir
                        // (iniciou pagamento)
                        if (consolidada > 0) {
                                consolidadeStr = consolidada + "/" + QUANTIDADE_DE_PARCELAS;
                                amortizacao = CalculadoraFinanceira.calcularAmortizacao(consolidada,
                                                valorEmprestimoDouble,
                                                QUANTIDADE_DE_PARCELAS);
                                saldo -= amortizacao;
                        }

                        System.out.println("[Saldo] " + saldo);

                        //
                        // Calculo da parcela do meio do mes
                        //
                        LocalDate meioMes = dataAtual.withDayOfMonth(15);
                        provisao = CalculadoraFinanceira.calcularJuros(Double.parseDouble(request.getTaxaJuros()),
                                        BASE_DE_DIAS, saldoPassado, acumuladoMesPassado, competenciaAnterior, meioMes);
                        acumulado = 0;
                        pago = CalculadoraFinanceira.calcularPago(provisao, acumuladoMesPassado, consolidada);
                        saldoDevedor = saldo + acumulado;
                        // Meio do mes
                        parcelas.add(
                                        new ParcelaDTO(meioMes, valorEmprestimo, saldoDevedor, consolidadeStr,
                                                        amortizacao + pago,
                                                        amortizacao,
                                                        saldo, provisao, acumulado,
                                                        pago));
                        acumuladoMesPassado = acumulado;
                        saldoPassado = saldo;

                        //
                        // Calculo da parcela do fim do mes
                        //
                        LocalDate fimMes = dataAtual.withDayOfMonth(dataAtual.lengthOfMonth());
                        provisao = CalculadoraFinanceira.calcularJuros(Double.parseDouble(request.getTaxaJuros()),
                                        BASE_DE_DIAS, saldoPassado, acumuladoMesPassado, meioMes, fimMes);
                        acumulado = provisao + acumuladoMesPassado;
                        saldoDevedor = saldo + acumulado;
                        // Fim do mes
                        parcelas.add(
                                        new ParcelaDTO(fimMes, valorEmprestimo, saldoDevedor, "", 0, 0,
                                                        saldo, provisao, acumulado,
                                                        0));
                        saldoPassado = saldo;

                        acumuladoMesPassado = acumulado; // Usado em ambas
                        competenciaAnterior = fimMes; // Isso é usado para a parcela meio mes

                        System.out.println("-------------");
                        System.out.println("[Proximo Mes] Anterior: " + dataAtual);
                        // Proximo mes
                        YearMonth proximoMes = YearMonth.from(dataAtual).plusMonths(1);
                        int ultimoDia = proximoMes.lengthOfMonth();
                        int dia = Math.min(dataAtual.getDayOfMonth(), ultimoDia);
                        dataAtual = LocalDate.of(proximoMes.getYear(), proximoMes.getMonth(), dia);
                        System.out.println("[Proximo Mes] Agora: " + dataAtual);
                        System.out.println("-------------");

                }

                return parcelas;
        }
}
