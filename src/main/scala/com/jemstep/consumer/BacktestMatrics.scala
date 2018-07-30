package com.jemstep.consumer

import com.jemstep.consumer.Goal.BusinessModel

import scala.util.parsing.json.JSON

object BacktestMatrics {
  case class RTQ_PE(annualsavings: Double,currentAnnualizedReturn:Double, currentBestYear:Double, currentBestYearPercentage:Double,
                    costOfFeesOnCurrentPortfolio:Double,currentExpenseRatio:Double,currentWorstYear:Double,
                    currentWorstYearPercentage:Double,currentYearsWithLosses:Double,opportunityFeeSaving20Yrs:Double,
                    targetAnnualizedReturn:Double,targetBestYear:Double,targetBestYearPercentage:Double,costOfFeesOnTargetPortfolio:Double,
                    targetExpenseRatio:Double,targetWorstYear:Double,targetWorstYearPercentage:Double,targetYearsWithLosses:Double) extends  BusinessModel
  {
    override def toString: String = annualsavings.toString+","+currentAnnualizedReturn.toString+","+currentBestYear.toString+
      ","+currentBestYearPercentage.toString+","+costOfFeesOnCurrentPortfolio.toString+","+currentExpenseRatio.toString+","+
      currentWorstYear.toString+","+currentWorstYearPercentage+","+currentYearsWithLosses.toString+","+opportunityFeeSaving20Yrs.toString+
      ","+targetAnnualizedReturn.toString+","+targetBestYear.toString+","+targetBestYearPercentage.toString+","+costOfFeesOnTargetPortfolio.toString+","+
      targetExpenseRatio.toString+","+targetWorstYear.toString+","+targetWorstYearPercentage.toString+","+targetYearsWithLosses.toString

  }

  def parsing(jsonstring1: String, organizationId: String):ListOfBussinesModel ={
    class CC[T] {
      def unapply(a: Any): Option[T] = Some(a.asInstanceOf[T])
    }


    object M extends CC[Map[String, Any]]
    // println(M.toString)
    //object L extends CC[List[Any]]
    //println(L.toString)
    //object S extends CC[String]
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

      D(annualsavings)=map("actualFeeSavingAnnual")
      D(currentAnnualizedReturn)=map("currentAnnualizedReturn")
      D(currentBestYear)=map("currentBestYear")
      D(currentBestYearPercentage)=map("currentBestYearPercentage")
      D(costOfFeesOnCurrentPortfolio)=map("costOfFeesOnCurrentPortfolio")
      D(currentExpenseRatio)=map("currentExpenseRatio")
      D(currentWorstYear)=map("currentWorstYear")
      D(currentWorstYearPercentage)=map("currentWorstYearPercentage")
      D(currentYearsWithLosses)=map("currentYearsWithLosses")
      D(opportunityFeeSaving20Yrs)=map("opportunityFeeSaving20Yrs")
      D(targetAnnualizedReturn)=map("targetAnnualizedReturn")
      D(targetBestYear)=map("targetBestYear")
      D(targetBestYearPercentage)=map("targetBestYearPercentage")
      D(costOfFeesOnTargetPortfolio)=map("costOfFeesOnTargetPortfolio")
      D(targetExpenseRatio)=map("targetExpenseRatio")
      D(targetWorstYear)=map("targetWorstYear")
      D(targetWorstYearPercentage)=map("targetWorstYearPercentage")
      D(targetYearsWithLosses)=map("targetYearsWithLosses")


    } yield {
      println("Annualsavings:"+annualsavings)
      println("currentAnnualizedReturn:"+currentAnnualizedReturn)
      println("currentBestYear:"+currentBestYear)
      println("currentBestYearPercentage:"+currentBestYearPercentage)
      println("costOfFeesOnCurrentPortfolio:"+costOfFeesOnCurrentPortfolio)
      println("currentExpenseRatio:"+currentExpenseRatio)
      println("currentWorstYear:"+currentWorstYear)
      println("currentWorstYearPercentage:"+currentWorstYearPercentage)
      println("currentWorstYear:"+currentWorstYear)
      println("currentYearsWithLosses:"+currentYearsWithLosses)
      println("opportunityFeeSaving20Yrs:"+opportunityFeeSaving20Yrs)
      println("targetAnnualizedReturn:"+targetAnnualizedReturn)
      println("targetBestYear:"+targetBestYear)
      println("targetBestYearPercentage:"+targetBestYearPercentage)
      println("costOfFeesOnTargetPortfolio:"+costOfFeesOnTargetPortfolio)
      println("targetExpenseRatio:"+targetExpenseRatio)
      println("targetWorstYear:"+targetWorstYear)
      println("targetWorstYearPercentage:"+targetWorstYearPercentage)
      println("targetYearsWithLosses:"+targetYearsWithLosses)
      RTQ_PE(annualsavings,currentAnnualizedReturn,currentBestYear,currentBestYearPercentage,costOfFeesOnCurrentPortfolio,currentExpenseRatio,
        currentWorstYear,currentWorstYearPercentage,currentYearsWithLosses,opportunityFeeSaving20Yrs,targetAnnualizedReturn,targetBestYear,
        targetBestYearPercentage,costOfFeesOnTargetPortfolio,targetExpenseRatio,targetWorstYear,targetWorstYearPercentage,targetYearsWithLosses)

    }
    new ListOfBussinesModel(organizationId, result)
  }
}
