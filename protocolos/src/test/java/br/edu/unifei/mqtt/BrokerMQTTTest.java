package br.edu.unifei.mqtt;

import br.edu.unifei.modelos.mensagem.Mensagem;
import com.google.gson.JsonObject;

public class BrokerMQTTTest {
    public static void main(String[] args) {
        BrokerMQTT broker = new BrokerMQTT();
        broker.startBroker();

        // Aguarda o broker iniciar
        try {
            Thread.sleep(2000); // 2 segundos
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Inicializa o cliente MQTT
        ProtocoloMQTT clienteMqtt = new ProtocoloMQTT();

        // Simula o envio de uma mensagem
        JsonObject conteudo = new JsonObject();
        conteudo.addProperty("teste", "Hello MQTT!");

        Mensagem mensagem = new Mensagem(
                "ENVIAR_MENSAGEM",
                "cliente_teste",
                "barramento",
                conteudo
        );

        System.out.println("Enviando mensagem de teste...");
        clienteMqtt.enviarMensagem(mensagem);

        // Aguarda por mensagens recebidas
        try {
            System.out.println("Aguardando mensagens...");
            Thread.sleep(5000); // 5 segundos
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Finaliza o cliente e o broker
        System.out.println("Finalizando o cliente e o broker...");
        clienteMqtt.desconectar();
        broker.stopBroker();
    }
}
