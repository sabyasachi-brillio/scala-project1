package com.jemstep.util

import akka.Done
import akka.actor.{ActorRef, ActorSystem}
import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import akka.util.Timeout
import com.jemstep.commons.JemstepKafkaAvroDeserializer
import com.jemstep.commons.Util.headerValue
import com.jemstep.model.ExtractorModel.IncomingData
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.apache.kafka.common.serialization.ByteArrayDeserializer

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

trait CustomConsumer{

  implicit val system: ActorSystem
  implicit val materializer: ActorMaterializer
  implicit val timeout: Timeout = Timeout(5.seconds)
  val supervisor: ActorRef

  import com.jemstep.logging.BulkStreamLogging._
  /**
    * get the consumer setting with custom deserializer
    *
    * @return consumer with avro array byes key and generic record type value
    */
  def getConsumerSetting(kafka_server: String): ConsumerSettings[Array[Byte], GenericRecord] =
    ConsumerSettings(system, new ByteArrayDeserializer, new JemstepKafkaAvroDeserializer)
      .withBootstrapServers(kafka_server)
      .withGroupId("PlainSourceConsumer")
      .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

  /**
    * start the streaming for given topics and consumer settings
    *
    * @param topics           give topics string
    * @param consumerSettings given consumer setting
    * @return
    */
  def startStreaming(topics: String,
                     consumerSettings: ConsumerSettings[Array[Byte], GenericRecord]): Unit = {
    val source: Source[Done, Consumer.Control] =
      Consumer.committableSource(consumerSettings, Subscriptions.topics(ts = topics))
        .mapAsync(1) { msg =>
          sendToSupervisor(msg.record)
            .flatMap { _ => msg.committableOffset.commitScaladsl() }
        }
    source.runWith(Sink.ignore)
      .foreach( _ => logInformation("Streaming Processing Done for received Messages."))
  }

  def sendToSupervisor(record: ConsumerRecord[Array[Byte], GenericRecord]): Future[Done] = {
    val userId: String = headerValue("userId", record.headers)
    val organizationId: String = headerValue("organizationId", record.headers)
    val operation: String = headerValue("operation", record.headers)
    val recordSchemaFullName: String = record.value.getSchema.getFullName
    // create incoming data
    val incomingData: IncomingData =
      IncomingData(recordSchemaFullName,
        record.value().toString,
        userId,
        organizationId,
        operation,
        record.offset())
    // IncomingMessage send to  Supervisor
    supervisor ! incomingData
    Future.successful(Done)
  }

}
