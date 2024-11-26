package br.edu.unifei.modelos.servico;

import br.edu.unifei.modelos.mensagem.Mensagem;

/**
 * Classe abstrata que representa um serviço. Esta classe serve como base para a criação de serviços específicos.
 */
public abstract class Servico {

    /**
     * Atributo para armazenar o nome do serviço, preenchido com o nome da classe.
     */
    protected final String nome;

    /**
     * Construtor padrão que inicializa o serviço atribuindo o nome da classe ao atributo {@code nome}.
     */
    protected Servico() {
        this.nome = getClass().getSimpleName();
    }

    /**
     * @return O nome do serviço.
     */
    public String getNome() {
        return nome;
    }

    /**
     * Executa a lógica do serviço com base na mensagem fornecida.
     *
     * @param mensagem A {@link Mensagem} a ser processada pelo serviço.
     * @return A {@link Mensagem} processada.
     */
    public Mensagem executar(Mensagem mensagem) {
        System.out.println("Processando mensagem: " + mensagem.toJson());

        // Adicionar a lógica de execução do serviço aqui

        return mensagem;
    }
}
