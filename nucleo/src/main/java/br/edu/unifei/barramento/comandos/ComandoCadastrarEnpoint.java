package br.edu.unifei.barramento.comandos;

import br.edu.unifei.barramento.Barramento;
import br.edu.unifei.modelos.mensagem.Mensagem;
import br.edu.unifei.modelos.protocolo.Protocolo;
import com.google.gson.JsonObject;

/**
 * Comando para cadastrar um novo endpoint no barramento.
 */
public class ComandoCadastrarEnpoint implements Comando {

    private final String endpoint;
    private final Protocolo protocolo;

    /**
     * Construtor que inicializa o comando com o endpoint e o protocolo.
     * @param endpoint  O endpoint a ser registrado.
     * @param protocolo O {@link Protocolo} a ser associado ao endpoint.
     */
    public ComandoCadastrarEnpoint(String endpoint, Protocolo protocolo) {
        this.endpoint = endpoint;
        this.protocolo = protocolo;
    }

    /**
     * Executa o registro de endpoint no barramento. Se o endpoint já estiver cadastrado, sobrescreve os dados.
     *
     * @param mensagem   A {@link Mensagem} contendo a requisição de cadastro.
     * @param barramento O {@link Barramento} onde o endpoint será registrado.
     * @return Uma mensagem de confirmação de registro.
     */
    @Override
    public Mensagem executar(Mensagem mensagem, Barramento barramento) {
        barramento.cadastrarEndpoint(endpoint, this.protocolo);

        JsonObject resultado = new JsonObject();
        resultado.addProperty("resultado", "Endpoint registrado com sucesso.");

        // Mantém os metadados da mensagem original se houver
        if (mensagem.getConteudo().has("metadata")) {
            resultado.add("metadata", mensagem.getConteudo().getAsJsonObject("metadata"));
        }

        return new Mensagem("CONFIRMACAO_CADASTRO", "barramento", mensagem.getOrigem(), resultado);
    }
}
