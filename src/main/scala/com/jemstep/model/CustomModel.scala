package com.jemstep.model

import com.jemstep.model.BusinessEntityModel._

object CustomModel {

  import EntityType._

  trait ActorMessage

  case object CacheRefresh extends ActorMessage

  case class CacheClean(org: String) extends ActorMessage

  case class CacheMessage(org: String, offSet: Long,
                          listOfObj: List[EntityObject]) extends ActorMessage

  case class EntityHolder(offSet: Long, holder: EntityModel) extends ActorMessage

  case class CsvData(entity: String, org: String,
                     csvObject: String, offsets: List[Long]) {
    override def toString: String =
      s"Unable to upload the data for entity: `$entity` " +
        s"org: `$org` and offsets: $offsets\n" + csvObject
  }

  case class CsvDataList(rtqPeU: CsvData, rtqPeP: CsvData,
                         rtqPeG: CsvData, rtqPeB: CsvData,
                         accPe: CsvData, hdPe: CsvData,
                         quPeG: CsvData, quPeMQ: CsvData, quDePe: CsvData) {
    override def toString: String =
      rtqPeU.toString + rtqPeP.toString +
        rtqPeG.toString + rtqPeB.toString +
        accPe.toString + hdPe.toString +
        quPeG.toString + quPeMQ.toString + quDePe.toString
  }

  case class CacheStore(lastBulkApiCall: Long, lastCacheUpdate: Long,
                        listOfObj: List[(Long, EntityObject)])

  case class ConnectionDetails(accessToken: String, instanceUrl: String)

  case class EntityInfo(entity: ENTITY_TYPE, offSet: Long, csvData: String)

