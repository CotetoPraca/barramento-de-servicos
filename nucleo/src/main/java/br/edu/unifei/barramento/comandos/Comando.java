package br.edu.unifei.barramento.comandos;

import br.edu.unifei.barramento.Barramento;
import br.edu.unifei.modelos.mensagem.Mensagem;

/**
 * Interface que define o contrato para execução de comandos no barramento.
 */
public interface Comando {

    /**
     * Executa uma operação no barramento com base na mensagem recebida e protocolo.
     *
     * @param mensagem   A {@link Mensagem} recebida que contém os parâmetros necessários para a execução.
     * @param barramento O {@link Barramento} onde os serviços e endpoints estão registrados.
     * @return Uma {@link Mensagem} de resposta com o resultado da operação.
     */
    Mensagem executar(Mensagem mensagem, Barramento barramento);
}
