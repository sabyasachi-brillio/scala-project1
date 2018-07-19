package com.jemstep.oauth

import java.io._
import java.nio.charset.StandardCharsets

import scala.collection.mutable.ListBuffer
import java.net.InetSocketAddress
import java.security.KeyManagementException
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.UnrecoverableKeyException
import java.security.cert.CertificateException
import java.util

import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import java.util.Properties

import javax.net.ssl.TrustManagerFactory
import org.apache.http.HttpResponse
import org.apache.http.NameValuePair
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.utils.URLEncodedUtils
import org.apache.http.impl.client.HttpClients
import org.apache.http.message.BasicNameValuePair
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpsConfigurator
import com.sun.net.httpserver.HttpsParameters
import com.sun.net.httpserver.HttpsServer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.jemstep.actor.RestConfig

import scala.collection.mutable._

object OAuthSSLHandler extends RestConfig{ // The base URL for every Connect API request
  val handshakingMap = Map.empty[String, String]

  //@SuppressWarnings("restriction")
  private[oauth] class CallbackHandler extends HttpHandler {
    @throws[IOException]
    @SuppressWarnings(Array("org.wartremover.warts.NonUnitStatements"))
    override def handle(t: HttpExchange): Unit = {
      System.out.println("Request received")
      try {
        if (!(t.getRequestMethod == "GET")) {
          t.sendResponseHeaders(405, 0)
          t.getResponseBody.close()
        }
        val requestUri = t.getRequestURI

        println("Get Uri " + requestUri.toString)
        println("Get Fragment " + requestUri.getFragment)
        //val out = ("Sample Response: URI-" + requestUri.toASCIIString + " FRagment: " + requestUri.getFragment).getBytes("UTF-8")
        val queryParameters = URLEncodedUtils.parse(requestUri, StandardCharsets.UTF_8)
        println(queryParameters)
        val responseMap = Map.empty[String, String]
        queryParameters.forEach(x => {
          x.getName match {
            case "code" => responseMap += (x.getName -> x.getValue)
            case "state" => responseMap += (x.getName -> x.getValue)
          }
        })
        println(responseMap)
        val url = getRefreshTokenFromCode(responseMap)
        val listBuf = new ListBuffer[String]
        responseMap("state").toString.split(",").foreach(x => listBuf += x)
        val list = listBuf.toList
        //val classicOrLightining = list(0).toUpperCase
        //val classicOrLight = if (classicOrLightining.equalsIgnoreCase("classic")) configProps.getProperty("CLASSIC") else configProps.getProperty("LIGHTNING")
        println(list(3))
        t.getResponseHeaders.set("Location", list(3).concat("?"+url(1)))
        //val responseString = "authentication: ".concat(url(1)).getBytes("UTF-8")
        t.sendResponseHeaders(302, 0)
        //t.sendResponseHeaders(200, out.length.toLong)
        //t.getResponseBody.write(responseString)
        t.getResponseBody.close()
      } catch {
        case e: Exception => e.printStackTrace()
      }
    }

    /**
      *
      * @param responseMap
      */
    @SuppressWarnings(Array("org.wartremover.warts.NonUnitStatements"))
    private def getRefreshTokenFromCode(responseMap: Map[String, String]): List[String] = {

      val listBuf = new ListBuffer[String]
      val responseStatusList = new ListBuffer[String]
      responseMap("state").toString.split(",").foreach(x => listBuf += x)
      val list = listBuf.toList
      println(list)
      val productionOrSandbox = list(1)
      val org = list(2)
      val tok_url = if (productionOrSandbox.equalsIgnoreCase("production")) PRODUCTION_TOKEN_URL else SANDBOX_TOKEN_URL
      val httpclient = HttpClients.createDefault
      try {
        val loginParams = new util.ArrayList[NameValuePair]
        val code = responseMap("code").toString
        loginParams.add(new BasicNameValuePair("code", code))
        loginParams.add(new BasicNameValuePair("grant_type", GRANT_TYPE_CODE))
        loginParams.add(new BasicNameValuePair("client_id", CLIENT_ID))
        loginParams.add(new BasicNameValuePair("client_secret", CLIENT_SECRET))
        loginParams.add(new BasicNameValuePair("redirect_uri", REDIRECT_URI))
        println(s" tok_url : $tok_url")
        val post: HttpPost = new HttpPost(tok_url)
        post.setEntity(new UrlEncodedFormEntity(loginParams))
        val loginResponse: HttpResponse = httpclient.execute(post)

        val mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)
        val loginResult = mapper.readValue(loginResponse.getEntity.getContent, classOf[JsonNode])
        val ack_url = parseJson(loginResult, org)
        println("loginResult -> " + loginResult.toString)
        println(s"ack_url : $ack_url")
          if(ack_url.isEmpty){
            val tok_url1 = tok_url.substring(0, tok_url.indexOfSlice(".com/") + 5)
            responseStatusList += tok_url1
            responseStatusList += "failure"
            responseStatusList.toList
          }else{
            responseStatusList += ack_url
            responseStatusList += "successful"
            responseStatusList.toList
          }
      } catch {
        case e: Exception => e.printStackTrace()

          val tok_url1 = tok_url.substring(0, tok_url.indexOfSlice(".com/") + 5)
          responseStatusList += tok_url1
          responseStatusList += "failure"
          responseStatusList.toList
      }
    }

