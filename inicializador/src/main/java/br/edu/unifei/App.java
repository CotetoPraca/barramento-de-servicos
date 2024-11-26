package br.edu.unifei;

import br.edu.unifei.barramento.Barramento;
import br.edu.unifei.barramento.ControladorDoBarramento;
import br.edu.unifei.barramento.gerenciamento.GerenciadorDeServicos;
import br.edu.unifei.coap.ControladorDoProtocoloCoAP;
import br.edu.unifei.mqtt.BrokerMQTT;
import br.edu.unifei.mqtt.ControladorDoProtocoloMQTT;
import br.edu.unifei.utils.LogUtils;

import java.io.IOException;

/**
 * Classe principal do Barramento de Serviços, responsável por inicializar o loop principal da aplicação e gerenciar a
 * execução dos controladores de protocolo (MQTT e CoAP).
 *
 * <p>
 *     A aplicação é projetada para rodar indefinidamente até que seja interrompida externamente, como através de um
 *     comando de encerramento no terminal (CTRL+C) ou interrupção da execução por uma IDE.
 * </p>
 *
 * Ao iniciar, a aplicação cria instâncias dos controladores de protocolo e inicia seus loops. Quando a aplicação é
 * encerrada, seja por uma interrupção externa ou por um hook de encerramento do Java, os controladores de protocolo
 * são finalizados corretamente.
 */
public class App {

    /**
     * Método principal que inicia a aplicação do Barramento de Serviços.
     * Inicializa os controladores de protocolo MQTT e CoAP, e configura um hook de encerramento para garantir que os
     * recursos sejam liberados quando a aplicação for interrompida.
     *
     * @param args Argumentos de linha de comando (não utilizados).
     * @throws IOException Se ocorrer um erro de I/O durante a inicialização dos controladores.
     */
    public static void main(String[] args) throws IOException {
        LogUtils.logInfo("Iniciando aplicação...");
        Barramento barramento = new Barramento(new GerenciadorDeServicos());

        BrokerMQTT brokerMQTT = new BrokerMQTT();
        brokerMQTT.startBroker();

        ControladorDoBarramento controladorMQTT = new ControladorDoProtocoloMQTT(barramento);
        controladorMQTT.iniciar();

        ControladorDoBarramento controladorCoAP = new ControladorDoProtocoloCoAP(barramento);
        controladorCoAP.iniciar();

        LogUtils.logInfo("Aplicação iniciada.");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LogUtils.logInfo("Encerrando aplicação...");
            controladorMQTT.parar();
            controladorCoAP.parar();
            brokerMQTT.stopBroker();
            LogUtils.logInfo("Aplicação encerrada.");
        }));
    }
}