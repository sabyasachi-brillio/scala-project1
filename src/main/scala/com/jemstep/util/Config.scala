package com.jemstep.util

object Config {

  val kafka_host: String = System.getProperty("kafka.host", "kafka") // 104.211.222.237
  val kafka_port: Int = System.getProperty("kafka.port", "9092").toInt
  val kafka_server: String = s"$kafka_host:$kafka_port"

  // Looks like some libraries still insist on using null in weird and wonderful ways
  // https://www.infoq.com/presentations/Null-References-The-Billion-Dollar-Mistake-Tony-Hoare
  @SuppressWarnings(Array("org.wartremover.warts.Null"))
  val default_avro_reuse: Null = null

}
