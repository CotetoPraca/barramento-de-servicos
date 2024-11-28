package br.edu.unifei.coap;

import br.edu.unifei.modelos.mensagem.Mensagem;
import br.edu.unifei.modelos.protocolo.Protocolo;
import br.edu.unifei.utils.LogUtils;
import com.google.gson.JsonObject;
import com.mbed.coap.client.CoapClient;
import com.mbed.coap.packet.*;
import com.mbed.coap.server.CoapServer;
import com.mbed.coap.server.RouterService;
import com.mbed.coap.transport.udp.DatagramSocketTransport;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

import static com.mbed.coap.packet.CoapResponse.coapResponse;

/**
 * Classe responsável por configurar e gerenciar a comunicação CoAP, permitindo ao barramento o envio e recebimento de
 * mensagens através deste protocolo.
 */
public class ProtocoloCoAP extends Protocolo {

    private static final String SERVER_HOST = "192.168.0.8"; // IPv4 da máquina atual
    private static final int SERVER_PORT = 5683;
    private CoapClient cliente;
    private CoapServer servidor;

    /**
     * Construtor que configura o servidor e o cliente CoAP.
     *
     * @throws IOException Se ocorrer um erro ao configurar o servidor ou cliente CoAP.
     */
    public ProtocoloCoAP() throws IOException {
        configurarServidorCoAP();
    }

    /**
     * @return O {@link CoapClient} associado a classe.
     */
    protected CoapClient getCliente() {
        return cliente;
    }

    /**
     * Método responsável por configurar o servidor CoAP, tornando a aplicação capaz de receber mensagens via CoAP.
     *
     * @throws IOException Se ocorrer um erro durante a configuração do servidor CoAP.
     */
    private void configurarServidorCoAP() throws IOException {
        servidor = CoapServer.builder()
                .transport(new DatagramSocketTransport(SERVER_PORT))
                .blockSize(BlockSize.S_1024) // Define o tamanho do bloco para 1024 bytes
                .maxIncomingBlockTransferSize(16384) // Transferência de até 16 KB
                .route(RouterService.builder()
                        .post("/coap/barramento", req -> {
                            try {
                                String payload = new String(req.getPayload().getBytes(), StandardCharsets.UTF_8);

                                LogUtils.logInfo("Mensagem recebida via CoAP: %s", payload);

                                Mensagem mensagemRecebida = Mensagem.fromJson(payload);
                                
                                String acao = mensagemRecebida.getAcao();
                                String destino = mensagemRecebida.getDestino();

                                String sufixo_timestamp = "";
                                if (mensagemRecebida.getOrigem().contains("cliente")) {
                                    sufixo_timestamp += "_cliente";
                                } else if (mensagemRecebida.getOrigem().contains("servidor")) {
                                    sufixo_timestamp += "_servidor";
                                } else if (mensagemRecebida.getOrigem().contains("embarcado")) {
                                    sufixo_timestamp += "_embarcado";
                                }
                                mensagemRecebida.adicionarTimestampAoMetadata(
                                        String.format("timestamp_bus_msg_recebida%s", sufixo_timestamp));
                                
                                if (acao.equals("ENVIAR_MENSAGEM") && destino.isEmpty()) {
                                    Mensagem mensagemErro = getMensagemErro(mensagemRecebida);
                                    this.enviarMensagem(mensagemErro);
                                    return coapResponse(Code.C400_BAD_REQUEST).toFuture();
                                }

                                ControladorDoProtocoloCoAP controlador = (ControladorDoProtocoloCoAP) getControlador();
                                controlador.adicionarMensagemAFila(mensagemRecebida);
                                return coapResponse(Code.C204_CHANGED).toFuture();
                            } catch (Exception e) {
                                LogUtils.logError("Erro ao processar mensagem recebida: %s", e.getMessage());
                                return coapResponse(Code.C500_INTERNAL_SERVER_ERROR).toFuture();
                            }
                        })
                )
                .build();
        servidor.start();
        LogUtils.logInfo("Servidor CoAP iniciado no endereço %s:%d", SERVER_HOST, SERVER_PORT);
    }

    /**
     * Gera uma mensagem de erro informando a falta de um destino na mensagem recebida.
     *
     * @param mensagemRecebida Mensagem recebida que gerou o erro.
     * @return Uma instância de {@link Mensagem} com as informações do erro.
     */
    private static Mensagem getMensagemErro(Mensagem mensagemRecebida) {
        JsonObject conteudoErro = new JsonObject();
        conteudoErro.addProperty("resultado", "Erro no envio: Destino não informado.");

        return new Mensagem(
                "ERRO_ENVIO",
                "barramento",
                mensagemRecebida.getOrigem(),
                conteudoErro
        );
    }

    /**
     * Envia uma mensagem via protocolo CoAP.
     *
     * @param mensagem A {@link Mensagem} a ser enviada.
     */
    public void enviarMensagem(Mensagem mensagem) {
        String destino = mensagem.getDestino();

        // Extrai o host, a porta e o path
        String hostDestino = "";
        int portaDestino = 0;
        String pathDestino = "";

        try {
            // Divide o endereço em duas partes: "host:porta" e "path"
            String[] partes = destino.split("/", 2);
            String hostEPorta = partes[0];

            // Divide o host e a porta
            String[] hostEPortaPartes = hostEPorta.split(":", 2);
            hostDestino = hostEPortaPartes[0];
            portaDestino = Integer.parseInt(hostEPortaPartes[1]);

            // Salva o path adicionando novamente o '/' usado no split
            pathDestino = "/" + (partes.length > 1 ? partes[1] : "");
        } catch (Exception e) {
            LogUtils.logError("Erro ao analisar a URL de destino '%s': %s", destino, e.getMessage());
        }
        
        try {
            cliente = CoapServer.builder()
                    .transport(DatagramSocketTransport.udp())
                    .blockSize(BlockSize.S_1024) // Define o tamanho do bloco para 1024 bytes
                    .maxIncomingBlockTransferSize(16384) // Transferência de até 16 KB
                    .buildClient(new InetSocketAddress(hostDestino, portaDestino));


            String mensagemJson = mensagem.toJson();
            LogUtils.logDebug("Iniciando envio da mensagem. Destino: %s, Mensagem: %s", destino, mensagemJson);
            CompletableFuture<CoapResponse> responseFuture = cliente.send(
                    CoapRequest.post(pathDestino).payload(mensagemJson, MediaTypes.CT_APPLICATION_JSON)
            );

            responseFuture.thenAccept(response -> {
                String payload = new String(response.getPayload().getBytes(), StandardCharsets.UTF_8);
                LogUtils.logInfo("Resposta recebida do servidor CoAP: Código: %s, Payload: %s",
                        response.getCode(), payload);
            }
            ).exceptionally(e -> {
                LogUtils.logError("Erro ao enviar mensagem CoAP: %s", e.getMessage());
                return null;
            });
        } catch (Exception e) {
            LogUtils.logError("Erro ao enviar mensagem CoAP: %s", e.getMessage());
        }
    }

    /**
     * Desconecta o cliente e o servidor CoAP, liberando os recursos utilizados.
     */
    public void desconectar() {
        try {
            servidor.stop();
            cliente.close();
            LogUtils.logInfo("Cliente e servidor CoAP desconectados.");
        } catch (Exception e) {
            LogUtils.logError("Erro ao desconectar o servidor CoAP: %s", e.getMessage());
        }
    }
}