  case class CacheHolder(rtqPeU: List[EntityInfo], rtqPeP: List[EntityInfo],
                         rtqPeG: List[EntityInfo], rtqPeB: List[EntityInfo],
                         accPe: List[EntityInfo], hdPe: List[EntityInfo],
                         quPeG: List[EntityInfo], quPeMQ: List[EntityInfo], quDePe: List[EntityInfo]) {
    def readyForUpload: Boolean =
      rtqPeU.nonEmpty & //rtqPeP.nonEmpty & rtqPeB.nonEmpty & rtqPeG.nonEmpty &
        accPe.nonEmpty & hdPe.nonEmpty & quPeG.nonEmpty & quPeMQ.nonEmpty & quDePe.nonEmpty

    def anyEntityReachedMaxSize(cs: Int): Boolean =
      if (readyForUpload)
        isLimitReached(rtqPeU, cs) |
          isLimitReached(rtqPeP, cs) |
          isLimitReached(rtqPeB, cs) |
          isLimitReached(rtqPeG, cs) |
          isLimitReached(accPe, cs) |
          isLimitReached(hdPe, cs) |
          isLimitReached(quPeG, cs) |
          isLimitReached(quPeMQ, cs) |
          isLimitReached(quDePe, cs)
      else false

    def getCsvData(org: String): CsvDataList = {
      CsvDataList(
        CsvData(RTQ_PE_U.toString, org, getCsvRows(RTQ_PE_U, rtqPeU), getOffset(rtqPeU)),
        CsvData(RTQ_PE_P.toString, org, getCsvRows(RTQ_PE_P, rtqPeP), getOffset(rtqPeP)),
        CsvData(RTQ_PE_B.toString, org, getCsvRows(RTQ_PE_B, rtqPeB), getOffset(rtqPeB)),
        CsvData(RTQ_PE_G.toString, org, getCsvRows(RTQ_PE_G, rtqPeG), getOffset(rtqPeG)),
        CsvData(ACCOUNT_PE.toString, org, getCsvRows(ACCOUNT_PE, accPe), getOffset(accPe)),
        CsvData(HOLDING_PE.toString, org, getCsvRows(HOLDING_PE, hdPe), getOffset(hdPe)),
        CsvData(QUESTIONNAIRE_PE_G.toString, org, getCsvRows(QUESTIONNAIRE_PE_G, quPeG), getOffset(quPeG)),
        CsvData(QUESTIONNAIRE_PE_MQ.toString, org, getCsvRows(QUESTIONNAIRE_PE_MQ, quPeMQ), getOffset(quPeMQ)),
        CsvData(QUESTIONNAIRE_DETAIL_PE.toString, org, getCsvRows(QUESTIONNAIRE_DETAIL_PE, quDePe), getOffset(quDePe)))
    }

    /**
      * is reached limit
      *
      * @param le list of entity nfo
      * @param cs cache size
      * @return
      */
    private def isLimitReached(le: List[EntityInfo], cs: Int): Boolean =
      le.map(_.csvData.getBytes().length).sum >= cs

    private def getCsvRows(en: ENTITY_TYPE, le: List[EntityInfo]): String =
      le.map(_.csvData).foldLeft(entityCsvHeader(en))(_ + "\n" + _)

    private def getOffset(le: List[EntityInfo]): List[Long] = le.map(_.offSet).distinct

    /**
      * entity specific header
      *
      * @param entity entity
      * @return
      */
    private def entityCsvHeader(entity: ENTITY_TYPE): String = entity match {
      case RTQ_PE_U => "Jemstep1__Jemstep_Id__c,Jemstep1__Address__c,Jemstep1__Email__c,Jemstep1__Last_Name__c,Jemstep1__First_Name__c\n"
      case RTQ_PE_P => "Jemstep1__Jemstep_Id__c,Jemstep1__Portfolio_Value__c\n"
      case RTQ_PE_B => "Jemstep1__Jemstep_Id__c,Jemstep1__Annual_Savings__c,Jemstep1__Current_Annualized_Return__c,Jemstep1__Current_Best_Year__c,Jemstep1__Current_Best_Year_Return__c,Jemstep1__Current_Cost_Of_Fees__c,Jemstep1__Current_Expense_Ratio__c,Jemstep1__Current_Worst_Year__c,Jemstep1__Current_Worst_Year_Return__c,Jemstep1__Current_Year_With_Loss__c,Jemstep1__Fee_Savings__c,Jemstep1__Target_Annualized_Return__c,Jemstep1__Target_Best_Year__c,Jemstep1__Target_Best_Year_Return__c,Jemstep1__Target_Cost_of_Fees__c,Jemstep1__Target_Expense_Ratio__c,Jemstep1__Target_Worst_Year__c,Jemstep1__Target_Worst_Year_Return__c,Jemstep1__Target_Years_With_Loss__c\n"
      case RTQ_PE_G => "Jemstep1__Jemstep_Id__c,Jemstep1__Mapped_Target_Model__c\n"
      case ACCOUNT_PE => "Jemstep1__Account_Name__c,Jemstep1__Account_Number__c,Jemstep1__Account_Status__c,Jemstep1__Account_Type__c,Jemstep1__Account_Value__c,Jemstep1__Contact__c,Jemstep1__Date_Updated__c,Jemstep1__Institution__c,Jemstep1__Parent_Jemstep_Id__c,Jemstep1__Jemstep_Id__c\n"
      case HOLDING_PE => "Jemstep1__Account_Id__c,Jemstep1__Asset_Class__c,Jemstep1__Cost_Basis__c,Jemstep1__Date_Updated__c,Jemstep1__Description__c,Jemstep1__Parent_Jemstep_Id__c,Jemstep1__Jemstep_Id__c,Jemstep1__Price__c,Jemstep1__Quantity__c,Jemstep1__Symbol__c,Jemstep1__Value__c\n"
      case QUESTIONNAIRE_PE_G => "Jemstep1__Jemstep_Id__c,Jemstep1__Jemstep_Questionaire_Name__c,Jemstep1__Questionnaire_Type__c,Jemstep1__Parent_Jemstep_Id__c\n"
      case QUESTIONNAIRE_PE_MQ => "Jemstep1__Jemstep_Id__c,Jemstep1__Jemstep_Questionaire_Name__c,Jemstep1__Questionnaire_Type__c,Jemstep1__Parent_Jemstep_Id__c\n"
      case QUESTIONNAIRE_DETAIL_PE => "Jemstep1__Answer__c,Jemstep1__Full_Question__c,Jemstep1__Jemstep_Id__c,Jemstep1__Parent_Jemstep_Id__c,Jemstep1__Question__c,Jemstep1__Questionnaire_Type__c,Jemstep1__Last_Reviewed_Modified__c\n"
    }
  }

}
