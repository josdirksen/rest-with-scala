package chapter7.json

import argonaut._
import Argonaut._
import org.restwithscala.common.model.{Task, Status, Person, Note}

import scalaz.\/

object ArgonautJSON extends App{


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

  // 1. Parse from String to JSON object
  val parsed: \/[String, Json] = json.parse // returns a scalaz disjunction (right biased either)
  println(parsed)
  val parsedValue = parsed | jString("Error parsing")

  // 2. output the json as a string
  println(parsedValue.spaces4)

  // 3. Create object by hand (explain operators)
  val notesList = List[Note](Note(1,"Note 1"), Note(2, "Note 2"))
  val jsonObjectBuilderWithCodec: Json =
      ("status" := Json("status" := "New")) ->:
      ("notes" := notesList.map(note => Json("id" := note.id, "content" := note.content)) ) ->:
      ("assignedTo" := Json("name" := "Someone")) ->:
      ("content" := "This is the content") ->:
      ("title" := "The Title") ->:
      ("id" := 1) ->: jEmptyObject

  println(jsonObjectBuilderWithCodec.spaces4)

  // 4. Query json using lenses
  val innerKey2StringLens = jObjectPL >=>   // Lens composition starts with converting to object...
    jsonObjectPL("notes") >=>           // ...Looking up the "outerkey1" field...
    jArrayPL >=>                           // ...Converting to an object...
    jsonArrayPL(0) >=>               // ...Looking up the "innerkey2" field...
    jObjectPL >=>               // ...Looking up the "innerkey2" field...
    jsonObjectPL("id") >=>               // ...Looking up the "innerkey2" field...
    jStringPL

  println(innerKey2StringLens.get(jsonObjectBuilderWithCodec)) // return Option

  // 5. converting to and from case classes
  object Encodings {

    implicit def StatusCodecJson: CodecJson[Status] =
      casecodec1(Status.apply, Status.unapply)("status")
    implicit def NoteCodecJson: CodecJson[Note] =
      casecodec2(Note.apply, Note.unapply)("id", "content")
    implicit def PersonCodecJson: CodecJson[Person] =
      casecodec1(Person.apply, Person.unapply)("name")
    implicit def TaskCodecJson: CodecJson[Task] =
      casecodec6(Task.apply, Task.unapply)("id", "title", "content", "assignedTo", "notes", "status")
  }

  import Encodings._

  val task = new Task(
    1, "This is the title", "This is the content", Some(Person("Me")),
    List[Note](Note(1,"Note 1"), Note(2, "Note 2")), Status("new"))

  val taskAsJson = task.asJson
  println(taskAsJson.spaces4)

  val taskBackAgain = Parse.decodeOption[Task](taskAsJson.spaces4)
  println(taskBackAgain)
}