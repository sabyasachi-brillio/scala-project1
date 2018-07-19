package com.jemstep.bulkapi.v2.client

import com.jemstep.bulkapi.v2.RestBulkClient

object UploadExample {

  def main(args: Array[String]): Unit = {

    val `object`: String =  args(0)
    val file: String = args(1)
    RestBulkClient.createFromRefreshToken("")

    val jobId: String = RestBulkClient.createJob(`object`)
    //var status: Int = 0
    if (jobId != null) {
      val status = RestBulkClient.uploadData(file, jobId)
      //val status = RestBulkClient.uploadJsonData(jsonFile, jobId)

      RestBulkClient.closeOrAbortJob(jobId)

      if (status == 201) {
        RestBulkClient.getSucessfulResults(jobId)
        RestBulkClient.getFailedResults(jobId)
        RestBulkClient.getUnprocessedResults(jobId)
      }
    }

  }



}
