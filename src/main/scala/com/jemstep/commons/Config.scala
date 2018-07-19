package com.jemstep.commons

object Config {

  val kafka_host = System.getProperty("kafka.host", "kafka") // 104.211.222.237
  val kafka_port = System.getProperty("kafka.port", "9092").toInt
  val kafka_server = s"$kafka_host:$kafka_port"

  @SuppressWarnings(Array("org.wartremover.warts.Null"))
  val default_avro_reuse = null

}
