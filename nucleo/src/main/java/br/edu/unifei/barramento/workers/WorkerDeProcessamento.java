package br.edu.unifei.barramento.workers;

import br.edu.unifei.barramento.Barramento;
import br.edu.unifei.barramento.FilaDeMensagens;
import br.edu.unifei.barramento.comandos.*;
import br.edu.unifei.modelos.mensagem.Mensagem;
import br.edu.unifei.modelos.protocolo.Protocolo;
import br.edu.unifei.utils.LogUtils;
import com.google.gson.JsonObject;

/**
 * Worker responsável por processar as mensagens recebidas, executando os comandos apropriados. Cada implementação de
 * {@link Protocolo} pode ter uma versão específica deste worker.
 *
 * <p>
 *     Este worker funciona de forma independente da recepção de mensagens, permitindo que o processamento ocorra
 *     em paralelo.
 * </p>
 */
public class WorkerDeProcessamento implements Runnable {

    /**
     * O {@link Protocolo} associado ao processamento das mensagens.
     */
    private final Protocolo protocolo;

    /**
     * O {@link Barramento} que gerencia a comunicação e execução de comandos.
     */
    private final Barramento barramento;

    /**
     * A {@link FilaDeMensagens} onde as mensagens serão retiradas para processamento.
     */
    private final FilaDeMensagens fila;

    /**
     * Indica se o worker está ativo. Responsável por manter o loop de execução do worker.
     */
    private boolean ativo;

    /**
     * Construtor do WorkerDeProcessamento.
     *
     * @param protocolo  O {@link Protocolo} de comunicação vinculado ao worker.
     * @param barramento O {@link Barramento} responsável por gerenciar as mensagens serviços.
     * @param fila       A {@link FilaDeMensagens} a ser processada.
     */
    public WorkerDeProcessamento(Protocolo protocolo, Barramento barramento, FilaDeMensagens fila) {
        this.protocolo = protocolo;
        this.barramento = barramento;
        this.fila = fila;
        this.ativo = true;
    }

    /**
     * Instancia um {@link Comando} com base na mensagem recebida e o protocolo atual.
     * @param mensagem       A {@link Mensagem} recebida.
     * @param protocoloAtual O {@link Protocolo} vinculado ao worker.
     * @return O {@link Comando} correspondente à ação da mensagem.
     */
    private Comando criarComando(Mensagem mensagem, Protocolo protocoloAtual) {
        String acao = mensagem.getAcao();
        JsonObject conteudo = mensagem.getConteudo();
        switch (acao) {
            case "CADASTRAR_ENDPOINT":
                return new ComandoCadastrarEnpoint(mensagem.getOrigem(), protocoloAtual);
            case "REGISTRAR_SERVICO":
                return new ComandoRegistrarServico(
                        conteudo.get("servico").getAsString(),
                        conteudo.get("bytecode").getAsString()
                );
            case "REMOVER_SERVICO":
                return new ComandoRemoverServico(
                        conteudo.get("servico").getAsString()
                );
            case "BUSCAR_SERVICO":
                return new ComandoBuscarServico(
                        conteudo.get("servico").getAsString()
                );
            case "LISTAR_SERVICOS":
                return new ComandoListarServicos();
            case "ENVIAR_ARQUIVOS_BASE":
                return new ComandoEnviarArquivosBase();
            default:
                return null;
        }
    }

    /**
     * Método de execução do worker. Enquanto estiver ativo, ele processará as mensagens da fila.
     */
    @Override
    public void run() {
        while (ativo) {
            Mensagem mensagem = fila.remover();
            if (mensagem != null) {
                Comando comando = criarComando(mensagem, this.protocolo);

                if (comando != null) {
                    mensagem.adicionarTimestampAoMetadata("timestamp_bus_processamento_inicio");
                    Mensagem resposta = comando.executar(mensagem, barramento, this.protocolo);
                    if (resposta != null) {
                        resposta.adicionarTimestampAoMetadata("timestamp_bus_processamento_fim");
                        barramento.enviarMensagem(resposta, this.protocolo);
                        LogUtils.logInfo("Mensagem processada e enviada.");
                    } else {
                        LogUtils.logError("Falha ao gerar mensagem de resposta.");
                        throw new IllegalArgumentException();
                    }
                } else {
                    LogUtils.logInfo("Iniciando tentativa de envio da mensagem de %s para %s.",
                            mensagem.getOrigem(), mensagem.getDestino());
                    barramento.enviarMensagem(mensagem, this.protocolo);
                }
            }
        }
        LogUtils.logInfo("Worker de Processamento encerrado.");
    }

    /**
     * Sinaliza que o loop de execução do worker deve ser interrompido.
     */
    public void parar() {
        this.ativo = false;
    }
}
