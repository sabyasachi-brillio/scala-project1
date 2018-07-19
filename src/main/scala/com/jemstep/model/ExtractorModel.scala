package com.jemstep.model

import com.jemstep.model.BacktestMetricsModel.BacktestMetrics
import com.jemstep.model.BusinessEntityModel.EntityModel
import com.jemstep.model.GoalModel.Goal
import com.jemstep.model.PortfolioByBrokerModel.PortfolioByBroker
import com.jemstep.model.UserIdentifiedModel.UserIdentified
import com.jemstep.model.QuestionnaireModel.ModelQuestionnaire

object ExtractorModel {

  import net.liftweb.json._

  implicit val formats: DefaultFormats = DefaultFormats

  case class IncomingData(recordSchemaFullName: String,
                          jsonString: String,
                          userId: String,
                          organizationId: String,
                          operation: String,
                          offSet: Long)

  /**
    * default unknown model
    */
  case object UnknownSchema extends BusinessModel {
    def extractEntityModels(userId: String, org: String, operation: String): List[EntityModel] = List.empty[EntityModel]
  }

  /**
    * given schema name and json string extract the message and give business model
    *
    * @param incomingData schema name and json string
    * @return
    */
  def parser(incomingData: IncomingData): BusinessModel =
    incomingData.recordSchemaFullName match {
      case "com.jemstep.model.portfolio.PortfolioByBroker" =>
        parse(incomingData.jsonString).extract[PortfolioByBroker]
      case "com.jemstep.model.goal.Goal" =>
        parse(incomingData.jsonString).extract[Goal]
      case "com.jemstep.model.questionnaire.ModelQuestionnaire" =>
	parse(incomingData.jsonString).extract[ModelQuestionnaire]
      case "com.jemstep.model.assetliability.BacktestMetrics" =>
        parse(incomingData.jsonString).extract[BacktestMetrics]
      case "com.jemstep.model.events.shared.UserIdentified" =>
        parse(incomingData.jsonString).extract[UserIdentified]
      case _ => UnknownSchema
    }

  def validSchema(schemaName: String): Option[String] =
    List("com.jemstep.model.portfolio.PortfolioByBroker",
      "com.jemstep.model.goal.Goal",
      "com.jemstep.model.questionnaire.ModelQuestionnaire",
      "com.jemstep.model.assetliability.BacktestMetrics",
      "com.jemstep.model.events.shared.UserIdentified")
      .find(_ == schemaName)
}
