package br.edu.unifei.barramento;

import br.edu.unifei.modelos.mensagem.Mensagem;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Classe responsável por armazenar e gerenciar uma fila de mensagens para processamento. A fila utiliza um mecanismo
 * de espera ativa para sincronizar a adição e remoção de mensagens.
 */
public class FilaDeMensagens {

    /**
     * Fila de mensagens a serem processadas pelo barramento. Ela armazena objetos {@link Mensagem} e é utilizado
     * para organizar o fluxo de mensagens recebidas antes de seu processamento.
     */
    private final Queue<Mensagem> fila = new LinkedList<>();

    /**
     * Adiciona uma nova mensagem à fila de forma sincronizada e notifica todas as threads em espera.
     *
     * @param mensagem A {@link Mensagem} a ser adicionada à fila.
     */
    public synchronized void adicionar(Mensagem mensagem) {
        fila.add(mensagem);
        notifyAll();
    }

    /**
     * Remove e retorna a primeira mensagem da fila de forma sincronizada. Caso a fila esteja vazia, a thread entra em
     * modo de espera até que uma nova mensagem seja adicionada.
     *
     * @return A {@link Mensagem} removida da fila.
     */
    public synchronized Mensagem remover() {
        while (fila.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        return fila.poll();
    }
}
