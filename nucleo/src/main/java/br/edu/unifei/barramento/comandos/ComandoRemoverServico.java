package br.edu.unifei.barramento.comandos;

import br.edu.unifei.barramento.Barramento;
import br.edu.unifei.modelos.mensagem.Mensagem;
import br.edu.unifei.modelos.protocolo.Protocolo;
import com.google.gson.JsonObject;

/**
 * Comando para remover um serviço registrado no banco de serviços do barramento.
 */
public class ComandoRemoverServico implements Comando {

    private final String servico;

    /**
     * Construtor que inicializa o comando com o nome do serviço a ser removido.
     *
     * @param servico O nome do serviço a ser removido.
     */
    public ComandoRemoverServico(String servico) {
        this.servico = servico;
    }

    /**
     * Executa a remoção do serviço do banco de serviços. Retorna a confirmação mesmo que o serviço não esteja
     * previamente cadastrado.
     *
     * @param mensagem   A {@link Mensagem} recebida que contém os parâmetros necessários para a execução.
     * @param barramento O {@link Barramento} onde os serviços e endpoints estão registrados.
     * @return Uma {@link Mensagem} de confirmação da remoção.
     */
    @Override
    public Mensagem executar(Mensagem mensagem, Barramento barramento) {
        barramento.getGerenciadorDeServicos().removerServico(this.servico);

        JsonObject resultado = new JsonObject();
        resultado.addProperty(
                "resultado", String.format("Serviço '%s' removido com sucesso.", this.servico)
        );

        // Mantém os metadados da mensagem original se houver
        if (mensagem.getConteudo().has("metadata")) {
            resultado.add("metadata", mensagem.getConteudo().getAsJsonObject("metadata"));
        }

        return new Mensagem("CONFIRMACAO_REMOCAO", "barramento", mensagem.getOrigem(), resultado);
    }
}
