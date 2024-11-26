package br.edu.unifei.coap;

import br.edu.unifei.barramento.Barramento;
import br.edu.unifei.barramento.ControladorDoBarramento;
import br.edu.unifei.barramento.workers.WorkerDeProcessamento;
import br.edu.unifei.barramento.workers.WorkerDeRecepcao;

import java.io.IOException;

/**
 * Controlador responsável por gerenciar a comunicação do protocolo CoAP, inicializando os workers responsáveis por
 * receber e processar mensagens. Lida também com a desconexão apropriada dos recursos CoAP quando a aplicação é
 * encerrada.
 */
public class ControladorDoProtocoloCoAP extends ControladorDoBarramento {

    private final ProtocoloCoAP protocoloCoAP;

    /**
     * Construtor que inicializa o controlador CoAP e configura o protocolo CoAP.
     *
     * @throws IOException Se ocorrer um erro durante a configuração do protocolo CoAP.
     */
    public ControladorDoProtocoloCoAP(Barramento barramento) throws IOException {
        super(barramento);
        this.protocoloCoAP = new ProtocoloCoAP();
        this.protocoloCoAP.setControlador(this);
    }

    /**
     * Inicializa os wokers responsáveis pelo recebimento ({@link WorkerDeRecepcao}) e processamento
     * ({@link WorkerDeProcessamento}) de mensagens CoAP.
     * Cada worker é executado em uma thread separada.
     */
    @Override
    protected void iniciarWorkers() {
        workerDeRecepcao = new WorkerDeRecepcao(filaDeMensagens);
        workerDeProcessamento = new WorkerDeProcessamento(
                protocoloCoAP,
                barramento,
                filaDeMensagens
        );

        new Thread(workerDeRecepcao).start();
        new Thread(workerDeProcessamento).start();
    }

    /**
     * Encerra os workers e desconecta o protocolo CoAP de forma segura.
     * Esta operação é realizada durante o desligamento da aplicação.
     */
    @Override
    protected void pararWorkers() {
        protocoloCoAP.desconectar();
        workerDeRecepcao.parar();
        workerDeProcessamento.parar();
    }
}
