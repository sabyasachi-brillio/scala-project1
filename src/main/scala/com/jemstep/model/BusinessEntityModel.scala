package com.jemstep.model

object BusinessEntityModel {

  import net.liftweb.json._

  implicit val formats: DefaultFormats = DefaultFormats

  object EntityType extends Enumeration {

    type ENTITY_TYPE = Value

    val ACCOUNT_PE: ENTITY_TYPE = Value("jemstep1__Account_PE__E")
    val HOLDING_PE: ENTITY_TYPE = Value("jemstep1__Holding_PE__e")
    val QUESTIONNAIRE_DETAIL_PE: ENTITY_TYPE = Value("jemstep1__Questionnaire_Detail_PE__e")
    val QUESTIONNAIRE_DETAIL_PEG: ENTITY_TYPE = Value("jemstep1__Questionnaire_Detail_PE__e")
    val QUESTIONNAIRE_DETAIL_PEMQ: ENTITY_TYPE = Value("jemstep1__Questionnaire_Detail_PE__e")
    val QUESTIONNAIRE_PE: ENTITY_TYPE = Value("jemstep1__Questionnaire_PE__e")
    val QUESTIONNAIRE_PE_G: ENTITY_TYPE = Value("jemstep1__Questionnaire_PE__e")
    val QUESTIONNAIRE_PE_MQ: ENTITY_TYPE = Value("jemstep1__Questionnaire_PE__e")
    val RTQ_PE: ENTITY_TYPE = Value("jemstep1__Investor_PE__e")
    val RTQ_PE_U: ENTITY_TYPE = Value("jemstep1__Investor_PE__e")
    val RTQ_PE_P: ENTITY_TYPE = Value("jemstep1__Investor_PE__e")
    val RTQ_PE_B: ENTITY_TYPE = Value("jemstep1__Investor_PE__e")
    val RTQ_PE_G: ENTITY_TYPE = Value("jemstep1__Investor_PE__e")
  }

  import EntityType._

  trait EntityObject

  case class EntityModel(entityType: ENTITY_TYPE,
                         organization: String,
                         jsonObj: List[EntityObject]) {
    override def toString: String =
      jsonObj.map(_.toString).fold("")((x, y) => x + "\n" + y)
  }

  case class AccountPe(Account_Name__c: String, Account_Number__c: String,
                       Account_Status__c: String, Account_Type__c: String,
                       Account_Value__c: String, Contact__c: String,
                       Date_Updated__c: String, Institution__c: String,
                       Parent_Jemstep_Id__c: String, Jemstep_Id__c: String) extends EntityObject {
    override def toString: String =
      Account_Name__c + "," + Account_Number__c + "," +
        Account_Status__c + "," + Account_Type__c + "," +
        Account_Value__c + "," + Contact__c + "," +
        Date_Updated__c + "," + Institution__c + "," +
        Parent_Jemstep_Id__c + "," + Jemstep_Id__c
  }

  case class HoldingPe(Account_Id__c: String, Asset_Class__c: String,
                       Cost_Basis__c: String, Date_Updated__c: String,
                       Description__c: String, Parent_Jemstep_Id__c: String,
                       Jemstep_Id__c: String, Price__c: String,
                       Quantity__c: String, Symbol__c: String, Value__c: String) extends EntityObject {
    override def toString: String =
      Account_Id__c + "," + Asset_Class__c + "," +
        Cost_Basis__c + "," + Date_Updated__c + "," +
        Description__c + "," + Parent_Jemstep_Id__c + "," +
        Jemstep_Id__c + "," + Price__c + "," +
        Quantity__c + "," + Symbol__c + "," + Value__c
  }

  abstract class QuestionnaireDetailPe() extends EntityObject

  case class QuestionnaireDetailPeG(Answer__c: String, Full_Question__c: String,
                                   Jemstep_Id__c: String, Jemstep_Questionaire_Detail_Id__c: String,
                                   Question__c: String, Questionnaire_Type__c: String,
                                   Last_Reviewed_Modified__c: String) extends  QuestionnaireDetailPe{
    override def toString: String =
      Answer__c + "," + Full_Question__c + "," +
        Jemstep_Id__c + "," + Jemstep_Questionaire_Detail_Id__c + "," +
        Question__c + "," + Questionnaire_Type__c + "," +
        Last_Reviewed_Modified__c
  }

