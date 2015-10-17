package chapter7.json

import spray.json._
import DefaultJsonProtocol._
import org.restwithscala.common.model.{Status, Person, Task, Note}

/**
 * For each example we show:
 *  - 1. Parse from String to JSON object
 *  - 2. Outputting a JSON object as a string
 *  - 3. Create JSON object by hand.
 *  - 4. Querying JSON
 *  - 5. Converting from and to a case class
 */
object SprayJson extends App {


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

  val parsed = json.parseJson
  println(parsed)

  // 2. Outputting a JSON object as a string
  println(parsed.prettyPrint) // or .compactPrint


  // 3. Create JSON object by hand.
  val notesList = Seq[Note](Note(1,"Note 1"), Note(2, "Note 2"))
  val manually = JsObject(
    "id" -> JsNumber(1),
    "title" -> JsString("title"),
    "content" -> JsString("the content"),
    "assignedTo" -> JsObject("name" -> JsString("person")),
    "notes" -> JsArray(
      notesList.map({ note =>
        JsObject(
        "id" -> JsNumber(note.id),
        "content" -> JsString(note.content)
        )
      }).toVector),
   "status" -> JsObject("status" -> JsString("new"))
  )

  println(manually)

  // 4. querying json isn't supported, but we can access the fields directory
  println(manually.getFields("id"));
  println(manually.fields)

  // 5. to and from case classes automatically, link to manual
  // Explain
  val task = new Task(
    1, "This is the title", "This is the content", Some(Person("Me")),
    List[Note](Note(1,"Note 1"), Note(2, "Note 2")), Status("new"))

  object MyJsonProtocol extends DefaultJsonProtocol {
    implicit val noteFormat = jsonFormat2(Note)
    implicit val personFormat = jsonFormat1(Person)
    implicit val statusFormat = jsonFormat1(Status)
    implicit val taskFormat = jsonFormat6(Task)
  }

  import MyJsonProtocol._
  val taskAsString = task.toJson
  println(taskAsString)

  // and back to a task again
  val backToTask = taskAsString.convertTo[Task]
  println(backToTask)


}