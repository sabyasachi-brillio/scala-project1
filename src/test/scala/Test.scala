import java.text.SimpleDateFormat
import java.util.Date

import com.jemstep.logging.BulkStreamLogging
import com.jemstep.logging.failed.FailureLogging
import com.jemstep.logging.unprocessed.UnprocessedLogging

object Test extends App {

  import net.liftweb.json._

  val dateFormat = new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss")
  val y = System.setProperty("current.date", dateFormat.format(new Date()))
  val x = System.setProperty("log.dir", "logs")

  implicit val formats: DefaultFormats = DefaultFormats

  //@SuppressWarnings(Array("org.wartremover.warts.null"))
  case class Model(x: String, y: Option[String])

  val jsonString: String =
    """{
      | "x": "test"
      |}""".stripMargin

  val jsonStringTwo =
    """{
      | "x": "test",
      | "y": "hello"
      |}""".stripMargin
  val k: Model = parse(jsonString).extract[Model]
  val o: Model = parse(jsonStringTwo).extract[Model]

  def getJson(m: Model): String = {
    import net.liftweb.json.Extraction._
    import net.liftweb.json.JsonAST._

    implicit val formats = net.liftweb.json.DefaultFormats

    prettyRender(render(decompose(m)).value)
  }

  def getJsonArray(lm: List[Model]): String = {
    import net.liftweb.json.Extraction._
    import net.liftweb.json.JsonAST._

    implicit val formats = net.liftweb.json.DefaultFormats

    prettyRender(render(JArray(lm.map(x => decompose(x)))).value)
      .replaceAll("\n", " ")
      .replaceAll("\t", " ")
      .replaceAll(" +", " ")
  }

  if (x == y) {
    BulkStreamLogging.logInformation(getJson(k))
    BulkStreamLogging.logDebugging(getJsonArray(List(k, o)))
  }
  if (x == y) {
    FailureLogging.flogInformation(getJson(k))
    FailureLogging.flogDebugging(getJsonArray(List(k, o)))
  }
  if (x == y) {
    UnprocessedLogging.ulogInformation(getJson(k))
    UnprocessedLogging.ulogDebugging(getJsonArray(List(k, o)))
  }

}
