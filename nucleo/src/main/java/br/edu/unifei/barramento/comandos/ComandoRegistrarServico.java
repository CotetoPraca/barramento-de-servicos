package br.edu.unifei.barramento.comandos;

import br.edu.unifei.barramento.Barramento;
import br.edu.unifei.modelos.mensagem.Mensagem;
import com.google.gson.JsonObject;

/**
 * Comando para registrar um novo serviço no banco de serviços do barramento.
 */
public class ComandoRegistrarServico implements Comando {

    private final String servico;
    private final String bytecode;

    /**
     * Construtor que inicializa o comando com o nome do serviço e o seu bytecode.
     *
     * @param servico  O nome do serviço a ser registrado.
     * @param bytecode O bytecode associado ao serviço.
     */
    public ComandoRegistrarServico(String servico, String bytecode) {
        this.servico = servico;
        this.bytecode = bytecode;
    }

    /**
     * Executa o registro do serviço no barramento
     *
     * @param mensagem   A {@link Mensagem} recebida que contém os parâmetros necessários para a execução.
     * @param barramento O {@link Barramento} onde os serviços e endpoints estão registrados.
     * @return Uma {@link Mensagem} de confirmação do registro.
     */
    @Override
    public Mensagem executar(Mensagem mensagem, Barramento barramento) {
        barramento.getGerenciadorDeServicos().registrarServico(this.servico, this.bytecode);

        JsonObject resultado = new JsonObject();
        resultado.addProperty(
                "resultado", String.format("Serviço '%s' registrado com sucesso.", this.servico)
        );

        // Mantém os metadados da mensagem original se houver
        if (mensagem.getConteudo().has("metadata")) {
            resultado.add("metadata", mensagem.getConteudo().getAsJsonObject("metadata"));
        }

        return new Mensagem("CONFIRMACAO_REGISTRO", "barramento", mensagem.getOrigem(), resultado);
    }
}
