package com.jemstep.consumer

import akka.Done
import akka.actor.ActorSystem
import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import com.jemstep.util.Config._
import com.jemstep.commons.JemstepKafkaAvroDeserializer
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.apache.kafka.common.serialization.ByteArrayDeserializer
import scala.collection.mutable.ListBuffer
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import com.jemstep.consumer.CacheActor


import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import com.jemstep.commons.Util._


object PlainSourceConsumerMain extends App {

  implicit val system: ActorSystem = ActorSystem("PlainSourceConsumerMain")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val consumerSettings = ConsumerSettings(system, new ByteArrayDeserializer, new JemstepKafkaAvroDeserializer)
    .withBootstrapServers(kafka_server)
    .withGroupId("PlainSourceConsumer")
    .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

  println(s"${getClass.getSimpleName} receiving messages from $kafka_server")
  //val timeStamp = "timestamp"//GetDate.getTimeStamp()
  val source: Source[Done, Consumer.Control] = Consumer.committableSource(consumerSettings, Subscriptions.topics("investor"))
    .mapAsync(1) { msg =>
      DB.save(msg.record).flatMap { _done =>
        msg.committableOffset.commitScaladsl()
      }
    }
  //println("after source :: before future")
  val done: Future[Done] = source.runWith(Sink.ignore)
  //println("after source :: after future")
  val res= CacheActor.getcache
  println("Number of organization id"+res.size)

}

object DB {

  def save(record: ConsumerRecord[Array[Byte], GenericRecord]): Future[Done] = {
    var models_goal = new ListBuffer[ListOfBussinesModel]()
    val userId: String = headerValue("userId", record.headers)
    val organizationId: String = headerValue("organizationId", record.headers)
    val operation: String = headerValue("operation", record.headers)
    val recordSchemaFullName: String = record.value.getSchema.getFullName
    val g = recordSchemaFullName match {
      case "com.jemstep.model.goal.Goal" => Goal.parsing(record.value().toString,organizationId)
      case "com.jemstep.model.portfolio.PortfolioByBroker" => PortfolioByBroker.parsing(record.value().toString, organizationId)
      case "com.jemstep.model.assetliability.BacktestMetrics" => BacktestMatrics.parsing(record.value().toString, organizationId)
      case "com.jemstep.model.events.shared.UserIdentified" => UserIdentified.parsing(record.value().toString, organizationId)
      //case _=> "New schema found"
    }


    CacheActor.insert(organizationId,recordSchemaFullName,g)
    //println(record)
    // Object_Writer.writetofile(recordSchemaFullName,organizationId,g)
    println(s"DB.$operation User: $userId, Org: $organizationId, Schema: $recordSchemaFullName\n")
    Future.successful(Done)
  }

}




