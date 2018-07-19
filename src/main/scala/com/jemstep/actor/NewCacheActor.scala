package com.jemstep.actor

import akka.actor._
import com.jemstep.logging.BulkStreamLogging.logInformation
import com.jemstep.model.BusinessEntityModel.EntityType
import com.jemstep.model.CustomModel.{CacheHolder, _}

class NewCacheActor(bulkApiActor: ActorRef) extends Actor {

  import EntityType._

  private val MAX_MESSAGE_SIZE: Int = 20 * 1024 // 2 * 1024 * 1024 // 2MB data in bytes
  private val MAX_CACHE_WAITING_TIME: Int = 3 // in min

  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  def receive: Receive = active(Map.empty[String, CacheHolder], getTimestamp)

  /**
    * cache holder org specific CacheHolder object
    *
    * @param cache cache data
    * @return
    */
  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  def active(cache: Map[String, CacheHolder], lastUpdatedTime: Long): Receive = {
    case eh: EntityHolder =>
      val newCacheHolder: CacheHolder =
        cache.get(eh.holder.organization)
          .map(existingData =>
            insertIntoCacheHolder(existingData, eh.holder.entityType,
              EntityInfo(eh.holder.entityType, eh.offSet, eh.holder.toString)))
          .getOrElse(insertIntoCacheHolder(getEmptyCacheHolder, eh.holder.entityType,
            EntityInfo(eh.holder.entityType, eh.offSet, eh.holder.toString)))
      
      logInformation(s"Cache ready for upload Status: ${newCacheHolder.readyForUpload} and " +
        s"rtqPeU ${newCacheHolder.rtqPeU.nonEmpty} " +
        s"accPe ${newCacheHolder.accPe.nonEmpty} " +
        s"holdPE ${newCacheHolder.hdPe.nonEmpty} " +
        s"quPeG ${newCacheHolder.quPeG.nonEmpty} " +
        s"quPeMQ ${newCacheHolder.quPeMQ.nonEmpty} " +
        s"quDePeG ${newCacheHolder.quDePeG.nonEmpty} " +
          s"quDePeMQ ${newCacheHolder.quDePeMQ.nonEmpty} and" +
        s"Cache Reached the limit: ${newCacheHolder.anyEntityReachedMaxSize(MAX_MESSAGE_SIZE)}")
      
      val newCache: Map[String, CacheHolder] =
        if (newCacheHolder.anyEntityReachedMaxSize(MAX_MESSAGE_SIZE)) {
          bulkApiActor ! newCacheHolder.getCsvData(eh.holder.organization)

          cache.updated(key = eh.holder.organization, value = getEmptyCacheHolder)
        } else cache.updated(key = eh.holder.organization, value = newCacheHolder)

      logInformation("Data stored into cache for " +
        s"Entity: `${eh.holder.entityType.toString}` " +
        s"Organization: `${eh.holder.organization}`")
      context become active(newCache, getTimestamp)

    case CacheRefresh =>
      if (isLongWaitingTiming(lastUpdatedTime) && checkCacheNonEmpty(cache)) {
        val newCache: Map[String, CacheHolder] = cache.map(keyValue => {
          logInformation(s"Cache clear activity started for `${keyValue._1}` org")
          bulkApiActor ! keyValue._2.getCsvData(keyValue._1)
          (keyValue._1, getEmptyCacheHolder)
        })

        context become active(newCache, getTimestamp)
      }
  }

  private def isLongWaitingTiming(startTimestamp: Long): Boolean =
    ((System.currentTimeMillis() - startTimestamp) / (1000 * 60)).toInt >= MAX_CACHE_WAITING_TIME

  private def getTimestamp: Long = System.currentTimeMillis()

  private def insertIntoCacheHolder(ch: CacheHolder, entity: ENTITY_TYPE,
                                    ei: EntityInfo): CacheHolder =
    entity match {
      case RTQ_PE_U => ch.copy(rtqPeU = ch.rtqPeU :+ ei)
      case RTQ_PE_P => ch.copy(rtqPeP = ch.rtqPeP :+ ei)
      case RTQ_PE_B => ch.copy(rtqPeB = ch.rtqPeB :+ ei)
      case RTQ_PE_G => ch.copy(rtqPeG = ch.rtqPeG :+ ei)
      case ACCOUNT_PE => ch.copy(accPe = ch.accPe :+ ei)
      case HOLDING_PE => ch.copy(hdPe = ch.hdPe :+ ei)
      case QUESTIONNAIRE_PE_G => ch.copy(quPeG = ch.quPeG :+ ei)
      case QUESTIONNAIRE_PE_MQ => ch.copy(quPeMQ = ch.quPeMQ :+ ei)
      case QUESTIONNAIRE_DETAIL_PEG => ch.copy(quDePeG = ch.quDePeG :+ ei)
      case QUESTIONNAIRE_DETAIL_PEMQ => ch.copy(quDePeMQ = ch.quDePeMQ :+ ei)
    }


  private def getEmptyCacheHolder: CacheHolder =
    CacheHolder(rtqPeU = List.empty[EntityInfo], rtqPeP = List.empty[EntityInfo],
      rtqPeG = List.empty[EntityInfo], rtqPeB = List.empty[EntityInfo],
      accPe = List.empty[EntityInfo], hdPe = List.empty[EntityInfo],
      quPeG = List.empty[EntityInfo], quPeMQ = List.empty[EntityInfo],
      quDePeG = List.empty[EntityInfo], quDePeMQ = List.empty[EntityInfo])

  private def checkCacheNonEmpty(cache: Map[String, CacheHolder]): Boolean =
  cache.values.exists(ch => ch != getEmptyCacheHolder)

}
