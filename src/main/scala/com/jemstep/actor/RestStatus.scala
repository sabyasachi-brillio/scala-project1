package com.jemstep.actor

import com.jemstep.model.CustomModel.ConnectionDetails
import org.apache.http.client.methods.{CloseableHttpResponse, HttpGet}
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils


trait RestStatus extends RestConfig {

  /**
    * get successful result
    *
    * @param jobId             job id
    * @param connectionDetails connection details
    * @return
    */
  def getSuccessfulResultsString(jobId: String, connectionDetails: ConnectionDetails): String = {
    val url: String = connectionDetails.instanceUrl + REST_URI + jobId + "/successfulResults/"
    val authorization: String = BEARER + " " + connectionDetails.accessToken
    val get: HttpGet = new HttpGet(url)
    get.setHeader("Authorization", authorization)
    get.setHeader("Content-Type", CONTENT_TYPE)
    val httpclient = HttpClients.createDefault()
    val response = httpclient.execute(get)
    if (response != null && response.getEntity != null) EntityUtils.toString(response.getEntity)
    else "No response"
  }

  /**
    * failure result
    *
    * @param jobId             job id
    * @param connectionDetails connection details
    * @return
    */
  def getFailedResultsString(jobId: String, connectionDetails: ConnectionDetails): String = {
    val url: String = connectionDetails.instanceUrl + REST_URI + jobId + "/failedResults/"
    val authorization: String = BEARER + " " + connectionDetails.accessToken
    val get: HttpGet = new HttpGet(url)
    get.setHeader("Authorization", authorization)
    get.setHeader("Content-Type", CONTENT_TYPE)
    val httpclient = HttpClients.createDefault()
    val response = httpclient.execute(get)
    if (response != null && response.getEntity != null) EntityUtils.toString(response.getEntity)
    else "no response"
  }

  /**
    * get unprocessed result
    *
    * @param jobId             job id
    * @param connectionDetails connection details
    * @return
    */
  def getUnprocessedResultsString(jobId: String, connectionDetails: ConnectionDetails): String = {
    val url: String = connectionDetails.instanceUrl + REST_URI + jobId + "/unprocessedrecords/"
    val authorization: String = BEARER + " " + connectionDetails.accessToken
    // Set Headers
    /*val apiParams = List(new BasicNameValuePair("Authorization", authorization),
      new BasicNameValuePair("Content-Type", CONTENT_TYPE),
      new BasicNameValuePair("Accept", ACCEPT))//.asJava*/
    val get: HttpGet = new HttpGet(url)
    get.setHeader("Authorization", authorization)
    get.setHeader("Content-Type", CONTENT_TYPE)
    val httpclient = HttpClients.createDefault()
    val response: CloseableHttpResponse = httpclient.execute(get)
    if (response != null && response.getEntity != null) EntityUtils.toString(response.getEntity)
    else "no response"
  }


}
