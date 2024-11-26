package br.edu.unifei.barramento.gerenciamento;

import br.edu.unifei.utils.LogUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Classe responsável por gerenciar o registro, remoção, busca e listagem de serviços.
 * Mantém um mapeamento de serviços onde cada serviço é armazenado com o seu nome e bytecode associado.
 */
public class GerenciadorDeServicos {
    /**
     * Mapa que representa o inventário de serviços, responsável por armazenar os serviços registrados usando nome do
     * serviço como chave e o seu bytecode como o valor.
     */
    private static final Map<String, String> servicos = new HashMap<>();

    /**
     * Registra um novo serviço no mapeamento de serviços. Sobrescreve o dado se o serviço já existir.
     *
     * @param servico  O nome do serviço a ser registrado.
     * @param bytecode O bytecode associado ao serviço.
     */
    public void registrarServico(String servico, String bytecode) {
        servicos.put(servico, bytecode);
        LogUtils.logInfo("Serviço '%s' registrado", servico);
    }

    /**
     * Remove o serviço do mapeamento de serviços.
     *
     * @param servico O nome do serviço a ser removido.
     */
    public void removerServico(String servico) {
        servicos.remove(servico);
        LogUtils.logInfo("Servico '%s' removido.", servico);
    }

    /**
     * Busca um serviço no mapeamento de serviços.
     *
     * @param servico O nome do serviço a ser buscado.
     * @return O bytecode do serviço se encontrado, ou {@code null} se o serviço não estiver registrado.
     */
    public String buscarServico(String servico) {
        return servicos.get(servico);
    }

    /**
     * Lista de todos os serviços registrados no mapeamento de serviços.
     *
     * @return Uma string contendo a lista dos nomes de todos os serviços disponíveis, ou uma mensagem indicando que não
     * há serviços registrados.
     */
    public String listarServicos() {
        Set<String> servicosCadastrados = servicos.keySet();
        return servicosCadastrados.isEmpty() ? "Nenhum serviço disponível."
                : "Serviços disponíveis: \n" + servicosCadastrados.stream()
                .map(key -> "    - " + key)
                .collect(Collectors.joining("\n"));
    }
}
