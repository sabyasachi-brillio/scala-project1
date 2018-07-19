package com.jemstep.actor


import java.util

import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper, SerializationFeature}
import com.jemstep.logging.BulkStreamLogging
import com.jemstep.model.CreateJobRequest
import com.jemstep.model.CustomModel.ConnectionDetails
import com.sforce.async.JobStateEnum
import org.apache.http.HttpResponse
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.{HttpPatch, HttpPost, HttpPut}
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClients
import org.apache.http.message.BasicNameValuePair

import scala.collection.JavaConverters._

trait RestClient extends RestConfig {

  /**
    * create the refresh token for given org
    *
    * @param org given user input
    * @return
    */
  def createFromRefreshToken(org: String): ConnectionDetails = {
    // create http client
    val httpclient = HttpClients.createDefault()
    // login params
    val loginParams: util.List[BasicNameValuePair] =
      List(
        new BasicNameValuePair("grant_type", GRANT_TYPE),
        new BasicNameValuePair("client_id", CLIENT_ID),
        new BasicNameValuePair("client_secret", CLIENT_SECRET),
        new BasicNameValuePair("refresh_token", getToken(org))
      ).asJava

    // http post body
    val post: HttpPost = new HttpPost(PRODUCTION_TOKEN_URL)
    post.setEntity(new UrlEncodedFormEntity(loginParams))
    val loginResponse: HttpResponse = httpclient.execute(post)

    // parse
    val mapper: ObjectMapper =
      new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)
    val loginResult: JsonNode = mapper.readValue(
      loginResponse.getEntity.getContent,
      classOf[JsonNode])

    val accessToken = loginResult.get("access_token").asText()
    val instanceUrl = loginResult.get("instance_url").asText()

    BulkStreamLogging.logInformation(s"Request login for `$org` organization got http login response code ${loginResponse.getStatusLine.getStatusCode}")

    ConnectionDetails(accessToken, instanceUrl)
  }

  /**
    * create the job
    *
    * @param obj               object
    * @param connectionDetails connection information
    * @return
    */
  def createJob(obj: String, connectionDetails: ConnectionDetails): String = {
    val httpclient = HttpClients.createDefault()
    val uri: String = connectionDetails.instanceUrl + REST_URI
    val authorization: String = BEARER + " " + connectionDetails.accessToken
    // Set JSON body
    val mapper: ObjectMapper =
      new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)
    val request: CreateJobRequest =
      new CreateJobRequest(obj, "CSV", "insert", "LF")
    val requestJson: String = mapper.writeValueAsString(request)
    val jsonBody: StringEntity = new StringEntity(requestJson)
    // post the request
    val post: HttpPost = new HttpPost(uri)
    post.setHeader("Authorization", authorization)
    post.setHeader("Content-Type", CONTENT_TYPE)
    post.setEntity(jsonBody)

    val response = httpclient.execute(post)
    val responseJson =
      mapper.readValue(response.getEntity.getContent, classOf[JsonNode])

    responseJson.get("id").asText()
  }

  /**
    * upload the data
    *
    * @param csvObject         csv data
    * @param jobId             job id
    * @param connectionDetails connection information
    * @return
    */
  def uploadCSVData(csvObject: String, jobId: String, connectionDetails: ConnectionDetails): Int = {
    val uploadUri: String = connectionDetails.instanceUrl + REST_URI + jobId + "/batches"
    val authorization: String = BEARER + " " + connectionDetails.accessToken

    val httpclient = HttpClients.createDefault()
    val data = new StringEntity(csvObject)
    data.setContentType("text/csv")
    val put: HttpPut = new HttpPut(uploadUri)
    put.setHeader("Authorization", authorization)
    put.setHeader("Content-Type", "text/csv")
    put.setHeader("Accept", "application/json")
    put.setEntity(data)
    val response = httpclient.execute(put)
    response.getStatusLine.getStatusCode
  }


  /**
    * close the job
    *
    * @param jobId             job id
    * @param connectionDetails connection details
    * @return
    */
  def closeOrAbortJob(jobId: String, connectionDetails: ConnectionDetails): String = {
    // Set JSON body
    val mapper: ObjectMapper =
      new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)
    val url: String = connectionDetails.instanceUrl + REST_URI + jobId
    val authorization: String = BEARER + " " + connectionDetails.accessToken
    val patch: HttpPatch = new HttpPatch(url)
    patch.setHeader("Authorization", authorization)
    patch.setHeader("Content-Type", CONTENT_TYPE)

    val httpclient = HttpClients.createDefault()
    val jsonBody = new StringEntity("{\"state\":" + "\"" + JobStateEnum.UploadComplete.toString + "\"}")
    patch.setEntity(jsonBody)
    val response = httpclient.execute(patch)
    val responseJson =
      mapper.readValue(response.getEntity.getContent, classOf[JsonNode])

    responseJson.asText()
  }


}
