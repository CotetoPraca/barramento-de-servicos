package br.edu.unifei.mqtt;

import br.edu.unifei.utils.LogUtils;
import com.hivemq.embedded.EmbeddedHiveMQ;
import com.hivemq.embedded.EmbeddedHiveMQBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class BrokerMQTT {

    private final EmbeddedHiveMQ hiveMQ;

    public BrokerMQTT() {
        try {
            Path tempDir = Files.createTempDirectory("hivemq_conf_");

            try (InputStream source = BrokerMQTT.class.getClassLoader().getResourceAsStream("hivemq_conf/config.xml")) {
                if (source == null) throw new RuntimeException("Configuração HiveMQ não encontrada.");

                Path targetFile = tempDir.resolve("config.xml");
                Files.copy(source, targetFile, StandardCopyOption.REPLACE_EXISTING);
            }

            EmbeddedHiveMQBuilder builder = EmbeddedHiveMQ.builder()
                    .withConfigurationFolder(tempDir);

            hiveMQ = builder.build();

        } catch (NullPointerException | IOException ex) {
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
