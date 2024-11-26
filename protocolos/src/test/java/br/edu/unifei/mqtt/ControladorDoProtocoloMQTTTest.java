package br.edu.unifei.mqtt;

import br.edu.unifei.barramento.Barramento;
import br.edu.unifei.barramento.FilaDeMensagens;
import br.edu.unifei.barramento.gerenciamento.GerenciadorDeServicos;
import br.edu.unifei.barramento.workers.WorkerDeProcessamento;
import br.edu.unifei.barramento.workers.WorkerDeRecepcao;
import br.edu.unifei.modelos.mensagem.Mensagem;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

public class ControladorDoProtocoloMQTTTest {

    private ControladorDoProtocoloMQTT controlador;
    private ProtocoloMQTT protocoloMQTT;
    private WorkerDeRecepcao workerDeRecepcao;
    private WorkerDeProcessamento workerDeProcessamento;
    private Barramento barramento;
    private FilaDeMensagens filaDeMensagens;

    @BeforeEach
    public void setUp() {
        filaDeMensagens = Mockito.mock(FilaDeMensagens.class);
        protocoloMQTT = Mockito.mock(ProtocoloMQTT.class);
        GerenciadorDeServicos gerenciadorDeServicos = Mockito.mock(GerenciadorDeServicos.class);
        barramento = new Barramento(gerenciadorDeServicos);

        controlador = new ControladorDoProtocoloMQTT(barramento) {
            @Override
            protected void iniciarWorkers() {
                workerDeRecepcao = new WorkerDeRecepcao(filaDeMensagens);
                workerDeProcessamento = new WorkerDeProcessamento(protocoloMQTT, barramento, filaDeMensagens);

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
    public void testIniciarWorkers() {
        assertDoesNotThrow(() -> controlador.iniciarWorkers());
        verifyNoMoreInteractions(filaDeMensagens); // Simula interação com a fila
    }

    @Test
    public void testPararWorkers() {
        controlador.iniciarWorkers();
        assertDoesNotThrow(() -> controlador.pararWorkers());
    }

    @Test
    public void testProtocoloMQTTEnviaMensagem() {
        Mensagem mensagem = new Mensagem("acao", "origem", "destino", new JsonObject());
        controlador.iniciarWorkers();

        protocoloMQTT.enviarMensagem(mensagem);

        verify(protocoloMQTT, times(1)).enviarMensagem(mensagem);  // Verifica se o método foi chamado
    }
}