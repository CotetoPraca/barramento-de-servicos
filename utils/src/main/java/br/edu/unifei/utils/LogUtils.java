package br.edu.unifei.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Classe utilitária para imprimir mensagems de log usando o SLF4J. Utiliza um logger configurado para automaticamente
 * informar qual classe está imprimindo aquela mensagem.
 * <p>
 * O {@code LogUtils} fornece métodos para registrar mensagens com diferentes níveis de severidade (INFO, WARN, ERROR,
 * DEBUG, TRACE). O cabeçalho do logger é configurado no arquivo {@code resources/logback.xml}.
 * </p>
 */
public class LogUtils {
    /**
     * Obtém o logger para a classe que chamou o método de log.
     * <p>
     * O logger é determinado pelo nome da classe que está no terceiro nível da pilha de chamadas.
     * Se o nível da pilha não for suficiente, usa o logger da própria classe LogUtil.
     * </p>
     *
     * @return O logger para a classe que chamou o método de log.
     */
    private static Logger getCallerLogger() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        int index = 3;
        if (index < stackTrace.length) {
            String callerClassName = stackTrace[index].getClassName();
            return LoggerFactory.getLogger(callerClassName);
        } else {
            return LoggerFactory.getLogger(LogUtils.class);
        }
    }

    /**
     * Registra uma mensagem com nível INFO.
     *
     * @param message A mensagem a ser registrada.
     * @param args    Argumentos opcionais para formatação da mensagem.
     */
    public static void logInfo(String message, Object... args) {
        Logger logger = getCallerLogger();
        if (args != null && args.length > 0) {
            message = String.format(message, args);
        }
        logger.info(message);
    }

    /**
     * Registra uma mensagem com nível WARN.
     *
     * @param message A mensagem a ser registrada.
     * @param args    Argumentos opcionais para formatação da mensagem.
     */
    public static void logWarn(String message, Object... args) {
        Logger logger = getCallerLogger();
        if (args != null && args.length > 0) {
            message = String.format(message, args);
        }
        logger.warn(message);
    }

    /**
     * Registra uma mensagem com nível ERROR.
     *
     * @param message A mensagem a ser registrada.
     * @param args    Argumentos opcionais para formatação da mensagem.
     */
    public static void logError(String message, Object... args) {
        Logger logger = getCallerLogger();
        if (args != null && args.length > 0) {
            message = String.format(message, args);
        }
        logger.error(message);
    }

    /**
     * Registra uma mensagem com nível DEBUG.
     *
     * @param message A mensagem a ser registrada.
     * @param args    Argumentos opcionais para formatação da mensagem.
     */
    public static void logDebug(String message, Object... args) {
        Logger logger = getCallerLogger();
        if (logger.isDebugEnabled()) {
            if (args != null && args.length > 0) {
                message = String.format(message, args);
            }
            logger.debug(message);
        }
    }

    /**
     * Registra uma mensagem com nível TRACE.
     *
     * @param message A mensagem a ser registrada.
     * @param args    Argumentos opcionais para formatação da mensagem.
     */
    public static void logTrace(String message, Object... args) {
        Logger logger = getCallerLogger();
        if (logger.isTraceEnabled()) {
            if (args != null && args.length > 0) {
                message = String.format(message, args);
            }
            logger.trace(message);
        }
    }
}
