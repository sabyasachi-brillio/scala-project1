package com.jemstep.actor

import akka.actor.Actor
import com.jemstep.logging.BulkStreamLogging
import com.jemstep.logging.uploadfailed.UnableToUpload
import com.jemstep.model.CustomModel.CsvDataList

import scala.annotation.tailrec

class BulkApiActor extends Actor with BulkUploadProcessor {

  val RETRY: Int = 5
  import BulkStreamLogging._
  import UnableToUpload._

  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  def receive: Receive = {
    case csvDataList: CsvDataList =>
      logInformation("Bulk Api Upload actor ")

      if(!uploadHandler(csvDataList, 1, 0))
        uulogInformation(csvDataList.toString)
  }

  @tailrec
  private def uploadHandler(csvDataList: CsvDataList, retryCount: Int, step: Int): Boolean = {
    val response = uploadInOrder(csvDataList, step)
    if (response == 5) true
    else if (retryCount >= RETRY) false
    else uploadHandler(csvDataList, retryCount + 1, response)
  }
private def uploadInOrder(csvDataList: CsvDataList, i: Int): Int =
    if (i >= 1 || uploadProcessor(csvDataList.rtqPeU))
      if (i >= 2 || (uploadProcessor(csvDataList.rtqPeP) &
        uploadProcessor(csvDataList.rtqPeG) &
        uploadProcessor(csvDataList.rtqPeB)))
        if (i >= 3 || (uploadProcessor(csvDataList.accPe) & 
	      uploadProcessor(csvDataList.hdPe)))
          if (i >= 4 || (uploadProcessor(csvDataList.quPeG) & 
	        uploadProcessor(csvDataList.quPeMQ)))
            if (i == 5 || (uploadProcessor(csvDataList.quDePeG) &
              uploadProcessor(csvDataList.quDePeMQ)))
              5
            else 4
          else 3
        else 2
      else 1
    else 0


}
