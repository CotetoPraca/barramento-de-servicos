package br.edu.unifei.mqtt;

import br.edu.unifei.modelos.mensagem.Mensagem;
import br.edu.unifei.modelos.protocolo.Protocolo;
import br.edu.unifei.utils.LogUtils;
import com.google.gson.JsonObject;
import org.eclipse.paho.client.mqttv3.*;

import java.nio.charset.StandardCharsets;

/**
 * Classe responsável por configurar e gerenciar a comunicação MQTT, permitindo o envio, recebimento e tratamento de
 * mensagens, além de gerenciar a reconexão com o broker MQTT em caso de falhas.
 */
public class ProtocoloMQTT extends Protocolo implements MqttCallback {

    private static final String BROKER_URL = "tcp://localhost:1883";
    private static final String CLIENT_ID = "barramento_mqtt";
    private static final String TOPICO = "topico/barramento";
    private static final int MAX_RETRIES = 5; // Máximo de tentativas
    private static final long RETRY_DELAY_MS = 5000; // Tempo de espera entre tentativas (5 segundos)

    MqttClient cliente;

    /**
     * Construtor que configura o cliente MQTT e gerencia tentativas de conexão ao broker MQTT.
     */
    public ProtocoloMQTT() {
        boolean connected = false;
        int tentativas = 0;

        while (!connected && tentativas < MAX_RETRIES) {
            try {
                cliente = new MqttClient(BROKER_URL, CLIENT_ID);
                MqttConnectOptions options = new MqttConnectOptions();
                options.setCleanSession(true);

                // Define a classe ProtocoloMQTT como callback
                cliente.setCallback(this);

                cliente.connect(options);

                cliente.subscribe(TOPICO);

                connected = true;
                LogUtils.logInfo("Cliente MQTT conectado ao broker com sucesso.");

            } catch (MqttException e) {
                tentativas++;
                LogUtils.logError("Erro ao iniciar o cliente MQTT, tentativa %d/%d: %s",
                        tentativas, MAX_RETRIES, e.getMessage());
                if (tentativas < MAX_RETRIES) {
                    try {
                        Thread.sleep(RETRY_DELAY_MS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        LogUtils.logError("Interrupção durante o intervalo de retentativas: %s", ie.getMessage());
                    }
                } else {
                    LogUtils.logError("Falha ao conectar ao broker MQTT após %d tentativas.", tentativas);
                }
            }
        }
    }

    /**
     * Envia uma mensagem via protocolo MQTT.
     *
     * @param mensagem A {@link Mensagem} a ser enviada.
     */
    public void enviarMensagem(Mensagem mensagem) {
        try {
            String mensagemJson = mensagem.toJson();
            MqttMessage mqttMessage = new MqttMessage(mensagemJson.getBytes(StandardCharsets.UTF_8));
            mqttMessage.setQos(1);
            cliente.publish(mensagem.getDestino(), mqttMessage);
            LogUtils.logInfo("Mensagem enviada via MQTT: %s", mensagemJson);
        } catch (MqttException e) {
            LogUtils.logError("Erro ao enviar mensagem via MQTT: %s", e.getMessage());
        }
    }

    /**
     * Desconecta o cliente MQTT e libera os recursos utilizados.
     */
    public void desconectar() {
        try {
            cliente.disconnect();
            LogUtils.logInfo("Cliente MQTT desconectado.");
        } catch (MqttException e) {
            LogUtils.logError("Erro ao desconectar o client MQTT: %s", e.getMessage());
        }
    }

    /**
     * Método chamado quando uma mensagem é recebida via MQTT.
     *
     * @param topico  O tópico no qual a mensagem foi recebida.
     * @param message A mensagem recebida no formato {@link MqttMessage}.
     */
    @Override
    public void messageArrived(String topico, MqttMessage message) {
        try {
            String payload = new String(message.getPayload());
            LogUtils.logInfo("Mensagem recebida via MQTT: %s", payload);
            Mensagem mensagemRecebida = Mensagem.fromJson(payload);

            String acao = mensagemRecebida.getAcao();
            String destino = mensagemRecebida.getDestino();

            if (acao.equals("ENVIAR_MENSAGEM") && destino.isEmpty()) {
                JsonObject conteudoErro = new JsonObject();
                conteudoErro.addProperty("resultado", "Erro no envio: Destino não informado.");

                Mensagem mensagemErro = new Mensagem(
                        "ERRO_ENVIO",
                        "barramento",
                        mensagemRecebida.getOrigem(),
                        conteudoErro
                );

                this.enviarMensagem(mensagemErro);
                return;
            }

            ControladorDoProtocoloMQTT controlador = (ControladorDoProtocoloMQTT) getControlador();
            controlador.adicionarMensagemAFila(mensagemRecebida);
        } catch (Exception e) {
            LogUtils.logError("Erro ao processar mensagem recebida: %s", e.getMessage());
        }
    }

    /**
     * Método chamado quando a conexão MQTT é perdida.
     *
     * @param cause A causa da perda de conexão.
     */
    @Override
    public void connectionLost(Throwable cause) {
        LogUtils.logError("Conexão perdida: %s", cause.getMessage());
    }

    /**
     * Método chamado quando a entrega de uma mensagem via MQTT é concluída.
     *
     * @param token O token que representa a entrega concluída.
     */
    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        LogUtils.logInfo("Entrega de mensagem via MQTT concluída.");
    }
}
