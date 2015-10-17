package chapter7.json

import org.json4s._
import org.json4s.JsonDSL._
import org.json4s.native.JsonMethods._
import org.restwithscala.common.model.{Task, Note}

/**
 * For each example we show:
 *  - 1. Parse from String to JSON object
 *  - 2. Outputting a JSON object as a string
 *  - 3. Create JSON object by hand.
 *  - 4. Querying JSON
 *  - 5. Converting from and to a case class
 */
object Json4S extends App {


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

  // parse the json to a json4s value
  val parsedJson = parse(json);
  println(parsedJson)

  // 2. Outputting a JSON object as a string
  val asString = pretty(render(parsedJson)); // or compact
  println(asString);

  // 3. Create JSON object by hand.
  val notesList = Seq[Note](Note(1,"Note 1"), Note(2, "Note 2"))
  val jsonManually =
    ("id" -> 1) ~
    ("title" -> "title") ~
    ("content" -> "the content") ~
    ("assignedTo" ->
      ("name" -> "pietje")) ~
    ("notes" ->
      notesList.map { note =>
            (("id" -> note.id) ~
             ("content" -> note.content))}) ~
    ("status" ->
      ("status" -> "new"))


  println(compact(render(jsonManually)))

  // 4. querying json
  // json4s provides two approaches, we use the xpath like one (explain the functions in doc)
  // 'find', 'filter', 'transform', 'remove' and 'values in description
  println(jsonManually \\ "content") // all the content
  println(jsonManually \ "assignedTo" \ "name") // single name
  // allows unboxing
  println(jsonManually \\ "id" \\ classOf[JInt])

  // 5. to and from case classes
  implicit val formats = DefaultFormats
  val task = jsonManually.extract[Task]
  println(task)

  // also support automatic serialization
  import org.json4s.native.Serialization
  import org.json4s.native.Serialization.{read, write}
  implicit val autoFormat = Serialization.formats(NoTypeHints)

  val taskAsJson = write(task)
  println(taskAsJson)

  val backToTask = read[Task](taskAsJson)
  println(backToTask)
}