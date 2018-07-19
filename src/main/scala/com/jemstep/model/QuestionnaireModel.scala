package com.jemstep.model

import com.jemstep.model.BusinessEntityModel.EntityType.QUESTIONNAIRE_PE_MQ
import com.jemstep.model.BusinessEntityModel.{EntityModel, EntityObject, EntityType, QuestionnaireDetailPeMQ, QuestionnairePeMQ}

object QuestionnaireModel {

  case class Questions(questionId: String, answer: String)
  case class ModelQuestionnaire(questions: List[Questions]) extends BusinessModel {
    import EntityType._

    def extractEntityModels(userId: String, org: String, operation: String): List[EntityModel] = {

      val questionnairePe: List[EntityObject] =
        List(QuestionnairePeMQ(Jemstep_Id__c = userId, Questionnaire_Id__c = "Profile Questionnaire", Questionnaire_Type__c = "PROFILE", Parent_Jemstep_Id__c = userId))

      val listOfEntityModel1: EntityModel = EntityModel(QUESTIONNAIRE_PE_MQ, org, questionnairePe)

      val listOfQuestionnaireDetails: List[EntityObject] =
        questions.map(x =>
          QuestionnaireDetailPeMQ(Answer__c = x.answer, Full_Question__c = "",
            Jemstep_Id__c = userId+"_"+x.questionId, Jemstep_Questionaire_Detail_Id__c = userId,
            Question__c = "", Questionnaire_Type__c = "PROFILE",
            Last_Reviewed_Modified__c = ""))

      val listOfEntityModel2: EntityModel = EntityModel(QUESTIONNAIRE_DETAIL_PEMQ, org, listOfQuestionnaireDetails)

      List(listOfEntityModel1, listOfEntityModel2)
    }
  }
}
