package chapter7.json

import org.restwithscala.common.model.{Status, Person, Task, Note}
import play.api.libs.json._
import play.api.libs.functional.syntax._

/**
 * For each example we show:
 *  - 1. Parse from String to JSON object
 *  - 2. Outputting a JSON object as a string
 *  - 3. Create JSON object by hand.
 *  - 4. Querying JSON
 *  - 5. Converting from and to a case class
 */
object PlayJson extends App {

  // 1. Parse from String to JSON object
  val json = """{
    "id": 1,
    "title": "The Title",
    "content": "This is the data to create a new task",
    "assignedTo": {
      "name": "pietje"
    },
    "notes": [],
    "status": {
      "status": "New"
    }
  }"""


  val fromJson = Json.parse(json)
  println(fromJson)

  // 2. Outputting a JSON object as a string
  println(Json.prettyPrint(fromJson)) // or .stringify

  // 3. Create JSON object by hand.
  val notesList = Seq[Note](Note(1,"Note 1"), Note(2, "Note 2"))
  val manually = JsObject(Seq(
    "id" -> JsNumber(1),
    "title" -> JsString("title"),
    "content" -> JsString("the content"),
    "assignedTo" -> JsObject(Seq("name" -> JsString("person"))),
    "notes" -> JsArray(
      notesList.map({ note =>
        JsObject(Seq(
          "id" -> JsNumber(note.id),
          "content" -> JsString(note.content)
        ))
      })),
    "status" -> JsObject(Seq("status" -> JsString("new")))
  ))

  println(manually)

  // 4. Query using the / and // parameters
  println(manually \\ "content") // all the content
  println(manually \ "assignedTo" \ "name") // single name
  // allows unboxing
  println((manually \\ "id" )(2))

  // 5. to and from case classes
  // Explain
  object Formats {
    implicit val noteFormat = Json.format[Note]
    implicit val statusFormat = Json.format[Status]
    implicit val personFormat = Json.format[Person]
    implicit val taskFormat = Json.format[Task]
  }

  import Formats._

  val task = new Task(
    1, "This is the title", "This is the content", Some(Person("Me")),
    List[Note](Note(1,"Note 1"), Note(2, "Note 2")), Status("new")) with Links {
    val links = "Hello"
  }

  println(task)

  trait Links {
    val links: String
  }


  val toJson = Json.toJson(task)
  println(toJson)
  val andBackAgain = Json.fromJson[Task](toJson)
  println(andBackAgain)

}
