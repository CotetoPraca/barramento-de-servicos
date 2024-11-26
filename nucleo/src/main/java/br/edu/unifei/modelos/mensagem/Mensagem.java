package br.edu.unifei.modelos.mensagem;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Classe que representa uma mensagem a ser trocada entre serviços e protocolos. Uma mensagem contém uma ação, o
 * endereço de origem, o endereço de destino e um conteúdo em formato JSON.
 */
public class Mensagem {
    private final String acao;
    private final String origem;
    private final String destino;
    private final JsonObject conteudo;

    /**
     * Construtor que inicializa uma mensagem com os parâmetros fornecidos.
     *
     * @param acao     A ação associada a mensagem.
     * @param origem   O identificador do remetente da mensagem.
     * @param destino  O identificador do destinatário da mensagem.
     * @param conteudo O conteúdo da mensagem em formato {@link JsonObject}.
     */
    public Mensagem(String acao, String origem, String destino, JsonObject conteudo) {
        this.acao = acao;
        this.origem = origem;
        this.destino = destino;
        this.conteudo = conteudo;
    }

    /**
     * Cria uma instância de {@code Mensagem} a partir de uma string JSON.
     *
     * @param json A string JSON a ser convertida em uma instância de {@code Mensagem}.
     * @return Uma nova instância de {@code Mensagem} a partir do JSON fornecido.
     */
    public static Mensagem fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, Mensagem.class);
    }

    /**
     * @return a ação da mensagem.
     */
    public String getAcao() {
        return acao;
    }

    /**
     * @return o identificador do remetente da mensagem.
     */
    public String getOrigem() {
        return origem;
    }

    /**
     * @return o identificador do destinatário da mensagem.
     */
    public String getDestino() {
        return destino;
    }

    /**
     * @return o conteúdo da mensagem no formato {@link JsonObject}.
     */
    public JsonObject getConteudo() {
        return conteudo;
    }

    /**
     * Converte esta mensagem para uma string JSON.
     *
     * @return a representação JSON desta mensagem.
     */
    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    /**
     * Auxiliar para adicionar timestamps ao campo "metadata" da mensagem.
     *
     * @param campo Nome do campo do timestamp a ser adicionado (Ex.: "timestamp_envio").
     */
    public void adicionarTimestampAoMetadata(String campo) {
        JsonObject metadata = this.conteudo.has("metadata")
                ? this.conteudo.getAsJsonObject("metadata")
                : new JsonObject();
        metadata.addProperty(campo, System.currentTimeMillis());
        this.conteudo.add("metadata", metadata);
    }
}
