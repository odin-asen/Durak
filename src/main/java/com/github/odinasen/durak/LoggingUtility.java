package com.github.odinasen.durak;

import java.io.IOException;
import java.util.logging.*;

/**
 * User: Timm Herrmann
 * Date: 05.01.13
 * Time: 19:29
 */
public class LoggingUtility {
  public static final String STARS = "********************";
  public static final String HASHES = "####################";
  public static final String SHORT_STARS = "*****";
  private static final String DEFAULT_NAME = "defaultLog.txt"; //NON-NLS
  private static Handler handler = null;
  private static boolean logFileChangable = true;
  private static final Logger LOGGER = Logger.getLogger(LoggingUtility.class.getName());

  /**
   * Sets the file name for the logging file handler. It can be set only one time. Once the
   * method was called the path can not be changed for the handler until the program will be
   * restarted.
   * @param fileName Path of the logging file.
   * @return Returns true, if the path was changed and false if it wasn't.
   */
  public static boolean setFirstTimeLoggingFile(String fileName) {
    if(!logFileChangable)
      return false;

    try {
      final SimpleFormatter formatter = new SimpleFormatter();
      handler = new FileHandler(fileName);
      handler.setFormatter(formatter);
      logFileChangable = false;
    } catch (IOException e) {
      LOGGER.severe("Error opening file \"" + fileName + "\": "+e.getMessage());
      return false;
    }

    return true;
  }

  private static Handler createDefaultHandler() {
    final Handler handler;
    try {
      handler = new FileHandler(DEFAULT_NAME);
      handler.setFormatter(new SimpleFormatter());
      return handler;
    } catch (IOException e) {
      LOGGER.severe("Error opening file \"" + DEFAULT_NAME + "\": " + e.getMessage());
    }
    return null;
  }

  /**
   * Returns or creates a logger for the specified name. The handler,
   * specified by the {@link #setFirstTimeLoggingFile} method, will be added to the
   * logger if it does not already exist.
   * @param name A name for the logger. This should be a dot-separated name and
   *             should normally be based on the package name or class name of
   *             the subsystem, such as java.net or javax.swing
   * @return A Logger object.
   */
  public static Logger getLogger(String name) {
    final Logger logger = Logger.getLogger(name);
    boolean handlerExists = false;
    if(handler == null)
      handler = createDefaultHandler();

    for (Handler h : logger.getHandlers())
      handlerExists = handlerExists || h.equals(handler);
    if(!handlerExists)
      logger.addHandler(handler);
    return logger;
  }

  public static void printStackTrace(Logger logger, Level level,
                                     StackTraceElement[] elements) {
    String text = "";
    for (StackTraceElement element : elements)
      text = text + element.toString();

    logger.log(level, text);
  }

  /**
   * Loggt eine Nachricht auf {@link java.util.logging.Level#INFO}. Ein uebergebener String wird
   * vor und nach der Nachricht gesetzt. Zwischen dem uebergenen String und der Info wird ein
   * Leerzeichen gesetzt.<br/>
   * z.B. 12345 Das Ist die INFO 12345
   * @param logger
   *    loggt die Info.
   * @param embedIn
   *    wird vor und nach der Info gesetzt.
   * @param info
   *    ist die zu loggende Info.
   */
  public static void embedInfo(Logger logger, String embedIn, String info) {
    logger.info(embedIn + " " + info + " " + embedIn);
  }
}
