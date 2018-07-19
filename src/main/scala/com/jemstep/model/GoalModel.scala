package com.jemstep.model

import com.jemstep.model.BusinessEntityModel._

object GoalModel {

  /**
    *
    * Type Two
    *
    */
  case class Questions(questionId: String, answer: String)

  case class Questionnaire(questions: List[Questions])

  case class Goal(questionnaire: Questionnaire, goalType: String,
                  overrideTargetModel: String, id: String, goalObjective: String) extends BusinessModel {

    import EntityType._

    def extractEntityModels(userId: String, org: String, operation: String): List[EntityModel] = {

      val listOfQuestionnaires: List[EntityObject] =
         List(QuestionnairePeG(Jemstep_Id__c = id,
            Questionnaire_Id__c = goalObjective.toLowerCase.capitalize+" Fund", Questionnaire_Type__c = goalType, Parent_Jemstep_Id__c = userId))

      val listOfEntityModel1: EntityModel = EntityModel(QUESTIONNAIRE_PE_G, org, listOfQuestionnaires)

      val listOfQuestionnaireDetails: List[EntityObject] =
        questionnaire.questions.map(x =>
          QuestionnaireDetailPe(Answer__c = x.answer, Full_Question__c = "",
            Jemstep_Id__c = x.questionId+"_"+System.currentTimeMillis.toString, Jemstep_Questionaire_Detail_Id__c = id,
            Question__c = "", Questionnaire_Type__c = "",
            Last_Reviewed_Modified__c = ""))

      val listOfEntityModel2: EntityModel = EntityModel(QUESTIONNAIRE_DETAIL_PE, org, listOfQuestionnaireDetails)


      val rtqPe: RtqPe =
        RtqPeG(Jemstep_Id__c = userId,
          Mapped_Target_Model__c = overrideTargetModel)

      val listOfEntityModel3: EntityModel = EntityModel(RTQ_PE_G, org, rtqPe :: Nil)

      List(listOfEntityModel1, listOfEntityModel2, listOfEntityModel3)
    }
  }

}
