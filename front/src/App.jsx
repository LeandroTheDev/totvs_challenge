// src/App.jsx
import { useState } from "react";

export default function App() {
  const [formData, setFormData] = useState({
    dataInicial: "",
    dataFinal: "",
    primeiroPagamento: "",
    valorEmprestimo: "",
    taxaJuros: "",
  });

  const [errors, setErrors] = useState({
    dataInicial: "",
    dataFinal: "",
    primeiroPagamento: "",
    valorEmprestimo: "",
    taxaJuros: "",
  });

  let [resultado, setResultado] = useState(null);

  const handleSubmit = async (e) => {
    e.preventDefault();

    let errors = {};

    // Validação dos campos
    if (!formData.dataInicial) errors.dataInicial = "Campo obrigatório não preenchido";
    if (!formData.dataFinal) errors.dataFinal = "Campo obrigatório não preenchido";
    if (!formData.primeiroPagamento) errors.primeiroPagamento = "Campo obrigatório não preenchido";
    if (!formData.valorEmprestimo || formData.valorEmprestimo === `R$ 0,00`) errors.valorEmprestimo = "Campo obrigatório não preenchido";
    if (!formData.taxaJuros) errors.taxaJuros = "Campo obrigatório não preenchido";

    // Atualiza os erros no state
    setErrors((prev) => ({ ...prev, ...errors }));

    // Se houver algum erro, não envia o formulário
    if (Object.keys(errors).length > 0) return;

    const formatDate = (date) => {
      if (!date) return null;
      return new Date(date).toISOString().split("T")[0]; // yyyy-MM-dd
    };

    // Remove tudo que não seja número ou vírgula
    const valorLimpo = formData.valorEmprestimo.replace(/[^\d,]/g, "");
    const payload = {
      ...formData,
      dataInicial: formatDate(formData.dataInicial),
      dataFinal: formatDate(formData.dataFinal),
      primeiroPagamento: formatDate(formData.primeiroPagamento),
      valorEmprestimo: parseFloat(valorLimpo.replace(",", "."))
    };

    console.log("Enviando dados:");
    console.log(payload);

    try {
      // Envia a requisição POST
      const response = await fetch("http://localhost:9090/calcular", {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify(payload)
      });

      if (!response.ok) {
        throw new Error(`Erro na requisição: ${response.status}`);
      }

      // Converte a resposta em JSON
      const data = await response.json();

      const formatarData = (dataStr) => {
        const [ano, mes, dia] = dataStr.split("-");
        return `${dia}/${mes}/${ano}`;
      };

      const transformado = data.map(item => ({
        "Data Competência": formatarData(item.dataCompetencia),
        "Valor Emprestimo": item.valorEmprestimo,
        "SaldoDevedor": item.saldoDevedor.toFixed(2),
        "Consolidada": item.consolidada,
        "Total": item.total.toFixed(2),
        "Amortização": item.amortizacao.toFixed(2),
        "Saldo": item.saldo.toFixed(2),
        "Provisao": item.provisao.toFixed(2),
        "Acumulado": item.acumulado.toFixed(2),
        "Pago": item.pago.toFixed(2),
      }));

      setResultado(transformado);
      console.log("Resposta do servidor:");
      console.log(transformado);
    } catch (err) {
      console.error(err);
      alert("Ocorreu um erro ao enviar o formulário.");
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;

    // Atualiza o valor do campo
    setFormData({ ...formData, [name]: value });

    // Limpa o erro deste campo ao digitar/modificar
    setErrors((prev) => ({ ...prev, [name]: "" }));

    // Validação imediata
    if (name === "dataFinal" || name === "dataInicial") {
      if (formData.dataInicial && value && value <= formData.dataInicial) {
        setErrors((prev) => ({ ...prev, dataFinal: "A data final deve ser maior que a data inicial." }));
      } else {
        setErrors((prev) => ({ ...prev, dataFinal: "" }));
      }
    }

    if (name === "primeiroPagamento" || name === "dataInicial" || name === "dataFinal") {
      if (formData.dataInicial && formData.dataFinal && formData.primeiroPagamento) {
        const dataInicial = new Date(formData.dataInicial);
        const dataFinal = new Date(formData.dataFinal);

        let primeiroPagamento;
        if (name === "primeiroPagamento")
          primeiroPagamento = new Date(value);
        else
          new Date(formData.primeiroPagamento);

        if (primeiroPagamento < dataInicial || primeiroPagamento > dataFinal) {
          setErrors((prev) => ({
            ...prev,
            primeiroPagamento: "O primeiro pagamento deve estar entre a data inicial e a data final.",
          }));
        } else {
          setErrors((prev) => ({ ...prev, primeiroPagamento: "" }));
        }
      }
    }
  };

  const handleValorChange = (e) => {
    const rawValue = e.target.value.replace(/\D/g, ""); // remove tudo que não for número
    const numericValue = Number(rawValue) / 100; // transforma em decimal
    const formattedValue = numericValue.toLocaleString("pt-BR", {
      style: "currency",
      currency: "BRL",
    });

    setFormData((prev) => ({
      ...prev,
      valorEmprestimo: formattedValue,
    }));
    setErrors((prev) => ({ ...prev, valorEmprestimo: "" }));
  };

  const handleTaxaChange = (e) => {
    let value = e.target.value;

    // Remove tudo que não for número ou ponto
    value = value.replace(/[^0-9.]/g, "");

    // Garantir apenas um ponto decimal
    const parts = value.split(".");
    if (parts.length > 2) {
      value = parts[0] + "." + parts[1];
    }

    // Limitar a duas casas decimais
    if (parts[1]?.length > 2) {
      value = parts[0] + "." + parts[1].slice(0, 2);
    }

    setFormData((prev) => ({
      ...prev,
      taxaJuros: value,
    }));
    setErrors((prev) => ({ ...prev, taxaJuros: "" }));
  };

  return (
    <div className="min-h-screen flex flex-col items-center p-4 bg-gray-900 text-white">
      {/* Formulario */}
      <form
        onSubmit={handleSubmit}
        className="bg-gray-800 rounded-lg shadow-md p-6 w-full"
        style={{
          paddingLeft: "calc(50vw - 600px)",
          paddingRight: "calc(50vw - 600px)",
        }}
      >
        {/* Título */}
        <h1 className="text-2xl font-bold text-center mb-6">
          Formulário de Empréstimo
        </h1>

        {/* Flexbox para os campos */}
        <div className="flex flex-wrap gap-6">
          {/* Data Inicial */}
          <div className="flex-1 min-w-[200px]">
            <label className="block text-gray-300 mb-1">Data Inicial</label>
            <input
              type="date"
              name="dataInicial"
              value={formData.dataInicial}
              onChange={handleChange}
              className="w-full border border-gray-600 rounded px-3 py-2 bg-gray-700 text-white focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
            {errors.dataInicial && (
              <p className="text-red-400 text-sm mt-1">{errors.dataInicial}</p>
            )}
          </div>

          {/* Data Final */}
          <div className="flex-1 min-w-[200px]">
            <label className="block text-gray-300 mb-1">Data Final</label>
            <input
              type="date"
              name="dataFinal"
              value={formData.dataFinal}
              onChange={handleChange}
              className={`w-full border rounded px-3 py-2 bg-gray-700 text-white focus:outline-none focus:ring-2 ${errors.dataFinal
                ? "border-red-500 focus:ring-red-500"
                : "border-gray-600 focus:ring-blue-500"
                }`}
            />
            {errors.dataFinal && (
              <p className="text-red-400 text-sm mt-1">{errors.dataFinal}</p>
            )}
          </div>

          {/* Primeiro Pagamento */}
          <div className="flex-1 min-w-[200px]">
            <label className="block text-gray-300 mb-1">Primeiro Pagamento</label>
            <input
              type="date"
              name="primeiroPagamento"
              value={formData.primeiroPagamento}
              onChange={handleChange}
              className={`w-full border rounded px-3 py-2 bg-gray-700 text-white focus:outline-none focus:ring-2 ${errors.primeiroPagamento
                ? "border-red-500 focus:ring-red-500"
                : "border-gray-600 focus:ring-blue-500"
                }`}
            />
            {errors.primeiroPagamento && (
              <p className="text-red-400 text-sm mt-1">
                {errors.primeiroPagamento}
              </p>
            )}
          </div>

          {/* Valor Empréstimo */}
          <div className="flex-1 min-w-[200px]">
            <label className="block text-gray-300 mb-1">Valor do Empréstimo</label>
            <input
              type="text"
              name="valorEmprestimo"
              value={formData.valorEmprestimo}
              onChange={handleValorChange}
              placeholder="R$ 0,00"
              className="w-full border border-gray-600 rounded px-3 py-2 bg-gray-700 text-white focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
            {errors.valorEmprestimo && (
              <p className="text-red-400 text-sm mt-1">{errors.valorEmprestimo}</p>
            )}
          </div>

          {/* Taxa Juros */}
          <div className="flex-1 min-w-[200px]">
            <label className="block text-gray-300 mb-1">Taxa de Juros (%)</label>
            <input
              type="text"
              name="taxaJuros"
              value={formData.taxaJuros}
              onChange={handleTaxaChange}
              placeholder="Ex: 5%"
              className="w-full border border-gray-600 rounded px-3 py-2 bg-gray-700 text-white focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
            {errors.taxaJuros && (
              <p className="text-red-400 text-sm mt-1">{errors.taxaJuros}</p>
            )}
          </div>

          {/* Botão Enviar */}
          <div className="flex-1 min-w-[30px] flex items-end">
            <button
              type="submit"
              className="bg-blue-600 text-white px-6 py-2 rounded hover:bg-blue-700 transition"
            >
              Enviar
            </button>
          </div>
        </div>
      </form>

      {/* Tabela de resultados */}
      {resultado && (
        <div className="overflow-x-auto mt-8 w-full">
          <table className="min-w-full border border-gray-700 text-white">
            <thead className="bg-gray-800">
              <tr>
                {Object.keys(resultado[0]).map((key) => (
                  <th
                    key={key}
                    className="border border-gray-700 px-4 py-2 text-left"
                  >
                    {key}
                  </th>
                ))}
              </tr>
            </thead>
            <tbody>
              {resultado.map((item, index) => (
                <tr key={index} className="hover:bg-gray-700">
                  {Object.values(item).map((val, idx) => (
                    <td key={idx} className="border border-gray-700 px-4 py-2">
                      {val}
                    </td>
                  ))}
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );

}