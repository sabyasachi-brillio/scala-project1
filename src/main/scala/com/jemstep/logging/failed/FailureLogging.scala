package com.jemstep.logging.failed

import org.apache.log4j.Logger

class FailureLogging

object FailureLogging {

  /* Get actual class name to be printed on *//* Get actual class name to be printed on */
  private val logger: Logger = Logger.getLogger(classOf[FailureLogging].getName)


  @SuppressWarnings(Array("org.wartremover.warts.Null"))
  def flogInformation(log: String): Unit = {
    logger.info(log)
  }

  @SuppressWarnings(Array("org.wartremover.warts.Null"))
  def flogDebugging(log: String): Unit = {
    logger.debug(log)
  }

  @SuppressWarnings(Array("org.wartremover.warts.Null"))
  def flogErrorMessage(log: String): Unit = {
    logger.error(log)
  }

}
