package br.edu.unifei.barramento.workers;

import br.edu.unifei.barramento.FilaDeMensagens;
import br.edu.unifei.modelos.mensagem.Mensagem;
import br.edu.unifei.utils.LogUtils;

/**
 * Worker responsável por receber mensagens e adicioná-las à fila para processamento posterior. Cada implementação
 * de {@link br.edu.unifei.modelos.protocolo.Protocolo} pode ter uma variação específica desse worker.
 *
 * <p>
 *     Este worker funciona de forma independente, permitindo que a recepção de mensagens ocorra paralelamente ao
 *     processamento, sem interferência entre as operações.
 * </p>
 */
public class WorkerDeRecepcao implements Runnable {

    /**
     * A {@link FilaDeMensagens} que armazena as mensagens recebidas para serem processadas.
     */
    private final FilaDeMensagens fila;

    /**
     * Indica se o worker está ativo. Responsável por manter o loop de execução do worker.
     */
    private boolean ativo;

    /**
     * Construtor do WorkerDeRecepcao.
     *
     * @param fila A {@link FilaDeMensagens} onde as mensagens recebidas serão armazenadas.
     */
    public WorkerDeRecepcao(FilaDeMensagens fila) {
        this.fila = fila;
        this.ativo = true;
    }

    /**
     * Adiciona a mensagem recebida à fila de mensagens para processamento.
     *
     * @param mensagem A mensagem recebida a ser adicionada à {@link FilaDeMensagens}.
     */
    public void adicionarMensagemAFila(Mensagem mensagem) {
        String sufixo_timestamp = "";
        if (mensagem.getOrigem().contains("cliente")) {
            sufixo_timestamp += "_cliente";
        } else if (mensagem.getOrigem().contains("servidor")) {
            sufixo_timestamp += "_servidor";
        } else if (mensagem.getOrigem().contains("embarcado")) {
            sufixo_timestamp += "_embarcado";
        }
        mensagem.adicionarTimestampAoMetadata(String.format("timestamp_bus_msg_recebida%s", sufixo_timestamp));
        fila.adicionar(mensagem);
        LogUtils.logInfo("Mensagem recebida e adicionada à fila.");
    }

    /**
     * Método de execução do worker. Enquanto estiver ativo, o worker ficará em estado de espera.
     */
    @Override
    public void run() {
        while (ativo) {
            // Aguarda interrupção do worker
        }
        LogUtils.logInfo("Worker de Recepção interrompido.");
    }

    /**
     * Sinaliza que o loop de execução do worker deve ser interrompido.
     */
    public void parar() {
        this.ativo = false;
    }
}
