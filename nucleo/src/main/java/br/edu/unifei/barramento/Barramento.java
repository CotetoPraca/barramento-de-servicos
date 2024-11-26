package br.edu.unifei.barramento;

import br.edu.unifei.barramento.gerenciamento.GerenciadorDeServicos;
import br.edu.unifei.modelos.mensagem.Mensagem;
import br.edu.unifei.modelos.protocolo.Protocolo;
import br.edu.unifei.utils.LogUtils;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Classe responsável por gerenciar a comunicação entre diferentes endpoints via protocolos de transporte variados.
 * O barramento mantém um {@link GerenciadorDeServicos} e mantém um mapeamento com os protocolos de preferência
 * associado a cada endpoint.
 */
public class Barramento {

    private final String id = UUID.randomUUID().toString();
    private final GerenciadorDeServicos gerenciadorDeServicos;

    /**
     * Mapeamento responsável por armazenar a relação entre o endpoint e o protocolo de preferência
     * associado a ele.
     */
    protected final Map<String, Protocolo> preferenciasDeProtocolo;

    /**
     * Constrói um novo Barramento com o gerenciador de serviços especificado.
     *
     * @param gerenciadorDeServicos O {@link GerenciadorDeServicos} responsável por gerenciar o inventário de serviços.
     */
    public Barramento(GerenciadorDeServicos gerenciadorDeServicos) {
        this.gerenciadorDeServicos = gerenciadorDeServicos;
        this.preferenciasDeProtocolo = new ConcurrentHashMap<>();
        LogUtils.logDebug("Instância do Barramento criada com ID: %s", id);
    }

    /**
     * @return O {@link GerenciadorDeServicos} associado ao barramento.
     */
    public GerenciadorDeServicos getGerenciadorDeServicos() {
        return gerenciadorDeServicos;
    }

    /**
     * Obtém o protocolo de preferência para o endpoint especificado.
     *
     * @param endpoint O nome do endpoint para o qual se deja obter o {@link Protocolo}.
     * @return O {@link Protocolo} de preferência associado ao endpoint, ou {@code null} se nenhum protocolo estiver
     * registrado.
     */
    public Protocolo getProtocoloPreferencia(String endpoint) {
        return preferenciasDeProtocolo.get(endpoint);
    }

    /**
     * Registra um novo endpoint com o protocolo especificado.
     *
     * @param endpoint  O nome do endpoint.
     * @param protocolo O {@link Protocolo} a ser associado ao endpoint.
     */
    public void cadastrarEndpoint(String endpoint, Protocolo protocolo) {
        if (protocolo == null) {
            LogUtils.logError("Tentativa de registrar o endpoint '%s' com um protocolo nulo.", endpoint);
            return;
        }
        preferenciasDeProtocolo.put(endpoint, protocolo);
        LogUtils.logInfo("Endpoint '%s' registrado com o protocolo '%s'.", endpoint, protocolo.getNomeProtocolo());
        LogUtils.logDebug("Instância '%s' - Conteúdo atual do mapa de preferências de protocolo: %s", id, preferenciasDeProtocolo);
    }

    /**
     * Envia uma mensagem utilizando o protocolo de origem ou o protocolo de destino associado ao endpoint de destino,
     * se este estiver previamente cadastrado.
     *
     * @param mensagem        A {@link Mensagem} a ser enviada.
     * @param protocoloOrigem O {@link Protocolo} utilizado caso não encontre um protocolo registrado para o
     *                        endpoint de destino.
     */
    public void enviarMensagem(Mensagem mensagem, Protocolo protocoloOrigem) {
        String origem = mensagem.getOrigem();
        String destino = mensagem.getDestino();

        if (!preferenciasDeProtocolo.containsKey(origem)) {
            LogUtils.logInfo("Novo endpoint conectado: '%s'. Registrando o protocolo '%s' como preferência " +
                            "padrão para comunicações futuras.",
                    origem, protocoloOrigem.getNomeProtocolo());
            cadastrarEndpoint(origem, protocoloOrigem);
        }

        Protocolo protocoloDestino = getProtocoloPreferencia(destino);
        String sufixo_timestamp = "";
        if (destino.contains("cliente")) {
            sufixo_timestamp += "_cliente";
        } else if (destino.contains("servidor")) {
            sufixo_timestamp += "_servidor";
        } else if (destino.contains("embarcado")) {
            sufixo_timestamp += "_embarcado";
        }
        mensagem.adicionarTimestampAoMetadata(String.format("timestamp_bus_msg_enviada%s", sufixo_timestamp));
        if (protocoloDestino == null) {
            LogUtils.logWarn("Protocolo de destino não encontrado para o endpoint '%s'. " +
                    "Enviando via protocolo de origem '%s'.", destino, protocoloOrigem.getNomeProtocolo());
            try {
                protocoloOrigem.enviarMensagem(mensagem);
                LogUtils.logInfo("Mensagem enviada para '%s' via '%s'.", destino,
                        protocoloOrigem.getNomeProtocolo());
            } catch (Exception e) {
                LogUtils.logError("Falha ao enviar mensagem para '%s' via '%s': '%s'.", destino,
                        protocoloOrigem.getNomeProtocolo(), e.getMessage());
            }
        } else {
            try {
                protocoloDestino.enviarMensagem(mensagem);
                LogUtils.logInfo("Mensagem enviada para '%s' via '%s'.", destino,
                        protocoloDestino.getNomeProtocolo());
            } catch (Exception e) {
                LogUtils.logError("Falha ao enviar mensagem para '%s' via '%s': '%s'.", destino,
                        protocoloOrigem.getNomeProtocolo(), e.getMessage());
            }

        }
    }
}
