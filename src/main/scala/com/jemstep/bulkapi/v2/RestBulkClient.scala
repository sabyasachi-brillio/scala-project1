
package com.jemstep.bulkapi.v2

import java.io.{FileInputStream, FileNotFoundException, IOException}

import java.nio.file.{Files, Paths}
import java.util.{ArrayList, List, Properties}

import org.apache.http.{HttpResponse, NameValuePair}

import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.{HttpGet, HttpPatch, HttpPost, HttpPut}
import org.apache.http.entity.StringEntity

import org.apache.http.impl.client.HttpClients
import org.apache.http.message.BasicNameValuePair
import org.apache.http.util.EntityUtils

import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper, SerializationFeature}
import com.sforce.async.JobStateEnum


object RestBulkClient {

  private val GRANT_TYPE: String = "refresh_token"

  private val BEARER: String = "Bearer"

  private val CONTENT_TYPE: String = "application/json"

  private val ACCEPT: String = "application/json"

  private val REST_URI: String = "/services/data/v42.0/jobs/ingest/"


  val configProps: Properties = new Properties()


  try {
    configProps.load(new FileInputStream("config.properties"))

  } catch {
    case e: FileNotFoundException => e.printStackTrace()

    case e: IOException => e.printStackTrace()

  }
  private val TOKEN_URL: String = configProps.getProperty("TOKEN_URL") //.asInstanceOf[String]

  private val CLIENT_ID: String = configProps.getProperty("CLIENT_ID") //.asInstanceOf[String]

  private val CLIENT_SECRET: String = configProps.getProperty("CLIENT_SECRET") //.asInstanceOf[String]

  //private val REFRESH_TOKEN: String = configProps.getProperty("REFRESH_TOKEN") //.asInstanceOf[String]

  private val auth: ArrayList[String] = new ArrayList()

