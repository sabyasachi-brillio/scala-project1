package com.jemstep.consumer

import com.jemstep.consumer.Goal.BusinessModel

import scala.util.parsing.json.JSON

object UserIdentified  {

  case class RTQ_User_PE(parentJemstepId: String,emailAddress:String, lastName:String) extends  BusinessModel{
    override def toString: String = parentJemstepId+","+emailAddress+","+lastName
  }

  def parsing(jsonstring1: String,organizationId:String):ListOfBussinesModel ={
    class CC[T] {
      def unapply(a: Any): Option[T] = Some(a.asInstanceOf[T])
    }


    object M extends CC[Map[String, Any]]
    // println(M.toString)
    //object L extends CC[List[Any]]
    //println(L.toString)
    object S extends CC[String]
    //println(S.toString)
    //object D extends CC[Double]

    //println(D.toString)

    //object B extends CC[Boolean]

    //println(B.toString)
    val jsonString = jsonstring1.stripMargin
    // println(jsonString)

    @SuppressWarnings(Array("org.wartremover.warts.NonUnitStatements"))
    // @SuppressWarnings(Array("org.wartremover.warts.Any"))
    val result = for {
      Some(M(map)) <- List(JSON.parseFull(jsonString))
      S(parentJemstepId) = map("userId")
      S(emailAddress) = map("emailAddress")
      S(lastName) = map("lastName")

    } yield {
      RTQ_User_PE(parentJemstepId,emailAddress,lastName)
    }
    new ListOfBussinesModel(organizationId, result)
  }
}
