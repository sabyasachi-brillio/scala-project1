package com.jemstep.consumer
import akka.actor._
import com.jemstep.logging.BulkStreamLogging.logInformation
//import com.jemstep.model.BusinessEntityModel.EntityType
import com.jemstep.model.CustomModel.{CacheHolder, _}
//import EntityType._

object CacheActor {
  val cache = scala.collection.mutable.Map.empty[String, CacheHolder]

  case class CacheHolder(goal: List[ListOfBussinesModel], portfolio: List[ListOfBussinesModel],
                         user: List[ListOfBussinesModel], backtest: List[ListOfBussinesModel],
                         )

  private def getEmptyCacheHolder: CacheHolder =
    CacheHolder(goal = List.empty[ListOfBussinesModel], portfolio = List.empty[ListOfBussinesModel],
      user = List.empty[ListOfBussinesModel], backtest = List.empty[ListOfBussinesModel])



  private def insertIntoCacheHolder(ch: CacheHolder, entity: String,
                                    ei: ListOfBussinesModel): CacheHolder =

    entity match {
      case "com.jemstep.model.goal.Goal" => ch.copy(goal = ch.goal :+ ei)
      case "com.jemstep.model.portfolio.PortfolioByBroker" => ch.copy(portfolio = ch.portfolio :+ ei)
      case "com.jemstep.model.events.shared.UserIdentified" => ch.copy(user = ch.user :+ ei)
      case "com.jemstep.model.assetliability.BacktestMetrics" => ch.copy(backtest = ch.backtest :+ ei)

    }
   def insert(org:String ,schema:String, info:ListOfBussinesModel): Unit= {

    val newCacheHolder: CacheHolder =cache.get(org)
      .map(existingData =>
        insertIntoCacheHolder(existingData, schema, info))
      .getOrElse(insertIntoCacheHolder(getEmptyCacheHolder,schema,info))

     cache+= (org-> newCacheHolder)

   }
  def getcache:scala.collection.mutable.Map[String, CacheHolder]={
    return cache
  }
}
