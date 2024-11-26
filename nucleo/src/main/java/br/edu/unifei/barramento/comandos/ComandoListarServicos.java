package br.edu.unifei.barramento.comandos;

import br.edu.unifei.barramento.Barramento;
import br.edu.unifei.modelos.mensagem.Mensagem;
import br.edu.unifei.modelos.protocolo.Protocolo;
import com.google.gson.JsonObject;

/**
 * Comando para listar todos os serviços registrados no barramento.
 */
public class ComandoListarServicos implements Comando {

    /**
     * Executa a listagem de todos os serviços registrados no barramento.
     *
     * @param mensagem   A {@link Mensagem} solicitando a listagem de serviços.
     * @param barramento O {@link Barramento} que gerencia o banco de serviços.
     * @return Uma {@link Mensagem} contendo uma lista com o nome de todos os serviços registrados.
     */
    @Override
    public Mensagem executar(Mensagem mensagem, Barramento barramento) {
        String listaDeServicos = barramento.getGerenciadorDeServicos().listarServicos();

        JsonObject resultado = new JsonObject();
        resultado.addProperty("resultado", listaDeServicos);

        // Mantém os metadados da mensagem original se houver
        if (mensagem.getConteudo().has("metadata")) {
            resultado.add("metadata", mensagem.getConteudo().getAsJsonObject("metadata"));
        }

        return new Mensagem("RESULTADO_LISTAGEM", "barramento", mensagem.getOrigem(), resultado);
    }
}
