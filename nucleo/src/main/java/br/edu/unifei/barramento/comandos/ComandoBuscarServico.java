package br.edu.unifei.barramento.comandos;

import br.edu.unifei.barramento.Barramento;
import br.edu.unifei.modelos.mensagem.Mensagem;
import br.edu.unifei.modelos.protocolo.Protocolo;
import com.google.gson.JsonObject;

/**
 * Comando para buscar um serviço registrado no barramento.
 */
public class ComandoBuscarServico implements Comando {

    private final String servico;

    /**
     * Construtor que inicializa o comando com o nome do serviço a ser buscado.
     *
     * @param servico O nome do serviço a ser buscado no barramento.
     */
    public ComandoBuscarServico(String servico) {
        this.servico = servico;
    }

    /**
     * Executa a busca do serviço no barramento e retorna o bytecode do serviço, se encontrado.
     *
     * @param mensagem   A {@link Mensagem} contendo a requisição de busca.
     * @param barramento O {@link Barramento} que gerencia o inventário de serviços.
     * @param protocolo  O {@link Protocolo} usado para a comunicação no barramento.
     * @return Uma {@link Mensagem} de resultado contendo o bytecode do serviço buscado ou um erro, se não encontrado.
     */
    @Override
    public Mensagem executar(Mensagem mensagem, Barramento barramento, Protocolo protocolo) {
        String bytecode = barramento.getGerenciadorDeServicos().buscarServico(this.servico);

        JsonObject resultado = new JsonObject();

        if (bytecode == null) {
            resultado.addProperty("erro", String.format("Serviço %s não encontrado.", this.servico));
        } else {
            resultado.addProperty("nome", this.servico);
            resultado.addProperty("bytecode", bytecode);

        }

        // Mantém os metadados da mensagem original se houver
        if (mensagem.getConteudo().has("metadata")) {
            resultado.add("metadata", mensagem.getConteudo().getAsJsonObject("metadata"));
        }

        return new Mensagem("RESULTADO_BUSCA", "barramento", mensagem.getOrigem(), resultado);
    }
}
