package com.jemstep.model

import com.jemstep.model.BusinessEntityModel.{EntityModel, EntityType, RtqPe, RtqPeB}

object BacktestMetricsModel {

  /**
    *
    * Type Three
    *
    */
  case class BacktestMetrics(actualFeeSavingAnnual: String, currentAnnualizedReturn: String,
                             currentBestYear: String, currentBestYearPercentage: String,
                             costOfFeesOnCurrentPortfolio: String, currentExpenseRatio: String,
                             currentWorstYear: String, currentWorstYearPercentage: String,
                             currentYearsWithLosses: String, opportunityFeeSaving20Yrs: String,
                             targetAnnualizedReturn: String, targetBestYear: String,
                             targetBestYearPercentage: String, costOfFeesOnTargetPortfolio: String,
                             targetExpenseRatio: String, targetWorstYear: String,
                             targetWorstYearPercentage: String, targetYearsWithLosses: String) extends BusinessModel {
    import EntityType._

    def extractEntityModels(userId: String, org: String, operation: String): List[EntityModel] = {

      val rtqPe: RtqPe =
        RtqPeB(Jemstep_Id__c = userId,
          Annual_Savings__c = actualFeeSavingAnnual, Current_Annualized_Return__c = currentAnnualizedReturn,
          Current_Best_Year__c = currentBestYear, Current_Best_Year_Return__c = currentBestYearPercentage,
          Current_Cost_Of_Fees__c = costOfFeesOnCurrentPortfolio, Current_Expense_Ratio__c = currentExpenseRatio,
          Current_Worst_Year__c = currentWorstYear, Current_Worst_Year_Return__c = currentWorstYearPercentage,
          Current_Year_With_Loss__c = currentYearsWithLosses, Fee_Savings__c = opportunityFeeSaving20Yrs,
          Target_Annualized_Return__c = targetAnnualizedReturn, Target_Best_Year__c = targetBestYear,
          Target_Best_Year_Return__c = targetBestYearPercentage, Target_Cost_of_Fees__c = costOfFeesOnTargetPortfolio,
          Target_Expense_Ratio__c = targetExpenseRatio, Target_Worst_Year__c = targetWorstYear,
          Target_Worst_Year_Return__c = targetWorstYearPercentage, Target_Years_With_Loss__c = targetYearsWithLosses)

      EntityModel(RTQ_PE_B, org, rtqPe :: Nil) :: Nil
    }
  }

}
