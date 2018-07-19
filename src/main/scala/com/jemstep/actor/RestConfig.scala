package com.jemstep.actor

import java.io.{FileInputStream, FileNotFoundException, IOException}
import java.util.Properties

trait RestConfig {

  val GRANT_TYPE_CODE = "authorization_code"
  val GRANT_TYPE: String = "refresh_token"
  val BEARER: String = "Bearer"
  val CONTENT_TYPE: String = "application/json"
  val ACCEPT: String = "application/json"
  val configProps: Properties = new Properties()
  try {
    configProps.load(new FileInputStream("config.properties"))
  } catch {
    case e: FileNotFoundException => e.printStackTrace()
    case e: IOException => e.printStackTrace()
  }
  val PRODUCTION_TOKEN_URL: String = configProps.getProperty("PRODUCTION_TOKEN_URL")
  val SANDBOX_TOKEN_URL: String = configProps.getProperty("SANDBOX_TOKEN_URL")
  val CLIENT_ID: String = configProps.getProperty("CLIENT_ID")
  val CLIENT_SECRET: String = configProps.getProperty("CLIENT_SECRET")
  val REDIRECT_URI = configProps.getProperty("REDIRECT_URI")
  val CLASSIC = configProps.getProperty("CLASSIC")
  val LIGHTNING = configProps.getProperty("LIGHTNING")
  val PORT_NUMBER = configProps.getProperty("PORT_NUMBER").toInt
  val REST_URI = configProps.getProperty("REST_URI")
  def getToken(org: String): String = configProps.getProperty(org.toUpperCase + "_REFRESH_TOKEN")
}
