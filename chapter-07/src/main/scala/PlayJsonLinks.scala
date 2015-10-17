import org.restwithscala.common.model.{Note, Person, Status, Task}
import play.api.libs.json._
import play.api.libs.functional.syntax._

object PlayJsonLinks extends App {

  /**
   * Trait that we can extend when we want to add links
   */
  trait HasLinks {
    val links: List[Links]
  }

  /**
   * Case class, representing a specific link
   */
  case class Links(rel: String, href: String, `media-type`: String)


  /**
   * The low priority write instances, these can be used if we want
   * to write a task without links
   */
  trait LowPriorityWritesInstances {

    // use standard writes for the case classes
    implicit val statusWrites = Json.writes[Status]
    implicit val noteWrites = Json.writes[Note]
    implicit val personWrites = Json.writes[Person]
    implicit val taskWrites = Json.writes[Task]
    implicit val linkWrites = Json.writes[Links]

    // and a custom one for the trait
    implicit object hiPriorityWrites extends OWrites[HasLinks] {
      def writes(hi: HasLinks) = Json.obj("_links" -> hi.links)
    }
  }

  /**
   * The write instance which we include
   */
  object WritesInstances extends LowPriorityWritesInstances {
    implicit val taskWithLinksWrites = new Writes[Task with HasLinks] {
      def writes(o: Task with HasLinks) = {
        (implicitly[OWrites[HasLinks]].writes(o)) ++ taskWrites.writes(o).as[JsObject]
      }
    }
  }

  val task = new Task(
    1, "This is the title", "This is the content", Some(Person("Me")),
    List[Note](Note(1,"Note 1"), Note(2, "Note 2")), Status("new")) with HasLinks {
    val links = List(Links("self","/tasks/123","application/vnd.restwithscala.task+json"))
  }

  // import the implicit convertors
  import WritesInstances._

  // output the text with the links
  println(Json.prettyPrint(Json.toJson(task)))
}
