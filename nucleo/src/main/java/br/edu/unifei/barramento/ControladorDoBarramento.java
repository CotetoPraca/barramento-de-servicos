package br.edu.unifei.barramento;

import br.edu.unifei.barramento.workers.WorkerDeProcessamento;
import br.edu.unifei.barramento.workers.WorkerDeRecepcao;
import br.edu.unifei.modelos.mensagem.Mensagem;

/**
 * Classe abstrata responsável por controlar o barramento, gerenciando workers de recepção e processamento.
 * Implementações concretas dessa classe devem inicializar e parar os workers conforme necessário.
 */
public abstract class ControladorDoBarramento {

    protected WorkerDeProcessamento workerDeProcessamento;
    protected WorkerDeRecepcao workerDeRecepcao;
    protected FilaDeMensagens filaDeMensagens;
    protected Barramento barramento;
    protected boolean ativo;

    /**
     * Construtor que inicializa a fila de mensagens e define o estado do controlador como ativo.
     */
    public ControladorDoBarramento(Barramento barramento) {
        this.barramento = barramento;
        this.filaDeMensagens = new FilaDeMensagens();
        this.ativo = true;
    }

    /**
     * Adiciona uma mensagem à fila de recepção para ser processada posteriormente.
     *
     * @param mensagem A {@link Mensagem} a ser adicionada à fila.
     */
    public void adicionarMensagemAFila(Mensagem mensagem) {
        workerDeRecepcao.adicionarMensagemAFila(mensagem);
    }

    /**
     * Inicia os workers de recepção e processamento.
     */
    public void iniciar() {
        iniciarWorkers();
    }

    /**
     * Interrompe os workers de recepção e processamento, definindo o controlador como inativo.
     */
    public void parar() {
        this.ativo = false;
        pararWorkers();
    }

    /**
     * Método abstrato para iniciar os workers. Deve ser implementado pelas subclasses.
     */
    protected abstract void iniciarWorkers();

    /**
     * Método abstrato para parar os workers. Deve ser implementado pelas subclasses.
     */
    protected abstract void pararWorkers();
}
