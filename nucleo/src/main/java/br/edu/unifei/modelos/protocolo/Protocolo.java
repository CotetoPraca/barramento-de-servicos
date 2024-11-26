package br.edu.unifei.modelos.protocolo;

import br.edu.unifei.barramento.ControladorDoBarramento;
import br.edu.unifei.modelos.mensagem.Mensagem;

/**
 * Classe abstrata que define a estrutura básica de um protocolo de comunicação. Um protocolo é responsável por enviar
 * mensagens e gerenciar a comunicação entre sistemas. Cada implementação específica de protocolo (por exemplo: MQTT,
 * COAP) deve fornecer seus próprios métodos de envio de mensagem e desconexão.
 */
public abstract class Protocolo {
    private ControladorDoBarramento controlador;

    /**
     * @return O {@link ControladorDoBarramento} associado ao protocolo.
     */
    public ControladorDoBarramento getControlador() {
        return controlador;
    }

    /**
     * @param controlador O {@link ControladorDoBarramento} do barramento a ser associado a este protocolo
     */
    public void setControlador(ControladorDoBarramento controlador) {
        this.controlador = controlador;
    }

    /**
     * @return O nome do protocolo, que por padrão é o nome da classe que implementa o protocolo.
     */
    public String getNomeProtocolo() {
        return this.getClass().getSimpleName();
    }

    /**
     * Método abstrato para enviar uma mensagem. Implementações específicas do protocolo devem fornecer a lógica de
     * envio de mensagem.
     *
     * @param mensagem A {@link Mensagem} a ser enviada.
     */
    public abstract void enviarMensagem(Mensagem mensagem);

    /**
     * Método abstrato para desconectar o protocolo. Implementações específicas do protocolo devem fornecer a lógica
     * para encerrar a conexão.
     */
    public abstract void desconectar();
}
