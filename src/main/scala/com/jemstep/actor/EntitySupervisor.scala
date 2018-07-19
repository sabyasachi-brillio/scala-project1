package com.jemstep.actor

import java.util.concurrent.TimeUnit

import akka.Done
import akka.actor._
import com.jemstep.model.CustomModel.{EntityHolder, CacheRefresh}
import com.jemstep.model.ExtractorModel.IncomingData
import com.jemstep.model._
//import com.jemstep.logging.uploadfailed.UnableToUpload._

import scala.concurrent.duration.FiniteDuration

class EntitySupervisor extends Actor {

  import context.dispatcher

  val bulkApiActor: ActorRef = context.actorOf(Props[BulkApiActor], "bulkApiActor")

  val cacheActor: ActorRef =
    context.actorOf(Props(new NewCacheActor(bulkApiActor)), "CacheActor")

  val cacheScheduler: Cancellable =
    context.system.scheduler.schedule(FiniteDuration(5, TimeUnit.MINUTES),
      FiniteDuration(5, TimeUnit.MINUTES), cacheActor, CacheRefresh)

  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  def receive: Receive = active()

  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  def active(): Receive = {
    case incomingData: IncomingData if isValidSchema(incomingData)=>
      newSupervision(incomingData, sender())
  }

  private def isValidSchema(incomingData: IncomingData): Boolean =
    ExtractorModel.validSchema(incomingData.recordSchemaFullName).isDefined

  private def newSupervision(incomingData: IncomingData, sender: ActorRef): Unit = {
    val businessData: BusinessModel = ExtractorModel.parser(incomingData)
    val listOfEntity: List[BusinessEntityModel.EntityModel] =
      businessData.extractEntityModels(incomingData.userId, incomingData.organizationId, incomingData.operation)

    listOfEntity.map(entityModel => EntityHolder(incomingData.offSet, entityModel))
      .foreach(ch => cacheActor ! ch)

    sender ! Done
  }



}
