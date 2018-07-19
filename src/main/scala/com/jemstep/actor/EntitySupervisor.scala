package com.jemstep.actor

import java.util.concurrent.TimeUnit

import akka.Done
import akka.actor._
import com.jemstep.model.BusinessEntityModel.EntityType
import com.jemstep.model.CustomModel.{CacheClean, EntityHolder, CacheRefresh}
import com.jemstep.model.ExtractorModel.IncomingData
import com.jemstep.model._
//import com.jemstep.logging.uploadfailed.UnableToUpload._

import scala.concurrent.duration.FiniteDuration

class EntitySupervisor extends Actor {

  import EntityType._
  import context.dispatcher

  val bulkApiActor: ActorRef = context.actorOf(Props[BulkApiActor], "bulkApiActor")

  val entityActors: Map[String, ActorRef] =
    Map(
      ACCOUNT_PE.toString -> context.actorOf(Props(new CacheActor(ACCOUNT_PE.toString, bulkApiActor)), ACCOUNT_PE.toString),
      HOLDING_PE.toString -> context.actorOf(Props(new CacheActor(HOLDING_PE.toString, bulkApiActor)), HOLDING_PE.toString),
      QUESTIONNAIRE_DETAIL_PE.toString -> context.actorOf(Props(new CacheActor(QUESTIONNAIRE_DETAIL_PE.toString, bulkApiActor)), QUESTIONNAIRE_DETAIL_PE.toString),
      QUESTIONNAIRE_PE_G.toString -> context.actorOf(Props(new CacheActor(QUESTIONNAIRE_PE.toString, bulkApiActor)), QUESTIONNAIRE_PE.toString),
      RTQ_PE.toString -> context.actorOf(Props(new CacheActor(RTQ_PE.toString, bulkApiActor)), RTQ_PE.toString))

  val cacheSch: List[Cancellable] =
    entityActors.values.toList.map(cacheActor =>
      context.system.scheduler.schedule(FiniteDuration(3, TimeUnit.MINUTES),
        FiniteDuration(3, TimeUnit.MINUTES), cacheActor, CacheRefresh)
    )

  val cacheActor: ActorRef =
    context.actorOf(Props(new NewCacheActor(bulkApiActor)), "CacheActor")

  val cacheScheduler: Cancellable =
    context.system.scheduler.schedule(FiniteDuration(10, TimeUnit.MINUTES),
      FiniteDuration(10, TimeUnit.MINUTES), cacheActor, CacheRefresh)

  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  def receive: Receive = active()

  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  def active(): Receive = {
    case incomingData: IncomingData if isValidSchema(incomingData)=>
      newSupervision(incomingData, sender())
    //case incomingData: IncomingData =>
      //supervision(incomingData, sender())
    case cacheClean: CacheClean =>
      cleanCache(cacheClean)
  }

  private def isValidSchema(incomingData: IncomingData): Boolean =
    ExtractorModel.validSchema(incomingData.recordSchemaFullName).isDefined

/*  private def supervision(incomingData: IncomingData, sender: ActorRef): Unit = {
    println("supervision")
  private def supervision(incomingData: IncomingData, sender: ActorRef): Unit = {
    //println("supervision")
    uulogInformation(incomingData.toString)
    /*
    val businessData: BusinessModel = ExtractorModel.parser(incomingData)
    val listOfEntity: List[BusinessEntityModel.EntityModel] =
      businessData.extractEntityModels(incomingData.userId, incomingData.organizationId, incomingData.operation)

    listOfEntity.foreach(entity =>
      entityActors(entity.entityType.toString) !
        CacheMessage(entity.organization, incomingData.offSet, entity.jsonObj))*/
    sender ! Done
  }*/
  private def newSupervision(incomingData: IncomingData, sender: ActorRef): Unit = {
    val businessData: BusinessModel = ExtractorModel.parser(incomingData)
    val listOfEntity: List[BusinessEntityModel.EntityModel] =
      businessData.extractEntityModels(incomingData.userId, incomingData.organizationId, incomingData.operation)

    listOfEntity.map(entityModel => EntityHolder(incomingData.offSet, entityModel))
      .foreach(ch => cacheActor ! ch)

    sender ! Done
  }


  private def cleanCache(cacheClean: CacheClean): Unit = {
    entityActors(RTQ_PE.toString) ! cacheClean
    entityActors(ACCOUNT_PE.toString) ! cacheClean
    entityActors(HOLDING_PE.toString) ! cacheClean
    entityActors(QUESTIONNAIRE_PE_G.toString) ! cacheClean
    entityActors(QUESTIONNAIRE_PE_MQ.toString) ! cacheClean
    entityActors(QUESTIONNAIRE_DETAIL_PE.toString) ! cacheClean
  }

}
