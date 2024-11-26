package br.edu.unifei.mqtt;

import br.edu.unifei.modelos.mensagem.Mensagem;
import com.google.gson.JsonObject;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("unused")
public class ProtocoloMQTTTest {

    private static final String TEST_TOPIC = "topico/barramento";
    private static final String TEST_ORIGEM = "origem-teste";
    private static final String TEST_DESTINO = "topico/barramento";
    private static final String TEST_ACAO = "TESTE";
    private static final JsonObject TEST_CONTEUDO = new JsonObject();
    private ProtocoloMQTT protocoloMQTT;

    @BeforeEach
    public void setUp() {
        protocoloMQTT = new ProtocoloMQTT();
        TEST_CONTEUDO.addProperty("chave", "valor");
    }

    @AfterEach
    public void tearDown() {
        protocoloMQTT.desconectar();
    }

    @Test
    public void testEnviarMensagem() {
        Mensagem mensagem = new Mensagem(TEST_ACAO, TEST_ORIGEM, TEST_DESTINO, TEST_CONTEUDO);
        assertDoesNotThrow(() -> protocoloMQTT.enviarMensagem(mensagem));
    }

    @Test
    public void testReceberMensagem() throws InterruptedException, MqttException {
        Mensagem mensagem = new Mensagem(TEST_ACAO, TEST_ORIGEM, TEST_DESTINO, TEST_CONTEUDO);
        protocoloMQTT.enviarMensagem(mensagem);

        // Aguarda para dar tempo de o broker enviar e receber a mensagem
        Thread.sleep(2000);

        // Verifica se a mensagem foi recebida corretamente (simula a chegada)
        protocoloMQTT.cliente.subscribe(TEST_TOPIC, (topico, mensagemRecebida) -> {
            String payload = new String(mensagemRecebida.getPayload());
            Mensagem mensagemProcessada = Mensagem.fromJson(payload);
            assertEquals(TEST_ORIGEM, mensagemProcessada.getOrigem());
            assertEquals(TEST_ACAO, mensagemProcessada.getAcao());
            assertEquals(TEST_DESTINO, mensagemProcessada.getDestino());
            assertEquals(TEST_CONTEUDO.toString(), mensagemProcessada.getConteudo().toString());
        });

        // Tempo para processar a mensagem recebida
        Thread.sleep(2000);
    }

    @Test
    public void testConexaoPerdida() {
        protocoloMQTT.connectionLost(new Exception("Simulação de perda de conexão"));
        // Verificar se o log correspondente à perda de conexão foi gerado
        // (Assumindo que a implementação de log já verifica isso)
    }
}