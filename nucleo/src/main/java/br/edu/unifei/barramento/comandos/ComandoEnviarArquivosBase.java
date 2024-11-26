package br.edu.unifei.barramento.comandos;

import br.edu.unifei.barramento.Barramento;
import br.edu.unifei.modelos.mensagem.Mensagem;
import br.edu.unifei.utils.FileUtils;
import br.edu.unifei.utils.LogUtils;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.IOException;

/**
 * Comando para enviar os arquivos base necessários para implementar os serviços.
 */
public class ComandoEnviarArquivosBase implements Comando {

    /**
     * Executa a busca, compactação e envio dos arquivos base necessários.
     *
     * @param mensagem   A {@link Mensagem} solicitando o envio dos arquivos.
     * @param barramento O {@link Barramento} responsável pelo gerenciamento do inventário de serviços.
     * @return Uma {@link Mensagem} contendo o bytecode dos arquivos base compactados ou um erro caso ocorra falha.
     */
    @Override
    public Mensagem executar(Mensagem mensagem, Barramento barramento) {
        JsonObject resultado = new JsonObject();

        // Mantém os metadados da mensagem original se houver
        if (mensagem.getConteudo().has("metadata")) {
            resultado.add("metadata", mensagem.getConteudo().getAsJsonObject("metadata"));
        }

        File[] files = FileUtils.getFiles(
                "modelos/mensagem/Mensagem.java",
                "modelos/servico/Servico.java"
        );

        if (files == null) {
            LogUtils.logError("Nenhum arquivo foi encontrado. Abortando envio.");

            resultado.addProperty("erro", "Erro ao encontrar os arquivos de base. " +
                    "Verifique as configurações do barramento.");
            return new Mensagem("ERRO_ENVIO", "barramento", mensagem.getOrigem(), resultado);
        }

        try {
            String zipBase64 = FileUtils.zipAndEncodeFiles(files);
            resultado.addProperty("resposta", zipBase64);
            return new Mensagem("ARQUIVOS_BASE", "barramento", mensagem.getOrigem(), resultado);
        } catch (IOException e) {
            LogUtils.logError("Erro ao compactar e codificar os arquivos. %s", e.getMessage());
            resultado.addProperty("erro", "Erro ao compactar e codificar os arquivos. " +
                    "Verifique as configurações do Barramento.");
            return new Mensagem("ERRO_CODIFICACAO", "barramento", mensagem.getOrigem(), resultado);
        }
    }
}