  case class QuestionnaireDetailPeMQ(Answer__c: String, Full_Question__c: String,
                                    Jemstep_Id__c: String, Jemstep_Questionaire_Detail_Id__c: String,
                                    Question__c: String, Questionnaire_Type__c: String,
                                    Last_Reviewed_Modified__c: String) extends QuestionnaireDetailPe {
    override def toString: String =
      Answer__c + "," + Full_Question__c + "," +
        Jemstep_Id__c + "," + Jemstep_Questionaire_Detail_Id__c + "," +
        Question__c + "," + Questionnaire_Type__c + "," +
        Last_Reviewed_Modified__c
  }

  abstract class QuestionnairePe() extends EntityObject

  case class QuestionnairePeG(Jemstep_Id__c: String,
                             Questionnaire_Id__c: String, Questionnaire_Type__c: String, Parent_Jemstep_Id__c: String) extends QuestionnairePe {
    override def toString: String =
      Jemstep_Id__c + "," +
        Questionnaire_Id__c + "," + Questionnaire_Type__c + "," + Parent_Jemstep_Id__c
  }


  case class QuestionnairePeMQ(Jemstep_Id__c: String, Questionnaire_Id__c: String, Questionnaire_Type__c: String, Parent_Jemstep_Id__c: String) extends QuestionnairePe {
    override def toString: String =
      Jemstep_Id__c + "," +
        Questionnaire_Id__c + "," + Questionnaire_Type__c + "," + Parent_Jemstep_Id__c
  }

  abstract class RtqPe() extends EntityObject

  case class RtqPeB(Jemstep_Id__c: String,
                    Annual_Savings__c: String, Current_Annualized_Return__c: String,
                    Current_Best_Year__c: String, Current_Best_Year_Return__c: String,
                    Current_Cost_Of_Fees__c: String, Current_Expense_Ratio__c: String,
                    Current_Worst_Year__c: String, Current_Worst_Year_Return__c: String,
                    Current_Year_With_Loss__c: String, Fee_Savings__c: String,
                    Target_Annualized_Return__c: String, Target_Best_Year__c: String,
                    Target_Best_Year_Return__c: String, Target_Cost_of_Fees__c: String,
                    Target_Expense_Ratio__c: String, Target_Worst_Year__c: String,
                    Target_Worst_Year_Return__c: String, Target_Years_With_Loss__c: String) extends RtqPe {
    override def toString: String =
      Jemstep_Id__c + "," +
        Annual_Savings__c + "," + Current_Annualized_Return__c + "," +
        Current_Best_Year__c + "," + Current_Best_Year_Return__c + "," +
        Current_Cost_Of_Fees__c + "," + Current_Expense_Ratio__c + "," +
        Current_Worst_Year__c + "," + Current_Worst_Year_Return__c + "," +
        Current_Year_With_Loss__c + "," + Fee_Savings__c + "," +
        Target_Annualized_Return__c + "," + Target_Best_Year__c + "," +
        Target_Best_Year_Return__c + "," + Target_Cost_of_Fees__c + "," +
        Target_Expense_Ratio__c + "," + Target_Worst_Year__c + "," +
        Target_Worst_Year_Return__c + "," + Target_Years_With_Loss__c
  }

  case class RtqPeG(Jemstep_Id__c: String, Mapped_Target_Model__c: String) extends RtqPe {
    override def toString: String = Jemstep_Id__c + "," + Mapped_Target_Model__c
  }

  case class RtqPeP(Jemstep_Id__c: String, Portfolio_Value__c: String) extends RtqPe {
    override def toString: String = Jemstep_Id__c + "," + Portfolio_Value__c
  }

  case class RtqPeU(Jemstep_Id__c: String, Address__c: String,
                    Email__c: String, Last_Name__c: String, First_Name__c: String) extends RtqPe {
    override def toString: String =
      Jemstep_Id__c + "," + Address__c + "," +
        Email__c + "," + Last_Name__c + "," + First_Name__c
  }