    /**
      * This methos parses the json string and stores the "request_token" value to properties file
      *
      * @return
      * @throws Exception
      */
    @SuppressWarnings(Array("org.wartremover.warts.NonUnitStatements"))
    @SuppressWarnings(Array("org.wartremover.warts.Null"))
    def parseJson(JsonObject: JsonNode, org: String): String = {
      try {
        val refresh_token = JsonObject.get("refresh_token").asText()
        val p = new Properties()
        p.load(new FileInputStream("config.properties"))
        val fOut = new FileOutputStream("config.properties")
        p.setProperty(org.toUpperCase + "_REFRESH_TOKEN", refresh_token)
        p.store(fOut, "UTF-8");
        fOut.close();
        JsonObject.get("instance_url").asText()
      } catch {
        case e: Exception => e.printStackTrace()
          ""
      }

    }
  }

  @SuppressWarnings(Array("restriction"))
  @SuppressWarnings(Array("org.wartremover.warts.NonUnitStatements"))
  @SuppressWarnings(Array("org.wartremover.warts.Null"))
  def main(args: Array[String]): Unit = {
    try {
      val keystoreFilename = "keystore.jks"
      val storepass = "jemstep".toCharArray
      val keypass = "jemstep".toCharArray
      val alias = "jemstep"
      val fIn = new FileInputStream(keystoreFilename)
      val keystore = KeyStore.getInstance("JKS")
      keystore.load(fIn, storepass)
      val cert = keystore.getCertificate(alias)
      System.out.println(cert)
      val kmf = KeyManagerFactory.getInstance("SunX509")
      kmf.init(keystore, keypass)
      val tmf = TrustManagerFactory.getInstance("SunX509")
      tmf.init(keystore)
      val portNumber = PORT_NUMBER
      val server = HttpsServer.create(new InetSocketAddress(portNumber), 0)
      // create ssl context
      val sslContext = SSLContext.getInstance("TLSv1.2")
      sslContext.init(kmf.getKeyManagers, tmf.getTrustManagers, null)
      server.setHttpsConfigurator(new HttpsConfigurator(sslContext) {
        override def configure(params: HttpsParameters): Unit = {
          try { // initialise the SSL context
            val c = SSLContext.getDefault
            val engine = c.createSSLEngine
            params.setNeedClientAuth(false)
            params.setCipherSuites(engine.getEnabledCipherSuites)
            params.setProtocols(engine.getEnabledProtocols)
            // get the default parameters
            val defaultSSLParameters = c.getDefaultSSLParameters
            params.setSSLParameters(defaultSSLParameters)
          } catch {
            case ex: Exception =>
              ex.printStackTrace()
              System.out.println("Failed to create HTTPS server")
          }
        }
      })
      server.createContext("/services/oauth2/success", new OAuthSSLHandler.CallbackHandler)
      server.setExecutor(null)
      server.start()
      println("Listening on port " + portNumber.toString)
    } catch {
      case e: IOException =>
        System.out.println("Server startup failed. Exiting.")
        e.printStackTrace()
        System.exit(1)
      case e: KeyStoreException =>
        e.printStackTrace()
      case e: NoSuchAlgorithmException =>
        e.printStackTrace()
      case e: CertificateException =>
        e.printStackTrace()
      case e: UnrecoverableKeyException =>
        e.printStackTrace()
      case e: KeyManagementException =>
        e.printStackTrace()
    }
  }


}
