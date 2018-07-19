package com.jemstep.model

import com.jemstep.model.BusinessEntityModel.EntityType.QUESTIONNAIRE_PE_MQ
import com.jemstep.model.BusinessEntityModel.{EntityModel, EntityType, QuestionnairePe, QuestionnairePeMQ}

object QuestionnaireModel {

  case class Questions(questionId: String)
  case class ModelQuestionnaire(questions: List[Questions]) extends BusinessModel {
    import EntityType._

    def extractEntityModels(userId: String, org: String, operation: String): List[EntityModel] = {

      val questionnairePe: List[QuestionnairePe] =
        questions.map(x => QuestionnairePeMQ(Jemstep_Id__c = userId, Questionnaire_Id__c = x.questionId+" Profile", Questionnaire_Type__c = "Profile", Parent_Jemstep_Id__c = userId))

      List(EntityModel(QUESTIONNAIRE_PE_MQ, org, questionnairePe))
    }
  }
}
