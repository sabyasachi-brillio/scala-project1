package com.jemstep.logging.unprocessed

import org.apache.log4j.Logger

class UnprocessedLogging

object UnprocessedLogging {

  /* Get actual class name to be printed on *//* Get actual class name to be printed on */
  private val logger: Logger = Logger.getLogger(classOf[UnprocessedLogging].getName)

  @SuppressWarnings(Array("org.wartremover.warts.Null"))
  def ulogInformation(log: String): Unit = {
    logger.info(log)
  }

  @SuppressWarnings(Array("org.wartremover.warts.Null"))
  def ulogDebugging(log: String): Unit = {
    logger.debug(log)
  }

  @SuppressWarnings(Array("org.wartremover.warts.Null"))
  def ulogErrorMessage(log: String): Unit = {
    logger.error(log)
  }

}
