package com.jemstep.actor

import akka.actor._
import com.jemstep.logging.BulkStreamLogging
import com.jemstep.model.BusinessEntityModel
import com.jemstep.model.CustomModel._

import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global

class CacheActor(entityName: String,
                 bulkApiActor: ActorRef)
  extends Actor {

  import BulkStreamLogging._
  import BusinessEntityModel._
  import akka.pattern._
  import scala.concurrent.duration._
  import akka.util.Timeout

  implicit val timeout: Timeout = Timeout(100.seconds)

  private val MAX_MESSAGE_SIZE: Int = 50 * 1024 // 2 * 1024 * 1024 // 2MB data in bytes
  private val MAX_CACHE_WAITING_TIME: Int = 30 // in min

  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  def receive: Receive = active(Map.empty[String, CacheStore])

  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  def active(listOfData: Map[String, CacheStore]): Receive = {
    case cacheData: CacheMessage =>
      val newList: Map[String, CacheStore] =
        listOfData
          .updated(
            key = cacheData.org,
            value = listOfData.get(cacheData.org)
              .map(existingData =>
                CacheStore(existingData.lastBulkApiCall, getTimeStamp,
                  existingData.listOfObj ++ cacheData.listOfObj.map(x => (cacheData.offSet, x))))
              .getOrElse(CacheStore(0, getTimeStamp,
                cacheData.listOfObj.map(x => (cacheData.offSet, x)))))
      // send supervisor to trigger all the cache clean for org
      if (checkMessageSize(cacheData.org, newList)) sender() ! CacheClean(cacheData.org)

      context become active(newList)

    case `CacheRefresh` =>
      logInformation(s"Scheduled Cache Clean up activity under process for $entityName")

      val finalList: Map[String, CacheStore] =
        listOfData.filter { case (_, v) => isLongWaitingTiming(v.lastBulkApiCall) }
          .keys.toList.foldLeft(listOfData)((x, y) => processJsonData(y, x))

      context become active(finalList)
      logInformation(s"Scheduled Cache Clean up activity done for $entityName")

    case cacheClean: CacheClean =>
      logInformation(s"Depended Cache Clean up activity under process for $entityName")

      val finalList: Map[String, CacheStore] = processJsonData(cacheClean.org, listOfData)
      context become active(finalList)
      logInformation(s"Depended Cache Clean up activity done for $entityName")
  }

  private def processJsonData(org: String,
                              newList: Map[String, CacheStore]): Map[String, CacheStore] = {
    val messageSize: Int = calculateCacheSize(newList(org).listOfObj.map(_._2))
    logInformation("Data sending into Bulk Api actor for updating. " +
      s"Entity: `$entityName` Organization: `$org` Message Size: `$messageSize`")

    val status: Future[Boolean] = Future.sequence(
      getCsvData(newList(org).listOfObj.map(_._2))
        .map(csvObj => CsvData(entityName, org, csvObj, newList(org).listOfObj.map(_._1)))
        .map(csvData => bulkApiActor.ask(csvData).mapTo[Boolean]))
      .map(listOfUploadStatus => listOfUploadStatus.foldLeft(true)(_ & _))

    val uploadResponse: Boolean = Await.result(status, 100.seconds)

    if (uploadResponse) {
      // clear the actor data
      val updatedList: Map[String, CacheStore] =
        newList.updated(org, CacheStore(getTimeStamp, getTimeStamp, List.empty[(Long, EntityObject)]))
      updatedList
    } else {
      newList
    }
  }

  private def checkMessageSize(org: String,
                               newList: Map[String, CacheStore]): Boolean = {
    val messageSize: Int = calculateCacheSize(newList(org).listOfObj.map(_._2))
    messageSize >= MAX_MESSAGE_SIZE
  }

  /**
    * get cache total size in "n" bytes
    *
    * @return
    */
  private def calculateCacheSize(dataList: List[EntityObject]): Int =
    getJsonArray(dataList).getBytes().length

  private def getCsvData(dataList: List[EntityObject]): List[String] = {
    val k: Map[String, List[EntityObject]] = dataList groupBy {
      case _: AccountPe => "AccountPe"
      case _: HoldingPe => "HoldingPe"
      case _: QuestionnaireDetailPe => "QuestionnaireDetailPe"
      case _: QuestionnairePeG => "QuestionnairePeG"
      case _: QuestionnairePeMQ => "QuestionnairePeMQ"
      case _: RtqPeB => "RtqPeB"
      case _: RtqPeG => "RtqPeG"
      case _: RtqPeP => "RtqPeP"
      case _: RtqPeU => "RtqPeU"
    }
    if (entityName == "Investor_PE__e") {
      List(k("RtqPeU"), k("RtqPeP"), k("RtqPeG"), k("RtqPeB"))
        .map(x => getCsvArray(x))
    } else {
      k.values.toList.map(x => getCsvArray(x))
    }
  }

  private def getTimeStamp: Long = System.currentTimeMillis()

  private def isLongWaitingTiming(startTimestamp: Long): Boolean =
    ((System.currentTimeMillis() - startTimestamp) / (1000 * 60)).toInt >= MAX_CACHE_WAITING_TIME
}
