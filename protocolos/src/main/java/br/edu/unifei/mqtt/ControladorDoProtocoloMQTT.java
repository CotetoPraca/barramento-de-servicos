package br.edu.unifei.mqtt;

import br.edu.unifei.barramento.Barramento;
import br.edu.unifei.barramento.ControladorDoBarramento;
import br.edu.unifei.barramento.workers.WorkerDeProcessamento;
import br.edu.unifei.barramento.workers.WorkerDeRecepcao;

/**
 * Classe responsável por gerenciar a comunicação utilizando o protocolo MQTT, inicializando os workers responsáveis
 * por receber e processar mensagens. Lida também com a desconexão apropriada dos recursos MQTT quando a aplicação é
 * encerrada.
 */
public class ControladorDoProtocoloMQTT extends ControladorDoBarramento {

    private final ProtocoloMQTT protocoloMQTT;

    /**
     * Construtor que inicializa o controlador MQTT e configura o protocolo MQTT.
     */
    public ControladorDoProtocoloMQTT(Barramento barramento) {
        super(barramento);
        this.protocoloMQTT = new ProtocoloMQTT();
        this.protocoloMQTT.setControlador(this);
    }

    /**
     * Inicializa os workers responsáveis pelo recebimento ({@link WorkerDeRecepcao}) e processamento
     * ({@link WorkerDeProcessamento}) de mensagens MQTT.
     * Cada worker é executado em uma thread separada.
     */
    @Override
    protected void iniciarWorkers() {
        workerDeRecepcao = new WorkerDeRecepcao(filaDeMensagens);
        workerDeProcessamento = new WorkerDeProcessamento(
                protocoloMQTT,
                barramento,
                filaDeMensagens
        );

        new Thread(workerDeRecepcao).start();
        new Thread(workerDeProcessamento).start();
    }

    /**
     * Encerra os workers e desconecta o protocolo MQTT de forma segura.
     * Esta operação é realizada durante o desligamento da aplicação.
     */
    @Override
    protected void pararWorkers() {
        protocoloMQTT.desconectar();
        workerDeRecepcao.parar();
        workerDeProcessamento.parar();
    }
}