  def createFromRefreshToken(org : String): Unit = {
    try {

      val httpclient = HttpClients.createDefault()
      val loginParams: List[NameValuePair] = new ArrayList[NameValuePair]()
      val bool1 = loginParams.add(new BasicNameValuePair("grant_type", GRANT_TYPE))
      val bool2 = loginParams.add(new BasicNameValuePair("client_id", CLIENT_ID))
      val bool3 = loginParams.add(new BasicNameValuePair("client_secret", CLIENT_SECRET))
      val bool4 = loginParams.add(new BasicNameValuePair("refresh_token", configProps.getProperty(org+"_REFRESH_TOKEN")))
      val post: HttpPost = new HttpPost(TOKEN_URL)
      post.setEntity(new UrlEncodedFormEntity(loginParams))
      val loginResponse: HttpResponse = httpclient.execute(post)
      // parse
      val mapper: ObjectMapper =
        new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)
      val loginResult: JsonNode = mapper.readValue(
        loginResponse.getEntity.getContent,
        classOf[JsonNode])

      val val1 = auth.add(loginResult.get("access_token").asText())
      val val2 = auth.add(loginResult.get("instance_url").asText())
      println(" aadasd " + val1.toString() + "" + val2.toString())

      println(bool1.toString() + "" + bool2.toString() + "" + bool3.toString() + "" + bool4.toString())

    } catch {
      case e: IOException => e.printStackTrace()

    }

  }

  def createJob(obj: String): String = {
    val httpclient = HttpClients.createDefault()
    val uri: String = auth.get(1) + REST_URI
    println("createjob URI -> " + uri)
    val authorization: String = BEARER + " " + auth.get(0)
    // Set JSON body
    val mapper: ObjectMapper =
      new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)
    val request: CreateJobRequest =
      new CreateJobRequest(obj, "CSV", "insert", "LF")
    val requestJson: String = mapper.writeValueAsString(request)
    println(requestJson)
    val jsonBody: StringEntity = new StringEntity(requestJson)
    // post the request
    val post: HttpPost = new HttpPost(uri)
    post.setHeader("Authorization", authorization)
    post.setHeader("Content-Type", CONTENT_TYPE)
    post.setEntity(jsonBody)
    println(
      "create job input jsonBody -> " +
        mapper.readValue(jsonBody.getContent, classOf[JsonNode]).asText())
    val response = httpclient.execute(post)
    val responseJson =
      mapper.readValue(response.getEntity.getContent, classOf[JsonNode])
    println("create job responseJson -> " + responseJson.asText())
    responseJson.get("id").asText()

  }

  def createJobForJsonUpload(`object`: String): String = {


    val httpclient = HttpClients.createDefault()
    val uri: String = auth.get(1) + REST_URI
    println("createjob URI -> " + uri)
    val authorization: String = BEARER + " " + auth.get(0)
    // Set JSON body
    val mapper: ObjectMapper =
      new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)
    val request: CreateJobRequest =
      new CreateJobRequest(`object`, "JSON", "insert", "")
    val requestJson: String = mapper.writeValueAsString(request)
    //println(requestJson)
    val jsonBody: StringEntity = new StringEntity(requestJson)
    // post the request
    val post: HttpPost = new HttpPost(uri)
    post.setHeader("Authorization", authorization)
    post.setHeader("Content-Type", CONTENT_TYPE)
    post.setEntity(jsonBody)
    println(
      "create job input jsonBody -> " +
        mapper.readValue(jsonBody.getContent, classOf[JsonNode]).asText())
    val response = httpclient.execute(post)
    val responseJson =
      mapper.readValue(response.getEntity.getContent, classOf[JsonNode])
    println("create job responseJson -> " + responseJson.asText())
    val jobId = responseJson.get("id").asText()
    println(s"created job id: $jobId")
    jobId
  }

  def uploadData(file: String, jobId: String): Int = {
    val uploadUri: String = auth.get(1) + REST_URI + jobId + "/batches"
    println("upload data uri -> " + uploadUri)
    val authorization: String = BEARER + " " + auth.get(0)
    //var data: StringEntity = null
    //var response: HttpResponse = null

    val httpclient = HttpClients.createDefault()
    val fileContents: String = new String(
      Files.readAllBytes(Paths.get(file)))
    val data = new StringEntity(fileContents)
    data.setContentType("text/csv")
    val put: HttpPut = new HttpPut(uploadUri)
    put.setHeader("Authorization", authorization)
    put.setHeader("Content-Type", "text/csv")
    put.setHeader("Accept", "application/json")
    put.setEntity(data)
    val response = httpclient.execute(put)

    println(
      "upload data response status code -> " + response.getStatusLine.getStatusCode.toString())
    response.getStatusLine.getStatusCode
  }

  def uploadCsvData(csvObject: String, jobId: String): Int = {
    val uploadUri: String = auth.get(1) + REST_URI + jobId + "/batches"
    println("upload data uri -> " + uploadUri)
    val authorization: String = BEARER + " " + auth.get(0)

    val httpclient = HttpClients.createDefault()
    val data = new StringEntity(csvObject)
    data.setContentType("text/csv")
    val put: HttpPut = new HttpPut(uploadUri)
    put.setHeader("Authorization", authorization)
    put.setHeader("Content-Type", "text/csv")
    put.setHeader("Accept", "application/json")
    put.setEntity(data)
    val response = httpclient.execute(put)

    println("upload data response status code -> " + response.getStatusLine.getStatusCode.toString())
    response.getStatusLine.getStatusCode
  }


  def uploadJsonData(file: String, jobId: String): Int = {
    val uploadUri: String = auth.get(1) + REST_URI + jobId + "/batches"
    println("upload data uri -> " + uploadUri)
    val authorization: String = BEARER + " " + auth.get(0)
    //var data: StringEntity = null
    //var response: HttpResponse = null

    val httpclient = HttpClients.createDefault()
    val fileContents: String = new String(
      Files.readAllBytes(Paths.get(file)))
    val data = new StringEntity(fileContents)
    data.setContentType("application/json")
    val put: HttpPut = new HttpPut(uploadUri)
    put.setHeader("Authorization", authorization)
    put.setHeader("Content-Type", "application/json")
    put.setHeader("Accept", "application/json")
    put.setEntity(data)
    val response = httpclient.execute(put)

    println(
      "upload data response status code -> " + response.getStatusLine.getStatusCode.toString())
    response.getStatusLine.getStatusCode
  }


  def getSucessfulResults(jobId: String): Unit = {
    val url: String = auth.get(1) + REST_URI + jobId + "/successfulResults/"
    val authorization: String = BEARER + " " + auth.get(0)
    val get: HttpGet = new HttpGet(url)
    get.setHeader("Authorization", authorization)
    get.setHeader("Content-Type", CONTENT_TYPE)
    val httpclient = HttpClients.createDefault()
    val response = httpclient.execute(get)
    if (response != null && response.getEntity != null) {
      val responseEntity = EntityUtils.toString(response.getEntity)
      println("get successful responseJson -> " + responseEntity)
    }
  }

  def getSucessfulResultsString(jobId: String): String = {
    val url: String = auth.get(1) + REST_URI + jobId + "/successfulResults/"
    val authorization: String = BEARER + " " + auth.get(0)
    val get: HttpGet = new HttpGet(url)
    get.setHeader("Authorization", authorization)
    get.setHeader("Content-Type", CONTENT_TYPE)
    val httpclient = HttpClients.createDefault()
    val response = httpclient.execute(get)
    if (response != null && response.getEntity != null) {
      val responseEntity = EntityUtils.toString(response.getEntity)
      //println("get successful responseJson -> " + responseEntity)
      responseEntity
    } else {
      "No response"
    }
  }


  def getFailedResults(jobId: String): Unit = {
    val url: String = auth.get(1) + REST_URI + jobId + "/failedResults/"
    val authorization: String = BEARER + " " + auth.get(0)
    val get: HttpGet = new HttpGet(url)
    get.setHeader("Authorization", authorization)
    get.setHeader("Content-Type", CONTENT_TYPE)
    val httpclient = HttpClients.createDefault()
    val response = httpclient.execute(get)
    val responseEntity = EntityUtils.toString(response.getEntity)

    println("get failed responseJson -> " + responseEntity)
  }

  def getFailedResultsString(jobId: String): String = {
    val url: String = auth.get(1) + REST_URI + jobId + "/failedResults/"
    val authorization: String = BEARER + " " + auth.get(0)
    val get: HttpGet = new HttpGet(url)
    get.setHeader("Authorization", authorization)
    get.setHeader("Content-Type", CONTENT_TYPE)
    val httpclient = HttpClients.createDefault()
    val response = httpclient.execute(get)
    if (response != null && response.getEntity != null) {
	    val responseEntity = EntityUtils.toString(response.getEntity)
	    //println("get failed responseJson -> " + responseEntity)
	    responseEntity
    } else{
	"no response"
    }
  }

  def getUnprocessedResults(jobId: String): Unit = {    val url: String = auth.get(1) + REST_URI + jobId + "/unprocessedrecords/"
    val authorization: String = BEARER + " " + auth.get(0)
    // Set Headers
    val apiParams: List[NameValuePair] = new ArrayList[NameValuePair]()
    val bool1 = apiParams.add(new BasicNameValuePair("Authorization", authorization))
    val bool2 = apiParams.add(new BasicNameValuePair("Content-Type", CONTENT_TYPE))
    val bool3 = apiParams.add(new BasicNameValuePair("Accept", ACCEPT))
    println(bool1.toString() + "" + bool2.toString() + "" + bool3.toString())
    val get: HttpGet = new HttpGet(url)
    get.setHeader("Authorization", authorization)
    get.setHeader("Content-Type", CONTENT_TYPE)

    val httpclient = HttpClients.createDefault()
    val response = httpclient.execute(get)
    val responseEntity = EntityUtils.toString(response.getEntity)
    println("get unprocessed responseJson -> " + responseEntity)
  }

  def getUnprocessedResultsString(jobId: String): String = {
    val url: String = auth.get(1) + REST_URI + jobId + "/unprocessedrecords/"
    val authorization: String = BEARER + " " + auth.get(0)
    // Set Headers
    val apiParams: List[NameValuePair] = new ArrayList[NameValuePair]()
    val bool1 = apiParams.add(new BasicNameValuePair("Authorization", authorization))
    val bool2 = apiParams.add(new BasicNameValuePair("Content-Type", CONTENT_TYPE))
    val bool3 = apiParams.add(new BasicNameValuePair("Accept", ACCEPT))
    println(bool1.toString() + "" + bool2.toString() + "" + bool3.toString())
    val get: HttpGet = new HttpGet(url)
    get.setHeader("Authorization", authorization)
    get.setHeader("Content-Type", CONTENT_TYPE)
    val httpclient = HttpClients.createDefault()
    val response = httpclient.execute(get)
    if (response != null && response.getEntity != null) {
	val responseEntity = EntityUtils.toString(response.getEntity)
 	//println("get unprocessed responseJson -> " + responseEntity)
	responseEntity
    }else{
	"no response"
    }
  }

  def closeOrAbortJob(jobId: String): Unit = {
    // Set JSON body
    val mapper: ObjectMapper =
      new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)
    val url: String = auth.get(1) + REST_URI + jobId
    val authorization: String = BEARER + " " + auth.get(0)
    val patch: HttpPatch = new HttpPatch(url)
    patch.setHeader("Authorization", authorization)
    patch.setHeader("Content-Type", CONTENT_TYPE)
    // var jsonBody: StringEntity = null
    //var response: HttpResponse = null
    //var responseJson: JsonNode = null

    val httpclient = HttpClients.createDefault()
    val jsonBody = new StringEntity(
      "{\"state\":" + "\"" + JobStateEnum.UploadComplete.toString +
        "\"}")
    patch.setEntity(jsonBody)
    val response = httpclient.execute(patch)
    val responseJson =
      mapper.readValue(response.getEntity.getContent, classOf[JsonNode])

    println("close job responseJson -> " + responseJson.asText())
  }


}
