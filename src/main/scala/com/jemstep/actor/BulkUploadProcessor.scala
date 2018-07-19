package com.jemstep.actor

import com.jemstep.logging.BulkStreamLogging
import com.jemstep.logging.failed.FailureLogging
import com.jemstep.logging.unprocessed.UnprocessedLogging
import com.jemstep.model.CustomModel
import com.jemstep.model.CustomModel.CsvData

import scala.util.Try

trait BulkUploadProcessor extends RestClient with RestStatus {

  import BulkStreamLogging._
  import FailureLogging._
  import UnprocessedLogging._

  def uploadProcessor(csvData: CsvData): Boolean = {
    val conDetails: Option[CustomModel.ConnectionDetails] =
      Try(createFromRefreshToken(csvData.org)).toOption

    conDetails match {
      case Some(connectionDetails) =>

        val job: Option[String] = Try {
          createJob(csvData.entity, connectionDetails)
        }.toOption

        job match {
          case Some(jobId) if jobId != null =>
            val statusCode: Int = uploadCSVData(csvData.csvObject, jobId, connectionDetails)
            val closeStatus: String = closeOrAbortJob(jobId, connectionDetails)

            logInformation(s"Job id: `$jobId` created for org: `${csvData.org}` and " +
              s"entity: `${csvData.entity}` got response code: $statusCode and close status: $closeStatus")

            // get the success response
            if (statusCode == 201) {
              val successMsg: String = getSuccessfulResultsString(jobId, connectionDetails)
              if (successMsg.count(_ == '\n') > 1) {
                logInformation(s"Job id: $jobId got processed successfully `${successMsg.count(_ == '\n') - 1}` records.")
                successMsg.split("\n").toList.foreach(record => logDebugging(s"$record"))
              }

              val failureMsg: String = getFailedResultsString(jobId, connectionDetails)
              val failureStatus: Boolean = failureMsg.count(_ == '\n') > 1
              if (failureStatus) {
                flogErrorMessage(s"Job id: $jobId got failed to process `${failureMsg.count(_ == '\n') - 1}` records.")
                failureMsg.split("\n").toList.foreach(errorRecord =>
                  flogErrorMessage(getErrorJson(ErrorLog(csvData.entity, csvData.org, csvData.offsets.distinct, errorRecord))))
              }

              val unprocessedMsg: String = getUnprocessedResultsString(jobId, connectionDetails)
              val unprocessedStatus: Boolean = unprocessedMsg.count(_ == '\n') > 1
              if (unprocessedStatus) {
                ulogInformation(s"Job id: $jobId got unable to process `${unprocessedMsg.count(_ == '\n') - 1}` records.")
                unprocessedMsg.split("\n").toList.foreach(errorRecord =>
                  ulogInformation(getErrorJson(ErrorLog(csvData.entity, csvData.org, csvData.offsets.distinct, errorRecord))))
              }
              !(failureStatus | unprocessedStatus)
            } else {
              false
            }

          case _ =>
            logInformation(s"Error: Unable create job for org: `${csvData.org}` and entity: `${csvData.entity}`")
            false
        }
      case _ =>
        logInformation(s"Error: Unable to create connection for org: `${csvData.org}` and entity: `${csvData.entity}`")
        false
    }
  }

}

