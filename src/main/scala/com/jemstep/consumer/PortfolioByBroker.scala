package com.jemstep.consumer

import com.jemstep.consumer.Goal.BusinessModel

import scala.util.parsing.json.JSON

object PortfolioByBroker {

  case class Account_PE(accountName: String,accountNumber:String, accountStatus:String, accountType:String,accountValue:Double,
                        institution:String,assetClass:String,costBasis:Double,update:String,description:String,uuid:String) extends  BusinessModel{
    override def toString: String = accountName+","+accountNumber+","+accountStatus+","+accountType+","+accountValue.toString+","+institution+","+assetClass+","+costBasis.toString+","+update+","+description+","+uuid
  }

  def parsing(jsonstring1: String, organizationId: String):ListOfBussinesModel ={
    class CC[T] {
      def unapply(a: Any): Option[T] = Some(a.asInstanceOf[T])
    }


    object M extends CC[Map[String, Any]]
    // println(M.toString)
    object L extends CC[List[Any]]
    //println(L.toString)
    object S extends CC[String]
    //println(S.toString)
    object D extends CC[Double]

    //println(D.toString)

    //object B extends CC[Boolean]

    //println(B.toString)
    val jsonString = jsonstring1.stripMargin
    // println(jsonString)

    @SuppressWarnings(Array("org.wartremover.warts.NonUnitStatements"))
    // @SuppressWarnings(Array("org.wartremover.warts.Any"))
    val result = for {
      Some(M(map)) <- List(JSON.parseFull(jsonString))

      L(brokers) = map("brokers")
      M(broker) <- brokers
      L(accounts)= broker("accounts")
      M(account) <- accounts
      L(positions)=account("positions")
      M(position)  <- positions
      S(accountName) = account("accountName")
      S(accountNumber) = account("accountNumber")
      S(accountStatus) = account("accountStatus")
      S(accountType) = account("accountType")
      D(accountValue) = account("dollarValue")
      S(institution) = account("uuid")
      //S(accountId) = position("accountId")
      S(assetClass) = position("assetClass")
      D(costBasis) = position("costBasis")
      S(update) = position("update")
      S(description) = position("description")
      S(uuid) = position("uuid")

    } yield {
      Account_PE(accountName,accountNumber,accountStatus,accountType,accountValue,institution,assetClass,costBasis,update,description,uuid)
    }
    new ListOfBussinesModel(organizationId, result)
  }

}
