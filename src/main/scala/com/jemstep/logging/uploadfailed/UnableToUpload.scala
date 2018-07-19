package com.jemstep.logging.uploadfailed

import org.apache.log4j.Logger

class UnableToUpload

object UnableToUpload {

  /* Get actual class name to be printed on *//* Get actual class name to be printed on */
  private val logger: Logger = Logger.getLogger(classOf[UnableToUpload].getName)

  @SuppressWarnings(Array("org.wartremover.warts.Null"))
  def uulogInformation(log: String): Unit = {
    logger.info(log)
  }

  @SuppressWarnings(Array("org.wartremover.warts.Null"))
  def uulogDebugging(log: String): Unit = {
    logger.debug(log)
  }

  @SuppressWarnings(Array("org.wartremover.warts.Null"))
  def uulogErrorMessage(log: String): Unit = {
    logger.error(log)
  }

}
