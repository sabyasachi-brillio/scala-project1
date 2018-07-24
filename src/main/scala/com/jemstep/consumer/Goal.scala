package com.jemstep.consumer
import scala.util.parsing.json._
object Goal {
  def parsing(jsonstring1:String) {
    class CC[T] {
      def unapply(a: Any): Option[T] = Some(a.asInstanceOf[T])
    }

    object M extends CC[Map[String, Any]]

    object L extends CC[List[Any]]

    object S extends CC[String]

    object D extends CC[Double]

    object B extends CC[Boolean]

    val jsonString =jsonstring1.stripMargin

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
      println("answer:" + answer)
      println("full_question:" + full_question)
      println("jemstep_Id:" + jemstep_Id)
      println("parent_jemstep_id:" + parent_jemstep_id)
    }
  }

}