  def getJsonArray(lm: List[EntityObject]): String = {
    import net.liftweb.json.Extraction._
    import net.liftweb.json.JsonAST._
    implicit val formats: DefaultFormats = net.liftweb.json.DefaultFormats
    prettyRender(render(JArray(lm.map(x => decompose(x)))).value)
  }


  def getCsvArray(lm: List[EntityObject]): String = {
    lm.headOption match {
      case Some(obj) =>
        val header: String = obj match {
          case _: AccountPe => "Jemstep1__Account_Name__c,Jemstep1__Account_Number__c,Jemstep1__Account_Status__c,Jemstep1__Account_Type__c,Jemstep1__Account_Value__c,Jemstep1__Contact__c,Jemstep1__Date_Updated__c,Jemstep1__Institution__c,Jemstep1__Parent_Jemstep_Id__c,Jemstep1__Jemstep_Id__c\n"
          case _: HoldingPe => "Jemstep1__Account_Id__c,Jemstep1__Asset_Class__c,Jemstep1__Cost_Basis__c,Jemstep1__Date_Updated__c,Jemstep1__Description__c,Jemstep1__Parent_Jemstep_Id__c,Jemstep1__Jemstep_Id__c,Jemstep1__Price__c,Jemstep1__Quantity__c,Jemstep1__Symbol__c,Jemstep1__Value__c\n"
          case _: QuestionnaireDetailPeG => "Jemstep1__Answer__c,Jemstep1__Full_Question__c,Jemstep1__Jemstep_Id__c,Jemstep1__Parent_Jemstep_Id__c,Jemstep1__Question__c,Jemstep1__Questionnaire_Type__c,Jemstep1__Last_Reviewed_Modified__c\n"
          case _: QuestionnaireDetailPeMQ => "Jemstep1__Answer__c,Jemstep1__Full_Question__c,Jemstep1__Jemstep_Id__c,Jemstep1__Parent_Jemstep_Id__c,Jemstep1__Question__c,Jemstep1__Questionnaire_Type__c,Jemstep1__Last_Reviewed_Modified__c\n"
          case _: QuestionnairePeG => "Jemstep1__Jemstep_Id__c,Jemstep1__Jemstep_Questionaire_Name__c,Jemstep1__Questionnaire_Type__c,Jemstep1__Parent_Jemstep_Id__c\n"
          case _: QuestionnairePeMQ => "Jemstep1__Jemstep_Id__c,Jemstep1__Jemstep_Questionaire_Name__c,Jemstep1__Questionnaire_Type__c,Jemstep1__Parent_Jemstep_Id__c\n"
          case _: RtqPeB => "Jemstep1__Jemstep_Id__c,Jemstep1__Annual_Savings__c,Jemstep1__Current_Annualized_Return__c,Jemstep1__Current_Best_Year__c,Jemstep1__Current_Best_Year_Return__c,Jemstep1__Current_Cost_Of_Fees__c,Jemstep1__Current_Expense_Ratio__c,Jemstep1__Current_Worst_Year__c,Jemstep1__Current_Worst_Year_Return__c,Jemstep1__Current_Year_With_Loss__c,Jemstep1__Fee_Savings__c,Jemstep1__Target_Annualized_Return__c,Jemstep1__Target_Best_Year__c,Jemstep1__Target_Best_Year_Return__c,Jemstep1__Target_Cost_of_Fees__c,Jemstep1__Target_Expense_Ratio__c,Jemstep1__Target_Worst_Year__c,Jemstep1__Target_Worst_Year_Return__c,Jemstep1__Target_Years_With_Loss__c\n"
          case _: RtqPeG => "Jemstep1__Jemstep_Id__c,Jemstep1__Mapped_Target_Model__c\n"
          case _: RtqPeP => "Jemstep1__Jemstep_Id__c,Jemstep1__Portfolio_Value__c\n"
          case _: RtqPeU => "Jemstep1__Jemstep_Id__c,Jemstep1__Address__c,Jemstep1__Email__c,Jemstep1__Last_Name__c,Jemstep1__First_Name__c\n"
        }
        lm.map(_.toString).fold(header)((x, y) => x + "\n" + y)
      case _ => ""
    }
  }
}
