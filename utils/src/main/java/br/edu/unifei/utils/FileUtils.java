package br.edu.unifei.utils;

import java.io.*;
import java.util.Base64;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * A classe FileUtil fornece métodos utilitários para manipulação de arquivos,
 * como compactação em formato ZIP e codificação Base64, além de localizar
 * recursos no projeto.
 */
public class FileUtils {

    /**
     * Compacta uma lista de arquivos em uma arquivo ZIP e codifica o resultado em Base64.
     *
     * @param files Array de arquivos a serem compactados.
     * @return Uma string codificada em Base64 que representa o conteúdo do arquivo ZIP.
     * @throws IOException Se ocorrer um erro ao ler ou escrever os arquivos.
     */
    public static String zipAndEncodeFiles(File[] files) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            for (File file : files) {
                if (!file.exists()) {
                    LogUtils.logError("Arquivo não encontrado %s", file.getAbsolutePath());
                    throw new FileNotFoundException();
                }

                // Determina o caminho relativo do arquivo dentro do ZIP
                String relativePath = file.getParentFile().getName() + "/" + file.getName();
                addFileToZip(zos, file, relativePath);
            }
        }
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }

    /**
     * Adicona um arquivo a um arquivo ZIP
     *
     * @param zos       O {@link ZipOutputStream} onde o arquivo será adicionado.
     * @param file      O arquivo a ser adicionado.
     * @param entryName O nome desejado para o arquivo ZIP gerado.
     * @throws IOException Se ocorrer um erro ao ler ou escrever o arquivo.
     */
    private static void addFileToZip(ZipOutputStream zos, File file, String entryName) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            ZipEntry zipEntry = new ZipEntry(entryName);
            zos.putNextEntry(zipEntry);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                zos.write(buffer, 0, length);
            }

            zos.closeEntry();
        }
    }

    /**
     * Localiza e retorna um array de arquivos com base nos nomes dos arquivos fornecidos.
     *
     * @param fileNames Os nomes dos arquivos a serem localizados.
     * @return Um array de objetos File representando os arquivos localizados.
     */
    public static File[] getFiles(String... fileNames) {
        String basePath = getResourcePath();

        File[] files = new File[fileNames.length];
        for (int i = 0; i < fileNames.length; i++) {
            files[i] = new File(basePath + "/" + fileNames[i]);
        }

        for (File file : files) {
            if (!file.exists()) {
                LogUtils.logError("Arquivo %s não encontrado.", file.getAbsolutePath());
                return null;
            }
        }

        return files;
    }

    /**
     * Obtém o caminho do diretório de recursos dentro do projeto.
     *
     * @return O caminho absoluto do diretório de recursos.
     * @throws IllegalStateException Se o diretório de recursos não for encontrado.
     */
    static String getResourcePath() {
        String basePath = System.getProperty("user.dir");
        File projectRoot = new File(basePath);

        File nucleoDir = new File(projectRoot, "/nucleo/src/main/java/br/edu/unifei");

        if (!nucleoDir.exists()) {
            LogUtils.logError("Diretório %s não encontrado.", nucleoDir.getAbsolutePath());
            throw new IllegalStateException();
        }

        return nucleoDir.getAbsolutePath();
    }
}
