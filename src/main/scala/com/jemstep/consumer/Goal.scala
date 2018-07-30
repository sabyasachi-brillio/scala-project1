package com.jemstep.consumer
//import Goal.Questionnaire_Detail_PE
import scala.util.parsing.json._
object Goal {

  trait BusinessModel
  case class Questionnaire_Detail_PE(jemstep_Id: String,full_question:String, answer:String, parent_jemstep_id:String) extends  BusinessModel{
    override def toString: String = jemstep_Id+","+full_question+","+answer+","+parent_jemstep_id
  }

  def parsing(jsonstring1: String, organizationId: String): ListOfBussinesModel ={
    class CC[T] {
      def unapply(a: Any): Option[T] = Some(a.asInstanceOf[T])
    }




    object M extends CC[Map[String, Any]]
    // println(M.toString)
    object L extends CC[List[Any]]
    //println(L.toString)
    object S extends CC[String]
    //println(S.toString)
    //object D extends CC[Double]

    //println(D.toString)

    //object B extends CC[Boolean]

    //println(B.toString)
    val jsonString = jsonstring1.stripMargin
    // println(jsonString)

    @SuppressWarnings(Array("org.wartremover.warts.NonUnitStatements"))
    // @SuppressWarnings(Array("org.wartremover.warts.Any"))
    val result = for {
      Some(M(map)) <- List(JSON.parseFull(jsonString))

      M(questionnaire) = map("questionnaire")
      L(questions) = questionnaire("questions")
      M(question) <- questions
      S(jemstep_Id) = question("questionId")
      S(full_question) = " "
      S(answer) = question("answer")
      S(parent_jemstep_id) = map("id")

    } yield {
      Questionnaire_Detail_PE(jemstep_Id,full_question,answer,parent_jemstep_id)
    }
    new ListOfBussinesModel(organizationId, result)
  }
}
