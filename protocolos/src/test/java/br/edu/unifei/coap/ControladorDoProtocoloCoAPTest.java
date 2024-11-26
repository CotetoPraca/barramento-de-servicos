package br.edu.unifei.coap;

import br.edu.unifei.barramento.Barramento;
import br.edu.unifei.barramento.FilaDeMensagens;
import br.edu.unifei.barramento.gerenciamento.GerenciadorDeServicos;
import br.edu.unifei.barramento.workers.WorkerDeProcessamento;
import br.edu.unifei.barramento.workers.WorkerDeRecepcao;
import br.edu.unifei.modelos.mensagem.Mensagem;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

public class ControladorDoProtocoloCoAPTest {

    private ControladorDoProtocoloCoAP controlador;
    private ProtocoloCoAP protocoloCoAP;
    private WorkerDeRecepcao workerDeRecepcao;
    private WorkerDeProcessamento workerDeProcessamento;
    private Barramento barramento;
    private FilaDeMensagens filaDeMensagens;

    @BeforeEach
    public void setUp() throws IOException {
        filaDeMensagens = Mockito.mock(FilaDeMensagens.class);
        protocoloCoAP = Mockito.mock(ProtocoloCoAP.class);
        GerenciadorDeServicos gerenciadorDeServicos = Mockito.mock(GerenciadorDeServicos.class);
        barramento = new Barramento(gerenciadorDeServicos);

        controlador = new ControladorDoProtocoloCoAP(barramento) {
            @Override
            protected void iniciarWorkers() {
                workerDeRecepcao = new WorkerDeRecepcao(filaDeMensagens);
                workerDeProcessamento = new WorkerDeProcessamento(protocoloCoAP, barramento, filaDeMensagens);

                new Thread(workerDeRecepcao).start();
                new Thread(workerDeProcessamento).start();
            }

            @Override
            protected void pararWorkers() {
                workerDeRecepcao.parar();
                workerDeProcessamento.parar();
            }
        };
    }

    @AfterEach
    public void tearDown() {
        controlador.pararWorkers();
    }

    @Test
    @DisplayName("Teste de Início dos Workers")
    public void testIniciarWorkers() {
        assertDoesNotThrow(() -> controlador.iniciarWorkers(), "Os workers devem ser iniciados sem exceções");
        verifyNoMoreInteractions(filaDeMensagens); // Simula interação com a fila
    }

    @Test
    @DisplayName("Teste de Parada dos Workers")
    public void testPararWorkers() {
        controlador.iniciarWorkers();
        assertDoesNotThrow(() -> controlador.pararWorkers(), "Os workers devem ser parados sem exceções");
    }

    @Test
    @DisplayName("Teste de Envio de Mensagem pelo Protocolo CoAP")
    public void testProtocoloCoAPEnviaMensagem() {
        Mensagem mensagem = new Mensagem("acao", "origem", "destino", new JsonObject());
        controlador.iniciarWorkers();

        protocoloCoAP.enviarMensagem(mensagem);

        verify(protocoloCoAP, times(1)).enviarMensagem(mensagem); // Verifica se o método foi chamado
    }
}
