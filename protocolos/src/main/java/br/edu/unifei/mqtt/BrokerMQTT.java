package br.edu.unifei.mqtt;

import br.edu.unifei.utils.LogUtils;
import com.hivemq.embedded.EmbeddedHiveMQ;
import com.hivemq.embedded.EmbeddedHiveMQBuilder;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class BrokerMQTT {

    private final EmbeddedHiveMQ hiveMQ;

    public BrokerMQTT() {
        try {
            Path configPath = Paths.get(Objects.requireNonNull(
                    BrokerMQTT.class.getClassLoader().getResource("hivemq_conf")).toURI());

            EmbeddedHiveMQBuilder builder = EmbeddedHiveMQ.builder()
                    .withConfigurationFolder(configPath);
            hiveMQ = builder.build();
        } catch (URISyntaxException | NullPointerException ex) {
            LogUtils.logError("Erro ao configurar o broker MQTT: %s", ex.getMessage());
            throw new RuntimeException("Falha ao configurar o broker MQTT", ex);
        }
    }

    public void startBroker() {
        try  {
            hiveMQ.start().join();
            LogUtils.logInfo("Broker MQTT iniciado com sucesso.");
        } catch (final Exception ex) {
            LogUtils.logError("Erro ao iniciar o broker MQTT: %s", ex.getMessage());
        }
    }

    public void stopBroker() {
        try {
            hiveMQ.stop().join();
            LogUtils.logInfo("Broker MQTT encerrado com sucesso.");
        } catch (final Exception ex) {
            LogUtils.logError("Erro ao encerrar o broker MQTT: %s", ex.getMessage());
        }
    }
}
